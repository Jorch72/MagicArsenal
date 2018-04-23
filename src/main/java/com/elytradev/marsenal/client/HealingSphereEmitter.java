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

import com.elytradev.marsenal.magic.HealingCircleSpell;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class HealingSphereEmitter extends Emitter {
	public static final float RADIUS = HealingCircleSpell.RADIUS;
	private Random random = new Random();
	private int ticksRemaining = 400;
	private float wubTime = 0f;
	
	
	
	@Override
	public void tick() {
		if (Minecraft.getMinecraft().gameSettings.particleSetting!=0) {
			for(int i=0; i<12; i++) {
				Vec3d pt = rndPolar().scale(random.nextFloat()*RADIUS);
				float px = (float)(x + pt.x);
				float py = (float)(y + 0.05f);
				float pz = (float)(z + pt.z);
				
				Particle particle = new ParticleVelocity(world,
						px, py, pz,
						0f, 0.02f, 0f
						);
				particle.setParticleTextureIndex(5); //Midway through redstone
				particle.setRBGColorF(0.4549f, 0.6667f, 0.0000f);
				
				Minecraft.getMinecraft().effectRenderer.addEffect(particle);
			}
		}
		
		ticksRemaining--;
		if (ticksRemaining<=0) kill();
	}

	private Vec3d rndPolar() {
		float theta = random.nextFloat()*Draw.TAU;
		return new Vec3d(
				MathHelper.cos(theta),
				0,
				MathHelper.sin(theta));
	}
	
	@Override
	public void draw(float partialFrameTime, double dx, double dy, double dz) {
		GlStateManager.disableLighting();
		GlStateManager.disableTexture2D();
		
		wubTime += partialFrameTime*128f;
		double wub = Math.sin(wubTime) * 0.3d;
		double wub2 = Math.sin(wubTime+(Draw.TAU/4f)*1) * 0.3d;
		double wub3 = Math.sin(wubTime+(Draw.TAU/4f)*2) * 0.3d;
		Draw.circle(this.x-dx, this.y-dy, this.z-dz, RADIUS+wub, Draw.TAU/32f, 0.20f, 0xFF74aa00);
		Draw.circle(this.x-dx, (this.y-dy)+0.5d, this.z-dz, RADIUS+wub2, Draw.TAU/32f, 0.15f, 0xFF74aa00);
		Draw.circle(this.x-dx, (this.y-dy)+1.0d, this.z-dz, RADIUS+wub3, Draw.TAU/32f, 0.10f, 0xFF74aa00);
		
		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
	}

}
