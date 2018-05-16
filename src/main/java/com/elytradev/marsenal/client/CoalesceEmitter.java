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
import java.util.Random;

import net.minecraft.client.renderer.GlStateManager;

public class CoalesceEmitter extends Emitter {
	private static final float TAU = (float)(2*Math.PI);
	private static final int MAX_STARS = 100;
	private static final float ROTATION_SPEED = TAU/3f * 200;
	private static final int FADEOUT_TICKS = 20*2;
	private Random rnd = new Random();
	private List<Star> dead  = new ArrayList<>();
	private List<Star> stars = new ArrayList<>();
	//                 still
	//                 burn
	private int ticksRemaining = 20*5;
	
	@Override
	public void tick() {
		if (stars.size()<MAX_STARS && ticksRemaining>=FADEOUT_TICKS) {
			Star star = new Star();
			star.move(this.x + (float)rnd.nextGaussian()*0.25f, this.y + 3f, this.z + (float)rnd.nextGaussian());
			star.setVelocity(0f, -60f, 0f);
			star.lifetime = 20;
			star.taild2=0.1f*0.1f;
			
			star.color = 0x7777FF99;
			
			stars.add(star);
		}
		
		if (ticksRemaining<FADEOUT_TICKS) {
			for(Star star : stars) {
				int alpha = (star.color >> 24) & 0xFF;
				float a = alpha / (float)0xFF;
				a = a * 0.90f;
				alpha = (int)(a * 0xFF);
				star.color = (star.color & 0xFFFFFF) | (alpha << 24);
			}
		}
		
		ticksRemaining--;
		if (ticksRemaining<=0) this.kill();
	}

	@Override
	public void draw(float partialFrameTime, double dx, double dy, double dz) {
		GlStateManager.disableLighting();
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		
		for(Star star : stars) {
			star.paint(dx, dy, dz);
			star.tick(partialFrameTime);
			rotateStar(star, partialFrameTime);
			if (star.lifetime<=0) dead.add(star);
		}
		stars.removeAll(dead);
		dead.clear();
		
		GlStateManager.disableBlend();
		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
	}
	
	
	public void rotateStar(Star star, float partialTime) {
		float dx = star.x - this.x;
		float dz = star.z - this.z;
		float r = (float)Math.sqrt(dx*dx + dz*dz); //JVM intrinsics JIT this to either fsqrt or sqrtsd, making this faster than Carmack/Mojang's fastSqrt
		                                           //Reminder: BENCHMARK EVERYTHING YOU INTEND TO OPTIMIZE
		float theta = (float)Math.atan2(dz, dx);
		
		theta+= ROTATION_SPEED*partialTime;
		star.x = x + (float)(Math.cos(theta)*r);
		star.z = z + (float)(Math.sin(theta)*r);
	}
}
