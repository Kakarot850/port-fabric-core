package net.p3pp3rf1y.sophisticatedcore.init;

import net.p3pp3rf1y.sophisticatedcore.SophisticatedCore;
import net.p3pp3rf1y.sophisticatedcore.compat.CompatInfo;
import net.p3pp3rf1y.sophisticatedcore.compat.CompatModIds;
import net.p3pp3rf1y.sophisticatedcore.compat.CompatRegistry;
import net.p3pp3rf1y.sophisticatedcore.compat.craftingtweaks.CraftingTweaksCompat;
import net.p3pp3rf1y.sophisticatedcore.compat.ftbchunks.FTBChunksCompat;
import net.p3pp3rf1y.sophisticatedcore.compat.iris.IrisCompat;
import net.p3pp3rf1y.sophisticatedcore.compat.mousetweaks.MouseTweaksCompat;
import net.p3pp3rf1y.sophisticatedcore.compat.openpartiesandclaims.OpenPACCompat;
import net.p3pp3rf1y.sophisticatedcore.compat.recipeviewers.jei.JeiCompat;
import net.p3pp3rf1y.sophisticatedcore.compat.trashslot.TrashSlotCompat;

public class ModCompat {
	private ModCompat() {
	}

	public static void register() {
		CompatRegistry registry = CompatRegistry.getRegistry(SophisticatedCore.MOD_ID);
		registry.registerCompat(new CompatInfo(CompatModIds.JEI), JeiCompat::new);
		// EMI & REI skipped: no 26.1.2 Fabric EMI build, and REI is excluded upstream in ref-neoforge-core.
		registry.registerCompat(new CompatInfo(CompatModIds.CRAFTING_TWEAKS), CraftingTweaksCompat::new);
		registry.registerCompat(new CompatInfo(CompatModIds.TRASH_SLOT), TrashSlotCompat::new);
		registry.registerCompat(new CompatInfo(CompatModIds.MOUSE_TWEAKS), MouseTweaksCompat::new);
		registry.registerCompat(new CompatInfo(CompatModIds.FTB_CHUNKS), FTBChunksCompat::new);
		registry.registerCompat(new CompatInfo(CompatModIds.OPEN_PARTIES_AND_CLAIMS_CHUNKS), OpenPACCompat::new);
		registry.registerCompat(new CompatInfo(CompatModIds.IRIS), IrisCompat::new);
	}
}
