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

import java.util.HashMap;

import com.elytradev.marsenal.recipe.EmcRegistry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class TileEntityBerkanoStele extends TileEntityAbstractStele {
	public static final EmcRegistry REGISTRY = new EmcRegistry();
	//protected HashMap<Block, Integer> EMC_VALUES = new HashMap<>();
	
	public TileEntityBerkanoStele() {
		//Note: Normal inefficiency rules apply (-5 EMC-per-block by default, mitigated by 1 for each Uncarved Stele, down to a minimum of -1)
		//So anything that's listed as 1 here is *always* consumed because it matches this stele's nature, but for no gain, and is surveyed for no Radiance.
		//If it doesn't have a comment it's probably an exact EE3/ProjectE default-value
		/*
		EMC_VALUES.put(Blocks.GRASS,                 2); // "  "
		EMC_VALUES.put(Blocks.LEAVES,                1); //Reduced back to the vanilla value because of leaf regrowth plugins
		EMC_VALUES.put(Blocks.LEAVES2,               1); // "  "
		EMC_VALUES.put(Blocks.LOG,                  32);
		EMC_VALUES.put(Blocks.LOG2,                 32);
		EMC_VALUES.put(Blocks.SAPLING,               2); // "  "*/
	}
	
	@Override
	public int getEffectiveEMC(BlockPos pos, IBlockState state) {
		Block block = state.getBlock();
		
		if (REGISTRY.contains(state)) return REGISTRY.get(state);
		
		if (block instanceof BlockLog) {
			return REGISTRY.get(Blocks.LOG.getDefaultState());
		}
		
		if (block instanceof BlockLeaves) {
			return REGISTRY.get(Blocks.LEAVES.getDefaultState());
		}
		
		if (block instanceof BlockSapling) {
			return REGISTRY.get(Blocks.SAPLING.getDefaultState());
		}
		
		return 0;
	}
	
	@Override
	public void consume(BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		if (block==Blocks.DIRT) return;
		if (
				block==Blocks.FARMLAND ||
				block==Blocks.MYCELIUM ||
				block==Blocks.GRASS) {
			world.playEvent(2001, pos, Block.getStateId(world.getBlockState(pos))); //Block break particles & sound. I'm sure there's a cleaner way to do this.
			world.setBlockState(pos, Blocks.DIRT.getDefaultState());
			return;
		}
		
		super.consume(pos);
	}

	@Override
	public String getSteleKey() {
		return "berkano";
	}

	@Override
	public void scan() {
		super.scanBlocks();
		this.producer.setRadiance(this.producer.getEMCAvailable() / 25);
	}

}
