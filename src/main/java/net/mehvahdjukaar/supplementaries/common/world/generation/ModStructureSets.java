package net.mehvahdjukaar.supplementaries.common.world.generation;

import net.mehvahdjukaar.supplementaries.Supplementaries;
import net.mehvahdjukaar.supplementaries.common.configs.ServerConfigs;
import net.mehvahdjukaar.supplementaries.common.world.generation.structure.WaySignStructure;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.StructureFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.structure.BuiltinStructureSets;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModStructureSets {

    public static void init(){}

    //structure sets

    public static final ResourceKey<StructureSet> WAY_SIGN_SET_KEY = makeKey(ModStructures.WAY_SIGN_NAME);

    public static final Holder<StructureSet> WAY_SIGNS = register(
            WAY_SIGN_SET_KEY, ModConfiguredStructureFeatures.CONFIGURED_WAY_SIGN_STRUCTURE,
            new RandomSpreadStructurePlacement(
                    ServerConfigs.spawn.ROAD_SIGN_DISTANCE_AVR.get(), //spacing
                    ServerConfigs.spawn.ROAD_SIGN_DISTANCE_MIN.get(), //separation
                    RandomSpreadType.LINEAR,
                    431041527));

    private static Holder<StructureSet> register(
            ResourceKey<StructureSet> setResourceKey,
            StructureSet structureSet) {
        return BuiltinRegistries.register(BuiltinRegistries.STRUCTURE_SETS, setResourceKey, structureSet);
    }

    private static Holder<StructureSet> register(
            ResourceKey<StructureSet> setResourceKey,
            Holder<ConfiguredStructureFeature<?, ?>> configuredFeature,
            StructurePlacement structurePlacement) {
        return register(setResourceKey, new StructureSet(configuredFeature, structurePlacement));
    }

    private static ResourceKey<StructureSet> makeKey(String name) {
        return ResourceKey.create(Registry.STRUCTURE_SET_REGISTRY, Supplementaries.res(name));
    }
}
