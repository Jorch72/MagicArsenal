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

import com.elytradev.concrete.recipe.ItemIngredient;
import com.elytradev.marsenal.MagicArsenal;
import com.elytradev.marsenal.block.ArsenalBlocks;
import com.elytradev.marsenal.block.EnumPoisonPlant;
import com.elytradev.marsenal.block.EnumRuneCarving;
import com.elytradev.marsenal.gui.ContainerCodex;
import com.elytradev.marsenal.potion.PotionInfuseLife;
import com.elytradev.marsenal.potion.PotionNightshade;
import com.elytradev.marsenal.potion.PotionWolfsbane;
import com.elytradev.marsenal.recipe.RunicAltarRecipes;
import com.elytradev.marsenal.recipe.ShapelessAltarRecipe;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;

public class ArsenalItems {
	private static List<Item> itemsForModels = new ArrayList<>();
	public static ItemSpellFocus      SPELL_FOCUS = null;
	public static ItemSpellBauble     SPELL_BAUBLE= null;
	public static ItemMagicIngredient INGREDIENT  = null;
	public static ItemPoisonRoot      ROOT_WOLFSBANE = null;
	public static ItemPoisonRoot      ROOT_NIGHTSHADE = null;
	
	public static ItemCodex         CODEX = null;
	
	public static PotionWolfsbane   POTION_WOLFSBANE = new PotionWolfsbane();
	public static PotionNightshade  POTION_NIGHTSHADE = new PotionNightshade();
	public static PotionInfuseLife  POTION_INFUSELIFE = new PotionInfuseLife();
	public static PotionType        POTIONTYPE_WOLFSBANE1 = null;
	public static PotionType        POTIONTYPE_WOLFSBANE2 = null;
	public static PotionType        POTIONTYPE_WOLFSBANE3 = null;
	public static PotionType        POTIONTYPE_NIGHTSHADE1 = null;
	public static PotionType        POTIONTYPE_NIGHTSHADE2 = null;
	public static PotionType        POTIONTYPE_NIGHTSHADE3 = null;
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();
		
		for(Block b : ArsenalBlocks.blocksForItems()) {
			item(r, new ItemBlockLessStupid(b));
		}
		
		SPELL_FOCUS = item(r, new ItemSpellFocus());
		SPELL_BAUBLE= item(r, new ItemSpellBauble());
		INGREDIENT  = item(r, new ItemMagicIngredient());
		ROOT_WOLFSBANE  = item(r, new ItemPoisonRoot("wolfsbane", ArsenalBlocks.CROP_WOLFSBANE, Blocks.FARMLAND));
		ROOT_NIGHTSHADE = item(r, new ItemPoisonRoot("nightshade", ArsenalBlocks.CROP_NIGHTSHADE, Blocks.FARMLAND)); //FIXME: Switch to nightshade when the plant is done
		//POISON_VIAL = item(r, new ItemPoisonVial());
		CODEX       = item(r, new ItemCodex());
		
		ArsenalBlocks.CROP_WOLFSBANE.setHarvestItems(EnumPoisonPlant.WOLFSBANE.getRoot(), EnumIngredient.PETAL_WOLFSBANE.getItem());
		ArsenalBlocks.CROP_NIGHTSHADE.setHarvestItems(EnumPoisonPlant.NIGHTSHADE.getRoot(), EnumIngredient.BERRY_NIGHTSHADE.getItem());
		
		OreDictionary.registerOre("dyePurple", EnumIngredient.PETAL_WOLFSBANE.getItem());
		OreDictionary.registerOre("dyeBlack", EnumIngredient.BERRY_NIGHTSHADE.getItem());
		
