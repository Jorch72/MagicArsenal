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

import com.elytradev.marsenal.item.ArsenalItems;
import com.elytradev.marsenal.item.EnumSpellBauble;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class BaublesCompat {
	public static final double FLIGHT_SPEED = 0.1d;
	public static final Vec3d UP = new Vec3d(0,1,0);
	
	
	public static boolean checkFor(EntityLivingBase player, EnumSpellBauble bauble) {
		if (!(player instanceof EntityPlayer)) return false;
		IBaublesItemHandler inv = BaublesApi.getBaublesHandler((EntityPlayer)player);
		for(int i=0; i<inv.getSlots(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack==null || stack.getItem()!=ArsenalItems.SPELL_BAUBLE) continue;
			if (bauble.equals(EnumSpellBauble.byId(stack.getMetadata()))) return true;
		}
		return false;
	}
	
	public static double dampen(double d, double amount) {
		if (d>0) {
			d -= amount;
			if (d<0) d=0;
		} else if (d<0) {
			d += amount;
			if (d>0) d=0;
		}
		return d;
	}
	
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent e) {
		if (e.phase!=TickEvent.Phase.END) return;
		
		if (checkFor(e.player, EnumSpellBauble.GRAVITY_MANTLE)) {
			//System.out.println("GRAVITY MANTLE CLIENT TICK");
			
			
			if (e.side==Side.CLIENT) {
				e.player.motionY += 0.08;
				e.player.fallDistance = 0.0f;
				
				//More damping
				e.player.motionX = dampen(e.player.motionX, 0.005);
				e.player.motionY = dampen(e.player.motionY, 0.015);
				e.player.motionZ = dampen(e.player.motionZ, 0.005);
				
				//Controlled flight
				//Alternate possibility: Add up all the vectors from these and clamp to a max velocity impulse, but where's the fun in that?
				if (Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown()) {
					Vec3d flight = e.player.getLookVec();
					flight = flight.scale(FLIGHT_SPEED);
					e.player.motionX += flight.x;
					e.player.motionY += flight.y;
					e.player.motionZ += flight.z;
				}
				
				if (Minecraft.getMinecraft().gameSettings.keyBindBack.isKeyDown()) {
					Vec3d flight = e.player.getLookVec();
					flight = flight.scale(-FLIGHT_SPEED/2);
					e.player.motionX += flight.x;
					e.player.motionY += flight.y;
					e.player.motionZ += flight.z;
				}
				
				if (Minecraft.getMinecraft().gameSettings.keyBindRight.isKeyDown()) {
					Vec3d flight = e.player.getLookVec().crossProduct(UP).scale(FLIGHT_SPEED/2);
					e.player.motionX += flight.x;
					e.player.motionY += flight.y;
					e.player.motionZ += flight.z;
				}
				
				if (Minecraft.getMinecraft().gameSettings.keyBindLeft.isKeyDown()) {
					Vec3d flight = e.player.getLookVec().crossProduct(UP).scale(-FLIGHT_SPEED/2);
					e.player.motionX += flight.x;
					e.player.motionY += flight.y;
					e.player.motionZ += flight.z;
				}
				
				if (Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown()) {
					Vec3d flight = UP.scale(FLIGHT_SPEED/2);
					e.player.motionX += flight.x;
					e.player.motionY += flight.y;
					e.player.motionZ += flight.z;
				}
				
				if (Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown()) {
					Vec3d flight = UP.scale(-FLIGHT_SPEED/2);
					e.player.motionX += flight.x;
					e.player.motionY += flight.y;
					e.player.motionZ += flight.z;
				}
			} else {
				e.player.fallDistance = 0.0f;
			}
		}
	}
}
