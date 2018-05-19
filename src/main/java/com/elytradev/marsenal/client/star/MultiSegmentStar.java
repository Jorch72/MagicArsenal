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

import java.util.Arrays;

import com.elytradev.marsenal.client.Draw;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.Vec3d;

public class MultiSegmentStar extends AbstractStar {
	Vec3d[] lastPoints = {};
	
	protected float halfThickness = 0.2f;
	protected float lengthSquared = 1f;
	
	public void setThickness(float thickness) {
		this.halfThickness = thickness/2f;
	}
	
	public void setSegmentLength(float length) {
		this.lengthSquared = length*length;
	}
	
	public void setNumSegments(int segments) {
		lastPoints = new Vec3d[segments];
		Arrays.fill(lastPoints, new Vec3d(x, y, z));
	}
	
	@Override
	public void tick(float partial) {
		super.tick(partial);
		adjustContrail();
	}
	
	@Override
	public void setPosition(float x, float y, float z) {
		super.setPosition(x, y, z);
		Arrays.fill(lastPoints, new Vec3d(x, y, z));
	}
	
	
	public void adjustPosition(float x, float y, float z) {
		super.setPosition(x, y, z);
		adjustContrail();
	}
	
	private void adjustContrail() {
		//This is a derivative of, and perhaps an improvement upon, the scarf dragger.
		Vec3d prev = new Vec3d(x,y,z);
		for(int i=0; i<lastPoints.length; i++) {
			Vec3d cur = lastPoints[i];
			Vec3d towardsHead = prev.subtract(cur); //previous node is the destination
			float d2 = (float)(towardsHead.x*towardsHead.x + towardsHead.y*towardsHead.y + towardsHead.z*towardsHead.z);
			float toMove = d2-lengthSquared;
			//if (toMove>2f) {
			//	System.out.println("WARNING: UNUSUALLY LARGE CONTRAIL MOVEMENT OF "+toMove);
			//}
			if (toMove>0) {
				Vec3d movement = towardsHead.normalize().scale(Math.sqrt(toMove));
				Vec3d next = cur.add(movement);
				lastPoints[i] = next;
			}
			prev = cur;
		}
	}

	@Override
	public void paint(BufferBuilder buffer) {
		Vec3d prev = new Vec3d(x,y,z);
		boolean first = true;
		Vec3d usw = prev;
		Vec3d use = prev;
		Vec3d dsw = prev;
		Vec3d dse = prev;
		
		for(int i=0; i<lastPoints.length; i++) {
			Vec3d cur = lastPoints[i];
			
			Vec3d north = prev.subtract(cur);
			Vec3d east;
			if (north.x==0 && north.z==0) { //Use a different vector than up
				east = north.crossProduct(new Vec3d(1, 0, 0)).normalize().scale(halfThickness);
			} else {
				east = north.crossProduct(new Vec3d(0, 1, 0)).normalize().scale(halfThickness);
			}
			Vec3d up = north.crossProduct(east).normalize().scale(halfThickness);
			
			//Pretend north (+z) is parallel to the line
			if (first) {
				usw = prev.subtract(east)     .add(up);
				use = prev     .add(east)     .add(up);
				dsw = prev.subtract(east).subtract(up);
				dse = prev     .add(east).subtract(up);
			}
			
			Vec3d unw = cur .subtract(east)     .add(up);
			Vec3d une = cur      .add(east)     .add(up);
			Vec3d dnw = cur .subtract(east).subtract(up);
			Vec3d dne = cur      .add(east).subtract(up);

			Draw._quad(buffer, dsw, dse, dne, dnw, (int)r, (int)g, (int)b, (int)a);
			Draw._quad(buffer, usw, unw, une, use, (int)r, (int)g, (int)b, (int)a);
			Draw._quad(buffer, usw, dsw, dnw, unw, (int)r, (int)g, (int)b, (int)a);
			Draw._quad(buffer, use, une, dne, dse, (int)r, (int)g, (int)b, (int)a);
			Draw._quad(buffer, usw, use, dse, dsw, (int)r, (int)g, (int)b, (int)a);
			Draw._quad(buffer, une, dne, dnw, unw, (int)r, (int)b, (int)b, (int)a);
			
			first = false;
			usw = unw;
			use = une;
			dsw = dnw;
			dse = dne;
		}
		
	}
	
	
}