		ContainerCodex.initCodex(); //Once items are done this should be fired *immediately*.
		CODEX.updatePages();
	}
	
	@SubscribeEvent
	public static void onRegisterPotions(RegistryEvent.Register<Potion> event) {
		IForgeRegistry<Potion> r = event.getRegistry();
		
		potion(r, POTION_WOLFSBANE);
		potion(r, POTION_NIGHTSHADE);
		potion(r, POTION_INFUSELIFE);
		EnumPoisonPlant.WOLFSBANE.registerPotion(POTION_WOLFSBANE);
		EnumPoisonPlant.NIGHTSHADE.registerPotion(POTION_NIGHTSHADE);
	}
	
	public static <T extends Potion> T potion(IForgeRegistry<Potion> registry, T t) {
		registry.register(t);
		return t;
	}
	
	@SubscribeEvent
	public static void onRegisterPotionTypes(RegistryEvent.Register<PotionType> event) {
		IForgeRegistry<PotionType> r = event.getRegistry();
		
		POTIONTYPE_WOLFSBANE1 = potionType(r, POTION_WOLFSBANE, 20*45, 0);
		POTIONTYPE_WOLFSBANE2 = potionType(r, POTION_WOLFSBANE, 20*40, 1);
		POTIONTYPE_WOLFSBANE3 = potionType(r, POTION_WOLFSBANE, 20*35, 2);
		
		POTIONTYPE_NIGHTSHADE1 = potionType(r, POTION_NIGHTSHADE, 20*45, 0);
		POTIONTYPE_NIGHTSHADE2 = potionType(r, POTION_NIGHTSHADE, 20*40, 1);
		POTIONTYPE_NIGHTSHADE3 = potionType(r, POTION_NIGHTSHADE, 20*35, 2);
	}
	
	public static PotionType potionType(IForgeRegistry<PotionType> registry, Potion potion, int duration, int amplifier) {
		PotionType result = new PotionType(new PotionEffect(potion, duration, amplifier));
		result.setRegistryName(potion.getRegistryName().getResourceDomain(), potion.getRegistryName().getResourcePath()+"."+(amplifier+1));
		registry.register(result);
		return result;
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
		
		PotionHelper.addMix(PotionTypes.AWKWARD, Ingredient.fromStacks(EnumIngredient.PETAL_WOLFSBANE.getItem()), POTIONTYPE_WOLFSBANE1);
		PotionHelper.addMix(POTIONTYPE_WOLFSBANE1, Items.GLOWSTONE_DUST, POTIONTYPE_WOLFSBANE2);
		PotionHelper.addMix(POTIONTYPE_WOLFSBANE2, Items.GLOWSTONE_DUST, POTIONTYPE_WOLFSBANE3);
		
		PotionHelper.addMix(PotionTypes.AWKWARD, Ingredient.fromStacks(EnumIngredient.BERRY_NIGHTSHADE.getItem()), POTIONTYPE_NIGHTSHADE1);
		PotionHelper.addMix(POTIONTYPE_NIGHTSHADE1, Items.GLOWSTONE_DUST, POTIONTYPE_NIGHTSHADE2);
		PotionHelper.addMix(POTIONTYPE_NIGHTSHADE2, Items.GLOWSTONE_DUST, POTIONTYPE_NIGHTSHADE3);
		
		ItemStack nightshadePotion = new ItemStack(Items.POTIONITEM);
		PotionUtils.addPotionToItemStack(nightshadePotion, POTIONTYPE_NIGHTSHADE1);
		
		r.register(new ShapedOreRecipe(new ResourceLocation(MagicArsenal.MODID, "runestone"),
				new ItemStack(ArsenalBlocks.RUNESTONE1, 8, 1),
				"sss", "sps", "sss",
				's', "stone",
				'p', new IngredientNBT(nightshadePotion)
				).setRegistryName("magicarsenal_runestone"));
		
		for(int i=1; i<EnumRuneCarving.values().length; i++) {
			EnumRuneCarving priorCarving = EnumRuneCarving.values()[i-1];
			EnumRuneCarving curCarving = EnumRuneCarving.values()[i];
			ItemStack priorItem = priorCarving.getUnwardedItem();
			ItemStack curItem = curCarving.getUnwardedItem();
			r.register(new ShapelessOreRecipe(new ResourceLocation(MagicArsenal.MODID, "runecarving"),
					curItem,
					priorItem
					).setRegistryName("runecarving_loop_"+i));
		}
		ItemStack firstItem = EnumRuneCarving.values()[0].getUnwardedItem();
		ItemStack lastItem = EnumRuneCarving.values()[EnumRuneCarving.values().length-1].getUnwardedItem();
		r.register(new ShapelessOreRecipe(new ResourceLocation(MagicArsenal.MODID, "runecarving"),
				firstItem,
				lastItem
				).setRegistryName("runecarving_loop_"+0));
		
		r.register(new ShapelessOreRecipe(new ResourceLocation(MagicArsenal.MODID, "initialcodex"),
				new ItemStack(CODEX),
				EnumIngredient.PORTAL_SEARED_TOME.getItem(),
				EnumIngredient.PORTAL_SEARED_TOME.getItem()
				).setRegistryName("initalcodex"));
		
		r.register(new CodexRecipe());
		
		
		ItemStack wolfsbanePotion = new ItemStack(Items.POTIONITEM);
		PotionUtils.addPotionToItemStack(wolfsbanePotion, POTIONTYPE_WOLFSBANE1);
		IngredientNBT wolfsbaneIngredient = new IngredientNBT(wolfsbanePotion);
		
		r.register(new ShapelessOreRecipe(new ResourceLocation(MagicArsenal.MODID, "ward.kenaz"),
				new ItemStack(ArsenalBlocks.STELE_KENAZ),
				Items.ENCHANTED_BOOK,
				Blocks.TORCH,
				wolfsbaneIngredient,
				EnumRuneCarving.KENAZ.getUnwardedItem()
				).setRegistryName("ward.kenaz"));
		
		r.register(new ShapedOreRecipe(new ResourceLocation(MagicArsenal.MODID, "runicaltar"),
				new ItemStack(ArsenalBlocks.RUNIC_ALTAR),
				"s s", "sss", "rrr",
				's', ArsenalBlocks.STELE_UNCARVED,
				'r', EnumRuneCarving.NONE.getUnwardedItem()
				).setRegistryName("magicarsenal_runicaltar"));
		
		RunicAltarRecipes.register(new ShapelessAltarRecipe(new ItemStack(ArsenalBlocks.ROSETTA_STONE),
				1, 200,
				ItemIngredient.of(Blocks.BOOKSHELF),
				ItemIngredient.of(Blocks.BOOKSHELF),
				ItemIngredient.of(Blocks.BOOKSHELF),
				ItemIngredient.of(Blocks.BOOKSHELF),
				ItemIngredient.of(Blocks.BOOKSHELF),
				ItemIngredient.of(Blocks.BOOKSHELF)
				));
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
