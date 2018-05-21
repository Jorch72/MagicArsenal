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
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.math.BlockPos;

public class TileEntityWunjoStele extends TileEntityAbstractStele {
	
	@Override
	public void scan() {
		scanBlocks();
	}
	
	@Override
	public int getEffectiveEMC(BlockPos pos, IBlockState state) {
		Block block = state.getBlock();
		
		if (block==Blocks.SKULL) {
			if (!hasWorld()) return 10;
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof TileEntitySkull) {
				TileEntitySkull skull = (TileEntitySkull)te;
				return EnumSkullValue.forType(skull.getSkullType()).getEMC();
			} else {
				return 1440;
			}
		} else {
			//TODO: Look for modded trophies
			return 0;
		}
	}

	@Override
	public String getSteleKey() {
		return "wunjo";
	}
	
	public static enum EnumSkullValue {
		
		/**
		 * <p>Basis Item: bone, 96 EMC
		 * <p>Drop Rate: ~100% drop rate for bones versus ~2.5% drop rate for skulls gives a ratio of 40:1
		 */
		SKELETON(3840),
		/**
		 * <p>This one could be really contentious. Three wither skeleton skulls is simply not the same as a wither kill.
		 * A wither skeleton kill is no serious feat, and can be merely 1.5x-2x the emc of regular skeletons.
		 */
		WITHER_SKELETON(7680),
		/** 
		 * <p>Basis Item: rotten flesh, 24 EMC
		 * <p>Drop Rate: ~150% drop rate for flesh versus ~2.5% drop rate for non-creepered skulls, about 60:1
		 */
		ZOMBIE(1440),
		/**
		 * <p>Player heads are absolutely, unquestionably worthless.
		 */
		PLAYER(1),
		/**
		 * <p>Basis Item: gunpowder, 192 EMC
		 * <p>Drop Rate: ~150% drop rate for gunpowder versus the ~2.5% drop rate for skulls.
		 * <p>Note: This might seem high, but imagine this scenario: doMobGriefing set to true, and you can choose to play
		 *          a map you're invested in with only *one* reliquary charm to repel *one* monster type. If it was me, I'd
		 *          take safety from creepers every time.
		 */
		CREEPER(11520),
		/**
		 * <p>Basis Item: Dragon Egg, 139264 EMC
		 * <p>Drop Rate: 100%
		 * <p>Note: This isn't really about beating the ender dragon, because the ender dragon never drops heads. It's more
		 *          that you need to kill the dragon and *then* journey through the end islands to find a ship, and then
		 *          *sometimes* you'll find one if you're really lucky. Elytra should also be 139264.
		 */
		ENDER_DRAGON(139264);
		
		private int emc;
		EnumSkullValue(int emc) {
			this.emc = emc;
		}
		
		public int getEMC() {
			return emc;
		}
		
		public static EnumSkullValue forType(int skullType) {
			if (skullType<0 || skullType>=values().length) {
				return PLAYER;
			} else {
				return values()[skullType];
			}
		}
	}
}
