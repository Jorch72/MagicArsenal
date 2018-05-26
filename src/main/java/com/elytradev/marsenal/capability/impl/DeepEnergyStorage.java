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

import java.math.BigInteger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elytradev.marsenal.compat.EnergyCompat;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.IEnergyStorage;

/** Gaze upon my new non-euclidean horror. What we have here is Literally Infinite energy storage.
 * <p>
 * Whenever I say Literally Infinite, I really mean 2^68,719,476,704 - 1, which is significantly larger than the
 * 9,223,372,036,854,775,807 (9 x 10^18) that "Almost Infinite" tends to mean. The "Literally Infinite" number I speak
 * of is about 1 x 10^20,686,623,775. The universe has about 1 x 10^80 atoms in it.
 * <p>
 * "Literally Infinite" is gigantically, unimaginably big in a way where it's disconnected with any magnitude we'll ever
 * encounter in real life, measurable or otherwise. When I put 2^68,719,476,704 into KCalc the result is "inf". Wolfram
 * Alpha completely gives up and shows nothing. It's nearly incalculable. If it's not enough RF for you, the problem was
 * never about RF.
 */

public class DeepEnergyStorage implements IEnergyStorage, EnergyCompat.IFusionEnergy {
	protected BigInteger storage = BigInteger.ZERO;
	protected BigInteger limit = BigInteger.valueOf(Long.MAX_VALUE); //Default us to 9,223,372,036,854,775,807 RF
	protected BigInteger transferLimit = BigInteger.valueOf(200_000_000L); //200 million per tick should be a decent starting speed
	
	protected Runnable listener;
	
	private static int saturateToInt(BigInteger b) {
		try {
			return b.intValueExact();
		} catch (ArithmeticException ex) {
			return Integer.MAX_VALUE;
		}
	}
	
	/** Listen for changes in the value of this energy storage, and run the provided Runnable when it happens. For
	 * TileEntities, it's reccommended they call {@code listen(this::markDirty);} from their constructor or chained from
	 * their field initializer.
	 */
	public DeepEnergyStorage listen(Runnable r) {
		this.listener = r;
		return this;
	}
	
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		if (maxReceive<=0) return 0;
		BigInteger toReceive = BigInteger.valueOf(maxReceive).min(transferLimit);
		
		if (limit==null) { // L I T E R A L L Y  I N F I N I T E
			if (!simulate) {
				storage = storage.add(toReceive);
				onChanged();
			}
			return saturateToInt(toReceive);
		}
		
		//Below is for non-infinite plebs
		
		BigInteger capacityLeft = limit.subtract(storage).max(BigInteger.ZERO);
		
		BigInteger received = toReceive.min(capacityLeft);
		if (!simulate && received.compareTo(BigInteger.ZERO) > 0) {
			storage = storage.add(received);
			onChanged();
		}
		return saturateToInt(received);
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		BigInteger toExtract = BigInteger.valueOf(maxExtract).min(transferLimit);
		
		BigInteger extracted = storage.min(toExtract);
		if (!simulate) {
			storage = storage.subtract(extracted);
			onChanged();
		}
		
		return saturateToInt(extracted);
	}

	@Override
	public int getEnergyStored() {
		return saturateToInt(storage);
	}
	
	@Nonnull
	public BigInteger getFullEnergyStored() {
		return storage;
	}
	
	@Nullable
	public BigInteger getFullEnergyLimit() {
		return limit;
	}
	
	@Nonnull
	public BigInteger getFullTransferLimit() {
		return transferLimit;
	}
	
	public void setEnergyLimit(BigInteger limit) {
		this.limit = limit; //Don't set current energy; soft-cap instead.
		onChanged();
	}
	
	public void setEnergyStored(BigInteger energy) {
		if (limit==null) { // L I T E R A L L Y  I N F I N I T E
			storage = energy;
			onChanged();
		} else { // Non-infinite
			BigInteger updatedValue = energy.min(limit);
			storage = updatedValue;
			onChanged();
		}
	}
	
	public void setTransferLimit(BigInteger limit) {
		this.transferLimit = limit;
	}
	
	/** Meant for serialization/deserialization. Does not trigger listeners. */
	public void setEnergyLimitInternal(BigInteger limit) {
		this.limit = limit;
	}
	
	/** Meant for serialization/deserialization. Does not trigger listeners. */
	public void setEnergyInternal(BigInteger energy) {
		this.storage = energy;
	}
	
	public NBTTagCompound writeToNBT() {
		NBTTagCompound result = new NBTTagCompound();
		result.setByteArray("Storage", storage.toByteArray());
		if (limit==null) {
			result.setByteArray("Limit", new byte[0]);
		} else {
			result.setByteArray("Limit", limit.toByteArray());
		}
		result.setByteArray("TransferLimit", transferLimit.toByteArray());
		
		return result;
	}
	
	public void readFromNBT(NBTBase nbt) {
		if (nbt!=null && nbt instanceof NBTTagCompound) {
			NBTTagCompound tag = (NBTTagCompound)nbt;
			
			byte[] storageArray = tag.getByteArray("Storage");
			storage = (storageArray.length==0) ? BigInteger.ZERO : new BigInteger(storageArray);
			
			byte[] limitArray = tag.getByteArray("Limit");
			if (limitArray.length==0) {
				limit = null;
			} else {
				limit = new BigInteger(limitArray);
			}
			
			byte[] transferLimitArray = tag.getByteArray("TransferLimit");
			transferLimit = (transferLimitArray.length==0) ? BigInteger.ZERO : new BigInteger(transferLimitArray);
		}
	}

	@Override
	public int getMaxEnergyStored() {
		return saturateToInt(limit);
	}

	@Override
	public boolean canExtract() {
		return true;
	}

	@Override
	public boolean canReceive() {
		return true;
	}
	
	public void onChanged() {
		if (listener!=null) listener.run();
	}
	
	
	
	/*
	 * #### IFUSIONPOWER IMPL ####
	 */
	
	@Override
	public long insert(long amount, boolean simulate) {
		if (amount<=0) return 0;
		BigInteger toRecieve = BigInteger.valueOf(amount).min(transferLimit);
		
		BigInteger capacityLeft = (limit==null) ? storage.add(transferLimit) : limit.subtract(storage);
		if (capacityLeft.compareTo(BigInteger.ZERO)<=0) {
			capacityLeft = BigInteger.ZERO;
		}
		
		BigInteger received = toRecieve.min(capacityLeft);
		if (!simulate && received.compareTo(BigInteger.ZERO)> 0) {
			storage = storage.add(received);
			onChanged();
		}
		return received.longValue(); //Since received can't exceed amount, we're okay to do this.
	}

	@Override
	public long extract(long amount, boolean simulate) {
		BigInteger toExtract = BigInteger.valueOf(amount).min(transferLimit);
		
		BigInteger extracted = storage.min(toExtract);
		if (!simulate) {
			storage = storage.subtract(extracted);
			onChanged();
		}
		return extracted.longValue(); //Again, we shouldn't be able to exceed long capacity
	}

	@Override
	public long getLevel() {
		try {
			return storage.longValueExact();
		} catch (ArithmeticException ex) {
			return Long.MAX_VALUE;
		}
	}

	@Override
	public long getMax() {
		if (limit==null) return Long.MAX_VALUE;
		try {
			return limit.longValueExact();
		} catch (ArithmeticException ex) {
			return Long.MAX_VALUE;
		}
	}

}
