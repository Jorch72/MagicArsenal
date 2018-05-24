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
import com.elytradev.marsenal.compat.EnergyCompat;

import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

/**
 * Wrapper for Tesla.
 * 
 * <p>Funny story: Long ago, in the twenty-sixteens, Tesla's maintainer, DarkHax, put up an issue to solicit
 * names for the energy unit for this API. Many prominent modders spoke up with fantastic suggestions, such as
 * "Walrus Flux", "Pointless Flux", "WATs", "Cubicurrent", and so on, but the clear winner was "Danks".
 * 
 * <p>Darkhax politely thanked them for the suggestions and closed the issue.
 * 
 * <p>So, with that, welcome to the running gag.
 */
public final class LongDankEnergy implements EnergyCompat.IFusionEnergy {
	private final ITeslaHolder holder;
	private final ITeslaProducer producer;
	private final ITeslaConsumer consumer;
	
	@SuppressWarnings("unchecked")
	public LongDankEnergy(TileEntity te, EnumFacing side) {
		if (te.hasCapability((Capability<ITeslaHolder>)MagicArsenal.CAPABILITY_TESLA_HOLDER, side)) {
			holder = te.getCapability((Capability<ITeslaHolder>)MagicArsenal.CAPABILITY_TESLA_HOLDER, side);
		} else {
			holder = null;
		}
		
		if (te.hasCapability((Capability<ITeslaConsumer>)MagicArsenal.CAPABILITY_TESLA_CONSUMER, side)) {
			consumer = te.getCapability((Capability<ITeslaConsumer>)MagicArsenal.CAPABILITY_TESLA_CONSUMER, side);
		} else {
			consumer = null;
		}
		
		if (te.hasCapability((Capability<ITeslaProducer>)MagicArsenal.CAPABILITY_TESLA_PRODUCER, side)) {
			producer = te.getCapability((Capability<ITeslaProducer>)MagicArsenal.CAPABILITY_TESLA_PRODUCER, side);
		} else {
			producer = null;
		}
	}
	
	/** Offer danks */
	@Override
	public long insert(long amount, boolean simulate) {
		if (consumer==null) return 0L;
		return consumer.givePower(amount, simulate);
	}
	
	/** Try to procure danks */
	@Override
	public long extract(long amount, boolean simulate) {
		if (producer==null) return 0L;
		return producer.takePower(amount, simulate);
	}
	
	/** Figure out where we're at with respect to dankness */
	@Override
	public long getLevel() {
		if (holder==null) return 0L;
		return holder.getStoredPower();
	}
	
	/** Find out how much more dank it could possibly be */
	@Override
	public long getMax() {
		if (holder==null) return 0L;
		return holder.getCapacity();
	}
}