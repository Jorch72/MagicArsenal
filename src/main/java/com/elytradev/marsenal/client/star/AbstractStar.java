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

package com.elytradev.marsenal.client.star;

import java.util.Objects;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.Vec3d;

public abstract strictfp class AbstractStar implements IStar, Comparable<IStar> {
	protected float x = 0;
	protected float y = 0;
	protected float z = 0;
	protected float vx = 0;
	protected float vy = 0;
	protected float vz = 0;
	protected float r = 255;
	protected float g = 255;
	protected float b = 255;
	protected float a = 255;
	protected float vr = 0;
	protected float vg = 0;
	protected float vb = 0;
	protected float va = 0;
	protected float ax = 0;
	protected float ay = 0;
	protected float az = 0;
	
	protected float timeRemaining = 20*5;
	
	@Override
	public void tick(float partial) {
		x += vx * partial;
		y += vy * partial;
		z += vz * partial;
		vx += ax * partial;
		vy += ay * partial;
		vz += az * partial;
		r += vr*partial;
		if (r<0) r=0;
		if (r>255) r=255;
		g += vg*partial;
		if (g<0) g=0;
		if (g>255) g=255;
		b += vb*partial;
		if (b<0) b=0;
		if (b>255) b=255;
		a += va*partial;
		if (a<0) a=0;
		if (a>255) a=255;
		timeRemaining-= partial;
	}
	
	public void setColor(int color) {
		a = (color >>> 24) & 0xFF;
		r = (color >>> 16) & 0xFF;
		g = (color >>>  8) & 0xFF;
		b = (color       ) & 0xFF;
	}
	
	public void setColorVelocity(float r, float g, float b, float a) {
		this.vr = r;
		this.vg = g;
		this.vb = b;
		this.va = a;
	}
	
	public void fuzzColor(float r, float g, float b) {
		float rFuzz = (float)(Math.random()*r)+(r*0.5f);
		float gFuzz = (float)(Math.random()*g)+(g*0.5f);
		float bFuzz = (float)(Math.random()*b)+(b*0.5f);
		
		int rf = (int)(rFuzz*255);
		int gf = (int)(gFuzz*255);
		int bf = (int)(bFuzz*255);
		
		fuzzColor(rf, gf, bf);
	}
	
	public void fuzzColor(int rr, int gg, int bb) {
		r += rr;
		if (r<0) r=0;
		if (r>255) r=255;
		
		g += gg;
		if (g<0) g=0;
		if (g>255) g=255;
		
		b += bb;
		if (b<0) b=0;
		if (b>255) b=255;
	}
	
	public void setPosition(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void setVelocity(float vx, float vy, float vz) {
		this.vx = vx;
		this.vy = vy;
		this.vz = vz;
	}
	
	public void setVelocity(Vec3d vec) {
		setVelocity((float)vec.x, (float)vec.y, (float)vec.z);
	}
	
	public void setAcceleration(float ax, float ay, float az) {
		this.ax = ax;
		this.ay = ay;
		this.az = az;
	}
	
	public void setLifetime(float lifetime) {
		timeRemaining = lifetime;
	}
	
	public void pointAt(float x, float y, float z, float v) {
		Vec3d vv = new Vec3d(x-this.x, y-this.y, z-this.z).normalize().scale(v);
		vx = (float)vv.x;
		vy = (float)vv.y;
		vz = (float)vv.z;
	}

	@Override
	public abstract void paint(BufferBuilder buffer);

	@Override
	public Vec3d getPosition() {
		return new Vec3d(x, y, z);
	}
	
	public float getX() { return this.x; }
	public float getY() { return this.y; }
	public float getZ() { return this.z; }
	
	@Override
	public Vec3d getVelocity() {
		return new Vec3d(vx, vy, vz);
	}
	
	@Override
	public boolean isDead() {
		return timeRemaining<0;
	}
	
	@Override
	public int compareTo(IStar star) {
		return Objects.compare(this, star, (a,b)->Integer.compare(a.hashCode(), b.hashCode()));
	}
}
