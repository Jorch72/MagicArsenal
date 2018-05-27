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

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class TileEntityFehuStele extends TileEntityAbstractStele {
	protected HashMap<Block, Integer> EMC_VALUES = new HashMap<>();
	
	public TileEntityFehuStele() {
		/* This list should be *very* short and uncontroversial. However, it will need to expand greatly for modded
		 * blocks, as a great many of them aim to be Expensive, and should thus fall under the nature of this stele.
		 */
		EMC_VALUES.put(Blocks.DIAMOND_BLOCK,      73_728);
		EMC_VALUES.put(Blocks.DIAMOND_ORE,         8_192);
		EMC_VALUES.put(Blocks.EMERALD_BLOCK,      73_728);
		EMC_VALUES.put(Blocks.EMERALD_ORE,         8_192);
		EMC_VALUES.put(Blocks.GOLD_BLOCK,         18_432);
		EMC_VALUES.put(Blocks.GOLD_ORE,            2_048);
		EMC_VALUES.put(Blocks.IRON_BLOCK,          2_304);
		EMC_VALUES.put(Blocks.IRON_BARS,              96);
		EMC_VALUES.put(Blocks.IRON_ORE,              256);
		EMC_VALUES.put(Blocks.LAPIS_BLOCK,         7_776);
		EMC_VALUES.put(Blocks.LAPIS_ORE,             864);
	}
	
	@Override
	public int getEffectiveEMC(BlockPos pos, IBlockState state) {
		if (EMC_VALUES.containsKey(state.getBlock())) {
			return EMC_VALUES.get(state.getBlock());
		} else {
			return 0;
		}
	}

	@Override
	public String getSteleKey() {
		return "fehu";
	}

	@Override
	public void scan() {
		super.scanBlocks();
	}
	
}
