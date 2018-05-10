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

package com.elytradev.marsenal.item;

import java.util.ArrayList;
import java.util.List;

import com.elytradev.marsenal.MagicArsenal;
import com.elytradev.marsenal.block.ArsenalBlocks;
import com.elytradev.marsenal.block.EnumPoisonPlant;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;

public class ArsenalItems {
	private static List<Item> itemsForModels = new ArrayList<>();
	public static ItemSpellFocus               SPELL_FOCUS = null;
	public static ItemSpellBauble              SPELL_BAUBLE= null;
	public static ItemSubtyped<EnumIngredient> INGREDIENT  = null;
	public static ItemPoisonRoot               ROOT_WOLFSBANE = null;
	public static ItemPoisonRoot               ROOT_NIGHTSHADE = null;
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();
		
		for(Block b : ArsenalBlocks.blocksForItems()) {
			item(r, new ItemBlock(b).setRegistryName(b.getRegistryName()));
		}
		
		SPELL_FOCUS = item(r, new ItemSpellFocus());
		SPELL_BAUBLE= item(r, new ItemSpellBauble());
		INGREDIENT  = item(r, new ItemSubtyped<>("ingredient", EnumIngredient.values(), false));
		ROOT_WOLFSBANE  = item(r, new ItemPoisonRoot("wolfsbane", ArsenalBlocks.CROP_WOLFSBANE, Blocks.FARMLAND));
		ROOT_NIGHTSHADE = item(r, new ItemPoisonRoot("nightshade", ArsenalBlocks.CROP_WOLFSBANE, Blocks.FARMLAND)); //FIXME: Switch to nightshade when the plant is done
		
