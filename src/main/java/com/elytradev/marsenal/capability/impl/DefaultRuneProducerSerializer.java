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

import com.elytradev.marsenal.capability.IRuneProducer;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class DefaultRuneProducerSerializer implements IStorage<IRuneProducer> {

	@Override
	public NBTBase writeNBT(Capability<IRuneProducer> capability, IRuneProducer instance, EnumFacing side) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("Radiance", instance.getProducerRadiance());
		tag.setInteger("EMC", instance.getEMCAvailable());
		
		return tag;
	}

	@Override
	public void readNBT(Capability<IRuneProducer> capability, IRuneProducer instance, EnumFacing side, NBTBase nbt) {
		if (nbt instanceof NBTTagCompound && instance instanceof RuneProducer) {
			NBTTagCompound tag = (NBTTagCompound)nbt;
			RuneProducer producer = (RuneProducer)instance;
			producer.setRadiance(tag.getInteger("Radiance"));
			producer.setEMC(tag.getInteger("EMC"));
		}
	}
}
