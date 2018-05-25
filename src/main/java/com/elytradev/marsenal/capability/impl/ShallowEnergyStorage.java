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

package com.elytradev.marsenal.capability.impl;

import com.elytradev.marsenal.compat.EnergyCompat;

import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Optional;

@Optional.InterfaceList({
		@Optional.Interface(modid="tesla", iface = "net.darkhax.tesla.api.ITeslaConsumer"),
		@Optional.Interface(modid="tesla", iface = "net.darkhax.tesla.api.ITeslaProducer"),
		@Optional.Interface(modid="tesla", iface = "net.darkhax.tesla.api.ITeslaHolder")
})
public class ShallowEnergyStorage implements IEnergyStorage, EnergyCompat.IFusionEnergy, ITeslaHolder, ITeslaProducer, ITeslaConsumer {
	
	protected long storage = 0L;
	protected long limit = Long.MAX_VALUE; //Default us to 9,223,372,036,854,775,807 RF
	protected long transferLimit = 200_000_000L; //200 million per tick should be a decent starting speed
	
	protected Runnable listener;
	
	public NBTTagCompound writeToNBT() {
		NBTTagCompound result = new NBTTagCompound();
		result.setLong("Storage", storage);
		result.setLong("Limit", limit);
		result.setLong("TransferLimit", transferLimit);
		
		return result;
	}
	
	public void readFromNBT(NBTBase nbt) {
		if (nbt!=null && nbt instanceof NBTTagCompound) {
			NBTTagCompound tag = (NBTTagCompound)nbt;
			
			storage = tag.getLong("Storage");
			limit = tag.getLong("Limit");
			transferLimit = tag.getLong("TransferLimit");
		}
	}
	
	public void listen(Runnable r) {
		this.listener = r;
	}
	
	public void onChanged() {
		if (listener!=null) listener.run();
	}
	
	public void setMax(long max) {
		limit = max;
		onChanged();
	}
	
	public void setTransferLimit(long limit) {
		this.transferLimit = limit;
		onChanged();
	}
	
	public void setLevel(long level) {
		this.storage = level;
		onChanged();
	}
	
	public long getTransferLimit() {
		return transferLimit;
	}
	
	/*
	 * #### FORGE IENERGYSTORAGE IMPL ####
	 */
	
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		if (maxReceive<=0) return 0;
		long toReceive = Math.min(maxReceive, transferLimit);
		
		long capacityLeft = limit - storage;
		if (capacityLeft<0L) capacityLeft = 0L;
		
		long received = Math.min(toReceive, capacityLeft);
		if (!simulate && received > 0) {
			storage += received;
			onChanged();
		}
		return EnergyCompat.saturateToInt(received);
	}
	
	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return EnergyCompat.saturateToInt(extract(maxExtract, simulate));
	}
	
	@Override
	public int getEnergyStored() {
		return EnergyCompat.saturateToInt(storage);
	}
	
	@Override
	public int getMaxEnergyStored() {
		return EnergyCompat.saturateToInt(limit);
	}
	
	@Override
	public boolean canExtract() {
		return true;
	}
	
	@Override
	public boolean canReceive() {
		return true;
	}
	
	/*
	 * #### TESLA IMPL ####
	 */

	@Override
	@Optional.Method(modid="tesla")
	public long getStoredPower() {
		return storage;
	}

	@Override
	@Optional.Method(modid="tesla")
	public long getCapacity() {
		return limit;
	}

	@Override
	@Optional.Method(modid="tesla")
	public long takePower(long power, boolean simulated) {
		return extract(power, simulated);
	}

	@Override
	@Optional.Method(modid="tesla")
	public long givePower(long power, boolean simulated) {
		return insert(power, simulated);
	}
	
	/*
	 * #### IFUSIONPOWER IMPL ####
	 */
	
	@Override
	public long insert(long amount, boolean simulate) {
		if (amount<=0) return 0;
		long toReceive = Math.min(amount, transferLimit);
		
		long capacityLeft = limit - storage;
		if (capacityLeft<0L) capacityLeft = 0L;
		
		long received = Math.min(toReceive, capacityLeft);
		if (!simulate && received > 0) {
			storage += received;
			onChanged();
		}
		return received;
	}

	@Override
	public long extract(long amount, boolean simulate) {
		long toExtract = Math.min(amount, transferLimit);
		
		long extracted = Math.min(storage,toExtract);
		if (!simulate) {
			storage -= extracted;
			onChanged();
		}
		return extracted;
	}

	@Override
	public long getLevel() {
		return storage;
	}

	@Override
	public long getMax() {
		return limit;
	}
}
