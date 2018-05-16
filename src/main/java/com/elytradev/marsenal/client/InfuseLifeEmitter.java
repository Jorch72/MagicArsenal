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

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;

public class InfuseLifeEmitter extends Emitter {
	Random random = new Random();
	private int ticksRemaining = 30;
	
	private ArrayList<Star> stars = new ArrayList<>();
	
	@Override
	public void tick() {
		if (entity.isDead) {
			ticksRemaining = 0;
			//kill();
			return;
		}
		
		if (ticksRemaining>0) {
			for(int i=0; i<4; i++) {
				if (entity==null) return;
				double startX = entity.posX + random.nextGaussian()*0.3d;
				double startY = entity.posY + entity.height + 2.5d;
				double startZ = entity.posZ + random.nextGaussian()*0.3d;
				//Vec3d towardsEntity = new Vec3d(entity.posX-startX, entity.posY-startY, this.entity.posZ-startZ)
				//		.normalize().scale(512.0f);
			
				Star star = new Star();
				star.taild2 = (float)(Math.pow(0.1f+(Math.random()*0.3f), 2));
				star.width = 0.04f;
				star.move((float)startX, (float)startY, (float)startZ);
				star.color = 0x8874aa00;
				star.fuzzColor(0.3f, 0.7f, 0.3f);
				
				star.lifetime = 20*2;
				//star.setAcceleration(towardsEntity);
				star.setVelocity(0, -0.4f + -(float)(Math.random()*0.2f), 0);
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
		
		for(Star star : stars) {
			if (star.y<entity.posY) {
				star.vy = 0.05f;
				star.vx += random.nextGaussian()*0.01f;
				star.vy += random.nextGaussian()*0.01f;
			}
		}
		
		ticksRemaining--;
		if (ticksRemaining<=0) {
			if (stars.isEmpty()) {
				kill();
			}
		}
	}

	@Override
	public void draw(float partialFrameTime, double dx, double dy, double dz) {
		Emitter.drawStars(partialFrameTime, dx, dy, dz, stars, true);
		/*
		GlStateManager.disableLighting();
		GlStateManager.disableTexture2D();
		
		//this.partialTicks += partialFrameTime*1.5f;
		
		for(Star star : stars) {
			star.tick(partialFrameTime);
			star.paint(dx, dy, dz);
			if (star.lifetime<=0) dead.add(star);
		}
		for(Star star : dead) {
			stars.remove(star);
		}
		dead.clear();

		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();*/
	}
}
