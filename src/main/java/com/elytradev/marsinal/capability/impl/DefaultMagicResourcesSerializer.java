package com.elytradev.marsinal.capability.impl;

import com.elytradev.marsinal.capability.IMagicResources;

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
