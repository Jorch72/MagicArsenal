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

package com.elytradev.marsenal.compat;

import com.elytradev.marsenal.MagicArsenal;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Loader;

public class EnergyCompat {
	public static DummyFusionEnergy DUMMY_ENERGY = new DummyFusionEnergy();
	
	public static int saturateToInt(long b) {
		return (int)Math.min(Integer.MAX_VALUE, b);
	}
	
	
	public static IFusionEnergy getEnergyWrapper(World world, BlockPos pos, EnumFacing side) {
		TileEntity te = world.getTileEntity(pos);
		if (te==null) return DUMMY_ENERGY;
		
		if (Loader.isModLoaded("tesla") && MagicArsenal.CAPABILITY_TESLA_HOLDER!=null) {
			if (te.hasCapability((Capability<?>) MagicArsenal.CAPABILITY_TESLA_HOLDER, side)) {
				return new LongDankEnergy(te, side);
			}
		}
		
		if (te.hasCapability(CapabilityEnergy.ENERGY, side)) {
			return new RFFusionEnergy(te.getCapability(CapabilityEnergy.ENERGY, side));
		}
		
		return DUMMY_ENERGY;
	}
	
	
	public static long tryPushEnergy(IFusionEnergy source, IFusionEnergy dest, long amount) {
		if (dest==DUMMY_ENERGY) return 0L;
		
		long toMove = amount;
		toMove = dest.insert(toMove, true);
		toMove = source.extract(toMove, false);
		toMove = dest.insert(toMove, false);
		
		return toMove;
	}
	
	/**
	 * Single wrapper interface representing a superset of functionality for all systems I need to talk to.
	 * <p>See {@link https://xkcd.com/927/}
	 */
	public interface IFusionEnergy {
		public long insert(long amount, boolean simulate);
		public long extract(long amount, boolean simulate);
		public long getLevel();
		public long getMax();
	}
	
	private static final class DummyFusionEnergy implements IFusionEnergy {
		@Override
		public long insert(long amount, boolean simulate) { return 0; }
		@Override
		public long extract(long amount, boolean simulate) { return 0; }
		@Override
		public long getLevel() { return 0; }
		@Override
		public long getMax() { return 0; }
	}
	
	private static final class RFFusionEnergy implements IFusionEnergy {
		private final IEnergyStorage delegate;
		
		public RFFusionEnergy(IEnergyStorage delegate) { this.delegate = delegate; }
		
		@Override
		public long insert(long amount, boolean simulate) {
			int delegateAmount = saturateToInt(amount);
			return delegate.receiveEnergy(delegateAmount, simulate);
		}

		@Override
		public long extract(long amount, boolean simulate) {
			int delegateAmount = saturateToInt(amount);
			return delegate.extractEnergy(delegateAmount, simulate);
		}

		@Override
		public long getLevel() {
			return delegate.getEnergyStored();
		}

		@Override
		public long getMax() {
			return delegate.getMaxEnergyStored();
		}
		
	}
}
