package net.mehvahdjukaar.supplementaries.setup;

import net.mehvahdjukaar.supplementaries.Supplementaries;
import net.mehvahdjukaar.supplementaries.blocks.*;
import net.mehvahdjukaar.supplementaries.gui.NoticeBoardContainer;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Registry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Supplementaries.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Supplementaries.MOD_ID);
    public static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Supplementaries.MOD_ID);
    private static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, Supplementaries.MOD_ID);

    public static void init(){
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILES.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    //planter
    public static final String PLANTER_NAME = "planter";
    public static final RegistryObject<Block> PLANTER = BLOCKS.register(PLANTER_NAME, () -> new PlanterBlock(
            AbstractBlock.Properties.create(Material.ROCK, MaterialColor.RED_TERRACOTTA)
                    .hardnessAndResistance(2f, 6f)
                    .harvestLevel(1)
                    .setRequiresTool()
                    .harvestTool(ToolType.PICKAXE)
                    .notSolid()
    ));
    public static final RegistryObject<Item> PLANTER_ITEM = ITEMS.register(PLANTER_NAME, () -> new BlockItem(PLANTER.get(),
            new Item.Properties().group(ItemGroup.DECORATIONS)));

    //clock
    public static final String CLOCK_BLOCK_NAME = "clock_block";
    public static final RegistryObject<Block> CLOCK_BLOCK = BLOCKS.register(CLOCK_BLOCK_NAME, () -> new ClockBlock(
            AbstractBlock.Properties.create(Material.WOOD, MaterialColor.BROWN)
                    .hardnessAndResistance(3f, 6f)
                    .harvestLevel(0)
                    .sound(SoundType.WOOD)
                    .harvestTool(ToolType.AXE)
                    .notSolid()
    ));
    public static final RegistryObject<TileEntityType<ClockBlockTile>> CLOCK_BLOCK_TILE = TILES.register(CLOCK_BLOCK_NAME,
            () -> TileEntityType.Builder.create(ClockBlockTile::new, CLOCK_BLOCK.get()).build(null));

    public static final RegistryObject<Item> CLOCK_BLOCK_ITEM = ITEMS.register(CLOCK_BLOCK_NAME, () -> new BlockItem(CLOCK_BLOCK.get(),
            new Item.Properties().group(ItemGroup.REDSTONE)));

    //pedestal
    public static final String PEDESTAL_NAME = "pedestal";
    public static final RegistryObject<Block> PEDESTAL = BLOCKS.register(PEDESTAL_NAME, () -> new PedestalBlock(
            AbstractBlock.Properties.create(Material.ROCK, MaterialColor.STONE)
                    .hardnessAndResistance(2f, 6f)
                    .harvestLevel(1)
                    .setRequiresTool()
                    .harvestTool(ToolType.PICKAXE)
                    .notSolid()
    ));
    public static final RegistryObject<TileEntityType<PedestalBlockTile>> PEDESTAL_TILE = TILES.register(PEDESTAL_NAME,
            () -> TileEntityType.Builder.create(PedestalBlockTile::new, PEDESTAL.get()).build(null));

    public static final RegistryObject<Item> PEDESTAL_ITEM = ITEMS.register(PEDESTAL_NAME, () -> new BlockItem(PEDESTAL.get(),
            new Item.Properties().group(ItemGroup.DECORATIONS)));

    //wind vane
    public static final String WIND_VANE_NAME = "wind_vane";
    public static final RegistryObject<Block> WIND_VANE = BLOCKS.register(WIND_VANE_NAME, () -> new WindVaneBlock(
            AbstractBlock.Properties.create(Material.IRON, MaterialColor.IRON)
                    .hardnessAndResistance(5f, 6f)
                    .harvestLevel(2)
                    .setRequiresTool()
                    .sound(SoundType.METAL)
                    .harvestTool(ToolType.PICKAXE)
                    .notSolid()
    ));
    public static final RegistryObject<TileEntityType<WindVaneBlockTile>> WIND_VANE_TILE = TILES.register(WIND_VANE_NAME,
            () -> TileEntityType.Builder.create(WindVaneBlockTile::new, WIND_VANE.get()).build(null));

    public static final RegistryObject<Item> WIND_VANE_ITEM = ITEMS.register(WIND_VANE_NAME, () -> new BlockItem(WIND_VANE.get(),
            new Item.Properties().group(ItemGroup.REDSTONE)));

    //illuminator
    public static final String REDSTONE_ILLUMINATOR_NAME = "redstone_illuminator";
    public static final RegistryObject<Block> REDSTONE_ILLUMINATOR = BLOCKS.register(REDSTONE_ILLUMINATOR_NAME, () -> new RedstoneIlluminatorBlock(
            AbstractBlock.Properties.create(Material.REDSTONE_LIGHT, MaterialColor.QUARTZ)
                    .hardnessAndResistance(0.3f, 0.3f)
                    .sound(SoundType.GLASS)
                    .notSolid()
                    .setLightLevel((state) -> 15)
    ));
    public static final RegistryObject<Item> REDSTONE_ILLUMINATOR_ITEM = ITEMS.register(REDSTONE_ILLUMINATOR_NAME, () -> new BlockItem(REDSTONE_ILLUMINATOR.get(),
            new Item.Properties().group(ItemGroup.REDSTONE)));

    //notice board
    public static final String NOTICE_BOARD_NAME = "notice_board";
    public static final RegistryObject<Block> NOTICE_BOARD = BLOCKS.register(NOTICE_BOARD_NAME, () -> new NoticeBoardBlock(
            AbstractBlock.Properties.create(Material.WOOD, MaterialColor.BROWN)
                    .hardnessAndResistance(2.5f, 2.5f)
                    .sound(SoundType.WOOD)
                    .harvestTool(ToolType.AXE)
                    .notSolid()
    ));
    public static final RegistryObject<TileEntityType<NoticeBoardBlockTile>> NOTICE_BOARD_TILE = TILES.register(NOTICE_BOARD_NAME,
            () -> TileEntityType.Builder.create(NoticeBoardBlockTile::new, NOTICE_BOARD.get()).build(null));

    public static final RegistryObject<Item> NOTICE_BOARD_ITEM = ITEMS.register(NOTICE_BOARD_NAME, () -> new BlockItem(NOTICE_BOARD.get(),
            new Item.Properties().group(ItemGroup.DECORATIONS)));

    public static final RegistryObject<ContainerType<NoticeBoardContainer>> NOTICE_BOARD_CONTAINER = CONTAINERS.register(NOTICE_BOARD_NAME, () -> IForgeContainerType.create(NoticeBoardContainer::new));







}