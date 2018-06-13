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

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.nbt.NBTTagCompound;

public class RadiantBeaconEmitter extends WorldEmitter {
	private float wubsPerTick = (float)(Math.PI/512f);
	private float wub =(float)(Math.random()*Math.PI*2.0);
	
	private int idleTime = 0;
	
	private double radius = 0f;
	private double lastRadius = -1f;
	
	private double lastRed = 255;
	private double lastGreen = 255;
	private double lastBlue = 255;
	private double lastAlpha = 255;
	
	private int red = 255;
	private int green = 255;
	private int blue = 255;
	private int alpha = 0;
	
	@Override
	public void refreshAndUpdate(NBTTagCompound tag) {
		this.radius = tag.getDouble("Radius");
		if (lastRadius < 0) lastRadius = radius; //Prevent warp-in effects at login, but allow warp-in for 0->X transitions
		
		//HSV -> RGB
		double hue = tag.getDouble("Hue");
		if (hue<0) hue+= 360d;
		double chroma = 1.0d*1.0d; //value == 1.0, saturation == 1.0
		double hueSegment = hue/60d;
		double chromaX = chroma * (1 - Math.abs((hueSegment % 2) - 1));
		switch((int)hueSegment) {
		case 0:
			red = 255; //C
			green = (int)(chromaX*255d);
			blue = 0;
			break;
		case 1:
			red = (int)(chromaX*255d);
			green = 255; //C
			blue = 0;
			break;
		case 2:
			red = 0;
			green = 255; //C
			blue = (int)(chromaX*255d);
			break;
		case 3:
			red = 0;
			green = (int)(chromaX*255d);
			blue = 255; //C
			break;
		case 4:
			red = (int)(chromaX*255d);
			green = 0;
			blue = 255; //C
			break;
		case 5:
			red = 255; //C
			green = 0;
			blue = (int)(chromaX*255d);
			break;
		}
		
		alpha = 8;
		
		this.idleTime = 0;
	}
	
	@Override
	public void paint(BufferBuilder buffer) {
		double wubAmplitude = lastRadius / 128f;
		double curWub = Math.sin(wub)*wubAmplitude;
		double wub2 = Math.sin(wub + Math.PI/4) * wubAmplitude;
		
		
		ChaosOrbEmitter.sphere(buffer, x, y, z, 36, lastRadius + curWub, (int)lastRed, (int)lastGreen, (int)lastBlue, (int)lastAlpha);
		ChaosOrbEmitter.sphere(buffer, x, y, z, 36, lastRadius*0.995 + wub2, (int)lastRed, (int)lastGreen, (int)lastBlue, (int)lastAlpha);
	}
	
	@Override
	public void tick() {
		idleTime++;
		if (idleTime > 1000) {
			kill();
		}
	}
	
	public static final double moveToward(double last, double next, double partialTicks) {
		if (last<next) {
			double delta = next-last;
			return last + ((delta/16d) * partialTicks);
		} else if (last>next) {
			double delta = last-next;
			return last - ((delta/16d) * partialTicks);
		} else {
			return last;
		}
	}
	
	@Override
	public void tick(float partialTicks) {
		super.tick(partialTicks);
		
		lastRadius = moveToward(lastRadius, radius, partialTicks);
		lastRed = moveToward(lastRed, red, partialTicks);
		lastGreen = moveToward(lastGreen, green, partialTicks);
		lastBlue = moveToward(lastBlue, blue, partialTicks);
		lastAlpha = moveToward(lastAlpha, alpha, partialTicks);
		
		wub += wubsPerTick;
		if (wub>Math.PI*2) wub-= (Math.PI*2);
	}

}
