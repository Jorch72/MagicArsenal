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

import java.util.ArrayList;
import java.util.Locale;

import com.elytradev.marsenal.block.ArsenalBlocks;
import com.elytradev.marsenal.capability.IMagicResources;
import com.elytradev.marsenal.gui.ContainerRunicAltar;
import com.elytradev.marsenal.item.ArsenalItems;
import com.elytradev.marsenal.item.EnumSpellFocus;
import com.elytradev.marsenal.magic.EnumElement;
import com.elytradev.marsenal.recipe.RunicAltarRecipes;
import com.elytradev.marsenal.recipe.ShapelessAltarRecipe;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.gui.elements.DrawableResource;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;

@SuppressWarnings("deprecation")
@JEIPlugin
public class JEICompat implements IModPlugin {
	
	//@Override
	//public void registerIngredients(IModIngredientRegistration registry) {
	//	registry.register(Integer.class, allIngredients, ingredientHelper, ingredientRenderer);
	//}
	
	@Override
	public void register(IModRegistry registry) {
		
		registry.addIngredientInfo(
				new ItemStack(ArsenalItems.SPELL_FOCUS, 1, EnumSpellFocus.DRAIN_LIFE.ordinal()),
				ItemStack.class,
				spellInfo(
						"drainLife",
						EnumElement.UNDEATH, EnumElement.NATURE,
						IMagicResources.RESOURCE_STAMINA
				));
		
		registry.addIngredientInfo(
				new ItemStack(ArsenalItems.SPELL_FOCUS, 1, EnumSpellFocus.HEALING_WAVE.ordinal()),
				ItemStack.class,
				spellInfo(
						"healingWave",
						EnumElement.HOLY, EnumElement.NATURE,
						IMagicResources.RESOURCE_STAMINA
				));
		
		registry.addIngredientInfo(
				new ItemStack(ArsenalItems.SPELL_FOCUS, 1, EnumSpellFocus.HEALING_CIRCLE.ordinal()),
				ItemStack.class,
				spellInfo(
						"healingCircle",
						EnumElement.HOLY, EnumElement.AIR,
						IMagicResources.RESOURCE_STAMINA
				));
		
		registry.addIngredientInfo(
				new ItemStack(ArsenalItems.SPELL_FOCUS, 1, EnumSpellFocus.RECOVERY.ordinal()),
				ItemStack.class,
				spellInfo(
						"recovery",
						EnumElement.ARCANE, EnumElement.NATURE,
						IMagicResources.RESOURCE_STAMINA
				));
		
		registry.addIngredientInfo(
				new ItemStack(ArsenalItems.SPELL_FOCUS, 1, EnumSpellFocus.OBLATION.ordinal()),
				ItemStack.class,
				spellInfo(
						"oblation",
						EnumElement.CHAOS, EnumElement.NATURE,
						IMagicResources.RESOURCE_STAMINA
				));
		
		registry.addIngredientInfo(
				new ItemStack(ArsenalItems.SPELL_FOCUS, 1, EnumSpellFocus.DISRUPTION.ordinal()),
				ItemStack.class,
				spellInfo(
						"disruption",
						EnumElement.ARCANE, EnumElement.FIRE,
						IMagicResources.RESOURCE_STAMINA
				));
		
		registry.addIngredientInfo(
				new ItemStack(ArsenalItems.SPELL_FOCUS, 1, EnumSpellFocus.MAGMA_BLAST.ordinal()),
				ItemStack.class,
				spellInfo(
						"magmaBlast",
						EnumElement.ARCANE, EnumElement.FIRE,
						IMagicResources.RESOURCE_STAMINA
				));
		
		registry.addIngredientInfo(
				new ItemStack(ArsenalItems.SPELL_FOCUS, 1, EnumSpellFocus.FROST_SHARDS.ordinal()),
				ItemStack.class,
				spellInfo(
						"frostShards",
						EnumElement.ARCANE, EnumElement.FROST,
						IMagicResources.RESOURCE_STAMINA
				));
		
		registry.addIngredientInfo(
				new ItemStack(ArsenalItems.SPELL_FOCUS, 1, EnumSpellFocus.WILL_O_WISP.ordinal()),
				ItemStack.class,
				spellInfo(
						"willOWisp",
						EnumElement.UNDEATH, EnumElement.FIRE,
						IMagicResources.RESOURCE_STAMINA
				));
		
		registry.addIngredientInfo(
				new ItemStack(ArsenalItems.SPELL_FOCUS, 1, EnumSpellFocus.CHAIN_LIGHTNING.ordinal()),
				ItemStack.class,
				spellInfo(
						"chainLightning",
						EnumElement.HOLY, EnumElement.AIR,
						IMagicResources.RESOURCE_STAMINA
				));
		
		registry.addIngredientInfo(
				new ItemStack(ArsenalBlocks.RUNIC_ALTAR),
				ItemStack.class,
				"info.magicarsenal.altar"
				);
		
		registry.addIngredientInfo(
				new ItemStack(ArsenalBlocks.STELE_UNCARVED),
				ItemStack.class,
				"info.magicarsenal.stele.uncarved"
				);
		
		registry.addIngredientInfo(
				new ItemStack(ArsenalBlocks.STELE_RAIDHO),
				ItemStack.class,
				"info.magicarsenal.stele.raidho"
				);
		
		registry.addIngredientInfo(
				new ItemStack(ArsenalBlocks.STELE_KENAZ),
				ItemStack.class,
				"info.magicarsenal.stele.kenaz"
				);
		
		registry.addIngredientInfo(
				new ItemStack(ArsenalBlocks.STELE_BERKANO),
				ItemStack.class,
				"info.magicarsenal.stele.berkano"
				);
		
		registry.addIngredientInfo(
				new ItemStack(ArsenalBlocks.STELE_WUNJO),
				ItemStack.class,
				"info.magicarsenal.stele.wunjo"
				);
		
		registry.addIngredientInfo(
				new ItemStack(ArsenalBlocks.ROSETTA_STONE),
				ItemStack.class,
				"info.magicarsenal.rosettastone");
		
		registry.addRecipeCatalyst(new ItemStack(ArsenalBlocks.RUNIC_ALTAR), "magicarsenal:altar");
		
		registry.addRecipes(RunicAltarRecipes.allRecipes(), "magicarsenal:altar");
		registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerRunicAltar.class, "magicarsenal:altar", 0, 6, 7, 36);
		
	}
	
	private static String[] spellInfo(String spell, EnumElement elem, EnumElement elem2, ResourceLocation resource) {
		ArrayList<String> result = new ArrayList<>();
		result.add(format("info.magicarsenal.label.spell", local("spell.magicarsenal."+spell)));
		result.add(format("info.magicarsenal.label.elements", element(elem), element(elem2)));
		if (resource!=null) {
			result.add(format("info.magicarsenal.label.resource", resource(resource)));
		}
		
		String effectText = local("spell.magicarsenal."+spell+".desc");
		if (effectText!=null && !effectText.equals("spell.magicarsenal."+spell+".desc")) {
			result.add("");
			result.add(format("info.magicarsenal.label.effect", effectText));
		}
		
		return result.toArray(new String[result.size()]);
	}
	
	private static String local(String key) {
		return I18n.translateToLocal(key);
	}
	
	private static String format(String key, Object... args) {
		return I18n.translateToLocalFormatted(key, args);
	}
	
	private static String element(EnumElement elem) {
		return local("info.magicarsenal.element."+ elem.name().toLowerCase(Locale.ENGLISH));
	}
	
	private static String resource(ResourceLocation resource) {
		String domain = resource.getResourceDomain();
		String resourceName = resource.getResourcePath();
		return local("resource."+domain+"."+resourceName);
	}

	public void registerCategories(IRecipeCategoryRegistration registry) {
		registry.addRecipeCategories(new IRecipeCategory<ShapelessAltarRecipe>() {
			protected int radiance;
			protected int emc;
			
			@Override
			public String getUid() {
				return "magicarsenal:altar";
			}

			@Override
			public String getTitle() {
				return I18n.translateToLocal("tile.magicarsenal.altar.name");
			}

			@Override
			public String getModName() {
				return "magicarsenal";
			}

			@Override
			public IDrawable getBackground() {
				return new DrawableResource(new ResourceLocation("magicarsenal", "textures/guis/slot.eldritch.jei.png"), 0, 0, 184, 80, 0, 0, 0, 0, 184, 80);
			}

			@Override
			public void setRecipe(IRecipeLayout recipeLayout, ShapelessAltarRecipe recipeWrapper, IIngredients ingredients) {
				int leftMargin = 38;
				int topMargin = 4;
				recipeLayout.getItemStacks().init(0, true, leftMargin + 18*1 + 3, topMargin + 18*0 + 0);
				recipeLayout.getItemStacks().init(1, true, leftMargin + 18*3 + 1, topMargin + 18*0 + 0);
				recipeLayout.getItemStacks().init(2, true, leftMargin + 18*5 - 2, topMargin + 18*1 + 0);
				recipeLayout.getItemStacks().init(3, true, leftMargin + 18*4 - 2, topMargin + 18*3 - 3);
				recipeLayout.getItemStacks().init(4, true, leftMargin + 18*2 + 0, topMargin + 18*3 - 3);
				recipeLayout.getItemStacks().init(5, true, leftMargin + 18*0 + 3, topMargin + 18*2 - 3);
				
				recipeLayout.getItemStacks().init(6, false, leftMargin + 18*2 + 8, topMargin + 18*1 + 7);
				
				recipeLayout.getItemStacks().set(ingredients);
				
				recipeLayout.setShapeless();
				
				recipeLayout.setRecipeTransferButton(184-16, topMargin + 18*4);
				
				radiance = recipeWrapper.getRadiance();
				emc = recipeWrapper.getEMC();
			}
			
			@Override
			public void drawExtras(Minecraft minecraft) {
				IRecipeCategory.super.drawExtras(minecraft);
				
				//TODO FIXME: Localize
				//TODO: Show a radiance bar instead of a number?
				minecraft.fontRenderer.drawString("Radiance: "+radiance, 10, 18*6 + 4, 0xFF444444);
				minecraft.fontRenderer.drawString("EMC: "+emc, 10, 18*7 + 4, 0xFF444444);
				
			}
			
		});
	}
}
