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

package com.elytradev.marsenal.client;

import com.elytradev.marsenal.ArsenalConfig;
import com.elytradev.marsenal.MagicArsenal;
import com.elytradev.marsenal.Proxy;
import com.elytradev.marsenal.capability.IMagicResources;
import com.elytradev.marsenal.capability.impl.MagicResources;
import com.elytradev.marsenal.item.ArsenalItems;
import com.elytradev.marsenal.item.IMetaItemModel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends Proxy {
	public static MagicResources scrambleTargets = new MagicResources();
	
	@Override
	public void preInit() {
		
	}
	
	@SubscribeEvent
	public void registerItemModels(ModelRegistryEvent event) {
		for(Item item : ArsenalItems.itemsForModels()) {
			if (item instanceof IMetaItemModel) {
				String[] models = ((IMetaItemModel)item).getModelLocations();
				for(int i=0; i<models.length; i++) {
					ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation(new ResourceLocation(MagicArsenal.MODID, models[i]), "inventory"));
				}
			} else {
				NonNullList<ItemStack> variantList = NonNullList.create();
				item.getSubItems(MagicArsenal.TAB_MARSENAL, variantList);
				ResourceLocation loc = Item.REGISTRY.getNameForObject(item);
				
				if (variantList.size()==1) {
					ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(loc, "inventory"));
				} else {
					for(ItemStack subItem : variantList) {
						ModelLoader.setCustomModelResourceLocation(item, subItem.getItemDamage(), new ModelResourceLocation(loc, "variant="+subItem.getItemDamage()));
					}
				}
			}
		}
	}
	
	private static void drawBar(int x, int y, int width, int height, int cur, int total, int bg, int fg) {
		float percent = cur/(float)total;
		int barWidth = (int)(width*percent);
		if (barWidth>width) barWidth = width;
		Gui.drawRect(x, y, x+width, y+height, bg);
		if (barWidth>0) {
			Gui.drawRect(x, y, x+barWidth, y+height, fg);
		}
	}
	
	@SubscribeEvent
	public void onRenderScreen(RenderGameOverlayEvent.Post evt) {
		if (!Minecraft.getMinecraft().player.hasCapability(MagicArsenal.CAPABILTIY_MAGIC_RESOURCES, null)) return;
		IMagicResources res = Minecraft.getMinecraft().player.getCapability(MagicArsenal.CAPABILTIY_MAGIC_RESOURCES, null);
		
		if (evt.getType()==ElementType.CROSSHAIRS) {
			int width = evt.getResolution().getScaledWidth();
			int height = evt.getResolution().getScaledHeight();
			int centerX = width/2;
			int centerY = height/2;
			if (res.getGlobalCooldown()>0) {
				drawBar(centerX-15, centerY+20, 32, 2, res.getGlobalCooldown(), res.getMaxCooldown(), 0xFF333333, 0xFF777777);
			}
			if (res.getResource(IMagicResources.RESOURCE_STAMINA, ArsenalConfig.get().resources.maxStamina)<ArsenalConfig.get().resources.maxStamina) {
				drawBar(centerX-15, centerY+23, 32, 2, res.getResource(IMagicResources.RESOURCE_STAMINA, ArsenalConfig.get().resources.maxStamina), ArsenalConfig.get().resources.maxStamina, 0xFF333333, 0xFF333399);
			}
			
			/*if (res.getResource(IMagicResources.RESOURCE_RAGE, ArsenalConfig.get().resources.maxRage)<ArsenalConfig.get().resources.maxRage) {
				//TODO: Figure out if the player's holding a totem that cares
				drawBar(centerX-15, centerY+26, 32, 2, res.getResource(IMagicResources.RESOURCE_RAGE, ArsenalConfig.get().resources.maxRage), ArsenalConfig.get().resources.maxRage, 0xFF333333, 0xFF993333);
			}*/
		}
	}
	
	
	public void registerItemModel(Item item) {
		
	}
}
