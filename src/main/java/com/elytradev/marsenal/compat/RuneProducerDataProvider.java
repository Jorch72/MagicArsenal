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

import java.util.List;

import com.elytradev.marsenal.capability.impl.RuneProducer;
import com.elytradev.marsenal.tile.INetworkParticipant;
import com.elytradev.marsenal.tile.TileEntityKenazStele;
import com.elytradev.marsenal.tile.TileEntityRunicAltar;
import com.elytradev.probe.api.IProbeData;
import com.elytradev.probe.api.IProbeDataProvider;
import com.elytradev.probe.api.IUnit;
import com.elytradev.probe.api.UnitDictionary;
import com.elytradev.probe.api.impl.ProbeData;
import com.elytradev.probe.api.impl.SIUnit;
import com.elytradev.probe.api.impl.Unit;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class RuneProducerDataProvider implements IProbeDataProvider {
	private static IUnit RADIANCE = null;
	private static IUnit EMC = null;
	
	TileEntity te;
	RuneProducer producer;
	
	public RuneProducerDataProvider(TileEntity te, RuneProducer producer) {
		this.te = te;
		this.producer = producer;
		
		if (RADIANCE==null) RADIANCE = new SIUnit("info.magicarsenal.radiance.name", "", 0xFFFFFFFF);
		if (EMC==null) EMC = new SIUnit("info.magicarsenal.emc.name", "", 0xFFeedd00);
	}
	
	@Override
	public void provideProbeData(List<IProbeData> data) {
		if (producer!=null) {
			int radiance = producer.getProducerRadiance();
			data.add(new ProbeData()
					.withLabel(new TextComponentTranslation("info.magicarsenal.label.radiance", new TextComponentString(""+radiance))));
			
			data.add(new ProbeData()
					.withLabel(new TextComponentTranslation("info.magicarsenal.label.emc", new TextComponentString(""+producer.getEMCAvailable()))));
		}
		
		if (te instanceof INetworkParticipant) {
			if (((INetworkParticipant)te).getBeamTo()==null) {
				data.add(new ProbeData()
						.withLabel(new TextComponentTranslation("info.magicarsenal.label.sleeping")));
			} else {
				data.add(new ProbeData()
						.withLabel(new TextComponentTranslation("info.magicarsenal.label.transmitting")));
			}
			
			if (te instanceof TileEntityKenazStele) {
				int cacheSize = ((TileEntityKenazStele)te).getBlockCache().size();
				data.add(new ProbeData()
						.withLabel("Bookcases: "+cacheSize)
						);
				long ticksSinceLastScan = te.getWorld().getTotalWorldTime()-((TileEntityKenazStele)te).getLastPollTick();
				data.add(new ProbeData()
						.withLabel("Scan Progress")
						.withBar(0, ticksSinceLastScan, 20*10, UnitDictionary.TICKS));
			}
		}
		
		if (te instanceof TileEntityRunicAltar) {
			TileEntityRunicAltar altar = (TileEntityRunicAltar)te;
			int radiance = altar.getRadiance();
			int emc = altar.getEMC();
			
			data.add(new ProbeData()
					.withLabel(new TextComponentTranslation("info.magicarsenal.label.radiance", new TextComponentString(""+radiance))));
					//.withBar(0, reported, radiance, RADIANCE));
			data.add(new ProbeData()
					.withLabel(new TextComponentTranslation("info.magicarsenal.label.emc", new TextComponentString(""+emc))));
					//.withBar(0, emc, emc, EMC));
		}
	}

}
