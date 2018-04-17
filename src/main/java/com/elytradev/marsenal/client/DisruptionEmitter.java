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

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;

public class DisruptionEmitter extends Emitter {
	private Random random = new Random();
	private int ticksRemaining = 11;
	
	
	@Override
	public void tick() {
		Vec3d ptSrc    = new Vec3d(source.posX, source.posY+(source.getEyeHeight())-0.3d, source.posZ);
		Vec3d ptTarget = (entity!=null) ?
				new Vec3d(entity.posX, entity.posY+(entity.height/2), entity.posZ) :
				new Vec3d(x,y,z);
		Vec3d ptVec = ptTarget.subtract(ptSrc).normalize().scale(3.5f);
		
		for(int i=0; i<4; i++) {
			Vec3d ptRnd = ptSrc.addVector(random.nextGaussian()/3, random.nextGaussian()/3, random.nextGaussian()/3);
			
			Particle particle = new ParticleVelocity(world, ptRnd, ptVec);
			//particle.setParticleTextureIndex(5); //Midway through redstone
			particle.setParticleTextureIndex(65);
			particle.setRBGColorF(0.8f, 0.1f, 0.3f);
			particle.setMaxAge(10);
			
			Minecraft.getMinecraft().effectRenderer.addEffect(particle);
		}
		ticksRemaining--;
		if (ticksRemaining<=0) kill();
	}

	@Override
	public void draw(float partialFrameTime, double dx, double dy, double dz) {
		GlStateManager.disableLighting();
		GlStateManager.disableTexture2D();
		
		Vec3d ptSrc    = new Vec3d(source.posX, source.posY+(source.getEyeHeight())-0.3d, source.posZ);
		Vec3d ptTarget = (entity!=null) ?
				new Vec3d(entity.posX, entity.posY+(entity.height/2), entity.posZ) :
				new Vec3d(x,y,z);
		
		Draw.fakeLine(ptSrc.x-dx, ptSrc.y-dy, ptSrc.z-dz, ptTarget.x-dx, ptTarget.y-dy, ptTarget.z-dz, 0.2f, 255, 255, 223, 255, true);
		
		for(int i=0; i<8; i++) {
			float ox = (float)(random.nextGaussian()/5);
			float oy = (float)(random.nextGaussian()/5);
			float oz = (float)(random.nextGaussian()/5);
			
			int r = random.nextInt(64)+191;
			int other = random.nextInt(128);
			int g = other/2;
			int b = other;
			
			Draw.fakeLine(ptSrc.x+ox-dx, ptSrc.y+oy-dy, ptSrc.z+oz-dz, ptTarget.x+ox-dx, ptTarget.y+oy-dy, ptTarget.z+oz-dz, 0.1f, r, g, b, 255, true);
		}
		
		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
	}

}
