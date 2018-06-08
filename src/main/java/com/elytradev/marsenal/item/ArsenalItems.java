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
import com.elytradev.marsenal.ArsenalConfig;
import com.elytradev.marsenal.MagicArsenal;
import com.elytradev.marsenal.block.ArsenalBlocks;
import com.elytradev.marsenal.block.EnumPoisonPlant;
import com.elytradev.marsenal.block.EnumRuneCarving;
import com.elytradev.marsenal.gui.ContainerCodex;
import com.elytradev.marsenal.potion.PotionGravityControl;
import com.elytradev.marsenal.potion.PotionInfuseLife;
import com.elytradev.marsenal.potion.PotionNightshade;
import com.elytradev.marsenal.potion.PotionStun;
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
import net.minecraftforge.event.RegistryEvent.MissingMappings.Mapping;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;

public class ArsenalItems {
	private static List<Item> itemsForModels = new ArrayList<>();
	public static ItemSpellFocus      SPELL_FOCUS = null;
	//public static ItemSpellBauble     SPELL_BAUBLE= null;
	public static ItemMagicIngredient INGREDIENT  = null;
	
	public static ItemGravityMantle   GRAVITY_MANTLE = null;
	
	public static ItemPoisonRoot      ROOT_WOLFSBANE = null;
	public static ItemPoisonRoot      ROOT_NIGHTSHADE = null;
	
	public static ItemCodex           CODEX = null;
	public static ItemChisel          CHISEL = null;
	public static ItemChisel          DIAMONDCHISEL = null;
	
	public static ItemPotionSigil       SIGIL_GRAVITY   = null;
	public static ItemBaublePotionSigil SIGIL_SPEED     = null;
	public static ItemBaublePotionSigil SIGIL_JUMPBOOST = null;
	
	//Potions and Potion Bottles
	public static PotionWolfsbane      POTION_WOLFSBANE = new PotionWolfsbane();
	public static PotionNightshade     POTION_NIGHTSHADE = new PotionNightshade();
	public static PotionInfuseLife     POTION_INFUSELIFE = new PotionInfuseLife();
	public static PotionGravityControl POTION_GRAVITYCONTROL = new PotionGravityControl();
	public static PotionStun           POTION_STUN       = new PotionStun();
	public static PotionType           POTIONTYPE_WOLFSBANE1 = null;
	public static PotionType           POTIONTYPE_WOLFSBANE2 = null;
	public static PotionType           POTIONTYPE_WOLFSBANE3 = null;
	public static PotionType           POTIONTYPE_NIGHTSHADE1 = null;
	public static PotionType           POTIONTYPE_NIGHTSHADE2 = null;
	public static PotionType           POTIONTYPE_NIGHTSHADE3 = null;
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();
		
		for(Block b : ArsenalBlocks.blocksForItems()) {
			item(r, new ItemBlockLessStupid(b));
		}
		
		SPELL_FOCUS     = item(r, new ItemSpellFocus());
		//SPELL_BAUBLE    = item(r, new ItemSpellBauble()); //Old, now-remapped. It was a bad idea to try to gang a bunch of gear up on the same ID
		INGREDIENT      = item(r, new ItemMagicIngredient());
		
		GRAVITY_MANTLE  = item(r, new ItemGravityMantle());
		
		ROOT_WOLFSBANE  = item(r, new ItemPoisonRoot("wolfsbane", ArsenalBlocks.CROP_WOLFSBANE, Blocks.FARMLAND));
		ROOT_NIGHTSHADE = item(r, new ItemPoisonRoot("nightshade", ArsenalBlocks.CROP_NIGHTSHADE, Blocks.FARMLAND));
		CODEX           = item(r, new ItemCodex());
		CHISEL          = item(r, new ItemChisel(Item.ToolMaterial.IRON));
		DIAMONDCHISEL   = item(r, new ItemChisel(Item.ToolMaterial.DIAMOND));
		DIAMONDCHISEL.lateralEffectiveness = 2;
		DIAMONDCHISEL.connectedEffectiveness = 100;
		
		SIGIL_GRAVITY   = item(r, new ItemPotionSigil("gravitycontrol", POTION_GRAVITYCONTROL, 0));
		SIGIL_SPEED     = item(r, new ItemBaublePotionSigil("speed", Potion.getPotionFromResourceLocation("minecraft:speed"), 2));
		SIGIL_JUMPBOOST = item(r, new ItemBaublePotionSigil("jumpboost", Potion.getPotionFromResourceLocation("minecraft:jump_boost"), 2));
		
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
		potion(r, POTION_GRAVITYCONTROL);
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
		
		
		
