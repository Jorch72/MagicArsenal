package com.elytradev.marsenal.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(modid="baubles", iface="baubles.api.IBauble")
public class ItemSpellBauble extends ItemSubtyped<EnumSpellBauble> implements IBauble {

	public ItemSpellBauble() {
		super("spellbauble", EnumSpellBauble.values(), false);
		this.setMaxStackSize(1);
	}

	@Override
	@Optional.Method(modid="baubles")
	public BaubleType getBaubleType(ItemStack stack) {
		EnumSpellBauble which = EnumSpellBauble.byId(stack.getMetadata());
		switch(which.getType()) {
		case "head"   : return BaubleType.HEAD;
		case "body"   : return BaubleType.BODY;
		case "amulet" : return BaubleType.AMULET;
		case "belt"   : return BaubleType.BELT;
		case "charm"  : return BaubleType.CHARM;
		case "ring"   : return BaubleType.RING;
		case "trinket": return BaubleType.TRINKET;
		default:
			return null;
		}
	}
}
