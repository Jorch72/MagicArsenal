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

package com.elytradev.marsenal.item;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.elytradev.marsenal.MagicArsenal;
import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

@SuppressWarnings("deprecation")
public class ItemChisel extends ItemTool {
	public int lateralEffectiveness = 1;
	public int connectedEffectiveness = 25;
	
	public ItemChisel(Item.ToolMaterial material) {
		super(1f, 0f, material, ImmutableSet.of());
		this.setRegistryName("chisel."+(material.name().toLowerCase(Locale.ROOT)));
		this.setUnlocalizedName("magicarsenal.chisel."+(material.name().toLowerCase(Locale.ROOT)));
		this.setMaxStackSize(1);
		this.setMaxDamage(EnumMode.values().length-1);
		this.setHasSubtypes(false);
		this.setCreativeTab(MagicArsenal.TAB_MARSENAL);
		this.efficiency = 0f;
	}
	
	@Override
	public boolean isEnchantable(ItemStack stack) {
		return true;
	}
	
	@Override
	public int getItemEnchantability(ItemStack stack) {
		return ToolMaterial.IRON.getEnchantability();
	}
	
	@Override
	public Set<String> getToolClasses(ItemStack stack) {
		return ImmutableSet.of("chisel");
	}
	
	@Override
	public boolean canHarvestBlock(IBlockState state, ItemStack stack) {
		return false;
	}
	
	@Override
	public int getHarvestLevel(ItemStack stack, String toolClass, EntityPlayer player, IBlockState blockState) {
		return 0; //HARVEST NONE OF THE THINGS
	}
	
	@Override
	public boolean canDestroyBlockInCreative(World world, BlockPos pos, ItemStack stack, EntityPlayer player) {
		return false;
	}
	
