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

import com.elytradev.marsenal.MagicArsenal;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public class ItemChisel extends Item {
	
	public ItemChisel() {
		this.setRegistryName("chisel");
		this.setUnlocalizedName("magicarsenal.chisel");
		this.setMaxStackSize(1);
		this.setHasSubtypes(false);
		this.setCreativeTab(MagicArsenal.TAB_MARSENAL);
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
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			IBlockState state = world.getBlockState(pos);
			RayTraceResult fakeRay = new RayTraceResult(RayTraceResult.Type.BLOCK, new Vec3d(hitX, hitY, hitZ), facing, pos);
			ItemStack item = state.getBlock().getPickBlock(state, fakeRay, world, pos, player);
			if (item.isEmpty()) return EnumActionResult.FAIL;
			IForgeRegistry<IRecipe> registry = ForgeRegistries.RECIPES;
			
	
	outer:
			for(IRecipe recipe : registry) {
				/* What we're primarily looking for is:
				 * + Not a dynamic recipe
				 * + Fits in a 2x2 grid
				 * + Has 1-4 ingredients
				 * + All ingredients match the pickBlock of this Block
				 * + Produces as many items as you supply
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
				if (!recipe.canFit(2, 2)) continue;
				
				NonNullList<Ingredient> ingredients = recipe.getIngredients();
				if (recipe.getRecipeOutput().getCount()!=ingredients.size()) continue;
				
				if (ingredients.size()>4) continue;
				for(Ingredient i : ingredients) {
					if (!i.test(item)) continue outer;
				}
				
				ItemStack result = recipe.getRecipeOutput();
				if (!(result.getItem() instanceof ItemBlock)) continue;
				
				ItemBlock blockItem = (ItemBlock)result.getItem();
				Block resultBlock = blockItem.getBlock();
				IBlockState toPlace = resultBlock.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, result.getMetadata(), player, hand);
				world.destroyBlock(pos, false);
				world.setBlockState(pos, toPlace, 3);
				
				return EnumActionResult.PASS;
			}
			return EnumActionResult.FAIL;
		}
		
		player.swingArm(hand);
		return EnumActionResult.PASS;
	}
}
