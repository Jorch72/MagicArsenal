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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ParticleEmitters {
	private static List<Emitter> emitters = new ArrayList<>();
	private static List<Emitter> dead = new ArrayList<>();
	
	public static void spawn(World world, float x, float y, float z, Entity entity, String key) {
		Emitter emitter = Emitter.create(key);
		if (emitter==null) return;
		emitter.init(world, x, y, z, entity);
		synchronized(emitters) {
			emitters.add(emitter);
		}
	}
	
	public static void tick() {
		synchronized(emitters) {
			for(Emitter emitter : emitters) {
				emitter.tick();
				if (emitter.isDead()) dead.add(emitter);
			}
			for(Emitter emitter : dead) emitters.remove(emitter);
			dead.clear();
		}
	}
	
	public static void draw(float partialTicks, EntityLivingBase player) {
		float frameTime = PartialTickTime.getFrameTime();
		Vec3d partialPos = player.getPositionEyes(partialTicks);
		
		synchronized(emitters) {
			
			for(Emitter emitter : emitters) {
				emitter.draw(frameTime, partialPos.x, partialPos.y, partialPos.z);
			}
			
			
		}
		
		PartialTickTime.endFrame();
	}
	
}
