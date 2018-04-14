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
	
	public boolean intercept;
	public float tx;
	public float ty;
	public float tz;
	
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
	
	public void setAcceleration(float x, float y, float z) {
		vx=x;
		vy=y;
		vz=z;
	}
	
	public void setAcceleration(Vec3d vec) {
		setAcceleration((float)vec.x, (float)vec.y, (float)vec.z);
	}
	
	public void paint(double dx, double dy, double dz) {
		Draw.fakeLine(tailx-dx, taily-dy, tailz-dz, x-dx, y-dy, z-dz, width, color, true);
	}
}
