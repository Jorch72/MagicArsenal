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

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public strictfp class CubeStar extends AbstractStar {
	protected float size = 1/16f; //as in, extends 1 pixel out in every direction from the center
	protected boolean bounce = false;
	protected float dampening = 0.8f;
	
	@Override
	public strictfp void tick(float partial) {
		if (bounce) {
			World world = Minecraft.getMinecraft().world;
			if (world==null) return;
			
			RayTraceResult trace = world.rayTraceBlocks(getPosition(), getPosition().add(getVelocity()), false);
			
			if (trace!=null && trace.typeOfHit==RayTraceResult.Type.BLOCK) {
				EnumFacing faceHit = trace.sideHit;
				if (faceHit.getFrontOffsetX()!=0 && Math.signum(faceHit.getFrontOffsetX())!=Math.signum(vx)) {
					vx = -vx * dampening;
				}
				if (faceHit.getFrontOffsetY()!=0 && Math.signum(faceHit.getFrontOffsetY())!=Math.signum(vy)) {
					vy = -vy * dampening;
				}
				if (faceHit.getFrontOffsetZ()!=0 && Math.signum(faceHit.getFrontOffsetZ())!=Math.signum(vz)) {
					vz = -vz * dampening;
				}
				//this.vx *= (faceHit.getFrontOffsetX()==0) ? 1 : -1;
				//this.vy *= (faceHit.getFrontOffsetY()==0) ? 1 : -1;
				//this.vz *= (faceHit.getFrontOffsetZ()==0) ? 1 : -1;
				this.setPosition((float)trace.hitVec.x + (faceHit.getFrontOffsetX()*(1.16f)), (float)trace.hitVec.y + (faceHit.getFrontOffsetY()*(1.16f)), (float)trace.hitVec.z + (faceHit.getFrontOffsetZ()*(1.16f)));
				timeRemaining -= partial;
				return;
			}
		}
		
		super.tick(partial);
	}
	
	public void setCollides(boolean collides) {
		bounce = collides;
	}
	
	public void setSize(float size) {
		this.size = size;
	}
	
	@Override
	public void paint(BufferBuilder buffer) {
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
}
