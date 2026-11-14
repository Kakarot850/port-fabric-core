/*
 * Sophisticated Core Fabric port: adapted from NeoForge 26.1.2.53-beta (LGPL-2.1), see the class javadoc for the
 * differences. Original header follows.
 */
/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.p3pp3rf1y.sophisticatedcore.fabric.fluids;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import net.minecraft.core.Holder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jspecify.annotations.Nullable;

/**
 * {@link ItemStack} equivalent for fluids.
 * The main difference is that a fluid stack is always required to have an amount, while an item stack defaults to 1.
 * Another difference is that the component prototype of a fluid stack is currently always empty, while an item stack gets its component prototype from the item.
 *
 * <p>Most methods in this class are adapted from {@link ItemStack}.
 */
/**
 * Fabric differences from NeoForge's {@code FluidStack}: amounts stay in millibuckets ({@link FluidType#BUCKET_VOLUME});
 * convert with {@link #toVariant()} / {@link FluidType#DROPLETS_PER_MB} only at Fabric Transfer API boundaries.
 * Implements vanilla {@link DataComponentHolder} with the non-{@code Supplier} methods of NeoForge's
 * {@code MutableDataComponentHolder} inlined (data components are plain values in this port). {@code getTooltipLines}
 * is omitted: it needs NeoForge's {@code Fluid#appendHoverText} patch and tooltip event, and neither mod calls it.
 * The prototype component map falls back to empty while registry components are not bound yet.
 */
public final class FluidStack implements DataComponentHolder, FluidInstance {
    /**
     * A standard map codec for fluid stacks that does not accept empty stacks.
     */
    public static final MapCodec<FluidStack> MAP_CODEC = MapCodec.recursive(
            "FluidStack",
            c -> RecordCodecBuilder.mapCodec(
                    instance -> instance.group(
                            FLUID_HOLDER_CODEC_WITH_BOUND_COMPONENTS.fieldOf(FIELD_ID).forGetter(FluidStack::typeHolder),
                            ExtraCodecs.POSITIVE_INT.fieldOf(FIELD_AMOUNT).forGetter(FluidStack::getAmount), // note: no .orElse(1) compared to ItemStack
                            DataComponentPatch.CODEC.optionalFieldOf(FIELD_COMPONENTS, DataComponentPatch.EMPTY)
                                    .forGetter(stack -> stack.components.asPatch()))
                            .apply(instance, FluidStack::new)));
    /**
     * A standard codec for fluid stacks that does not accept empty stacks.
     */
    public static final Codec<FluidStack> CODEC = Codec.lazyInitialized(MAP_CODEC::codec);

    /**
     * A standard codec for fluid stacks that always deserializes with a fixed amount,
     * and does not accept empty stacks.
     *
     * <p>Fluid equivalent of {@link ItemStack#SINGLE_ITEM_CODEC}.
     */
    public static Codec<FluidStack> fixedAmountCodec(int amount) {
        return Codec.lazyInitialized(
                () -> RecordCodecBuilder.create(
                        instance -> instance.group(
                                FLUID_HOLDER_CODEC.fieldOf(FIELD_ID).forGetter(FluidStack::typeHolder),
                                DataComponentPatch.CODEC.optionalFieldOf(FIELD_COMPONENTS, DataComponentPatch.EMPTY)
                                        .forGetter(stack -> stack.components.asPatch()))
                                .apply(instance, (holder, patch) -> new FluidStack(holder, amount, patch))));
    }

