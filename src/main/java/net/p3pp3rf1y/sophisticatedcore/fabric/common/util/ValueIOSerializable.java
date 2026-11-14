package net.p3pp3rf1y.sophisticatedcore.fabric.common.util;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Stand-in for NeoForge's {@code net.neoforged.neoforge.common.util.ValueIOSerializable} (same members): an object
 * that saves to and loads from vanilla's {@link ValueOutput} / {@link ValueInput}.
 */
public interface ValueIOSerializable {
    void serialize(ValueOutput output);

    void deserialize(ValueInput input);
}
