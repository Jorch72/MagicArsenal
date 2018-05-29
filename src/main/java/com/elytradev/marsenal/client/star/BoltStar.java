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

import java.util.ArrayList;

import com.elytradev.marsenal.client.Draw;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.Vec3d;

public strictfp class BoltStar extends AbstractStar {
	ArrayList<Vec3d> points = new ArrayList<Vec3d>();
	Vec3d source = Vec3d.ZERO;
	Vec3d target = Vec3d.ZERO;
	
	public BoltStar() {
		
	}
	
	@Override
	public void tick(float partial) {
		if (points.isEmpty()) setupPoints();
		
		//Replace the super's impl with mostly the same code, but the bolt doesn't move *itself* with its position velocity.
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
		
		double r2 = source.squareDistanceTo(target);
		for(int i=0; i<points.size(); i++) {
			Vec3d old = points.get(i);
			Vec3d cur = old.addVector(vx*partial, vy*partial, vz*partial);
			points.set(i, cur);
			if (cur.squareDistanceTo(target) > r2) {
				points.remove(i);
				cur = new Vec3d(
						x+Math.random()*1.0 - 0.5,
						y+Math.random()*1.0 - 0.5,
						z+Math.random()*1.0 - 0.5);
				points.add(i, cur);
				//Since we remove and then add, both before the iteration marker, we should still be sync'd
			}
		}
	}
	
	public void setupPoints() {
		Vec3d travelVector = target.subtract(source);
		int r = (int)(travelVector.lengthVector());
		Vec3d spacing = travelVector.normalize().scale(0.5);
		
		for(int i=0; i<r; i++) {
			Vec3d basis = new Vec3d(x + spacing.x*i, y + spacing.y*i, z + spacing.z*i);
			
			Vec3d cur = new Vec3d(
						basis.x+Math.random()*1.0 - 0.5,
						basis.y+Math.random()*1.0 - 0.5,
						basis.z+Math.random()*1.0 - 0.5);
			points.add(cur);
		}
	}
	
	@Override
	public strictfp void setPosition(float x, float y, float z) {
		super.setPosition(x, y, z);
		this.source = new Vec3d(x,y,z);
	}
	
	@Override
	public void pointAt(float x, float y, float z, float v) {
		super.pointAt(x, y, z, v);
		this.target = new Vec3d(x,y,z);
	}
	
	@Override
	public void paint(BufferBuilder buffer) {
		Vec3d previous = source;
		float width = 0.4f;
		for(int i=0; i<points.size(); i++) {
			Vec3d cur = points.get(i);
			width -= 0.05f;
			if (width<0.1f) width=0.1f;
			
			Draw.fastLine(previous.x, previous.y, previous.z, cur.x, cur.y, cur.z, width, (int)r, (int)g, (int)b, (int)a, true);
			Draw.fastLine(previous.x, previous.y, previous.z, cur.x, cur.y, cur.z, width*2, (int)r, (int)g, (int)b, (int)a/2, true);
			
			previous = cur;
		}
	}
	
	
}
