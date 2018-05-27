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
import net.minecraft.util.math.Vec3d;

public class ChaosOrbEmitter extends WorldEmitter {
	private int idleTime = 0;
	private double radius = 0;
	private double lastRadius = 0;
	
	private float wubsPerTick = (float)(Math.PI/128f);
	private float wub = 0;
	
	@Override
	public void tick() {
		//Probably nothing?
		//System.out.println("Tick");
		
		idleTime++;
		if (idleTime > 1000) kill();
		
		//CubeStar secondary = new CubeStar();
		//float t = (float)(Math.random()*Draw.TAU);
		//float sx = (float)(radius*Math.cos(t));
		//float sz = (float)(radius*Math.sin(t));
		//secondary.setPosition(sx, this.y, sz);
		//secondary.setColor(0x77FF6622);
		//StarFlinger.spawn(secondary);
	}
	
	@Override
	public void tick(float partialTicks) {
		super.tick(partialTicks);
		
		if (lastRadius<radius) {
			double delta = radius - lastRadius;
			lastRadius += (delta/16d) * partialTicks;
		} else if (lastRadius>radius) {
			double delta = lastRadius - radius;
			lastRadius -= (delta/16d)*partialTicks;
		}
		
		wub += wubsPerTick;
		if (wub>Math.PI*2) wub-= (Math.PI*2);
	}
	
	@Override
	public void paint(BufferBuilder buffer) {
		double wubAmplitude = lastRadius / 64f;
		double curWub = Math.sin(wub)*wubAmplitude;
		
		int steps = (int)(lastRadius * 2f);
		if (steps>8) steps=8;
		for(int i=0; i<steps; i++) {
			int a = 255 - (i*24); if (a<0) a=0;
			int g = 128 + (i*24); if (g<0) g=0;
			sphere(buffer, x, y, z, 14, lastRadius*((i+1)/(float)steps) + curWub, 255, g, 255, a);
		}
	}
	
	public static void sphere(BufferBuilder buffer, double x, double y, double z, int steps, double radius, int r, int g, int b, int a) {
		float harc = Draw.TAU / steps;
		
		for(int yi=0; yi<steps; yi++) {
			double curTheta = (yi / (float)steps) * Math.PI;
			double nextTheta = ((yi+1) / (float)steps) * Math.PI;
			
			double curElevation = Math.cos(curTheta);
			double nextElevation = Math.cos(nextTheta);
			
			double curRadius = Math.sin(curTheta)*radius;
			double nextRadius = Math.sin(nextTheta)*radius;
			
			for(int hi=0; hi<steps; hi++) {
				double curYModel = curElevation * radius;
				double nextYModel = nextElevation * radius;
				
				double x1i = Math.sin(hi*harc);
				double z1i = Math.cos(hi*harc);
				
				double x2i = Math.sin((hi+1)*harc);
				double z2i = Math.cos((hi+1)*harc);
				
				Draw._quad(buffer, x+(x1i*curRadius), y+curYModel, z+(z1i*curRadius), x+(x2i*curRadius), y+curYModel, z+(z2i*curRadius), x+(x2i*nextRadius), y+nextYModel, z+(z2i*nextRadius), x+(x1i*nextRadius), y+nextYModel, z+(z1i*nextRadius), r, g, b, a);
			}
		}
	}
	
	public static void cube(BufferBuilder buffer, double x, double y, double z, double size, int r, int g, int b, int a) {
		Vec3d nwd = new Vec3d(x-size, y-size, z-size);
		Vec3d swd = new Vec3d(x-size, y-size, z+size);
		Vec3d ned = new Vec3d(x+size, y-size, z-size);
		Vec3d sed = new Vec3d(x+size, y-size, z+size);
		
		Vec3d nwu = new Vec3d(x-size, y+size, z-size);
		Vec3d swu = new Vec3d(x-size, y+size, z+size);
		Vec3d neu = new Vec3d(x+size, y+size, z-size);
		Vec3d seu = new Vec3d(x+size, y+size, z+size);
		
		Draw._quad(buffer, swd, sed, seu, swu, (int)r, (int)g, (int)b, (int)a); //South face
		Draw._quad(buffer, sed, ned, neu, seu, (int)r, (int)g, (int)b, (int)a); //East face
		Draw._quad(buffer, ned, nwd, nwu, neu, (int)r, (int)g, (int)b, (int)a); //North face
		Draw._quad(buffer, swd, swu, nwu, nwd, (int)r, (int)g, (int)b, (int)a); //West face
		Draw._quad(buffer, nwd, ned, sed, swd, (int)r, (int)g, (int)b, (int)a); //Bottom face
		Draw._quad(buffer, swu, seu, neu, nwu, (int)r, (int)g, (int)b, (int)a); //Top face
	}
	
	@Override
	public void refreshAndUpdate(NBTTagCompound tag) {
		this.radius = tag.getDouble("Radius");
		this.idleTime = 0;
	}
}
