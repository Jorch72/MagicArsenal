/*
 * MIT License
 *
 * Copyright (c) 2018 Isaac Ellingson (Falkreon) and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.elytradev.marsenal.block;

import java.util.Random;

import com.elytradev.marsenal.MagicArsenal;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockPoisonDoublePlant extends Block implements IPlantable, IGrowable {
	private static final double PX = 1/16d;
	private static final AxisAlignedBB[] PLANT_AABB = new AxisAlignedBB[] {
			new AxisAlignedBB(3*PX, 0.0d, 3*PX, 13*PX,  2*PX, 13*PX),
			new AxisAlignedBB(3*PX, 0.0d, 3*PX, 13*PX,  3*PX, 13*PX),
			new AxisAlignedBB(3*PX, 0.0d, 3*PX, 13*PX,  4*PX, 13*PX),
			new AxisAlignedBB(3*PX, 0.0d, 3*PX, 13*PX,  6*PX, 13*PX),
			new AxisAlignedBB(3*PX, 0.0d, 3*PX, 13*PX,  9*PX, 13*PX),
			new AxisAlignedBB(3*PX, 0.0d, 3*PX, 13*PX, 12*PX, 13*PX),
			new AxisAlignedBB(3*PX, 0.0d, 3*PX, 13*PX, 14*PX, 13*PX),
			new AxisAlignedBB(3*PX, 0.0d, 3*PX, 13*PX, 16*PX, 13*PX)
		};

	public static final PropertyInteger AGE = BlockCrops.AGE;
	public static final PropertyEnum<BlockDoublePlant.EnumBlockHalf> HALF = BlockDoublePlant.HALF;
	private final String id;
	private ItemStack root;
	private ItemStack tip;
	
	public BlockPoisonDoublePlant(String id) {
		super(Material.PLANTS, MapColor.PURPLE);
		this.id = id;
		
		this.setRegistryName("magicarsenal:poisondoubleplant."+id);
		this.setUnlocalizedName("magicarsenal.poisondoubleplant."+id);
		this.setCreativeTab(MagicArsenal.TAB_MARSENAL); //TODO: REMOVE
		setDefaultState(this.blockState.getBaseState().withProperty(AGE, 0).withProperty(HALF, BlockDoublePlant.EnumBlockHalf.LOWER));
		
		this.setTickRandomly(true);
		this.setSoundType(SoundType.PLANT);
		this.setHardness(0.0f);
		
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		if (state.getValue(HALF)==BlockDoublePlant.EnumBlockHalf.UPPER) return PLANT_AABB[7];
        return PLANT_AABB[ state.getValue(AGE) ];
    }

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		IBlockState soil = world.getBlockState(pos.down());
		return soil.getBlock()==Blocks.FARMLAND;
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        this.checkAndDropBlock(worldIn, pos, state);
    }
	
	/*
	@Override
	protected boolean canSustainBush(IBlockState state) {
		return state.getBlock() == Blocks.FARMLAND || state.getBlock() == this;
    }
	
	@Override
	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
		this.
        IBlockState soil = worldIn.getBlockState(pos.down());
        return (worldIn.getLight(pos) >= 8 || worldIn.canSeeSky(pos)) && soil.getBlock().canSustainPlant(soil, worldIn, pos.down(), net.minecraft.util.EnumFacing.UP, this);
    }*/
	
	public void setHarvestItems(ItemStack root, ItemStack tip) {
		this.root = root;
		this.tip = tip;
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
        BlockDoublePlant.EnumBlockHalf half = (meta & 8) > 0 ? BlockDoublePlant.EnumBlockHalf.UPPER : BlockDoublePlant.EnumBlockHalf.LOWER;
        int age = meta & 7;
        
        return this.getDefaultState().withProperty(HALF, half).withProperty(AGE, age);
    }

	@Override
	public int getMetaFromState(IBlockState state) {
		return ( (state.getValue(HALF)==BlockDoublePlant.EnumBlockHalf.UPPER) ? 8 : 0 ) |
				state.getValue(AGE);
	}
	
	@Override
	public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
		if (state.getValue(HALF)==BlockDoublePlant.EnumBlockHalf.UPPER) return false;
		return (
				world.getBlockState(pos).getValue(AGE)<7 || (pos.getY()<255 && world.isAirBlock(pos.up()))
				);
	}
	
	@Override
	public BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, AGE, HALF);
	}

	@Override
	public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state) {
		return true;
	}

	protected int getAge(IBlockState state) {
        return state.getValue(AGE);
    }
	
	protected static float getGrowthChance(Block block, World world, BlockPos pos) {
        float result = 1.0F;
        BlockPos blockpos = pos.down();

        for (int x = -1; x <= 1; ++x) {
            for (int z = -1; z <= 1; ++z) {
                float groundContribution = 0.0F;
                IBlockState ground = world.getBlockState(blockpos.add(x, 0, z));

                if (ground.getBlock().canSustainPlant(ground, world, blockpos.add(x, 0, z), net.minecraft.util.EnumFacing.UP, (IPlantable)block)) {
                    groundContribution = 1.0F;

                    if (ground.getBlock().isFertile(world, blockpos.add(x, 0, z))) {
                        groundContribution = 3.0F;
                    }
                }
                
                if (x != 0 || z != 0) {
                    groundContribution /= 4.0F;
                }

                result += groundContribution;
            }
        }

        BlockPos north = pos.north();
        BlockPos south = pos.south();
        BlockPos east = pos.west();
        BlockPos west = pos.east();
        boolean ewRow = block == world.getBlockState(east).getBlock() || block == world.getBlockState(west).getBlock();
        boolean nsRow = block == world.getBlockState(north).getBlock() || block == world.getBlockState(south).getBlock();

        if (ewRow && nsRow) {
            result /= 2.0F; //Crowding - plant in rows to avoid
        } else {
            boolean diagonallyPlanted = block == world.getBlockState(east.north()).getBlock() || block == world.getBlockState(west.north()).getBlock() || block == world.getBlockState(west.south()).getBlock() || block == world.getBlockState(east.south()).getBlock();

            if (diagonallyPlanted) {
                result /= 2.0F;
            }
        }

        return result;
    }
	
	@Override
	public void grow(World world, Random rand, BlockPos pos, IBlockState state) {
		if (state.getBlock()!=this) return;
		if (state.getValue(HALF)==BlockDoublePlant.EnumBlockHalf.UPPER) return;
		
		int newAge = state.getValue(AGE) + 1;
		if (newAge>7) {
			if (pos.getY()<255 && world.isAirBlock(pos.up())) {
				world.setBlockState(pos.up(), this.getDefaultState().withProperty(HALF, BlockDoublePlant.EnumBlockHalf.UPPER).withProperty(AGE, 0));
			}
		} else {
			world.setBlockState(pos, state.withProperty(AGE, newAge));
		}
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
		return EnumPlantType.Crop;
	}

	@Override
	public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
		return this.getDefaultState();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}
	
	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
        if (state.getBlock() == this) {
        	IBlockState soil = worldIn.getBlockState(pos.down());
        	if (state.getValue(HALF)==BlockDoublePlant.EnumBlockHalf.LOWER) {
	            return soil.getBlock().canSustainPlant(soil, worldIn, pos.down(), EnumFacing.UP, this);
        	} else {
        		return //Only mature lower-halves can support an upper.
        				soil.getBlock()==this &&
        				soil.getValue(HALF)==BlockDoublePlant.EnumBlockHalf.LOWER &&
        				soil.getValue(AGE)>=7;
        	}
        }
        return worldIn.getBlockState(pos.down())==Blocks.FARMLAND;
    }
	
	public void checkAndDropBlock(World world, BlockPos pos, IBlockState state) {
		if (!this.canBlockStay(world, pos, state)) {
	        this.dropBlockAsItem(world, pos, state, 0);
	        world.setBlockToAir(pos);
	    }
	}
	
	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(world, pos, state, rand);
        if (world.getBlockState(pos).getBlock()!=this) return;
        
        if (state.getValue(HALF)==BlockDoublePlant.EnumBlockHalf.UPPER) return;
        
        //Test to see if we can stay
        this.checkAndDropBlock(world, pos, state);
        
        if (world.getLightFromNeighbors(pos.up()) >= 9) {
            int lastAge = this.getAge(state);
            
            if (lastAge < 7) {
                boolean shouldGrow = rand.nextInt((int)(25.0f / getGrowthChance(this, world, pos))+1) == 0;
                
                if (ForgeHooks.onCropsGrowPre(world, pos, state, shouldGrow)) {
                    world.setBlockState(pos, world.getBlockState(pos).withProperty(AGE, lastAge + 1));
                    ForgeHooks.onCropsGrowPost(world, pos, state, world.getBlockState(pos));
                }
            } else {
            	if (world.isAirBlock(pos.up())) {
            		boolean shouldGrow = rand.nextInt((int)(25.0f / getGrowthChance(this, world, pos))+1) == 0;
            		
            		if (ForgeHooks.onCropsGrowPre(world, pos, state, shouldGrow)) {
            			world.setBlockState(pos.up(), this.getDefaultState().withProperty(HALF, BlockDoublePlant.EnumBlockHalf.UPPER).withProperty(AGE, 0));
            			ForgeHooks.onCropsGrowPost(world, pos, state, world.getBlockState(pos));
            		}
            	}
            	
            }
        }
    }

	@Override
	public void getDrops(NonNullList<ItemStack> drops, net.minecraft.world.IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		if (state.getValue(HALF)==BlockDoublePlant.EnumBlockHalf.LOWER) {
		
			int age = getAge(state);
			Random rand = world instanceof World ? ((World)world).rand : new Random();

			if (age >= 7) {
				
				for (int i = 0; i < 3 + fortune; i++) {
					if (rand.nextInt(2 * 7) <= age) {
						drops.add(root.copy());
					}
				}
			}
		} else {
			drops.add(tip.copy());
		}
    }
	
	//TODO: Remove
	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
		items.add(new ItemStack(this));
	}
	
	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

	@Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return NULL_AABB;
	}
}
