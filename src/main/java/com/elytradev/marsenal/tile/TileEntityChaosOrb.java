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

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import com.elytradev.marsenal.capability.impl.DeepEnergyStorage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

public class TileEntityChaosOrb extends TileEntity implements ITickable {
	private static final BigInteger RADIANCE_SCALE = BigInteger.valueOf(1_000_000L);
	private DeepEnergyStorage energy = new DeepEnergyStorage();
	private Set<BlockPos> resonatorCache = new HashSet<>();
	private Set<BlockPos> dead = new HashSet<>();
	
	public TileEntityChaosOrb() {
		energy.setEnergyLimitInternal(BigInteger.ZERO); //No capacity until connected
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey("Energy")) {
			energy.readFromNBT(compound.getTag("Energy"));
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("Energy", energy.writeToNBT());
		return compound;
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability==CapabilityEnergy.ENERGY) return true;
		return super.hasCapability(capability, facing);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability==CapabilityEnergy.ENERGY) return (T) energy;
		return super.getCapability(capability, facing);
	}
	
	@Override
	public void update() {
		if (!hasWorld() || world.isRemote) return; //We don't even need to tick on the client.
		
		for(BlockPos cur : resonatorCache) {
			if (!world.isAreaLoaded(cur, 0)) {
				System.out.println("Culling unloaded resonator at "+cur);
				dead.add(cur);
				continue;
			}
			
			TileEntity te = world.getTileEntity(cur);
			if (te instanceof TileEntityChaosResonator) {
				TileEntityChaosResonator resonator = (TileEntityChaosResonator)te;
				
				resonator.transferFromOrb(energy);
			} else {
				System.out.println("ORB Removed dead/missing resonator at "+cur);
				dead.add(cur);
			}
		}
		resonatorCache.removeAll(dead);
		if (resonatorCache.isEmpty()) {
			energy.setEnergyLimit(BigInteger.ZERO); //No resonators means no radiance and no storage.
		}
	}
	
	/**
	 * Notifies this Chaos Orb that a resonator is positioned to beam energy in and out of it.
	 */
	public void pingFromResonator(BlockPos pos) {
		System.out.println("ORB Connected to resonator at "+pos);
		resonatorCache.add(pos);
	}

	public void updateRadiance(int radiance) {
		energy.setEnergyLimit(BigInteger.valueOf(radiance).multiply(RADIANCE_SCALE));
		System.out.println("Energy limit updated to "+energy.getFullEnergyLimit().toString());
	}
}
