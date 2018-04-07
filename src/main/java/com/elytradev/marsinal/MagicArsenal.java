/*
 * MIT License
 *
 * Copyright (c) 2017 Isaac Ellingson (Falkreon) and contributors
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

package com.elytradev.marsinal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.elytradev.concrete.network.NetworkContext;
import com.elytradev.marsinal.capability.IMagicResources;
import com.elytradev.marsinal.capability.impl.DefaultMagicResourcesSerializer;
import com.elytradev.marsinal.capability.impl.MagicResources;
import com.elytradev.marsinal.network.SpawnParticleEmitterMessage;

import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod(modid=MagicArsenal.MODID, version=MagicArsenal.VERSION, name="Magic Arsenal")
public class MagicArsenal {
	public static final String MODID = "magicarsenal";
	public static final String VERSION = "@VERSION@";
	public static Logger LOG;
	public static NetworkContext CONTEXT;
	
	@Mod.EventHandler
	public void onPreInit(FMLPreInitializationEvent e) {
		LOG = LogManager.getLogger("MagicArsenal");
		ArsenalConfig.LOCAL = ArsenalConfig.load(e.getSuggestedConfigurationFile());
		CapabilityManager.INSTANCE.register(IMagicResources.class, new DefaultMagicResourcesSerializer(), MagicResources::new);
		
		CONTEXT = NetworkContext.forChannel("mafx");
		CONTEXT.register(SpawnParticleEmitterMessage.class);
		
		
	}
	
	@SubscribeEvent
	public void onTick(TickEvent event) {
		
	}
}