		r.register(new ShapelessOreRecipe(new ResourceLocation(MagicArsenal.MODID, "spellfocus"), new ItemStack(SPELL_FOCUS, 1, EnumSpellFocus.WILL_O_WISP.ordinal()),
				new ItemStack(INGREDIENT, 1, EnumIngredient.FOCUS_CORE.ordinal()),
				Items.ENDER_EYE,
				Items.MAGMA_CREAM
				).setRegistryName("magicarsenal_spellfocus_will_o_wisp"));
		
		r.register(new ShapedOreRecipe(new ResourceLocation(MagicArsenal.MODID, "chisel"), new ItemStack(CHISEL),
				" n",
				"s ",
				'n', "nuggetIron",
				's', "stickWood"
				).setRegistryName("magicarsenal_chisel"));
		
		r.register(new ShapedOreRecipe(new ResourceLocation(MagicArsenal.MODID, "chisel.diamond"), new ItemStack(DIAMONDCHISEL),
				"s",
				"n",
				'n', "gemDiamond",
				's', "stickWood"
				).setRegistryName("magicarsenal_chisel_diamond"));
		
		
		PotionHelper.addMix(PotionTypes.AWKWARD, Ingredient.fromStacks(EnumIngredient.PETAL_WOLFSBANE.getItem()), POTIONTYPE_WOLFSBANE1);
		PotionHelper.addMix(POTIONTYPE_WOLFSBANE1, Items.GLOWSTONE_DUST, POTIONTYPE_WOLFSBANE2);
		PotionHelper.addMix(POTIONTYPE_WOLFSBANE2, Items.GLOWSTONE_DUST, POTIONTYPE_WOLFSBANE3);
		
		PotionHelper.addMix(PotionTypes.AWKWARD, Ingredient.fromStacks(EnumIngredient.BERRY_NIGHTSHADE.getItem()), POTIONTYPE_NIGHTSHADE1);
		PotionHelper.addMix(POTIONTYPE_NIGHTSHADE1, Items.GLOWSTONE_DUST, POTIONTYPE_NIGHTSHADE2);
		PotionHelper.addMix(POTIONTYPE_NIGHTSHADE2, Items.GLOWSTONE_DUST, POTIONTYPE_NIGHTSHADE3);
		
		ItemStack nightshadePotion = new ItemStack(Items.POTIONITEM);
		PotionUtils.addPotionToItemStack(nightshadePotion, POTIONTYPE_NIGHTSHADE1);
		
