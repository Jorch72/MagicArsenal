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

import com.elytradev.marsenal.client.star.CubeStar;
import com.elytradev.marsenal.client.star.StarFlinger;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;

public class ChaosOrbEmitter extends WorldEmitter {
	private int idleTime = 0;
	private double radius = 0;
	
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
	public void paint(BufferBuilder buffer) {
		//System.out.println("Painting at "+x+","+y+","+z+" r="+radius);
		//Draw.fastCircle(this.x, this.y, this.z, radius, 12, 1, 0xFF000000);
		//Draw.fastCircle(this.x, this.y, this.z, radius, 12, 2, 0x33000000);
		
		cube(buffer, x, y, z, radius, 255, 255, 255, 64);
		cube(buffer, x, y, z, radius*0.9, 255, 255, 255, 175);
		cube(buffer, x, y, z, radius*0.8, 255, 255, 255, 195);
		cube(buffer, x, y, z, radius*0.7, 255, 255, 255, 215);
		cube(buffer, x, y, z, radius*0.6, 255, 255, 255, 235);
		cube(buffer, x, y, z, radius*0.5, 255, 255, 255, 255);
		
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
