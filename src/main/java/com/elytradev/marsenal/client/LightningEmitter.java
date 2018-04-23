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

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;

public class LightningEmitter extends Emitter {
	private Random random = new Random();
	private int ticksRemaining = 11;
	
	
	@Override
	public void tick() {
		
		ticksRemaining--;
		if (ticksRemaining<=0) kill();
	}

	@Override
	public void draw(float partialFrameTime, double dx, double dy, double dz) {
		if (source==null || entity==null) {
			kill();
			return;
		}
		
		GlStateManager.disableLighting();
		GlStateManager.disableTexture2D();
		
		
		//if (ticksRemaining<5) {
			Vec3d ptSrc    = new Vec3d(source.posX, source.posY+(source.getEyeHeight())-0.3d, source.posZ);
			Vec3d ptTarget = (entity!=null) ?
					new Vec3d(entity.posX, entity.posY+(entity.height/2), entity.posZ) :
					new Vec3d(x,y,z);
					
			drawBolt(dx, dy, dz, ptSrc, ptTarget, 10, 1.5f, 0.02f, 0xFFffff77);
			drawBolt(dx, dy, dz, ptSrc, ptTarget, 10, 1.5f, 0.004f, 0xFFffee23);
		//}
		
		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
	}
	
	private void drawBolt(double dx, double dy, double dz, Vec3d src, Vec3d dest, int steps, float magnitude, float width, int color) {
		Vec3d lastPt = src;
		
		for(int i=0; i<8; i++) {
			float progress = i/(float)steps;
			float wiggle = progress*magnitude;
			
			Vec3d nextPt = new Vec3d(
					lerp(src.x, dest.x, progress) + uniform(wiggle),
					lerp(src.y, dest.y, progress) + uniform(wiggle),
					lerp(src.z, dest.z, progress) + uniform(wiggle));
			
			Draw.fakeLine(lastPt.x-dx, lastPt.y-dy, lastPt.z-dz, nextPt.x-dx, nextPt.y-dy, nextPt.z-dz, 0.1f, color, true);
			
			lastPt = nextPt;
		}
	}
	
	private static float lerp(double a, double b, float prog) {
		return (float)(a*(1-prog) + b*prog);
	}
	
	public float uniform(float magnitude) {
		float realMag = magnitude*2;
		return random.nextFloat()*realMag - magnitude;
	}
	
}
