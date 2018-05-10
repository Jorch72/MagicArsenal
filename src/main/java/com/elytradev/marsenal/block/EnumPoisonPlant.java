package com.elytradev.marsenal.block;

import com.elytradev.marsenal.item.ArsenalItems;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

public enum EnumPoisonPlant implements IStringSerializable {
	WOLFSBANE("wolfsbane"),
	NIGHTSHADE("nightshade")
	;
	
	private String name;
	
	EnumPoisonPlant(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
	
	public static EnumPoisonPlant valueOf(int i) {
		return values()[i%values().length];
	}
	
	public ItemStack getRoot() {
		return new ItemStack(ArsenalItems.ROOT_WOLFSBANE, 1, this.ordinal());
	}
}
