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

import java.util.Map;

import com.elytradev.marsenal.item.ArsenalItems;
import com.elytradev.marsenal.item.EnumSpellBauble;
import com.google.common.collect.MapMaker;

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
	public static Map<EntityPlayer, PlayerFlightData> flightDataMap = new MapMaker().weakKeys().concurrencyLevel(2).makeMap();
	
	/** The maximum amount of time that two spacebar presses will be considered one double-press */
	public static final long DOUBLETAP_TIME = 300L;
	
	public static final double MAX_IMPULSE_SPRINTING = 2.8;
	public static final double MAX_IMPULSE = 1.4;
	public static final double FLIGHT_IMPULSE_SPRINTING = 0.08;
	public static final double FLIGHT_IMPULSE = 0.06;
	
	public static final double FLIGHT_SPEED = 0.3;
	public static final double FLIGHT_SPEED_SPRINTING = FLIGHT_SPEED * 4;
	
	public static final double FLIGHT_DAMPENING = 0.01;
	public static final double FLIGHT_DAMPENING_RATIO = 0.90;
	
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
	
	public static Vec3d dampen(Vec3d vec, double amount) {
		return new Vec3d(
				dampen(vec.x, amount),
				dampen(vec.y, amount),
				dampen(vec.z, amount)
				);
	}
	
	public static Vec3d dampen(Vec3d vec, double ratio, double gutter) {
		double mag = vec.lengthVector();
		if (mag<gutter) return Vec3d.ZERO;
		return vec.scale(ratio);
	}
	
	public static Vec3d addSoftCap(Vec3d vel, Vec3d acc, double cap) {
		double softCap = Math.max(cap, vel.lengthVector());
		
		Vec3d result = vel.add(acc);
		if (result.lengthVector()>softCap) {
			return result.normalize().scale(softCap);
		} else {
			return result;
		}
	}
	
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent e) {
		if (e.phase!=TickEvent.Phase.END) return;
		
		if (checkFor(e.player, EnumSpellBauble.GRAVITY_MANTLE)) {
			//System.out.println("GRAVITY MANTLE CLIENT TICK");
			
			//System.out.println("PrevMagnitude: "+prevMotion.lengthVector());
			
			if (e.side==Side.CLIENT) {
				if (e.player.capabilities.allowFlying) return; //Don't clobber creative flight
				
				PlayerFlightData flightData = flightDataMap.get(e.player);
				if (flightData==null) {
					flightData = new PlayerFlightData();
					flightDataMap.put(e.player, flightData);
				}
				
				if (Minecraft.getMinecraft().gameSettings.keyBindJump.isPressed()) {
					if (!flightData.jumpLock) {
						flightData.jumpLock = true;
						//This is the initial tick of a spacebar press
						long curJump = System.currentTimeMillis();
						if (flightData.checkLastJump(curJump)) {
							flightData.clearLastJump();
							flightData.flying = !flightData.flying;
							
							//TODO: Send the server a message that we're toggling flight, triggering changes in our particle effects?
						} else {
							flightData.setLastJump(curJump);
						}
						
					}
				} else {
					//The spacebar isn't pressed. Log it so we can pick up the next initial-press.
					flightData.jumpLock = false;
				}
				
				
				
				if (flightData.flying) {
					//e.player.motionY += 0.08;
					e.player.fallDistance = 0.0f;
					
					//More damping
					Vec3d prevMotion = dampen(flightData.velocity, FLIGHT_DAMPENING_RATIO, FLIGHT_DAMPENING);
					
					//Controlled flight
					double acceleration = (e.player.isSprinting()) ? FLIGHT_IMPULSE_SPRINTING : FLIGHT_IMPULSE;
					double speedLimit = (e.player.isSprinting()) ? FLIGHT_SPEED_SPRINTING : FLIGHT_SPEED;
					Vec3d impulse = new Vec3d(0,0,0);
					
					if (Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown()) {
						Vec3d flight = e.player.getLookVec();
						flight = flight.scale(acceleration);
						impulse = impulse.add(flight);
					}
					
					if (Minecraft.getMinecraft().gameSettings.keyBindBack.isKeyDown()) {
						Vec3d flight = e.player.getLookVec();
						flight = flight.scale(-acceleration/2);
						impulse = impulse.add(flight);
					}
					
					if (Minecraft.getMinecraft().gameSettings.keyBindRight.isKeyDown()) {
						Vec3d flight = e.player.getLookVec().crossProduct(UP).scale(acceleration/2);
						impulse = impulse.add(flight);
					}
					
					if (Minecraft.getMinecraft().gameSettings.keyBindLeft.isKeyDown()) {
						Vec3d flight = e.player.getLookVec().crossProduct(UP).scale(-acceleration/2);
						impulse = impulse.add(flight);
					}
					
					if (Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown()) {
						Vec3d flight = UP.scale(acceleration);
						impulse = impulse.add(flight);
					}
					
					if (Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown()) {
						Vec3d flight = UP.scale(-acceleration);
						impulse = impulse.add(flight);
					}
					
					//Cap impulse
					double magnitude = impulse.lengthVector();
					//System.out.println("Magnitude: "+magnitude);
					if (e.player.isSprinting()) {
						if(magnitude>MAX_IMPULSE_SPRINTING) {
							impulse = impulse.normalize().scale(MAX_IMPULSE_SPRINTING);
						}
					} else {
						if(magnitude>MAX_IMPULSE) {
							//System.out.println("Capping speed from "+magnitude+" to "+MAX_IMPULSE);
							impulse = impulse.normalize().scale(MAX_IMPULSE);
						}
					}
					
					flightData.velocity = addSoftCap(prevMotion, impulse, speedLimit);
					/*
					flightData.velocity = prevMotion.add(impulse);
					if (flightData.velocity.lengthVector()>speedLimit) {
						flightData.velocity = flightData.velocity.normalize().scale(speedLimit);
					}*/
					
					e.player.motionX = flightData.velocity.x;
					e.player.motionY = flightData.velocity.y;
					e.player.motionZ = flightData.velocity.z;
				}
			} else {
				e.player.fallDistance = 0.0f;
			}
		}
	}
	
	public static class PlayerFlightData {
		boolean flying;
		long lastJump = 0L;
		boolean hasLastJump = false;
		boolean jumpLock = false;
		Vec3d velocity = Vec3d.ZERO;
		
		public void setLastJump(long lastJump) {
			hasLastJump = true;
			this.lastJump = lastJump;
		}
		
		public void clearLastJump() {
			hasLastJump = false;
			lastJump = 0L;
		}
		
		public boolean checkLastJump(long time) {
			if (!hasLastJump) return false;
			return time-lastJump < DOUBLETAP_TIME;
		}
	}
}