		r.register(new ShapedOreRecipe(new ResourceLocation(MagicArsenal.MODID, "runestone"),
				new ItemStack(ArsenalBlocks.RUNESTONE1, 8, 0),
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
		
		if (ArsenalConfig.local().general.simpleBookRecipe) { //Recipes are done far too early to sync with the server
			r.register(new ShapelessOreRecipe(new ResourceLocation(MagicArsenal.MODID, "simplecodex"),
				new ItemStack(CODEX, 1, 9000),
				new ItemStack(Items.BOOK),
				"dyePurple"
			).setRegistryName("simplecodex"));
		} else {
			r.register(new ShapelessOreRecipe(new ResourceLocation(MagicArsenal.MODID, "initialcodex"),
					new ItemStack(CODEX),
					EnumIngredient.PORTAL_SEARED_TOME.getItem(),
					EnumIngredient.PORTAL_SEARED_TOME.getItem()
					).setRegistryName("initalcodex"));
			
			r.register(new CodexRecipe());
		}
		
		ItemStack wolfsbanePotion = new ItemStack(Items.POTIONITEM);
		PotionUtils.addPotionToItemStack(wolfsbanePotion, POTIONTYPE_WOLFSBANE1);
		IngredientNBT wolfsbaneIngredient = new IngredientNBT(wolfsbanePotion);
		
		r.register(new ShapelessOreRecipe(new ResourceLocation(MagicArsenal.MODID, "ward.uncarved"),
				new ItemStack(ArsenalBlocks.STELE_UNCARVED),
				EnumRuneCarving.NONE.getUnwardedItem(),
				wolfsbaneIngredient
				).setRegistryName("ward.uncarved"));
		
		r.register(new ShapelessOreRecipe(new ResourceLocation(MagicArsenal.MODID, "ward.kenaz"),
				new ItemStack(ArsenalBlocks.STELE_KENAZ),
				Items.ENCHANTED_BOOK,
				Blocks.TORCH,
				wolfsbaneIngredient,
				EnumRuneCarving.KENAZ.getUnwardedItem()
				).setRegistryName("ward.kenaz"));
		
		
		//Block skullIngredient = Blocks.SKULL;
		ItemStack skullIngredient = new ItemStack(Items.SKULL, 1, OreDictionary.WILDCARD_VALUE);
		//Ingredient skullIngredient = Ingredient.fromStacks(new ItemStack(Items.SKULL));
		//Ingredient skullIngredient = Ingredient.fromItem(Items.SKULL);
		
		r.register(new ShapelessOreRecipe(new ResourceLocation(MagicArsenal.MODID, "ward.wunjo"),
				new ItemStack(ArsenalBlocks.STELE_WUNJO),
				skullIngredient,
				new ItemStack(Items.COOKIE),
				wolfsbaneIngredient,
				EnumRuneCarving.WUNJO.getUnwardedItem()
				).setRegistryName("ward.wunjo"));
		
		r.register(new ShapelessOreRecipe(new ResourceLocation(MagicArsenal.MODID, "ward.berkano"),
				new ItemStack(ArsenalBlocks.STELE_BERKANO),
				new ItemStack(Blocks.SAPLING, 1, OreDictionary.WILDCARD_VALUE),
				new ItemStack(Blocks.VINE),
				wolfsbaneIngredient,
				EnumRuneCarving.BERKANO.getUnwardedItem()
				).setRegistryName("ward.berkano"));
		
		r.register(new ShapelessOreRecipe(new ResourceLocation(MagicArsenal.MODID, "ward.fehu"),
				new ItemStack(ArsenalBlocks.STELE_FEHU),
				new ItemStack(Items.DIAMOND),
				new ItemStack(Items.EMERALD),
				wolfsbaneIngredient,
				EnumRuneCarving.FEHU.getUnwardedItem()
				).setRegistryName("ward.fehu"));
		
		r.register(new ShapelessOreRecipe(new ResourceLocation(MagicArsenal.MODID, "ward.raidho"),
				new ItemStack(ArsenalBlocks.STELE_RAIDHO),
				new ItemStack(Items.FEATHER),
				new ItemStack(Items.ENDER_PEARL),
				wolfsbaneIngredient,
				EnumRuneCarving.RAIDHO.getUnwardedItem()
				).setRegistryName("ward.raidho"));
		
		r.register(new ShapelessOreRecipe(new ResourceLocation(MagicArsenal.MODID, "ward.jera"),
				new ItemStack(ArsenalBlocks.STELE_JERA),
				new ItemStack(Items.MELON),
				new ItemStack(Items.CARROT),
				wolfsbaneIngredient,
				EnumRuneCarving.JERA.getUnwardedItem()
				).setRegistryName("ward.jera"));
		
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
		
		RunicAltarRecipes.register(new ShapelessAltarRecipe(new ItemStack(SPELL_FOCUS, 1, EnumSpellFocus.CHAIN_LIGHTNING.ordinal()),
				100, 3600,
				ItemIngredient.of(new ItemStack(SPELL_FOCUS, 1, EnumSpellFocus.LIT_BOLT.ordinal())),
				ItemIngredient.of(Blocks.GLOWSTONE),
				ItemIngredient.of(Items.CLOCK),
				ItemIngredient.of(Items.EMERALD)
				));
		
		RunicAltarRecipes.register(new ShapelessAltarRecipe(new ItemStack(SPELL_FOCUS, 1, EnumSpellFocus.LIT_BOLT.ordinal()),
				10, 1800,
				ItemIngredient.of(new ItemStack(INGREDIENT, 1, EnumIngredient.FOCUS_CORE.ordinal())),
				ItemIngredient.of(Blocks.IRON_BARS),
				ItemIngredient.of(Items.GLOWSTONE_DUST)
				));
		
		RunicAltarRecipes.register(new ShapelessAltarRecipe(new ItemStack(SPELL_FOCUS, 1, EnumSpellFocus.STUN.ordinal()),
				1, 1800,
				ItemIngredient.of(new ItemStack(INGREDIENT, 1, EnumIngredient.FOCUS_CORE.ordinal())),
				ItemIngredient.of(Blocks.SOUL_SAND),
				ItemIngredient.of(Items.SLIME_BALL),
				ItemIngredient.of(Items.STICK)
				));
		
		RunicAltarRecipes.register(new ShapelessAltarRecipe(new ItemStack(SPELL_FOCUS, 1, EnumSpellFocus.FROST_SHARDS.ordinal()),
				1, 1800,
				ItemIngredient.of(new ItemStack(INGREDIENT, 1, EnumIngredient.FOCUS_CORE.ordinal())),
				ItemIngredient.of(Blocks.SNOW),
				ItemIngredient.of(Blocks.IRON_BARS)
				));
		
		RunicAltarRecipes.register(new ShapelessAltarRecipe(new ItemStack(ArsenalBlocks.RADIANT_BEACON),
				100, 6400,
				ItemIngredient.of(Blocks.BEACON),
				ItemIngredient.of(Blocks.DIAMOND_BLOCK),
				ItemIngredient.of(Blocks.DIAMOND_BLOCK),
				ItemIngredient.of(Blocks.DIAMOND_BLOCK),
				ItemIngredient.of(Blocks.DIAMOND_BLOCK),
				ItemIngredient.of(ArsenalBlocks.ROSETTA_STONE)
				));
		
		RunicAltarRecipes.register(new ShapelessAltarRecipe(new ItemStack(SIGIL_GRAVITY),
				100, 3200,
				ItemIngredient.of(new ItemStack(INGREDIENT, 1, EnumIngredient.FOCUS_CORE.ordinal())),
				ItemIngredient.of(Items.GOLD_NUGGET),
				ItemIngredient.of(Items.STRING),
				
				ItemIngredient.of(Items.SHULKER_SHELL),
				ItemIngredient.of(Items.SHULKER_SHELL),
				ItemIngredient.of(Items.FEATHER)
				));
		
		ItemStack speedPotion = new ItemStack(Items.POTIONITEM);
		PotionUtils.addPotionToItemStack(speedPotion, PotionTypes.STRONG_SWIFTNESS);
		
		RunicAltarRecipes.register(new ShapelessAltarRecipe(new ItemStack(SIGIL_SPEED),
				100, 3200,
				ItemIngredient.of(new ItemStack(INGREDIENT, 1, EnumIngredient.FOCUS_CORE.ordinal())),
				ItemIngredient.of(Items.GOLD_NUGGET),
				ItemIngredient.of(Items.STRING),
				
				ItemIngredient.of(speedPotion),
				ItemIngredient.of(Items.SUGAR),
				ItemIngredient.of(Items.SUGAR)
				));
		
		RunicAltarRecipes.register(new ShapelessAltarRecipe(new ItemStack(SIGIL_JUMPBOOST),
				100, 3200,
				ItemIngredient.of(new ItemStack(INGREDIENT, 1, EnumIngredient.FOCUS_CORE.ordinal())),
				ItemIngredient.of(Items.GOLD_NUGGET),
				ItemIngredient.of(Items.STRING),
				
				ItemIngredient.of(Items.RABBIT_FOOT),
				ItemIngredient.of(Items.CARROT),
				ItemIngredient.of(Items.CARROT)
				));
		/*//Old recipe
		r.register(new ShapelessOreRecipe(new ResourceLocation(MagicArsenal.MODID, "spellfocus"), new ItemStack(SPELL_FOCUS, 1, EnumSpellFocus.CHAIN_LIGHTNING.ordinal()),
				new ItemStack(INGREDIENT, 1, EnumIngredient.FOCUS_CORE.ordinal()),
				Items.CLOCK,
				Items.GLOWSTONE_DUST
				).setRegistryName("magicarsenal_spellfocus_chain_lightning"));*/
	}
	
	
	
	public static Iterable<Item> itemsForModels() {
		System.out.println(itemsForModels.toString());
		return itemsForModels;
	}
	
	public static <T extends Item> T item(IForgeRegistry<Item> registry, T t) {
		registry.register(t);
		itemsForModels.add(t);
		return t;
	}
	
	@SubscribeEvent
	public static void missingMappings(RegistryEvent.MissingMappings<Item> event) {
		
		for (Mapping<Item> mapping : event.getMappings()) {
			
			String mod = mapping.key.getResourceDomain();
			if (!mod.equals("magicarsenal")) continue;
			
			String oldid = mapping.key.getResourcePath();
			
			
			
			switch(oldid) {
			case "spellbauble":
				mapping.remap(GRAVITY_MANTLE);
				break;
			default:
				//We don't know how to remap this.
				break;
			}
		}
	}
}
