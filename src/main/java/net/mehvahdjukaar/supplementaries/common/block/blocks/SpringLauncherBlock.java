package net.mehvahdjukaar.supplementaries.common.block.blocks;

import net.mehvahdjukaar.supplementaries.common.block.tiles.SpringLauncherArmBlockTile;
import net.mehvahdjukaar.supplementaries.setup.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SpringLauncherBlock extends Block {
    protected static final VoxelShape PISTON_BASE_EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, 12.0D, 16.0D, 16.0D);
    protected static final VoxelShape PISTON_BASE_WEST_AABB = Block.box(4.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape PISTON_BASE_SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 12.0D);
    protected static final VoxelShape PISTON_BASE_NORTH_AABB = Block.box(0.0D, 0.0D, 4.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape PISTON_BASE_UP_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);
    protected static final VoxelShape PISTON_BASE_DOWN_AABB = Block.box(0.0D, 4.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty EXTENDED = BlockStateProperties.EXTENDED; // is base only?
    public SpringLauncherBlock(Properties properties){
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(EXTENDED, false));
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return state.getValue(EXTENDED)?PushReaction.BLOCK:PushReaction.NORMAL;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return state.getValue(EXTENDED);
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return state.getValue(EXTENDED);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, EXTENDED);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        if (state.getValue(EXTENDED)) {
            return switch (state.getValue(FACING)) {
                case DOWN -> PISTON_BASE_DOWN_AABB;
                default -> PISTON_BASE_UP_AABB;
                case NORTH -> PISTON_BASE_NORTH_AABB;
                case SOUTH -> PISTON_BASE_SOUTH_AABB;
                case WEST -> PISTON_BASE_WEST_AABB;
                case EAST -> PISTON_BASE_EAST_AABB;
            };
        } else {
            return Shapes.block();
        }
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        this.checkForMove(state, worldIn, pos);
    }

    public void checkForMove(BlockState state, Level world, BlockPos pos) {
        if (!world.isClientSide()) {
            boolean flag = this.shouldBeExtended(world, pos, state.getValue(FACING));
            BlockPos offset = pos.offset(state.getValue(FACING).getNormal());
            if (flag && !state.getValue(EXTENDED)) {
                boolean flag2 = false;
                BlockState targetBlock = world.getBlockState(offset);
                if (targetBlock.getPistonPushReaction() == PushReaction.DESTROY || targetBlock.isAir()) {
                    BlockEntity blockEntity = targetBlock.hasBlockEntity() ? world.getBlockEntity(offset) : null;
                    dropResources(targetBlock, world, offset, blockEntity);
                    flag2 = true;
                }
                /*
                 * else if (targetBlock.getBlock() instanceof FallingBlock &&
                 * world.getBlockState(offset.add(state.get(FACING).getDirectionVec())).isAir(
                 * world, offset)){ FallingBlockEntity fallingblockentity = new
                 * FallingBlockEntity(world, (double)offset.getX() + 0.5D, (double)offset.getY() ,
                 * (double)offset.getZ() + 0.5D, world.getBlockState(offset));
                 *
                 * world.addEntity(fallingblockentity); flag2=true; }
                 */
                if (flag2) {
                    world.setBlockAndUpdate(offset, ModRegistry.SPRING_LAUNCHER_ARM.get().defaultBlockState()
                            .setValue(SpringLauncherArmBlock.EXTENDING, true).setValue(FACING, state.getValue(FACING)));
                    world.setBlockAndUpdate(pos, state.setValue(EXTENDED, true));
                    world.playSound(null, pos, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.53F,
                            world.random.nextFloat() * 0.25F + 0.45F);
                    world.gameEvent(GameEvent.PISTON_EXTEND, pos);
                }
            } else if (!flag && state.getValue(EXTENDED)) {
                BlockState bs = world.getBlockState(offset);
                if (bs.getBlock() instanceof SpringLauncherHeadBlock && state.getValue(FACING) == bs.getValue(FACING)) {
                    // world.setBlockState(offset, Blocks.AIR.getDefaultState(), 3);
                    world.setBlockAndUpdate(offset, ModRegistry.SPRING_LAUNCHER_ARM.get().defaultBlockState()
                                    .setValue(SpringLauncherArmBlock.EXTENDING, false).setValue(FACING, state.getValue(FACING)));
                    world.playSound(null, pos, SoundEvents.PISTON_CONTRACT, SoundSource.BLOCKS, 0.53F,
                            world.random.nextFloat() * 0.15F + 0.45F);
                    world.gameEvent(GameEvent.PISTON_CONTRACT, pos);
                } else if (bs.getBlock() instanceof SpringLauncherArmBlock
                        && state.getValue(FACING) == bs.getValue(FACING)) {
                    if (world.getBlockEntity(offset) instanceof SpringLauncherArmBlockTile) {
                        world.scheduleTick(pos, world.getBlockState(pos).getBlock(), 1);
                    }
                }
            }
        }
    }

    // piston code
    private boolean shouldBeExtended(Level worldIn, BlockPos pos, Direction facing) {
        for (Direction direction : Direction.values()) {
            if (direction != facing && worldIn.hasSignal(pos.relative(direction), direction)) {
                return true;
            }
        }
        if (worldIn.hasSignal(pos, Direction.DOWN)) {
            return true;
        } else {
            BlockPos blockpos = pos.above();
            for (Direction direction1 : Direction.values()) {
                if (direction1 != Direction.DOWN && worldIn.hasSignal(blockpos.relative(direction1), direction1)) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block neighborBlock, BlockPos fromPos, boolean moving) {
        super.neighborChanged(state, world, pos, neighborBlock, fromPos, moving);
        this.checkForMove(state, world, pos);
    }
}