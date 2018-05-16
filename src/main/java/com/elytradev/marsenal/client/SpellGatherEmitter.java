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

public class SpellGatherEmitter extends Emitter {
	private static final int LIFETIME = (int)(20*2.5f);
	private Random random = new Random();
	
	private int ticksRemaining = LIFETIME;
	
	@Override
	public void tick() {
		Vec3d ptTarget = null;
		if (source==null) {
			ptTarget = new Vec3d(this.x, this.y, this.z);
		} else {
			ptTarget = new Vec3d(source.posX, source.posY+(source.height/2), source.posZ);
		}
		
		float escalation = (LIFETIME-ticksRemaining) / (float)LIFETIME;
		int numParticles = 2 + (int)(8*escalation);
		for(int i=0; i<numParticles; i++) {
			Vec3d ptRnd = ptTarget.addVector(random.nextGaussian()*2, random.nextGaussian()*2, random.nextGaussian()*2);
			Vec3d ptVec = ptTarget.subtract(ptRnd).normalize().scale(0.6f + (0.8f*escalation));
			
			Particle particle = new ParticleVelocity(world, ptRnd, ptVec);
			particle.setParticleTextureIndex(65); //Start of the crit2 particles
			float intensity = random.nextFloat()*0.4f*escalation;
			particle.setRBGColorF(0.6f+intensity, intensity, 0.6f+intensity);
			particle.setMaxAge(2);
			
			Minecraft.getMinecraft().effectRenderer.addEffect(particle);
		}
		ticksRemaining--;
		if (ticksRemaining<=0) kill();
	}

	@Override
	public void draw(float partialFrameTime, double dx, double dy, double dz) {
		Vec3d ptTarget = null;
		if (source==null) {
			ptTarget = new Vec3d(this.x, this.y, this.z);
		} else {
			ptTarget = new Vec3d(source.posX, source.posY+(source.height/2), source.posZ);
		}
		
		GlStateManager.disableLighting();
		GlStateManager.disableTexture2D();
		
		float escalation = (LIFETIME-ticksRemaining) / (float)LIFETIME;
		Draw.circle(ptTarget.x-dx, ptTarget.y-dy, ptTarget.z-dz, (1-escalation)*3.5f, Draw.TAU/8f, 0.2f*escalation, 0xFFFF00FF);
		
		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
	}
	
}
