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

import java.util.HashSet;
import java.util.Set;

import com.elytradev.marsenal.ArsenalConfig;
import com.elytradev.marsenal.MagicArsenal;
import com.elytradev.marsenal.Proxy;
import com.elytradev.marsenal.capability.IMagicResources;
import com.elytradev.marsenal.capability.impl.MagicResources;
import com.elytradev.marsenal.entity.EntityFrostShard;
import com.elytradev.marsenal.entity.EntityWillOWisp;
import com.elytradev.marsenal.item.ArsenalItems;
import com.elytradev.marsenal.item.IMetaItemModel;
import com.elytradev.marsenal.item.ISpellFocus;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ClientProxy extends Proxy {
	public static MagicResources scrambleTargets = new MagicResources();
	
	@Override
	public void preInit() {
		Emitter.register("healingSphere", HealingSphereEmitter.class);
		Emitter.register("drainLife", DrainLifeEmitter.class);
		Emitter.register("infuseLife", InfuseLifeEmitter.class);
		Emitter.register("disruption", DisruptionEmitter.class);
		Emitter.register("spellGather", SpellGatherEmitter.class);
		Emitter.register("magmaBlast", MagmaBlastEmitter.class);
		Emitter.register("lightning", LightningEmitter.class);
		
		RenderingRegistry.registerEntityRenderingHandler(EntityFrostShard.class, RenderFrostShard::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityWillOWisp.class,  RenderWillOWisp::new);
	}
	
	@Override
	public void init() {
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
	
	private static void checkForResources(EntityLivingBase caster, ItemStack stack, Set<ResourceLocation> set) {
		if (stack==null || stack.isEmpty()) return;
		if (stack.getItem() instanceof ISpellFocus) {
			((ISpellFocus)stack.getItem()).addResources(caster, stack, set);
		}
	}
	
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) return;
		if (Minecraft.getMinecraft().player==null) return;
		
		ParticleEmitters.tick();
		
		if (!Minecraft.getMinecraft().player.hasCapability(MagicArsenal.CAPABILTIY_MAGIC_RESOURCES, null)) return;
		
		IMagicResources res = Minecraft.getMinecraft().player.getCapability(MagicArsenal.CAPABILTIY_MAGIC_RESOURCES, null);
		//Scramble towards each resource value
		ClientProxy.scrambleTargets.forEach((resource, val)->{
			int oldValue = res.getResource(resource, 0);
			int dist = Math.abs(val-oldValue);
			dist /= 2;
			if (dist<1) dist=1;
			
			
			if (val>oldValue) {
				res.set(resource, oldValue+dist);
			} else if (val<oldValue) {
				res.set(resource, oldValue-dist);
			}
		});
		
		//Scramble towards the GCD target
		res.setMaxCooldown(ClientProxy.scrambleTargets.getMaxCooldown());
		int cur = res.getGlobalCooldown();
		int target = ClientProxy.scrambleTargets.getGlobalCooldown();
		int delta = Math.abs(cur - target) / 2;
		if (delta<1) delta=1;
		if (cur<target) {
			if (res instanceof MagicResources) {
				((MagicResources)res)._setGlobalCooldown(cur+delta);
			} else {
				res.setGlobalCooldown(cur+delta);
			}
		} else if (cur>target) {
			res.reduceGlobalCooldown(delta);
		}
		
	}
	
	@SubscribeEvent
	public void onRenderScreen(RenderGameOverlayEvent.Post evt) {
		EntityLivingBase player = Minecraft.getMinecraft().player;
		if (!player.hasCapability(MagicArsenal.CAPABILTIY_MAGIC_RESOURCES, null)) return;
		
		if (evt.getType()==ElementType.CROSSHAIRS) {
			
			IMagicResources res = player.getCapability(MagicArsenal.CAPABILTIY_MAGIC_RESOURCES, null);
			HashSet<ResourceLocation> relevantResources = new HashSet<ResourceLocation>();
			
			checkForResources(player, player.getHeldItemMainhand(), relevantResources);
			checkForResources(player, player.getHeldItemOffhand(), relevantResources);
			
			int width = evt.getResolution().getScaledWidth();
			int height = evt.getResolution().getScaledHeight();
			int centerX = width/2;
			int centerY = height/2;
			if (res.getGlobalCooldown()>0) {
				drawBar(centerX-15, centerY+20, 32, 2, res.getGlobalCooldown(), res.getMaxCooldown(), 0xFF333333, 0xFF777777);
			}
			if (relevantResources.contains(IMagicResources.RESOURCE_STAMINA) && res.getResource(IMagicResources.RESOURCE_STAMINA, ArsenalConfig.get().resources.maxStamina)<ArsenalConfig.get().resources.maxStamina) {
				drawBar(centerX-15, centerY+23, 32, 2, res.getResource(IMagicResources.RESOURCE_STAMINA, ArsenalConfig.get().resources.maxStamina), ArsenalConfig.get().resources.maxStamina, 0xFF333333, 0xFF333399);
			}
			
			if (relevantResources.contains(IMagicResources.RESOURCE_RAGE) && res.getResource(IMagicResources.RESOURCE_RAGE, ArsenalConfig.get().resources.maxRage)<ArsenalConfig.get().resources.maxRage) {
				drawBar(centerX-15, centerY+26, 32, 2, res.getResource(IMagicResources.RESOURCE_RAGE, ArsenalConfig.get().resources.maxRage), ArsenalConfig.get().resources.maxRage, 0xFF333333, 0xFF993333);
			}
		}
	}
	
	@SubscribeEvent(priority=EventPriority.LOW)
	public void onRenderWorldLast(RenderWorldLastEvent event) {
		ParticleEmitters.draw(event.getPartialTicks(), Minecraft.getMinecraft().player);
	}
}
