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

package com.elytradev.marsenal.tile;

import com.elytradev.marsenal.recipe.EmcRegistry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.BlockFluidClassic;

public class TileEntityKenazStele extends TileEntityAbstractStele {
	public static final EmcRegistry REGISTRY = new EmcRegistry();
	
	@Override
	public void scan() {
		scanBlocks();
	}
	
	@Override
	public int getEffectiveEMC(BlockPos pos, IBlockState state) {
		if (REGISTRY.contains(state)) return REGISTRY.get(state);
		
		Block block = state.getBlock();
		if (block.isAir(state, world, pos)) return 0; //Do not accept light air, enchanted or otherwise.
		
		float enchantPower = block.getEnchantPowerBonus(world, pos);
		if (enchantPower>0) {
			//Prefer enchant value to light level
			return (int)(528 * enchantPower);
		}
		
		//REJECT some cases that would ruin heuristics
		if (block==Blocks.LAVA || block==Blocks.FLOWING_LAVA) return 0; //These, while bright, don't match our nature.
		if (block instanceof BlockFluidClassic && !((BlockFluidClassic)block).isSourceBlock(world, pos)) return 0; //Potential cheese
		if (block instanceof BlockLiquid) return 0; //Same deal - we could potentially check against zero but this is probably a bad idea
		
		//Note: If it's a finite fluid block, value it exactly as much as its emitted light value, because Forge finite fluid scales light by quanta.
		
		//If there's no enchant power and isn't a Known Light, drastically undervalue this block based on its light level versus torches.
		//Also prevent cheese by returning a light level > 15.
		float lightLevel = (block.getLightValue(state, world, pos) & 0xF) / 16f;
		return (int)(REGISTRY.get(Blocks.TORCH.getDefaultState()) * lightLevel); //Value the light level "in torches"
	}

	@Override
	public String getSteleKey() {
		return "kenaz";
	}
}
