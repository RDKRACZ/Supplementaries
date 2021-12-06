package net.mehvahdjukaar.supplementaries.block.blocks;

import net.mehvahdjukaar.supplementaries.entities.FallingAshEntity;
import net.mehvahdjukaar.supplementaries.setup.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Random;

public class AshBlock extends FallingBlock {
    private static final int MAX_LAYERS = 8;
    public static final IntegerProperty LAYERS = BlockStateProperties.LAYERS;
    protected static final VoxelShape[] SHAPE_BY_LAYER = new VoxelShape[MAX_LAYERS + 1];

    static {
        Arrays.setAll(SHAPE_BY_LAYER, l -> Block.box(0.0D, 0.0D, 0.0D, 16.0D, l * 2, 16.0D));
        SHAPE_BY_LAYER[0] = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 0.1f, 16.0D);
    }

    public AshBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LAYERS, 1));
    }

    @Override
    public int getDustColor(BlockState state, BlockGetter reader, BlockPos pos) {
        return 0x9a9090;
    }

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (state.getBlock() != oldState.getBlock())
            worldIn.getBlockTicks().scheduleTick(pos, this, this.getDelayAfterPlace());
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE_BY_LAYER[pState.getValue(LAYERS)];
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if (pContext instanceof EntityCollisionContext c) {
            var e = c.getEntity();
            if (e.isPresent() && e.get() instanceof LivingEntity) {
                return SHAPE_BY_LAYER[pState.getValue(LAYERS) - 1];
            }
        }
        return this.getShape(pState, pLevel, pPos, pContext);
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return SHAPE_BY_LAYER[pState.getValue(LAYERS)];
    }

    @Override
    public VoxelShape getVisualShape(BlockState pState, BlockGetter pReader, BlockPos pPos, CollisionContext pContext) {
        return SHAPE_BY_LAYER[pState.getValue(LAYERS)];
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter blockGetter, BlockPos pos, PathComputationType pathType) {
        if (pathType == PathComputationType.LAND) {
            return state.getValue(LAYERS) <= MAX_LAYERS / 2;
        }
        return false;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    //ugly but works
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos otherPos) {
        if (!state.canSurvive(world, currentPos)) {
            if (world instanceof ServerLevel serverLevel) {
                this.tick(state, serverLevel, currentPos, world.getRandom());
            }
            return state;
        }
        return super.updateShape(state, direction, facingState, world, currentPos, otherPos);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random pRand) {
        BlockState below = level.getBlockState(pos.below());
        if ((FallingAshEntity.isFree(below) || hasIncompleteAshPileBelow(below)) && pos.getY() >= level.getMinBuildHeight()) {
            FallingBlockEntity fallingblockentity = new FallingAshEntity(level, pos, state);
            this.falling(fallingblockentity);
            level.addFreshEntity(fallingblockentity);
        }
    }

    private boolean hasIncompleteAshPileBelow(BlockState state) {
        return state.is(this) && state.getValue(LAYERS) != MAX_LAYERS;
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos());
        if (blockstate.is(this)) {
            int i = blockstate.getValue(LAYERS);
            return blockstate.setValue(LAYERS, Math.min(MAX_LAYERS, i + 1));
        } else {
            return super.getStateForPlacement(context);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LAYERS);

    }


    @Override
    public void randomTick(BlockState pState, ServerLevel level, BlockPos pPos, Random pRandom) {
        if (level.isRainingAt(pPos.above()) && level.random.nextInt(4) == 0) {
            this.removeOneLayer(pState, pPos, level);
        }
    }

    @Override
    public void handlePrecipitation(BlockState pState, Level level, BlockPos pPos, Biome.Precipitation pPrecipitation) {
        super.handlePrecipitation(pState, level, pPos, pPrecipitation);
        if (level.random.nextInt(2) == 0) {
            this.removeOneLayer(pState, pPos, level);
        }
    }

    private void removeOneLayer(BlockState state, BlockPos pos, Level level) {
        int levels = state.getValue(LAYERS);
        if (levels > 1) level.setBlockAndUpdate(pos, state.setValue(LAYERS, levels - 1));
        else level.removeBlock(pos, false);
    }

    @Override
    public boolean canBeReplaced(BlockState pState, BlockPlaceContext pUseContext) {
        int i = pState.getValue(LAYERS);
        if (pUseContext.getItemInHand().is(this.asItem()) && i < MAX_LAYERS) {
            return true;
        } else {
            return i == 1;
        }
    }

    public static boolean tryConvertToAsh(Level level, BlockPos pPos) {
        BlockState state = level.getBlockState(pPos);

        Item i = state.getBlock().asItem();
        int count = ForgeHooks.getBurnTime(i.getDefaultInstance(), null) / 100;
        if (ItemTags.LOGS_THAT_BURN.contains(i)) count += 2;

        if (count > 0) {
            int layers = Mth.clamp(level.random.nextInt(count), 1, 8);
            if (layers != 0) {
                ((ServerLevel) level).sendParticles(ModRegistry.ASH_PARTICLE.get(), (double) pPos.getX() + 0.5D,
                        (double) pPos.getY() + 0.5D, (double) pPos.getZ() + 0.5D, 10 + layers,
                        0.5D, 0.5D, 0.5D, 0.0D);
                return level.setBlock(pPos, ModRegistry.ASH_BLOCK.get()
                        .defaultBlockState().setValue(AshBlock.LAYERS, layers), 3);
            }
        }
        return false;
    }

    private void addParticle(Entity entity, BlockPos pos, Level level, int layers, float upSpeed) {
        level.addParticle(ModRegistry.ASH_PARTICLE.get(), entity.getX(), pos.getY() + layers * (1 / 8f), entity.getZ(),
                Mth.randomBetween(level.random, -1.0F, 1.0F) * 0.083333336F,
                upSpeed,
                Mth.randomBetween(level.random, -1.0F, 1.0F) * 0.083333336F);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (level.isClientSide && level.random.nextInt(8) == 0 && (entity.xOld != entity.getX() || entity.zOld != entity.getZ())) {
            addParticle(entity, pos, level, state.getValue(LAYERS), 0.05f);
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float height) {
        int layers = state.getValue(LAYERS);
        entity.causeFallDamage(height, layers > 2 ? 0.3f : 1, DamageSource.FALL);
        if (level.isClientSide) {
            for (int i = 0; i < Math.min(12, height * 1.4); i++) {

                addParticle(entity, pos, level, layers, 0.12f);

                /*
                double dy = Mth.clamp((0.03 * height / 7f), 0.03, 0.055) * 10;
                world.addParticle(new BlockParticleOption(ParticleTypes.FALLING_DUST, state), entity.getX() + r(random, 0.35),
                        entity.getY(), entity.getZ() + r(random, 0.35), r(random, 0.007), dy, r(random, 0.007));

                world.addParticle(ParticleTypes.ASH, entity.getX() + r(random, 0.35),
                        entity.getY(), entity.getZ() + r(random, 0.35), r(random, 0.007), dy, r(random, 0.007));
            */
            }


        }
    }

    private double r(Random random, double a) {
        return a * (random.nextFloat() + random.nextFloat() - 1);
    }


}
