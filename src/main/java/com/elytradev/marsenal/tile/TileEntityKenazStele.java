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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.elytradev.marsenal.MagicArsenal;
import com.elytradev.marsenal.block.ArsenalBlocks;
import com.elytradev.marsenal.capability.impl.RuneProducer;
import com.elytradev.marsenal.compat.ProbeDataCompat;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;

public class TileEntityKenazStele extends TileEntity implements INetworkParticipant {
	private RuneProducer producer = new RuneProducer("kenaz", this::produceEMC);
	private Set<EnumFacing> openFaces = new HashSet<>();
	private List<BlockPos> blockCache = new ArrayList<>();
	int range = 2;
	
	private long lastPoll;
	private BlockPos controller;
	private BlockPos beamTo;
	private int inefficiency = 5;
	
	public void scan() {
		blockCache.clear();
		openFaces.clear();
		range = 2;
		producer.clearEMC();
		producer.clearRadiance();
		inefficiency = 5;
		
		for(EnumFacing facing : EnumFacing.values()) {
			if (world.isAirBlock(pos.offset(facing))) {
				openFaces.add(facing);
			} else {
				IBlockState state = world.getBlockState(pos.offset(facing));
				if (state.getBlock()==ArsenalBlocks.STELE_UNCARVED) {
					range++;
					inefficiency--;
					if (inefficiency<1) inefficiency = 1;
				}
			}
		}
		
		//Throw a cone out from each face
		for(EnumFacing face : openFaces) {
			//plusD is already 'face'
			EnumFacing plusX = EnumFacing.EAST;
			EnumFacing plusY = EnumFacing.SOUTH;
			
			if (face.getAxis()==EnumFacing.Axis.X) {
				plusX = EnumFacing.SOUTH;
				plusY = EnumFacing.UP;
			} else if (face.getAxis()==EnumFacing.Axis.Z) {
				plusX = EnumFacing.EAST;
				plusY = EnumFacing.UP;
			} else {
				//axis is Y
				plusX = EnumFacing.EAST;
				plusY = EnumFacing.SOUTH;
			}
			
			
			for(int d=0; d<range; d++) {
				//X and Y are in rotated space. They are not X and Y.
				for(int x=-d; x<=d; x++) {
					for(int y=-d; y<=d; y++) {
						BlockPos searchPos = pos.offset(face, d+1).offset(plusX, x).offset(plusY, y);
						if (blockCache.contains(searchPos)) continue;
						int emc = getEffectiveEMC(searchPos, world.getBlockState(searchPos));
						if (emc>0) {
							blockCache.add(searchPos);
							producer.addEMC(emc-inefficiency);
						}
					}
				}
			}
		}
		
		/**
		 * Make the order of bookcase draws random, but the *same* random for the purposes of draw simulations.
		 */
		Collections.shuffle(blockCache);
		producer.setRadianceFromEMC();
		markDirty();
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (MagicArsenal.CAPABILITY_PROBEDATA!=null && capability==MagicArsenal.CAPABILITY_PROBEDATA) return true;
		if (capability==MagicArsenal.CAPABILITY_RUNEPRODUCER) return true;
		return super.hasCapability(capability, facing);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		//TODO: IRuneProducer cap
		if (MagicArsenal.CAPABILITY_PROBEDATA!=null && capability==MagicArsenal.CAPABILITY_PROBEDATA) {
			return (T) ProbeDataCompat.getProvider(this, producer);
		}
		if (capability==MagicArsenal.CAPABILITY_RUNEPRODUCER) return (T) producer;
		
		return super.getCapability(capability, facing);
	}
	
	public int getEffectiveEMC(BlockPos pos, IBlockState state) {
		Block block = state.getBlock();
		if (block==Blocks.TORCH) return 9;
		if (block==Blocks.BOOKSHELF) return 336; //Yep, EMC.
		
		return 0;
	}
	
	public int produceEMC(int amount, boolean simulate) {
		int produced = 0;
		for(int i=0; i<blockCache.size(); i++) {
			BlockPos cur = blockCache.get(i);
			int emcDrawn = getEffectiveEMC(cur, world.getBlockState(cur));
			if (!simulate) {
				world.destroyBlock(cur, false); //TODO: MORE EFFECTS
			}
			produced += emcDrawn;
			if (emcDrawn>amount) return emcDrawn;
		}
		
		return produced;
	}
	
	public void setBeamTo(BlockPos pos) {
		this.beamTo = pos;
	}
	
	public BlockPos getBeamTo() {
		return beamTo;
	}

	@Override
	public boolean canJoinNetwork(BlockPos controller) {
		if (this.controller==null || this.controller.equals(controller)) return true;
		
		long now = world.getTotalWorldTime();
		return now-lastPoll > 20*5; //Has it been five seconds since the last poll?
	}

	@Override
	public void joinNetwork(BlockPos controller, BlockPos beamTo) {
		this.controller = controller;
		setBeamTo(beamTo);
		lastPoll = world.getTotalWorldTime();
		scan();
	}

	@Override
	public void pollNetwork(BlockPos controller, BlockPos beamTo) {
		if (this.controller==null || this.controller!=controller) return;
		setBeamTo(beamTo);
		lastPoll = world.getTotalWorldTime();
		scan();
	}
	
	public List<BlockPos> getBlockCache() {
		return blockCache;
	}
	
	public long getLastPollTick() {
		return lastPoll;
	}
}
