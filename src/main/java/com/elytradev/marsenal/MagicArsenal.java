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

import com.elytradev.concrete.inventory.IContainerInventoryHolder;
import com.elytradev.concrete.inventory.gui.ConcreteContainer;
import com.elytradev.concrete.inventory.gui.client.ConcreteGui;
import com.elytradev.concrete.network.NetworkContext;
import com.elytradev.marsenal.block.ArsenalBlocks;
import com.elytradev.marsenal.capability.IMagicResources;
import com.elytradev.marsenal.capability.IRuneProducer;
import com.elytradev.marsenal.capability.impl.DefaultMagicResourcesSerializer;
import com.elytradev.marsenal.capability.impl.DefaultRuneProducerSerializer;
import com.elytradev.marsenal.capability.impl.MagicResources;
import com.elytradev.marsenal.capability.impl.RuneProducer;
import com.elytradev.marsenal.compat.BaublesCompat;
import com.elytradev.marsenal.compat.ChiselCompat;
import com.elytradev.marsenal.entity.EntityFrostShard;
import com.elytradev.marsenal.entity.EntityWillOWisp;
import com.elytradev.marsenal.gui.GuiCodex;
import com.elytradev.marsenal.gui.ContainerCodex;
import com.elytradev.marsenal.gui.EnumGui;
import com.elytradev.marsenal.item.ArsenalItems;
import com.elytradev.marsenal.item.EnumIngredient;
import com.elytradev.marsenal.item.ItemChisel;
import com.elytradev.marsenal.magic.SpellScheduler;
import com.elytradev.marsenal.network.ConfigMessage;
import com.elytradev.marsenal.network.MagicResourcesMessage;
import com.elytradev.marsenal.network.SpawnParticleEmitterMessage;
import com.elytradev.probe.api.IProbeDataProvider;
import com.google.common.collect.ImmutableSet;

import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryTable;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
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
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid=MagicArsenal.MODID, version=MagicArsenal.VERSION, name="Magic Arsenal")
public class MagicArsenal {
	public static final String MODID = "magicarsenal";
	public static final String VERSION = "@VERSION@";
	@SidedProxy(clientSide="com.elytradev.marsenal.client.ClientProxy", serverSide="com.elytradev.marsenal.Proxy")
	public static Proxy PROXY;
	public static MagicArsenal INSTANCE;
	public static Logger LOG;
	public static NetworkContext CONTEXT;
	@CapabilityInject(value = IMagicResources.class)
	public static Capability<IMagicResources> CAPABILTIY_MAGIC_RESOURCES;
	@CapabilityInject(value = IRuneProducer.class)
	public static Capability<IRuneProducer> CAPABILITY_RUNEPRODUCER;
	@CapabilityInject(value = IProbeDataProvider.class)
	public static Object CAPABILITY_PROBEDATA;
	@CapabilityInject(value = ITeslaHolder.class)
	public static Object CAPABILITY_TESLA_HOLDER;
	@CapabilityInject(value = ITeslaProducer.class)
	public static Object CAPABILITY_TESLA_PRODUCER;
	@CapabilityInject(value = ITeslaConsumer.class)
	public static Object CAPABILITY_TESLA_CONSUMER;
	