    /**
     * A standard codec for fluid stacks that accepts empty stacks, serializing them as {@code {}}.
     */
    public static final Codec<FluidStack> OPTIONAL_CODEC = ExtraCodecs.optionalEmptyMap(CODEC)
            .xmap(optional -> optional.orElse(FluidStack.EMPTY), stack -> stack.isEmpty() ? Optional.empty() : Optional.of(stack));
    /**
     * A stream codec for fluid stacks that accepts empty stacks.
     */
    public static final StreamCodec<RegistryFriendlyByteBuf, FluidStack> OPTIONAL_STREAM_CODEC = new StreamCodec<>() {
        @Override
        public FluidStack decode(RegistryFriendlyByteBuf buf) {
            int amount = buf.readVarInt();
            if (amount <= 0) {
                return FluidStack.EMPTY;
            } else {
                Holder<Fluid> holder = FLUID_HOLDER_STREAM_CODEC.decode(buf);
                DataComponentPatch patch = DataComponentPatch.STREAM_CODEC.decode(buf);
                return new FluidStack(holder, amount, patch);
            }
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, FluidStack stack) {
            if (stack.isEmpty()) {
                buf.writeVarInt(0);
            } else {
                buf.writeVarInt(stack.getAmount());
                FLUID_HOLDER_STREAM_CODEC.encode(buf, stack.typeHolder());
                DataComponentPatch.STREAM_CODEC.encode(buf, stack.components.asPatch());
            }
        }
    };
    /**
     * A stream codec for fluid stacks that does not accept empty stacks.
     */
    public static final StreamCodec<RegistryFriendlyByteBuf, FluidStack> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public FluidStack decode(RegistryFriendlyByteBuf buf) {
            FluidStack stack = FluidStack.OPTIONAL_STREAM_CODEC.decode(buf);
            if (stack.isEmpty()) {
                throw new DecoderException("Empty FluidStack not allowed");
            } else {
                return stack;
            }
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, FluidStack stack) {
            if (stack.isEmpty()) {
                throw new EncoderException("Empty FluidStack not allowed");
            } else {
                FluidStack.OPTIONAL_STREAM_CODEC.encode(buf, stack);
            }
        }
    };
    public static final FluidStack EMPTY = new FluidStack(null);
    private int amount;
    private final @Nullable Holder<Fluid> fluid;
    private final PatchedDataComponentMap components;

    @Override
    public DataComponentMap getComponents() {
        return isEmpty() ? DataComponentMap.EMPTY : components;
    }

    public DataComponentMap getPrototype() {
        return isEmpty() ? DataComponentMap.EMPTY : prototypeComponents(typeHolder());
    }

    public DataComponentPatch getComponentsPatch() {
        return !this.isEmpty() ? this.components.asPatch() : DataComponentPatch.EMPTY;
    }

    public DataComponentMap immutableComponents() {
        return !this.isEmpty() ? this.components.toImmutableMap() : DataComponentMap.EMPTY;
    }

    public boolean hasNonDefault(DataComponentType<?> type) {
        return !isEmpty() && components.hasNonDefault(type);
    }

    public boolean isComponentsPatchEmpty() {
        return !this.isEmpty() ? this.components.asPatch().isEmpty() : true; // NeoForge-patched PatchedDataComponentMap#isPatchEmpty
    }

    public FluidStack(Fluid fluid, int amount, DataComponentPatch patch) {
        this(fluid.builtInRegistryHolder(), amount, patch);
    }

    public FluidStack(Fluid fluid, int amount) {
        this(fluid, amount, DataComponentPatch.EMPTY);
    }

    public FluidStack(Holder<Fluid> fluid, int amount) {
        this(fluid, amount, DataComponentPatch.EMPTY);
    }

    public FluidStack(Holder<Fluid> fluid, int amount, DataComponentPatch patch) {
        this(fluid, amount, PatchedDataComponentMap.fromPatch(prototypeComponents(fluid), patch));
    }

    private FluidStack(Holder<Fluid> fluid, int amount, PatchedDataComponentMap components) {
        this.fluid = fluid;
        this.amount = amount;
        this.components = components;
    }

    private FluidStack(@Nullable Void unused) {
        this.fluid = null;
        this.components = new PatchedDataComponentMap(DataComponentMap.EMPTY);
    }

    /**
     * Checks if this fluid stack is empty.
     */
    public boolean isEmpty() {
        return this == EMPTY || fluid.value().isSame(Fluids.EMPTY) || this.amount <= 0;
    }

    /**
     * Splits off a stack of the given amount of this stack and reduces this stack by the amount.
     */
    public FluidStack split(int amount) {
        int i = Math.min(amount, getAmount());
        FluidStack fluidStack = this.copyWithAmount(i);
        this.shrink(i);
        return fluidStack;
    }

    /**
     * Creates a copy of this stack with {@code 0} amount.
     */
    public FluidStack copyAndClear() {
        if (this.isEmpty()) {
            return EMPTY;
        } else {
            FluidStack fluidStack = this.copy();
            this.setAmount(0);
            return fluidStack;
        }
    }

    /**
     * Returns the fluid in this stack, or {@link Fluids#EMPTY} if this stack is empty.
     */
    public Fluid getFluid() {
        return typeHolder().value();
    }

    @Override
    public Holder<Fluid> typeHolder() {
        return isEmpty() ? Fluids.EMPTY.builtInRegistryHolder() : fluid;
    }

    public boolean is(Predicate<Holder<Fluid>> holderPredicate) {
        return holderPredicate.test(this.typeHolder());
    }

    /**
     * Creates a copy of this fluid stack.
     */
    public FluidStack copy() {
        if (this.isEmpty()) {
            return EMPTY;
        } else {
            return new FluidStack(typeHolder(), amount(), this.components.copy());
        }
    }

    /**
     * Creates a copy of this fluid stack with the given amount.
     */
    public FluidStack copyWithAmount(int amount) {
        if (this.isEmpty()) {
            return EMPTY;
        } else {
            FluidStack fluidStack = this.copy();
            fluidStack.setAmount(amount);
            return fluidStack;
        }
    }

    public FluidStack transmuteCopy(Fluid newFluid) {
        return transmuteCopy(newFluid, amount());
    }

    public FluidStack transmuteCopy(Fluid newFluid, int newAmount) {
        return isEmpty() ? EMPTY : transmuteCopyIgnoreEmpty(newFluid, newAmount);
    }

    private FluidStack transmuteCopyIgnoreEmpty(Fluid newFluid, int newAmount) {
        return new FluidStack(newFluid, newAmount, components.asPatch());
    }

    /**
     * Checks if the two fluid stacks are equal. This checks the fluid, amount, and components.
     *
     * @return {@code true} if the two fluid stacks have equal fluid, amount, and components
     */
    public static boolean matches(FluidStack first, FluidStack second) {
        if (first == second) {
            return true;
        } else {
            return first.getAmount() != second.getAmount() ? false : isSameFluidSameComponents(first, second);
        }
    }

    /// Compares a fluidstack with a [FluidStackTemplate] as per [#matches(FluidStack, FluidStack)].
    public static boolean matches(FluidStack a, @Nullable FluidStackTemplate b) {
        if (b == null) {
            return a.isEmpty();
        }

        return a.amount() == b.amount() && isSameFluidSameComponents(a, b);
    }

    /**
     * Checks if the two fluid stacks have the same fluid. Ignores amount and components.
     *
     * @return {@code true} if the two fluid stacks have the same fluid
     */
    public static boolean isSameFluid(FluidStack first, FluidStack second) {
        return first.is(second.getFluid());
    }

    /**
     * Checks if the two fluid stacks have the same fluid and components. Ignores amount.
     *
     * @return {@code true} if the two fluid stacks have the same fluid and components
     */
    public static boolean isSameFluidSameComponents(FluidStack first, FluidStack second) {
        if (!first.is(second.getFluid())) {
            return false;
        } else {
            return first.isEmpty() && second.isEmpty() ? true : Objects.equals(first.components, second.components);
        }
    }

    /// {@return true if a and b refer to the same fluid, or if a is empty and b is null}
    public static boolean isSameFluid(FluidStack a, @Nullable FluidStackTemplate b) {
        return b == null ? a.isEmpty() : a.is(b.fluid());
    }

    /// Compares the fluid and components of this stack against a [FluidStackTemplate].
    ///
    /// @return True if either this stack is empty and the template is null, or they reference the same fluid and have equivalent component patches.
    public static boolean isSameFluidSameComponents(FluidStack a, @Nullable FluidStackTemplate b) {
        if (a.isEmpty() || b == null) {
            return a.isEmpty() == (b == null);
        } else {
            return a.is(b.fluid()) && a.components.asPatch().equals(b.components()); // NeoForge-patched PatchedDataComponentMap#patchEquals
        }
    }

    public static MapCodec<FluidStack> lenientOptionalFieldOf(String fieldName) {
        return CODEC.lenientOptionalFieldOf(fieldName)
                .xmap(optional -> optional.orElse(EMPTY), stack -> stack.isEmpty() ? Optional.empty() : Optional.of(stack));
    }

    /**
     * Hashes the fluid and components of this stack, ignoring the amount.
     */
    public static int hashFluidAndComponents(@Nullable FluidStack stack) {
        if (stack != null) {
            int i = 31 + stack.getFluid().hashCode();
            return 31 * i + stack.getComponents().hashCode();
        } else {
            return 0;
        }
    }

    /**
     * Returns the {@link FluidType#getDescriptionId(FluidStack) description id} of this stack.
     */
    public String getDescriptionId() {
        return this.getFluidType().getDescriptionId(this);
    }

    @Override
    public String toString() {
        return this.getAmount() + " " + this.getFluid();
    }


    /**
     * Sets a data component.
     */
    @Nullable
    public <T> T set(DataComponentType<T> type, @Nullable T component) {
        return this.components.set(type, component);
    }

    public <T> @Nullable T set(TypedDataComponent<T> value) {
        return components.set(value);
    }

    /**
     * Removes a data component.
     */
    @Nullable
    public <T> T remove(DataComponentType<? extends T> type) {
        return this.components.remove(type);
    }

    /**
     * Applies a set of component changes to this stack.
     */
    public void applyComponents(DataComponentPatch patch) {
        this.components.applyPatch(patch);
    }

    /**
     * Applies a set of component changes to this stack.
     */
    public void applyComponents(DataComponentMap components) {
        this.components.setAll(components);
    }

    /**
     * Returns the hover name of this stack.
     */
    public Component getHoverName() {
        return getFluidType().getDescription(this);
    }

    @Override
    public int amount() {
        return this.isEmpty() ? 0 : this.amount;
    }

    /**
     * Returns the amount of this stack.
     */
    public int getAmount() {
        return amount();
    }

    /**
     * Sets the amount of this stack.
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * Limits the amount of this stack is at most the given amount.
     */
    public void limitSize(int amount) {
        if (!this.isEmpty() && this.getAmount() > amount) {
            this.setAmount(amount);
        }
    }

    /**
     * Adds the given amount to this stack.
     */
    public void grow(int addedAmount) {
        this.setAmount(this.getAmount() + addedAmount);
    }

    /**
     * Removes the given amount from this stack.
     */
    public void shrink(int removedAmount) {
        this.grow(-removedAmount);
    }

    // Fabric: non-Supplier defaults of NeoForge's MutableDataComponentHolder
    public <T> void copyFrom(DataComponentType<T> type, DataComponentGetter getter) {
        this.set(type, getter.get(type));
    }

    public <T, U> @Nullable T update(DataComponentType<T> componentType, T value, U updateContext, BiFunction<T, U, T> updater) {
        return this.set(componentType, updater.apply(this.getOrDefault(componentType, value), updateContext));
    }

    public <T> @Nullable T update(DataComponentType<T> componentType, T value, UnaryOperator<T> updater) {
        T t = this.getOrDefault(componentType, value);
        return this.set(componentType, updater.apply(t));
    }

    public void copyFrom(DataComponentHolder src, DataComponentType<?>... componentTypes) {
        for (DataComponentType<?> type : componentTypes) {
            copyFrom(type, src);
        }
    }

    // Fabric: Transfer API bridge. Flowing fluids map to their source, as FluidVariant requires.
    public FluidVariant toVariant() {
        if (isEmpty()) {
            return FluidVariant.blank();
        }
        Fluid f = getFluid();
        if (f instanceof FlowingFluid flowing && !f.isSource(f.defaultFluidState())) {
            f = flowing.getSource();
        }
        return FluidVariant.of(f, getComponentsPatch());
    }

    public static FluidStack fromVariant(FluidVariant variant, int amountMb) {
        return variant.isBlank() || amountMb <= 0 ? EMPTY : new FluidStack(variant.getFluid(), amountMb, variant.getComponentsPatch());
    }

    static DataComponentMap prototypeComponents(Holder<Fluid> fluid) {
        return fluid.areComponentsBound() ? fluid.components() : DataComponentMap.EMPTY;
    }

    // Extra methods that are not directly adapted from ItemStack go below
}