	@Override
	public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
		return true;
	}
	
	@Override
	public boolean isDamageable() {
		return false;
	}
	
	@Override
	public boolean isRepairable() {
		return false;
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return 0;
	}
	
	@Override
	public boolean isDamaged(ItemStack stack) {
		return false;
	}
	
	@Override
	public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
		return true; //Don't damage the chisel!
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		EnumMode mode = EnumMode.forStack(stack);
		return "item.magicarsenal.chisel."+this.toolMaterial.name().toLowerCase(Locale.ROOT)+"."+mode.getName();
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(I18n.translateToLocal("tooltip.magicarsenal.chisel.mode"));
		tooltip.add(I18n.translateToLocal("tooltip.magicarsenal.chisel."+this.toolMaterial.name().toLowerCase(Locale.ROOT)+".lateral"));
		tooltip.add(I18n.translateToLocal("tooltip.magicarsenal.chisel."+this.toolMaterial.name().toLowerCase(Locale.ROOT)+".connected"));
	}
	
	
	public void chiselBlockFirst(World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumMode mode) {
		switch(mode) {
			case SINGLE: {
				chiselBlock(world, pos, player, hand);
				return;
			}
			case PLANE: {
				RayTraceResult ray = doRayTrace(player);
				
				boolean didChisel = chiselBlock(world, pos, player, hand, ray, true);
				if (didChisel) {
					for(BlockPos p : blocksAround(pos, ray.sideHit)) {
						chiselBlock(world, p, player, hand, ray, false);
					}
				}
				return;
			}
			case VERTICAL: {
				RayTraceResult ray = doRayTrace(player);
				
				boolean didChisel = chiselBlock(world, pos, player, hand, ray, true);
				if (didChisel) {
					for(BlockPos p : blocksVertical(player, pos, ray.sideHit)) {
						chiselBlock(world, p, player, hand, ray, false);
					}
				}
				return;
			}
			case HORIZONTAL: {
				RayTraceResult ray = doRayTrace(player);
				
				boolean didChisel = chiselBlock(world, pos, player, hand, ray, true);
				if (didChisel) {
					for(BlockPos p : blocksHorizontal(player, pos, ray.sideHit)) {
						chiselBlock(world, p, player, hand, ray, false);
					}
				}
				return;
			}
			case CONNECTED: {
				RayTraceResult ray = doRayTrace(player);
				IBlockState original = world.getBlockState(pos);
				boolean didChisel = chiselBlock(world, pos, player, hand, ray, true);
				if (didChisel) {
					for(BlockPos p : blocksConnected(world, pos, original)) {
						chiselBlock(world, p, player, hand, ray, false);
					}
				}
				return;
			}
		}
	}
	
	public Set<BlockPos> blocksAround(BlockPos pos, EnumFacing face) {
		Set<BlockPos> result = new HashSet<>();
		EnumFacing right = EnumFacing.EAST;
		EnumFacing up = EnumFacing.UP;
		
		switch(face) {
		case NORTH:
		case SOUTH:
			right = EnumFacing.EAST;
			up = EnumFacing.UP;
			break;
		case EAST:
		case WEST:
			right = EnumFacing.SOUTH;
			up = EnumFacing.UP;
			break;
		case UP:
		case DOWN:
		default:
			right = EnumFacing.EAST;
			up = EnumFacing.NORTH;
			break;
		}
		
		for(int u = -lateralEffectiveness; u<=lateralEffectiveness; u++) {
			for(int v=-lateralEffectiveness; v<=lateralEffectiveness; v++) {
				if (u==0 && v==0) continue;
				result.add(pos.offset(right, u).offset(up,v));
			}
		}
		
		return result;
	}
	
	public Set<BlockPos> blocksVertical(EntityPlayer player, BlockPos pos, EnumFacing face) {
		Set<BlockPos> result = new HashSet<>();
		EnumFacing up = EnumFacing.UP;
		
		switch(face) {
		case NORTH:
		case SOUTH:
		case EAST:
		case WEST:
		default:
			up = EnumFacing.UP;
			break;
		case UP:
		case DOWN:
			up = player.getAdjustedHorizontalFacing().getOpposite();
			break;
		}
		
		for(int v=-lateralEffectiveness; v<=lateralEffectiveness; v++) {
			if (v==0) continue;
			result.add(pos.offset(up,v));
		}
		
		return result;
	}
	
	public Set<BlockPos> blocksHorizontal(EntityPlayer player, BlockPos pos, EnumFacing face) {
		Set<BlockPos> result = new HashSet<>();
		EnumFacing right = EnumFacing.EAST;
		
		switch(face) {
		case NORTH:
		case SOUTH:
			right = EnumFacing.EAST;
			break;
		case EAST:
		case WEST:
		default:
			right = EnumFacing.SOUTH;
			break;
		case UP:
		case DOWN:
			right = player.getAdjustedHorizontalFacing().rotateY();
			break;
		}
		
		for(int v=-lateralEffectiveness; v<=lateralEffectiveness; v++) {
			if (v==0) continue;
			result.add(pos.offset(right,v));
		}
		
		return result;
	}
	
	public Set<BlockPos> blocksConnected(World world, BlockPos pos, IBlockState original) {
		
		Set<BlockPos> result = new HashSet<>();
		List<BlockPos> work = new ArrayList<>();
		
		result.add(pos);
		for(EnumFacing face : EnumFacing.VALUES) work.add(pos.offset(face));
		
		while(!work.isEmpty()) {
			//for(int i=0; i<connectedEffectiveness; i++) {
			//if (work.isEmpty()) break;
			if (result.size()>connectedEffectiveness+1) break;
			
			BlockPos cur = work.remove(0);
			
			
			if (world.isAirBlock(cur)) continue;
			if (world.getBlockState(cur)!=original) continue;
			
			result.add(cur);
			
			for(EnumFacing face : EnumFacing.VALUES) work.add(cur.offset(face));
		}
		
		result.remove(pos);
		return result;
	}
	
	/** This method exists on the client, but we need to approximate it on the server */
	public static RayTraceResult doRayTrace(EntityPlayer player) {
		double blockReachDistance = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
		Vec3d eyePosition = player.getPositionEyes(1);
		Vec3d lookVec = player.getLook(1);
		Vec3d furthestReach = eyePosition.addVector(lookVec.x * blockReachDistance, lookVec.y * blockReachDistance, lookVec.z * blockReachDistance);
		return player.getEntityWorld().rayTraceBlocks(eyePosition, furthestReach, false, false, true);
	}
	
	public static boolean chiselBlock(World world, BlockPos pos, EntityPlayer player, EnumHand hand) {
		double blockReachDistance = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
		Vec3d eyePosition = player.getPositionEyes(1);
		Vec3d lookVec = player.getLook(1);
		Vec3d furthestReach = eyePosition.addVector(lookVec.x * blockReachDistance, lookVec.y * blockReachDistance, lookVec.z * blockReachDistance);
		RayTraceResult ray = world.rayTraceBlocks(eyePosition, furthestReach, false, false, true);
		
		//Verify that the raytrace matches the block we think we hit
		if (ray.typeOfHit!=RayTraceResult.Type.BLOCK) return false;
		if (!ray.getBlockPos().equals(pos)) return false;
		
		return chiselBlock(world, pos, player, hand, ray, true);
	}
	
	protected static final Set<String> CHISEL_BLACKLIST = new HashSet<>();
	static {
		CHISEL_BLACKLIST.add("minecraft:stone_button");
		CHISEL_BLACKLIST.add("minecraft:wooden_button");
	}
	
	
	public static boolean chiselBlock(World world, BlockPos pos, EntityPlayer player, EnumHand hand, RayTraceResult ray, boolean particles) {
		if (world.getTileEntity(pos)!=null) return false;
		
		IBlockState state = world.getBlockState(pos);
		
		//Start chiseling.
		ItemStack item = state.getBlock().getPickBlock(state, ray, world, pos, player);
		if (item.isEmpty()) return false;
		
		IForgeRegistry<IRecipe> registry = ForgeRegistries.RECIPES;
		
outer:
		for(IRecipe recipe : registry) {
			/* What we're primarily looking for is:
			 * + Not a dynamic recipe
			 * + Fits in a 3x3 grid
			 * + Has 1-9 ingredients
			 * + All ingredients match the pickBlock of this Block
			 * + Produces exactly as many items as you supply
			 * + Result is an ItemBlock
			 * 
			 * ? Can be shapeless or shaped
			 * 
			 * - Does not have to be reversible!
			 * 
			 * This will *intentionally* ignore a transformation into an item which isn't an ItemBlock,
			 * (such as if a block is 1:1 transformable into String.
			 */
			
			if (recipe.isDynamic()) continue;
			if (!recipe.canFit(3, 3)) continue;
			
			NonNullList<Ingredient> ingredients = recipe.getIngredients();
			if (recipe.getRecipeOutput().getCount()!=ingredients.size()) continue;
			
			if (ingredients.size()>9) continue;
			for(Ingredient i : ingredients) {
				if (!i.test(item)) continue outer;
			}
			
			ItemStack result = recipe.getRecipeOutput();
			if (!(result.getItem() instanceof ItemBlock)) continue;
			
			ItemBlock blockItem = (ItemBlock)result.getItem();
			Block resultBlock = blockItem.getBlock();
			if (CHISEL_BLACKLIST.contains(resultBlock.getRegistryName().toString())) continue;
			//I would love to additionally check for valid placement of the target state, but the canPlaceBlockAt and canPlaceBlockOnSide methods typically check if the block that's already present is replaceable.
			
			IBlockState toPlace = resultBlock.getStateForPlacement(world, pos, ray.sideHit, (float)ray.hitVec.x, (float)ray.hitVec.y, (float)ray.hitVec.z, result.getMetadata(), player, hand);
			
			if (particles) {
				world.destroyBlock(pos, false);
			} else {
				world.setBlockToAir(pos);
			}
			world.setBlockState(pos, toPlace, 3);
			
			
			return true;
		}
		
		return false;
	}
	
	public enum EnumMode implements IStringSerializable {
		SINGLE("single"),
		PLANE("plane"),
		VERTICAL("vertical"),
		HORIZONTAL("horizontal"),
		CONNECTED("connected");
		
		private final String name;
		
		EnumMode(String name) {
			this.name = name;
		}
		
		@Override
		public String getName() {
			return name;
		}
		
		public static EnumMode forMeta(int meta) {
			return values()[meta % values().length];
		}
		
		public static EnumMode forStack(ItemStack stack) {
			return forMeta(stack.getMetadata());
		}
	}
	
	public static class Handler {
		@SubscribeEvent
		public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
			if (event.getWorld().isRemote) return;
			if (!event.getItemStack().isEmpty() && event.getItemStack().getItem() instanceof ItemChisel) {
				EnumMode mode = EnumMode.forStack(event.getItemStack());
				ItemChisel item = (ItemChisel)event.getItemStack().getItem();
				
				item.chiselBlockFirst(event.getWorld(), event.getPos(), event.getEntityPlayer(), event.getHand(), mode);
			}
		}
		
		@SubscribeEvent
		public static void onRightClick(PlayerInteractEvent.RightClickItem event) {
			ItemStack stack = event.getItemStack();
			if (!stack.isEmpty() && stack.getItem() instanceof ItemChisel) {
				int oldMeta = stack.getMetadata();
				int newMeta = (oldMeta+1) % EnumMode.values().length;
				
				ItemStack newStack = stack.copy();
				newStack.setItemDamage(newMeta);
				event.getEntityPlayer().setHeldItem(event.getHand(), newStack);
				if (event.getWorld().isRemote) {
					event.getEntityPlayer().sendStatusMessage(new TextComponentString("§a").appendSibling(new TextComponentTranslation(newStack.getUnlocalizedName()+".name")), true);
				}
				
				event.setCanceled(true);
				event.setCancellationResult(EnumActionResult.SUCCESS);
			}
		}
	}
}
