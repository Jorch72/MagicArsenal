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

import java.util.function.Consumer;

import net.minecraft.util.math.Vec3d;

public strictfp class Star {
	public float x;
	public float y;
	public float z;
	public float vx;
	public float vy;
	public float vz;
	public float tailx;
	public float taily;
	public float tailz;
	public float taild2 = 2f;
	public float width = 0.1f;
	public int color = 0xFFFFFFFF;
	public float acceleration = 60_000f;
	public float limit = 1024f;
	
	public boolean intercept;
	public float tx;
	public float ty;
	public float tz;
	public float interceptRange;
	public Consumer<Star> onIntercept;
	
	public float lifetime = 20*1;
	
	public void tick(float partial) {
		x += vx*partial;
		y += vy*partial;
		z += vz*partial;
		
		//yank tail towards head
		Vec3d towardsHead = new Vec3d(x-tailx, y-taily, z-tailz);
		float d2 = (float)(towardsHead.x*towardsHead.x + towardsHead.y*towardsHead.y + towardsHead.z*towardsHead.z);
		float toMove = d2-taild2;
		if (toMove>0) {
			Vec3d movement = towardsHead.normalize().scale(toMove);
			tailx+=movement.x;
			taily+=movement.y;
			tailz+=movement.z;
		}
		
		//check for intercepts
		if (intercept) {
			float dx = tx-x;
			float dy = ty-y;
			float dz = tz-z;
			float delta2 = dx*dx+dy*dy+dz*dz;
			if (delta2<interceptRange*interceptRange) {
				if (onIntercept!=null) onIntercept.accept(this);
				lifetime-=partial;
				return;
			}
			
			//Accelerate towards intercept
			//System.out.println("Accelerating towards intercept- delta:"+dx+","+dy+","+dz+" vel:"+vx+","+vy+","+vz);
			if (dx<-interceptRange) vx-= acceleration*partial; if (vx<-limit) vx=-limit;
			if (dx>interceptRange) vx+= acceleration*partial;  if (vx> limit) vx= limit;
			
			if (dy<-interceptRange) vy-= acceleration*partial; if (vy<-limit) vy=-limit;
			if (dy>interceptRange) vy+= acceleration*partial;  if (vy> limit) vy= limit;
			
			if (dz<-interceptRange) vz-= acceleration*partial; if (vz<-limit) vz=-limit;
			if (dz>interceptRange) vz+= acceleration*partial;  if (vz> limit) vz= limit;
			//System.out.println("Resulting vel:"+vx+","+vy+","+vz);
		}
		
		lifetime-=partial;
	}
	
	public void move(float x, float y, float z) {
		this.x=x;
		this.y=y;
		this.z=z;
		this.tailx=x;
		this.taily=y;
		this.tailz=z;
	}
	
	public void setVelocity(float x, float y, float z) {
		vx=x;
		vy=y;
		vz=z;
	}
	
	public void setVelocity(Vec3d vec) {
		setVelocity((float)vec.x, (float)vec.y, (float)vec.z);
	}
	
	public void paint(double dx, double dy, double dz) {
		Draw.fakeLine(tailx-dx, taily-dy, tailz-dz, x-dx, y-dy, z-dz, width, color, true);
	}
	
	public void fuzzColor(float r, float g, float b) {
		float rFuzz = (float)(Math.random()*r)+(r*0.5f);
		float gFuzz = (float)(Math.random()*g)+(g*0.5f);
		float bFuzz = (float)(Math.random()*b)+(b*0.5f);
		
		int rf = (int)(rFuzz*255);
		int gf = (int)(gFuzz*255);
		int bf = (int)(bFuzz*255);
		
		int oldr = (color >> 16) & 0xFF;
		int oldg = (color >>  8) & 0xFF;
		int oldb = (color      ) & 0xFF;
		int olda = (color >> 24) & 0xFF;
		
		int newr = oldr+rf;
		if (newr<0) newr=0;
		if (newr>255) newr=255;
		
		int newg = oldg+gf;
		if (newg<0) newg=0;
		if (newg>255) newg=255;
		
		int newb = oldb+bf;
		if (newb<0) newb=0;
		if (newb>255) newb=255;
		
		this.color =
				(olda << 24) |
				(newr << 16) |
				(newg <<  8) |
				(newb      );
	}
}
