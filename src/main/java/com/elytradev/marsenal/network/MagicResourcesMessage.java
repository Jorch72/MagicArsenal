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

package com.elytradev.marsenal.network;

import com.elytradev.concrete.network.Message;
import com.elytradev.concrete.network.annotation.field.MarshalledAs;
import com.elytradev.concrete.network.annotation.type.ReceivedOn;
import com.elytradev.marsenal.MagicArsenal;
import com.elytradev.marsenal.capability.IMagicResources;
import com.elytradev.marsenal.capability.impl.MagicResources;
import com.elytradev.marsenal.client.ClientProxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;

@ReceivedOn(Side.CLIENT)
public class MagicResourcesMessage extends Message {
	@MarshalledAs("nbt")
	NBTTagCompound map = new NBTTagCompound();
	
	public MagicResourcesMessage() {
		super(MagicArsenal.CONTEXT);
	}

	public MagicResourcesMessage(IMagicResources res) {
		this();
		if (res instanceof MagicResources) {
			map.setInteger("GCD", res.getGlobalCooldown());
			map.setInteger("GCD_MAX", res.getMaxCooldown());
			
			((MagicResources)res).forEach((resource, amount)->{
				map.setInteger(resource.toString(), amount);
			});
		} else {
			//Make our best guess as to what needs to be transferred
			map.setInteger("GCD", res.getGlobalCooldown());
			map.setInteger("GCD_MAX", res.getMaxCooldown());
			
			map.setInteger(IMagicResources.RESOURCE_STAMINA.toString(), res.getResource(IMagicResources.RESOURCE_STAMINA, 100));
		}
	}
	
	@Override
	protected void handle(EntityPlayer player) {
		//if (!player.hasCapability(MagicArsenal.CAPABILTIY_MAGIC_RESOURCES, null)) return;
		//IMagicResources res = player.getCapability(MagicArsenal.CAPABILTIY_MAGIC_RESOURCES, null);
		MagicResources res = ClientProxy.scrambleTargets;
		for(String s : map.getKeySet()) {
			if (s.equals("GCD")) {
				res._setGlobalCooldown(map.getInteger(s));
			} else if (s.equals("GCD_MAX")) {
				res.setMaxCooldown(map.getInteger(s));
			} else {
				res.set(new ResourceLocation(s), map.getInteger(s));
			}
		}
	}
}
