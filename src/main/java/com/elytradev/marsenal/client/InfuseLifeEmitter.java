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
import java.util.Random;

import com.elytradev.marsenal.client.star.LineStar;
import com.elytradev.marsenal.client.star.StarFlinger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.math.Vec3d;

public class InfuseLifeEmitter extends Emitter {
	Random random = new Random();
	private int ticksRemaining = 30;
	private ArrayList<LineStar> stars = new ArrayList<>();
	
	@Override
	public void tick() {
		if (entity.isDead) {
			ticksRemaining = 0;
			kill();
			return;
		}
		
		if (ticksRemaining>0) {
			for(int i=0; i<4; i++) {
				if (entity==null) return;
				double startX = entity.posX + random.nextGaussian()*0.3d;
				double startY = entity.posY + entity.height + 2.5d;
				double startZ = entity.posZ + random.nextGaussian()*0.3d;
				
				LineStar star = new LineStar();
				star.setLength(0.1f+ (float)(Math.random()*0.3f));
				star.setThickness(0.04f);
				star.setPosition((float)startX, (float)startY, (float)startZ);
				star.setColor(0x8874aa00);
				star.fuzzColor(0.3f, 0.7f, 0.3f);
				star.setVelocity(0, -0.4f + -(float)(Math.random()*0.2f), 0);
				star.setLifetime(20*2);
				StarFlinger.spawn(star);
				stars.add(star);
			}
			
			if (Minecraft.getMinecraft().gameSettings.particleSetting!=2) {
				for(int i=0; i<3; i++) {
					float px = (float)(entity.posX + random.nextGaussian()*0.2d);
					float py = (float)(entity.posY + 0.1f);
					float pz = (float)(entity.posZ + random.nextGaussian()*0.2d);
					
					Particle particle = new ParticleVelocity(world,
							px, py, pz,
							0f, 0.1f, 0f
							);
					particle.setParticleTextureIndex(5); //Midway through redstone
					particle.setRBGColorF(0.4549f, 0.6667f, 0.0000f);
					particle.setAlphaF(0.7f);
					
					Minecraft.getMinecraft().effectRenderer.addEffect(particle);
				}
			}
		}
		
		for(LineStar star : stars) {
			if (star.getY()<entity.posY) {
				Vec3d old = star.getVelocity();
				star.setVelocity(
						(float)old.x + (float)random.nextGaussian()*0.01f,
						0.05f,
						(float)old.z + (float)random.nextGaussian()*0.01f);
			}
		}
		
		ticksRemaining--;
		if (ticksRemaining<=0) kill(); //Flung stars will persist
	}

	@Override
	public void draw(float partialFrameTime, double dx, double dy, double dz) {}
}
