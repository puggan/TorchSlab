package com.endlesnights.torchslabsmod.blocks.bambooblock;

import java.util.Map;
import java.util.Random;

import com.endlesnights.torchslabsmod.blocks.vanilla.BlockWallTorchSlab;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.pugz.bambooblocks.core.registry.BambooBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockBambooTorchWall extends WallTorchBlock
{
	private static final Map<Direction, VoxelShape> SHAPES_BOTTOM = Maps.newEnumMap(ImmutableMap.of(
			   Direction.NORTH, Block.makeCuboidShape(5.5D, 3.0D, 11.0D, 10.5D, 13.0D, 16.0D), 
			   Direction.SOUTH, Block.makeCuboidShape(5.5D, 3.0D, 0.0D, 10.5D, 13.0D, 5.0D), 
			   Direction.WEST, Block.makeCuboidShape(11.0D, 3.0D, 5.5D, 16.0D, 13.0D, 10.5D), 
			   Direction.EAST, Block.makeCuboidShape(0.0D, 3.0D, 5.5D, 5.0D, 13.0D, 10.5D)));

	private static final Map<Direction, VoxelShape> SHAPES_TOP = Maps.newEnumMap(ImmutableMap.of(
			   Direction.NORTH, Block.makeCuboidShape(5.5D, 11.0D, 11.0D, 10.5D, 21.0D, 16.0D), 
			   Direction.SOUTH, Block.makeCuboidShape(5.5D, 11.0D, 0.0D, 10.5D, 21.0D, 5.0D), 
			   Direction.WEST, Block.makeCuboidShape(11.0D, 11.0D, 5.5D, 16.0D, 21.0D, 10.5D), 
			   Direction.EAST, Block.makeCuboidShape(0.0D, 11.0D, 5.5D, 5.0D, 21.0D, 10.5D)));
	
	public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
	
	public BlockBambooTorchWall()
	{
		super(Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.0F).lightValue(14).sound(SoundType.BAMBOO));
		this.setDefaultState(this.stateContainer.getBaseState().with(HORIZONTAL_FACING, Direction.NORTH).with(HALF, Half.BOTTOM));
	}
	
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(HORIZONTAL_FACING, HALF);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
	{
		if(state.get(HALF) == Half.TOP)
			return SHAPES_TOP.get(state.get(HORIZONTAL_FACING));
		else
			return SHAPES_BOTTOM.get(state.get(HORIZONTAL_FACING));
	}
	
	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos)
	{
		Direction direction = state.get(HORIZONTAL_FACING).getOpposite();
		
		if(state.get(HALF) == Half.BOTTOM
			&& ((worldIn.getBlockState(pos.offset(direction)).getBlock() instanceof SlabBlock && worldIn.getBlockState(pos.offset(direction)).get(SlabBlock.TYPE) == SlabType.BOTTOM)
			|| (worldIn.getBlockState(pos.offset(direction)).getBlock() instanceof StairsBlock && worldIn.getBlockState(pos.offset(direction)).get(StairsBlock.HALF) == Half.BOTTOM )
			|| super.isValidPosition(state, worldIn, pos)))
				return true;
		else if(state.get(HALF) == Half.TOP
			&& ((worldIn.getBlockState(pos.offset(direction)).getBlock() instanceof SlabBlock && worldIn.getBlockState(pos.offset(direction)).get(SlabBlock.TYPE) == SlabType.TOP)
			|| (worldIn.getBlockState(pos.offset(direction)).getBlock() instanceof StairsBlock && worldIn.getBlockState(pos.offset(direction)).get(StairsBlock.HALF) == Half.TOP )
			|| super.isValidPosition(state, worldIn, pos)))
		{
			return BlockWallTorchSlab.validTop(worldIn.getBlockState(pos.up()), state);
		}	
		
		return false;
	}
	
	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
	{
		return !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : stateIn;
	}
	
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
	{
		Direction direction = stateIn.get(HORIZONTAL_FACING);
		double d0 = (double)pos.getX() + 0.5D;
		double d1 = (double)pos.getY() + 0.8D;
		double d2 = (double)pos.getZ() + 0.5D;
	      
		if(stateIn.get(HALF) == Half.TOP)
			d1 += 0.5D;
	      
		Direction direction1 = direction.getOpposite();
        worldIn.addParticle(ParticleTypes.SMOKE, d0 + 0.18D * (double)direction1.getXOffset(), d1 + 0.22D, d2 + 0.18D * (double)direction1.getZOffset(), 0.0D, 0.0D, 0.0D);
        worldIn.addParticle(ParticleTypes.FLAME, d0 + 0.18D * (double)direction1.getXOffset(), d1 + 0.22D, d2 + 0.18D * (double)direction1.getZOffset(), 0.0D, 0.0D, 0.0D);
	   }
	
	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
	{
		return new ItemStack(BambooBlocks.BAMBOO_TORCH.get().asItem());
	}
}

