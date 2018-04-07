/*
 * MIT License
 *
 * Copyright (c) 2017 Isaac Ellingson (Falkreon) and contributors
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

import com.elytradev.marsenal.capability.IMagicResources;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;

public class DefaultMagicResourcesSerializer implements Capability.IStorage<IMagicResources> {
	private static final int TYPE_NBT_INT = 3;
	private static final int TYPE_NBT_COMPOUND = 10;
		
	@Override
	public NBTBase writeNBT(Capability<IMagicResources> capability, IMagicResources instance, EnumFacing side) {
		NBTTagCompound tag = new NBTTagCompound();
		if (!(instance instanceof MagicResources)) return tag;
		MagicResources res = (MagicResources)instance;
		tag.setInteger("Cooldown", res.getGlobalCooldown());
		tag.setInteger("MaxCooldown", res.getMaxCooldown());
		NBTTagCompound map = new NBTTagCompound();
		res.forEach((id, amount) -> {
			map.setInteger(id.toString(), amount);
		});
		tag.setTag("Resources", map);
		
		return tag;
	}

	@Override
	public void readNBT(Capability<IMagicResources> capability, IMagicResources instance, EnumFacing side, NBTBase nbt) {
		if (nbt instanceof NBTTagCompound) {
			NBTTagCompound tag = (NBTTagCompound)nbt;
			if (tag.hasKey("Cooldown",    TYPE_NBT_INT)) instance.setGlobalCooldown(tag.getInteger("Cooldown"));
			if (tag.hasKey("MaxCooldown", TYPE_NBT_INT)) instance.setMaxCooldown(tag.getInteger("MaxCoodlown"));
			if (tag.hasKey("Resources",   TYPE_NBT_COMPOUND)) {
				NBTTagCompound map = tag.getCompoundTag("Resources");
				for(String name : map.getKeySet()) {
					ResourceLocation id = new ResourceLocation(name);
					if (map.hasKey(name, TYPE_NBT_INT)) instance.set(id, map.getInteger(name));
				}
			}
		}
	}
}
