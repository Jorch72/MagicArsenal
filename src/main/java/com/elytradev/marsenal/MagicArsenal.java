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
import com.elytradev.marsenal.block.ArsenalBlocks;
import com.elytradev.marsenal.capability.IMagicResources;
import com.elytradev.marsenal.capability.impl.DefaultMagicResourcesSerializer;
import com.elytradev.marsenal.capability.impl.MagicResources;
import com.elytradev.marsenal.compat.BaublesCompat;
import com.elytradev.marsenal.entity.EntityFrostShard;
import com.elytradev.marsenal.entity.EntityWillOWisp;
import com.elytradev.marsenal.item.ArsenalItems;
import com.elytradev.marsenal.magic.SpellScheduler;
import com.elytradev.marsenal.network.ConfigMessage;
import com.elytradev.marsenal.network.MagicResourcesMessage;
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
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
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
		ArsenalConfig.setLocal(ArsenalConfig.load(e.getSuggestedConfigurationFile()));
		CapabilityManager.INSTANCE.register(IMagicResources.class, new DefaultMagicResourcesSerializer(), MagicResources::new);
		
		CONTEXT = NetworkContext.forChannel("mafx");
		CONTEXT.register(SpawnParticleEmitterMessage.class);
		CONTEXT.register(ConfigMessage.class);
		CONTEXT.register(MagicResourcesMessage.class);
		
		EntityRegistry.registerModEntity(new ResourceLocation(MODID, "frostShard"), EntityFrostShard.class, "magicarsenal.frostShard", 0, this, 16*5, 10, true);
		EntityRegistry.registerModEntity(new ResourceLocation(MODID, "willOWisp"),  EntityWillOWisp.class,  "magicarsenal.willOWisp",  1, this, 16*5, 10, true);
		
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(PROXY);
		MinecraftForge.EVENT_BUS.register(ArsenalItems.class);
		MinecraftForge.EVENT_BUS.register(ArsenalBlocks.class);
		
		if (Loader.isModLoaded("baubles")) {
			MinecraftForge.EVENT_BUS.register(BaublesCompat.class);
		}
		
		PROXY.preInit();
	}
	
	@Mod.EventHandler
	public void onInit(FMLInitializationEvent e) {
		PROXY.init();
		
		MinecraftForge.addGrassSeed(new ItemStack(ArsenalItems.ROOT_WOLFSBANE), 2);
	}
	
	@Mod.EventHandler
	public void onPostInit(FMLPostInitializationEvent e) {
		if (e.getSide()==Side.SERVER) {
			//Only gets called this way on dedicated server(!!!)
			ArsenalConfig.resolve(ArsenalConfig.local().toString());
		}
	}
	
	@SubscribeEvent
	public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> e) {
		if (e.getObject() instanceof EntityPlayer) {
			/* FakePlayers are kept weakly, and can disappear and reappear at any time. It's STRONGLY reccommended that
			 * if you're implementing an autocaster, you forward the capability up from your block so that it's
			 * partitioned from other blocks, gets ticks reliably, and doesn't reset its values to defaults randomly.
			 */
			if (!(e.getObject() instanceof FakePlayer)) {
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
	}
	
	/**
	 * This method is safe to call for any synthetic MagicResources to update per-tick effects like GCD reduction. Note
	 * that double-calling this isn't necessarily better, as it causes rage to bleed away faster too!
	 */
	public static void tickResources(IMagicResources res) {
		res.reduceGlobalCooldown(1);
		
		//if (res.getGlobalCooldown()<=0) { //Disabled - try to rebalance for constant mana regen
			//Regen Stamina
			int stamina = res.getResource(IMagicResources.RESOURCE_STAMINA, ArsenalConfig.get().resources.maxStamina);
			res.set(IMagicResources.RESOURCE_STAMINA, Math.min(ArsenalConfig.get().resources.maxStamina, stamina+1));
		//}
		
		//Waste Vengeance
		res.spend(IMagicResources.RESOURCE_VENGEANCE, 1, 0, false);
		
		//Waste Rage
		res.spend(IMagicResources.RESOURCE_RAGE, 1, 0, false);
	}
	
	private int tickCounter = 0;
	private static final int MAX_TICK_COUNTER = 10;
	@SubscribeEvent
	public void onTick(TickEvent.WorldTickEvent event) {
		if (event.phase != TickEvent.Phase.END) return;
		if (event.world.isRemote) return;
		
		for(EntityPlayer player : event.world.playerEntities) {
			if (!(player instanceof FakePlayer) && player.hasCapability(CAPABILTIY_MAGIC_RESOURCES, null)) {
				IMagicResources res = player.getCapability(CAPABILTIY_MAGIC_RESOURCES, null);
				
				tickResources(res);
				
				if (tickCounter>=MAX_TICK_COUNTER && res instanceof MagicResources && ((MagicResources)res).isDirty()) {
					((MagicResources)res).clearDirty();
					new MagicResourcesMessage(res).sendTo(player);
				}
			}
		}
		
		//if (tickCounter>=MAX_TICK_COUNTER) tickCounter = 0;
	}
	
	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) return;
		tickCounter++;
		if (tickCounter>MAX_TICK_COUNTER) tickCounter = 0;
		SpellScheduler.tick();
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
		new ConfigMessage(ArsenalConfig.local()).sendTo(evt.player);
	}
}
