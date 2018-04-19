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

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;

public class MagmaBlastEmitter extends Emitter {
	private int ticksRemaining = 20;
	private Random random = new Random();
	
	private ArrayList<Star> dead = new ArrayList<>();
	private ArrayList<Star> stars = new ArrayList<>();
	//                      still
	//                      burn
	
	@Override
	public void tick() {
		if (entity!=null && ticksRemaining>15) {
			for(int i=0; i<32; i++) {
				Vec3d lookVec = entity.getLookVec();
				lookVec = lookVec.addVector(random.nextGaussian()*0.5f, random.nextGaussian()*0.5f, random.nextGaussian()*0.5f).normalize(); //Fuzz
				lookVec = lookVec.scale(800.0f); //velocity magnitude
				
				Star star = new Star();
				star.width = 0.1f;
				star.taild2 = 1.5f;
				//star.limit = 0.4f;
				star.move((float)entity.posX, (float)entity.posY+entity.getEyeHeight(), (float)entity.posZ);
				star.color = 0xFFff3300;
				star.fuzzColor(0.6f, 0.6f, 0.3f);
				star.lifetime = 2;
				star.setVelocity(lookVec);
				stars.add(star);
			}
		}
		
		
		ticksRemaining--;
		if (ticksRemaining<=0) kill();
	}
	
	@Override
	public void draw(float partialFrameTime, double dx, double dy, double dz) {
		GlStateManager.disableLighting();
		GlStateManager.disableTexture2D();
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
		GlStateManager.enableTexture2D();
	}

}
