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

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;

public abstract class TileEntityAbstractStele  extends TileEntity implements INetworkParticipant {
	protected RuneProducer producer = new RuneProducer(getSteleKey(), this::produceEMC);
	protected Set<EnumFacing> openFaces = new HashSet<>();
	protected List<BlockPos> blockCache = new ArrayList<>();
	protected int range = 2;
	
	protected long lastPoll;
	protected BlockPos controller;
	protected BlockPos beamTo;
	protected int inefficiency = 5;
	
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

	@Override
	public BlockPos getBeamTo() {
		return beamTo;
	}

	public void setBeamTo(BlockPos beamTo) {
		this.beamTo = beamTo;
	}
	
	public List<BlockPos> getBlockCache() {
		return blockCache;
	}
	
	public long getLastPollTick() {
		return lastPoll;
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
		if (MagicArsenal.CAPABILITY_PROBEDATA!=null && capability==MagicArsenal.CAPABILITY_PROBEDATA) {
			return (T) ProbeDataCompat.getProvider(this, producer);
		}
		if (capability==MagicArsenal.CAPABILITY_RUNEPRODUCER) return (T) producer;
		
		return super.getCapability(capability, facing);
	}
	
	/** Utility method for stele who have similar scan processes */
	protected void scanBlocks() {
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
		
		Vec3d centerOfBlock = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
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
			
			Vec3d faceCenter = centerOfBlock.add(new Vec3d(face.getDirectionVec()).scale(0.5 + (1/16f)));
			
			for(int d=0; d<range; d++) {
				//X and Y are in rotated space. They are not X and Y.
				for(int x=-d; x<=d; x++) {
					for(int y=-d; y<=d; y++) {
						BlockPos searchPos = pos.offset(face, d+1).offset(plusX, x).offset(plusY, y);
						if (blockCache.contains(searchPos)) continue;
						
						int emc = getEffectiveEMC(searchPos, world.getBlockState(searchPos));
						if (emc>0) {
							//Raycast over to the block
							RayTraceResult trace = world.rayTraceBlocks(faceCenter, new Vec3d(searchPos).addVector(0.5, 0.5, 0.5), false);
							boolean blocked = false;
							if (trace!=null) {
								if (trace.typeOfHit==RayTraceResult.Type.BLOCK) {
									BlockPos pos = trace.getBlockPos();
									if (!pos.equals(searchPos)) blocked = true;
								}
							} //else if trace is null, we hit air(?) and probably the trace is fine, we're just looking for a nonsolid block maybe.
							
							
							if (!blocked) {
								blockCache.add(searchPos);
								producer.addEMC(emc-inefficiency);
							}
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
	
	/** Override this method if your producer does not draw from the block cache */
	public int produceEMC(int amount, boolean simulate) {
		int produced = 0;
		for(int i=0; i<blockCache.size(); i++) {
			BlockPos cur = blockCache.get(i);
			int emcDrawn = getEffectiveEMC(cur, world.getBlockState(cur)) - inefficiency;
			if (!simulate) {
				world.destroyBlock(cur, false); //TODO: MORE EFFECTS
			}
			produced += emcDrawn;
			if (emcDrawn>=amount) return emcDrawn;
		}
		
		return produced;
	}
	
	/**
	 * Not all blocks have EMC value. Possibly none. But this lets us at least ask the concrete question of what a stele
	 * "sees" and what its value is.
	 */
	public abstract int getEffectiveEMC(BlockPos pos, IBlockState state);
	
	/**
	 * Get the key which is used to mutually exclude other stele of this kind
	 */
	public abstract String getSteleKey();
	/**
	 * Update the EMC/Radiance values and the producer cache
	 */
	public abstract void scan();
}
