package com.elytradev.marsenal.item;

import net.minecraft.util.IStringSerializable;

public enum EnumSpellBauble implements IStringSerializable {
	GRAVITY_MANTLE("gravitymantle", "body"),
	
	;
	
	private String name;
	private String type;
	EnumSpellBauble(String name, String type) {
		this.name = name;
		this.type = type;
	}
	
	public String getType() { return type; }
	
	public static EnumSpellBauble byId(int id) {
		return values()[id%values().length];
	}

	@Override
	public String getName() {
		return name;
	}
}
