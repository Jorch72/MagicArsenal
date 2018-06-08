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
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.IPlantable;

public class TileEntityJeraStele extends TileEntityAbstractStele {
	
	protected HashMap<Block, Integer> EMC_VALUES = new HashMap<>();
	
	public TileEntityJeraStele() {
		//Note: Normal inefficiency rules apply (-5 EMC-per-block by default, mitigated by 1 for each Uncarved Stele, down to a minimum of -1)
		//So anything that's listed as 1 here is *always* consumed because it matches this stele's nature, but for no gain, and is surveyed for no Radiance.
		//If it doesn't have a comment it's probably an exact EE3/ProjectE default-value
		EMC_VALUES.put(Blocks.BEETROOTS,            24); //From wheat
		EMC_VALUES.put(Blocks.BROWN_MUSHROOM,       32);
		EMC_VALUES.put(Blocks.BROWN_MUSHROOM_BLOCK, 32*9);
		EMC_VALUES.put(Blocks.CACTUS,                8);
		EMC_VALUES.put(Blocks.CARROTS,              24); //wheat
		EMC_VALUES.put(Blocks.CHORUS_FLOWER,       240); //  should be pretty expensive, 
		EMC_VALUES.put(Blocks.CHORUS_PLANT,          1); //  but then once you get it it's easy to grow
		EMC_VALUES.put(Blocks.COCOA,                 8); //less than wheat
		EMC_VALUES.put(Blocks.DEADBUSH,              1);
		EMC_VALUES.put(Blocks.DOUBLE_PLANT,         16); //From short flower
		EMC_VALUES.put(Blocks.FARMLAND,              2); //1 more than it would normally be to make EMC obtainable
		//EMC_VALUES.put(Blocks.GRASS,                 2); // "  "
		//EMC_VALUES.put(Blocks.LEAVES,                1); //Reduced back to the vanilla value because of leaf regrowth plugins
		//EMC_VALUES.put(Blocks.LEAVES2,               1); // "  "
		EMC_VALUES.put(Blocks.LIT_PUMPKIN,         144);
		//EMC_VALUES.put(Blocks.LOG,                  32);
		//EMC_VALUES.put(Blocks.LOG2,                 32);
		EMC_VALUES.put(Blocks.MELON_BLOCK,         144);
		EMC_VALUES.put(Blocks.MELON_STEM,           16); //custom
		EMC_VALUES.put(Blocks.MYCELIUM,              3);
		EMC_VALUES.put(Blocks.NETHER_WART,           1); //Doesn't match our nature very well
		EMC_VALUES.put(Blocks.NETHER_WART_BLOCK,     1*9);//Ditto, just 9 times not-very-much
		EMC_VALUES.put(Blocks.POTATOES,             24); //wheat
		EMC_VALUES.put(Blocks.PUMPKIN,             144);
		EMC_VALUES.put(Blocks.PUMPKIN_STEM,         16); //custom
		EMC_VALUES.put(Blocks.RED_FLOWER,           16);
		EMC_VALUES.put(Blocks.RED_MUSHROOM,         32);
		EMC_VALUES.put(Blocks.RED_MUSHROOM_BLOCK,   32*9);
		EMC_VALUES.put(Blocks.REEDS,                 2); //1 more than it would normally be to make EMC obtainable
		//EMC_VALUES.put(Blocks.SAPLING,               2); // "  "
		EMC_VALUES.put(Blocks.SPONGE,             1000); //Potentially controversial
		EMC_VALUES.put(Blocks.TALLGRASS,             1);
		EMC_VALUES.put(Blocks.VINE,                  8);
		EMC_VALUES.put(Blocks.WATERLILY,            16);
		EMC_VALUES.put(Blocks.WHEAT,                24);
		EMC_VALUES.put(Blocks.YELLOW_FLOWER,        16);
	}
	
	@Override
	public int getEffectiveEMC(BlockPos pos, IBlockState state) {
		Block block = state.getBlock();
		
		if (EMC_VALUES.containsKey(block)) return EMC_VALUES.get(block);
		
		/*
		if (block instanceof BlockLog) {
			return EMC_VALUES.get(Blocks.LOG);
		}
		
		if (block instanceof BlockLeaves) {
			return EMC_VALUES.get(Blocks.LEAVES);
		}
		
		if (block instanceof BlockSapling) {
			return EMC_VALUES.get(Blocks.SAPLING);
		}
		*/
		if (block instanceof IPlantable || block instanceof IGrowable) {
			return 24; //Actual wheat value, and we all know wheat is the apatite of the crop world
		}
		
		if (block instanceof BlockBush) {
			return EMC_VALUES.get(Blocks.DEADBUSH); //I wish we could value you more highly, but you're not a crop and don't respond to bonemeal.
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
		return "jera";
	}

	@Override
	public void scan() {
		super.scanBlocks();
		this.producer.setRadiance(this.producer.getEMCAvailable() / 50);
	}

}
