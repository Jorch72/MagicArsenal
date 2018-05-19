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

import com.elytradev.marsenal.client.Draw;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.Vec3d;

public strictfp class LineStar extends AbstractStar {
	protected float halfThickness = 0.2f;
	protected float lengthSquared = 1f;
	protected float tailx = 0;
	protected float taily = 0;
	protected float tailz = 0;
	
	public void setThickness(float thickness) {
		this.halfThickness = thickness/2f;
	}
	
	public void setLength(float length) {
		this.lengthSquared = length*length;
	}
	
	@Override
	public void tick(float partial) {
		super.tick(partial);
		
		Vec3d towardsHead = new Vec3d(x-tailx, y-taily, z-tailz);
		float d2 = (float)(towardsHead.x*towardsHead.x + towardsHead.y*towardsHead.y + towardsHead.z*towardsHead.z);
		float toMove = d2-lengthSquared;
		if (toMove>0) {
			Vec3d movement = towardsHead.normalize().scale(toMove);
			tailx+=movement.x;
			taily+=movement.y;
			tailz+=movement.z;
		}
	}
	
	@Override
	public void setPosition(float x, float y, float z) {
		super.setPosition(x, y, z);
		tailx = x;
		taily = y;
		tailz = z;
	}
	
	@Override
	public void paint(BufferBuilder buffer) {
		Vec3d north = new Vec3d(x-tailx, y-taily, z-tailz);
		Vec3d east;
		if (north.x==0 && north.z==0) { //Use a different vector than up
			east = north.crossProduct(new Vec3d(1, 0, 0)).normalize().scale(halfThickness);
		} else {
			east = north.crossProduct(new Vec3d(0, 1, 0)).normalize().scale(halfThickness);
		}
		Vec3d up = north.crossProduct(east).normalize().scale(halfThickness);
		
		//Pretend north (+z) is parallel to the line
		Vec3d usw = new Vec3d(x     - east.x + up.x,     y - east.y + up.y, z     -east.z + up.z);
		Vec3d use = new Vec3d(x     + east.x + up.x,     y + east.y + up.y, z     +east.z + up.z);
		Vec3d unw = new Vec3d(tailx - east.x + up.x, taily - east.y + up.y, tailz -east.z + up.z);
		Vec3d une = new Vec3d(tailx + east.x + up.x, taily + east.y + up.y, tailz +east.z + up.z);
		Vec3d dsw = new Vec3d(x     - east.x - up.x,     y - east.y - up.y, z     -east.z - up.z);
		Vec3d dse = new Vec3d(x     + east.x - up.x,     y + east.y - up.y, z     +east.z - up.z);
		Vec3d dnw = new Vec3d(tailx - east.x - up.x, taily - east.y - up.y, tailz -east.z - up.z);
		Vec3d dne = new Vec3d(tailx + east.x - up.x, taily + east.y - up.y, tailz +east.z - up.z);

		Draw._quad(buffer, dsw, dse, dne, dnw, (int)r, (int)g, (int)b, (int)a);
		Draw._quad(buffer, usw, unw, une, use, (int)r, (int)g, (int)b, (int)a);
		Draw._quad(buffer, usw, dsw, dnw, unw, (int)r, (int)g, (int)b, (int)a);
		Draw._quad(buffer, use, une, dne, dse, (int)r, (int)g, (int)b, (int)a);
		Draw._quad(buffer, usw, use, dse, dsw, (int)r, (int)g, (int)b, (int)a);
		Draw._quad(buffer, une, dne, dnw, unw, (int)r, (int)b, (int)b, (int)a);
	}
	
}
