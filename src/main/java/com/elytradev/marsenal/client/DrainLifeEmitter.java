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
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;

public class DrainLifeEmitter extends Emitter {
	Random random = new Random();
	private int ticksRemaining = 20;
	//private float partialTicks = 0f;
	
	private ArrayList<Star> dead = new ArrayList<>();
	private ArrayList<Star> stars = new ArrayList<>();
	private boolean         still = true;
	//                      burn
	
	@Override
	public void tick() {
		if (entity==null || source==null || entity.isDead) {
			kill();
			return;
		}
		
		if (still) for(int i=0; i<64; i++) {
			double startX = entity.posX + random.nextGaussian()*entity.width;
			double startY = entity.posY - (entity.height/2) + random.nextGaussian()*entity.height;
			double startZ = entity.posZ + random.nextGaussian()*entity.width;
			//Vec3d towardsEntity = new Vec3d(source.posX - startX, source.posY + (source.height/2) - startY, source.posZ - startZ)
			//		.normalize().scale(1024f);
			/*
			double startX = entity.posX + random.nextGaussian()*1.0d;
			double startY = entity.posY + (entity.height/2) + random.nextGaussian()*1.0d;
			double startZ = entity.posZ + random.nextGaussian()*1.0d;
			Vec3d towardsEntity = new Vec3d(entity.posX-startX, entity.posY-startY, this.entity.posZ-startZ)
					.normalize().scale(512.0f);*/
			Vec3d towardsEntity = new Vec3d(entity.posX-startX, entity.posY-startY, this.entity.posZ-startZ)
					.normalize().scale(-128.0f);
		
			Star star = new Star();
			star.width = 0.05f;
			star.move((float)startX, (float)startY, (float)startZ);
			star.color = 0xFF8b0722;
			star.lifetime = 8;
			star.intercept = true;
			star.tx = (float) source.posX;
			star.ty = (float) (source.posY + (source.height/2));
			star.tz = (float) source.posZ;
			star.interceptRange=1.5f*1.5f;
			star.taild2=0.5f*0.5f;
			star.onIntercept = (it) -> {
					it.lifetime = 0;
					//TODO: Spawn a green puff?
				};
			star.setVelocity(towardsEntity);
			stars.add(star);
		}
		still = false;
		
		for(int i=0; i<6; i++) {
			float px = (float)(entity.posX + random.nextGaussian()*0.2d);
			float py = (float)(entity.posY + 0.1f);
			float pz = (float)(entity.posZ + random.nextGaussian()*0.2d);
			
			Particle particle = new ParticleVelocity(world,
					px, py, pz,
					0f, 0.01f, 0f
					);
			particle.setParticleTextureIndex(5); //Midway through redstone
			particle.setRBGColorF(0.545f, 0.0274f, 0.130f);
			
			Minecraft.getMinecraft().effectRenderer.addEffect(particle);
		}
		
		ticksRemaining--;
		if (ticksRemaining<=0) kill();
	}

	@Override
	public void draw(float partialFrameTime, double dx, double dy, double dz) {
		GlStateManager.disableLighting();
		GlStateManager.disableTexture2D();
		
		//this.partialTicks += partialFrameTime*1.5f;
		
		for(Star star : stars) {
			star.tx = (float) source.posX;
			star.ty = (float) (source.posY + (source.height/2));
			star.tz = (float) source.posZ;
			
			star.tick(partialFrameTime);
			star.paint(dx, dy, dz);
			if (star.lifetime<=0) dead.add(star);
		}
		for(Star star : dead) {
			stars.remove(star);
		}
		dead.clear();

		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
	}

}
