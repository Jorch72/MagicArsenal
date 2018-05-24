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

import com.elytradev.marsenal.block.BlockChaosResonator;
import com.elytradev.marsenal.tile.TileEntityChaosResonator;
import com.elytradev.probe.api.IProbeData;
import com.elytradev.probe.api.IProbeDataProvider;
import com.elytradev.probe.api.UnitDictionary;
import com.elytradev.probe.api.impl.ProbeData;

import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class ResonatorDataProvider implements IProbeDataProvider {
	private TileEntityChaosResonator resonator;
	
	public ResonatorDataProvider(TileEntityChaosResonator te) {
		this.resonator = te;
	}
	
	@Override
	public void provideProbeData(List<IProbeData> data) {
		data.add(new ProbeData()
				.withLabel(new TextComponentTranslation("info.magicarsenal.label.energybuffer"))
				.withBar(0, resonator.getEnergyStorage().getLevel(), resonator.getEnergyStorage().getMax(), UnitDictionary.DANKS));
		if (resonator.getWorld()==null) return;
		try {
			String s = resonator.getWorld().getBlockState(resonator.getPos()).getValue(BlockChaosResonator.MODE).getName();
			data.add(new ProbeData()
					.withLabel(new TextComponentTranslation("info.magicarsenal.label.resonator.mode."+s)));
		} catch (Throwable t) {
			data.add(new ProbeData()
					.withLabel(new TextComponentString("Unknown Mode")));
		}
	}

}
