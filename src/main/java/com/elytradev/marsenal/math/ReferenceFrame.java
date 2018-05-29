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

package com.elytradev.marsenal.math;

import net.minecraft.util.math.Vec3d;

public class ReferenceFrame {
	
	//The "forward matrix" to get coordinates from global-space into local-space
	protected double[] matrix = {
			1.0, 0.0, 0.0,
			0.0, 1.0, 0.0,
			0.0, 0.0, 1.0 };
	protected double[] inverse;
	
	
	protected Vec3d origin;
	
	protected Vec3d xAxis;
	protected Vec3d yAxis;
	protected Vec3d zAxis;
	
	/** The rotation of the frame around the global Y axis */
	protected double inclination;
	/** The rotation of the frame around the global Z axis */
	protected double azimuth;
	
	public Vec3d xAxis() {
		return xAxis;
	}
	
	public Vec3d yAxis() {
		return yAxis;
	}
	
	public Vec3d zAxis() {
		return zAxis;
	}
	
	public Vec3d pointAt(Vec3d vec) {
		double dx = vec.x - origin.x;
		double dy = vec.y - origin.y;
		double dz = vec.z - origin.z;
		
		
		double xzDistance = Math.sqrt(dx*dx + dz*dz);
		
		inclination = Math.atan2(dz, dx);
		azimuth = Math.atan2(dy, xzDistance);
		
		
		
		
		return vec; //TODO: Implement
	}
	
	/** Transform a direction vector into global space */
	//public Vec3d orient2Global(Vec3d vec) {
		
		
	//}
	
	public Vec3d global2Local(Vec3d vec) {
		return vec; //TODO: Implement
	}
}
