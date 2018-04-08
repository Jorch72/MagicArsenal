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

package com.elytradev.marsenal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.elytradev.concrete.network.NetworkContext;
import com.elytradev.marsenal.capability.IMagicResources;
import com.elytradev.marsenal.capability.impl.DefaultMagicResourcesSerializer;
import com.elytradev.marsenal.capability.impl.MagicResources;
import com.elytradev.marsenal.item.ArsenalItems;
import com.elytradev.marsenal.network.SpawnParticleEmitterMessage;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid=MagicArsenal.MODID, version=MagicArsenal.VERSION, name="Magic Arsenal")
public class MagicArsenal {
	public static final String MODID = "magicarsenal";
	public static final String VERSION = "@VERSION@";
	@SidedProxy(clientSide="com.elytradev.marsenal.client.ClientProxy", serverSide="com.elytradev.marsenal.Proxy")
	public static Proxy PROXY;
	public static Logger LOG;
	public static NetworkContext CONTEXT;
	@CapabilityInject(value = IMagicResources.class)
	public static Capability<IMagicResources> CAPABILTIY_MAGIC_RESOURCES;
	
	public static final CreativeTabs TAB_MARSENAL = new CreativeTabs("magicarsenal") {
		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(ArsenalItems.SPELL_FOCUS);
		}
	};
	
	@Mod.EventHandler
	public void onPreInit(FMLPreInitializationEvent e) {
		LOG = LogManager.getLogger("MagicArsenal");
		ArsenalConfig.LOCAL = ArsenalConfig.load(e.getSuggestedConfigurationFile());
		CapabilityManager.INSTANCE.register(IMagicResources.class, new DefaultMagicResourcesSerializer(), MagicResources::new);
		
		CONTEXT = NetworkContext.forChannel("mafx");
		CONTEXT.register(SpawnParticleEmitterMessage.class);
		
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(PROXY);
		MinecraftForge.EVENT_BUS.register(ArsenalItems.class);
		
		PROXY.preInit();
	}
	
	@Mod.EventHandler
	public void onPostInit(FMLPostInitializationEvent e) {
		if (e.getSide()==Side.SERVER) {
			ArsenalConfig.RESOLVED = ArsenalConfig.LOCAL;
		}
	}
	
	@SubscribeEvent
	public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> e) {
		if (e.getObject() instanceof EntityPlayer) {
			e.addCapability(new ResourceLocation("magicarsenal", "magicresources"), new ICapabilityProvider() {
				private IMagicResources info = new MagicResources();
				
				@Override
				public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
					return (capability==CAPABILTIY_MAGIC_RESOURCES);
				}
	
				@SuppressWarnings("unchecked")
				@Override
				public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
					if (capability==CAPABILTIY_MAGIC_RESOURCES) {
						return (T) info;
					} else {
						return null;
					}
				}
			});
		}
	}
	
	@SubscribeEvent
	public void onTick(TickEvent.WorldTickEvent event) {
		if (event.world.isRemote) return;
		
		for(EntityPlayer player : event.world.playerEntities) {
			if (player.hasCapability(CAPABILTIY_MAGIC_RESOURCES, null)) {
				IMagicResources res = player.getCapability(CAPABILTIY_MAGIC_RESOURCES, null);
				
				
				if (res.getGlobalCooldown()>0) {
					//Regen Stamina
					int stamina = res.getResource(IMagicResources.RESOURCE_STAMINA, 100);
					res.set(IMagicResources.RESOURCE_STAMINA, Math.min(100, stamina+100));
				}
				
				//Waste Vengeance
				res.spend(IMagicResources.RESOURCE_VENGEANCE, 1, 0, false);
				
				//Waste Rage
				res.spend(IMagicResources.RESOURCE_RAGE, 1, 0, false);
			}
		}
	}
	
	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void onPlayerAttack(AttackEntityEvent evt) {
		if (evt.getResult()==Result.DENY) return;
	
		if(evt.getEntityPlayer().hasCapability(CAPABILTIY_MAGIC_RESOURCES, null)) {
			IMagicResources res = evt.getEntityPlayer().getCapability(CAPABILTIY_MAGIC_RESOURCES, null);
			int rage = res.getResource(IMagicResources.RESOURCE_RAGE, 0);
			res.set(IMagicResources.RESOURCE_RAGE, Math.min(100, rage+5));
		}
	}
	
	
	@SubscribeEvent
	public void onConnect(PlayerEvent.PlayerLoggedInEvent evt) {
		//TODO: Send them a prefs packet
	}
}