	public static final CreativeTabs TAB_MARSENAL = new CreativeTabs("magicarsenal") {
		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(ArsenalItems.SPELL_FOCUS);
		}
	};
	
	@Mod.EventHandler
	public void onPreInit(FMLPreInitializationEvent e) {
		INSTANCE = this;
		LOG = LogManager.getLogger("MagicArsenal");
		ArsenalConfig.setLocal(ArsenalConfig.load(e.getSuggestedConfigurationFile()));
		CapabilityManager.INSTANCE.register(IMagicResources.class, new DefaultMagicResourcesSerializer(), MagicResources::new);
		CapabilityManager.INSTANCE.register(IRuneProducer.class, new DefaultRuneProducerSerializer(), RuneProducer::new);
		
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
		MinecraftForge.EVENT_BUS.register(ItemChisel.Handler.class);
		
		if (Loader.isModLoaded("baubles")) {
			MinecraftForge.EVENT_BUS.register(BaublesCompat.class);
		}
		
		PROXY.preInit();
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new IGuiHandler() {
			@Override
			public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
				EnumGui gui = EnumGui.forId(id);
				if (gui==EnumGui.TOME) return null;
				
				TileEntity te = world.getTileEntity(new BlockPos(x,y,z));
				
				if (te!=null && (te instanceof IContainerInventoryHolder)) {
					ConcreteContainer container = gui.createContainer(
							player.inventory,
							((IContainerInventoryHolder)te).getContainerInventory(),
							te);
					container.validate();
					return container;
				}
				
				return null; //For now!
			}

			@Override
			public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
				EnumGui gui = EnumGui.forId(id);
				if (gui==EnumGui.TOME) return new GuiCodex(new ContainerCodex(player.inventory, x));
				
				TileEntity te = world.getTileEntity(new BlockPos(x,y,z));
				ConcreteContainer container = null;
				if (te!=null && (te instanceof IContainerInventoryHolder)) {
					container = gui.createContainer(
							player.inventory,
							((IContainerInventoryHolder)te).getContainerInventory(),
							te);
				}
				
				return new ConcreteGui(container);
			}
			
		});
	}
	
	@Mod.EventHandler
	public void onInit(FMLInitializationEvent e) {
		PROXY.init();
		ChiselCompat.init(); //Only IMC in here
		
		MinecraftForge.addGrassSeed(new ItemStack(ArsenalItems.ROOT_WOLFSBANE), 2);
		MinecraftForge.addGrassSeed(new ItemStack(ArsenalItems.ROOT_NIGHTSHADE), 2);
		
		LootTableList.register(new ResourceLocation(MODID, "inject/simple_dungeon"));
		LootTableList.register(new ResourceLocation(MODID, "inject/mob_death"));
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
	
	@SubscribeEvent
	public void onCraftingResult(PlayerEvent.ItemCraftedEvent event) {
		for(int i=0; i<event.craftMatrix.getSizeInventory(); i++) {
			ItemStack stack = event.craftMatrix.getStackInSlot(i);
			if (stack!=null && !stack.isEmpty() && stack.getItem()==ArsenalItems.INGREDIENT && stack.getMetadata()==EnumIngredient.PETAL_WOLFSBANE.ordinal()) {
				NBTTagCompound tag = event.crafting.getTagCompound();
				if (tag==null) {
					tag = new NBTTagCompound();
					event.crafting.setTagCompound(tag);
				}
				tag.setBoolean("magicarsenal:Poisoned", true);
			}
		}
	}
	
	@SubscribeEvent
	public void onItemEaten(LivingEntityUseItemEvent.Finish event) {
		if (event.getEntityLiving().getEntityWorld().isRemote) return;
		
		ItemStack stack = event.getItem();
		NBTTagCompound tag = stack.getTagCompound();
		if (tag==null) return;
		
		if (tag.hasKey("magicarsenal:Poisoned")) {
			event.getEntityLiving().addPotionEffect(new PotionEffect(ArsenalItems.POTION_WOLFSBANE, 20*30));
		}
	}
	
	private static final ImmutableSet<String> PORTAL_SEARED_FOES = ImmutableSet.of(
			"zombie", "husk", "zombie_pigman", "skeleton"
	);
	
	@SubscribeEvent
	public void onLootTableLoad(LootTableLoadEvent event) {
		String entityPrefix = "minecraft:entities/";
		if (event.getName().toString().startsWith(entityPrefix)) {
			String tableName = event.getName().toString().substring(event.getName().toString().indexOf(entityPrefix) + entityPrefix.length());
			if (PORTAL_SEARED_FOES.contains(tableName)) {
				event.getTable().addPool(getInjectPool("mob_death"));
			}
		}
		
		if (event.getName().toString().equals("minecraft:chests/simple_dungeon")) {
			event.getTable().addPool(getInjectPool("simple_dungeon"));
		}
	}
	
	//Utility methods below adapted from Botania and originally written by Vazkii. I can't take credit for these gems but they make editing drops 900% easier
	private LootPool getInjectPool(String entryName) {
		return new LootPool(new LootEntry[] { getInjectEntry(entryName, 1) }, new LootCondition[0], new RandomValueRange(1), new RandomValueRange(0, 1), "magicarsenal_inject_pool");
	}

	private LootEntryTable getInjectEntry(String name, int weight) {
		return new LootEntryTable(new ResourceLocation(MODID, "inject/" + name), weight, 0, new LootCondition[0], "magicarsenal_inject_entry");
	}
}
