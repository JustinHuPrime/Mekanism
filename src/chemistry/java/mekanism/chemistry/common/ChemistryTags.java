package mekanism.chemistry.common;

import mekanism.api.chemical.ChemicalTags;
import mekanism.api.chemical.gas.Gas;
import mekanism.common.Mekanism;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag.Named;
import net.minecraft.world.level.material.Fluid;

public class ChemistryTags {

    private ChemistryTags() {
    }

    public static void init() {
        Fluids.init();
        Gases.init();
    }

    public static class Fluids {

        public static final Named<Fluid> AMMONIA = forgeTag("ammonia");
        public static final Named<Fluid> NITROGEN = forgeTag("nitrogen");
        public static final Named<Fluid> AIR = forgeTag("air");

        private Fluids() {
        }

        private static void init() {
        }

        private static Named<Fluid> forgeTag(String name) {
            return FluidTags.bind("forge:" + name);
        }
    }

    public static class Gases {

        public static final Named<Gas> AMMONIA = tag("ammonia");
        public static final Named<Gas> NITROGEN = tag("nitrogen");
        public static final Named<Gas> AIR = tag("air");

        private Gases() {
        }

        private static void init() {
        }

        private static Named<Gas> tag(String name) {
            return ChemicalTags.GAS.tag(Mekanism.rl(name));
        }
    }
}
