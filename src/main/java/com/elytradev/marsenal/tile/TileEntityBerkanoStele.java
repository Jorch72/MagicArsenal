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

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class TileEntityBerkanoStele extends TileEntityAbstractStele {

	@Override
	public int getEffectiveEMC(BlockPos pos, IBlockState state) {
		Block block = state.getBlock();
		/**
		 * Many of these values are significantly greater than the equivalent EE/ProjectE values. Even so, the amount of
		 * EMC you can expect from this stele is pretty negligible. You generally include this stele because the radiance
		 * bonus for surveyed blocks is quite generous.
		 */
		
		if (block instanceof BlockLog) {
			return 32;
		}
		
		if (block instanceof BlockLeaves) {
			return 4;
		}
		
		if (block == Blocks.GRASS) {
			return 2;
		}
		
		if (block instanceof BlockSapling) {
			return 4;
		}
		
		
		
		return 0;
	}
	
	@Override
	public int produceEMC(int amount, boolean simulate) {
		int produced = 0;
		for(int i=0; i<blockCache.size(); i++) {
			BlockPos cur = blockCache.get(i);
			int emcDrawn = getEffectiveEMC(cur, world.getBlockState(cur)) - inefficiency;
			if (!simulate) {
				Block block = world.getBlockState(cur).getBlock();
				
				world.destroyBlock(cur, false); //TODO: MORE EFFECTS
				
				if (block==Blocks.GRASS) world.setBlockState(cur, Blocks.DIRT.getDefaultState());
			}
			produced += emcDrawn;
			if (emcDrawn>=amount) return emcDrawn;
		}
		
		return produced;
	}

	@Override
	public String getSteleKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void scan() {
		super.scanBlocks();
		this.producer.setRadiance(this.producer.getEMCAvailable() / 50);
	}

}