		ArsenalBlocks.CROP_WOLFSBANE.setHarvestItems(EnumPoisonPlant.WOLFSBANE.getRoot(), EnumIngredient.PETAL_WOLFSBANE.getItem());
	}
	
	@SubscribeEvent
	public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
		IForgeRegistry<IRecipe> r = event.getRegistry();
		
		r.register(new ShapedOreRecipe(new ResourceLocation(MagicArsenal.MODID, "focuscore"), new ItemStack(INGREDIENT, 1, EnumIngredient.FOCUS_CORE.ordinal()),
				"qrq", "rLr", "qrq",
				'q', "gemQuartz",
				'r', "dustRedstone",
				'L', "blockLapis"
				).setRegistryName("magicarsenal_focus_core"));
		
		r.register(new ShapelessOreRecipe(new ResourceLocation(MagicArsenal.MODID, "spellfocus"), new ItemStack(SPELL_FOCUS, 1, EnumSpellFocus.HEALING_WAVE.ordinal()),
				new ItemStack(INGREDIENT, 1, EnumIngredient.FOCUS_CORE.ordinal()),
				"treeSapling",
				"gemEmerald"
				).setRegistryName("magicarsenal_spellfocus_healing_wave"));
		
		r.register(new ShapelessOreRecipe(new ResourceLocation(MagicArsenal.MODID, "spellfocus"), new ItemStack(SPELL_FOCUS, 1, EnumSpellFocus.DRAIN_LIFE.ordinal()),
				new ItemStack(INGREDIENT, 1, EnumIngredient.FOCUS_CORE.ordinal()),
				Items.SKULL,
				"feather"
				).setRegistryName("magicarsenal_spellfocus_drain_life"));
		
		r.register(new ShapelessOreRecipe(new ResourceLocation(MagicArsenal.MODID, "spellfocus"), new ItemStack(SPELL_FOCUS, 1, EnumSpellFocus.OBLATION.ordinal()),
				new ItemStack(INGREDIENT, 1, EnumIngredient.FOCUS_CORE.ordinal()),
				"ingotGold",
				Items.FERMENTED_SPIDER_EYE
				).setRegistryName("magicarsenal_spellfocus_oblation"));
		
		ItemStack healingPotion = new ItemStack(Items.POTIONITEM);
		PotionUtils.addPotionToItemStack(healingPotion, PotionTypes.STRONG_HEALING);
		IngredientNBT potionIngredient = new IngredientNBT(healingPotion);
		
		r.register(new ShapelessOreRecipe(new ResourceLocation(MagicArsenal.MODID, "spellfocus"), new ItemStack(SPELL_FOCUS, 1, EnumSpellFocus.RECOVERY.ordinal()),
				new ItemStack(INGREDIENT, 1, EnumIngredient.FOCUS_CORE.ordinal()),
				"gemEmerald",
				potionIngredient
				).setRegistryName("magicarsenal_spellfocus_recovery"));
		
		r.register(new ShapelessOreRecipe(new ResourceLocation(MagicArsenal.MODID, "spellfocus"), new ItemStack(SPELL_FOCUS, 1, EnumSpellFocus.HEALING_CIRCLE.ordinal()),
				new ItemStack(INGREDIENT, 1, EnumIngredient.FOCUS_CORE.ordinal()),
				Blocks.IRON_BARS,
				Blocks.GLOWSTONE
				).setRegistryName("magicarsenal_spellfocus_healing_circle"));
		
		r.register(new ShapelessOreRecipe(new ResourceLocation(MagicArsenal.MODID, "spellfocus"), new ItemStack(SPELL_FOCUS, 1, EnumSpellFocus.DISRUPTION.ordinal()),
				new ItemStack(INGREDIENT, 1, EnumIngredient.FOCUS_CORE.ordinal()),
				Items.BLAZE_ROD,
				Items.MAGMA_CREAM
				).setRegistryName("magicarsenal_spellfocus_disruption"));
		
		r.register(new ShapelessOreRecipe(new ResourceLocation(MagicArsenal.MODID, "spellfocus"), new ItemStack(SPELL_FOCUS, 1, EnumSpellFocus.MAGMA_BLAST.ordinal()),
				new ItemStack(INGREDIENT, 1, EnumIngredient.FOCUS_CORE.ordinal()),
				Items.IRON_INGOT,
				Items.FIRE_CHARGE
				).setRegistryName("magicarsenal_spellfocus_magma_blast"));
		
		r.register(new ShapelessOreRecipe(new ResourceLocation(MagicArsenal.MODID, "spellfocus"), new ItemStack(SPELL_FOCUS, 1, EnumSpellFocus.FROST_SHARDS.ordinal()),
				new ItemStack(INGREDIENT, 1, EnumIngredient.FOCUS_CORE.ordinal()),
				"gemDiamond",
				Items.ENDER_PEARL
				).setRegistryName("magicarsenal_spellfocus_frost_shards"));
		
		r.register(new ShapelessOreRecipe(new ResourceLocation(MagicArsenal.MODID, "spellfocus"), new ItemStack(SPELL_FOCUS, 1, EnumSpellFocus.WILL_O_WISP.ordinal()),
				new ItemStack(INGREDIENT, 1, EnumIngredient.FOCUS_CORE.ordinal()),
				Items.ENDER_EYE,
				Items.MAGMA_CREAM
				).setRegistryName("magicarsenal_spellfocus_will_o_wisp"));
		
		r.register(new ShapelessOreRecipe(new ResourceLocation(MagicArsenal.MODID, "spellfocus"), new ItemStack(SPELL_FOCUS, 1, EnumSpellFocus.CHAIN_LIGHTNING.ordinal()),
				new ItemStack(INGREDIENT, 1, EnumIngredient.FOCUS_CORE.ordinal()),
				Items.CLOCK,
				Items.GLOWSTONE_DUST
				).setRegistryName("magicarsenal_spellfocus_chain_lightning"));
	}
	
	
	
	public static Iterable<Item> itemsForModels() {
		return itemsForModels;
	}
	
	public static <T extends Item> T item(IForgeRegistry<Item> registry, T t) {
		registry.register(t);
		itemsForModels.add(t);
		return t;
	}
}
