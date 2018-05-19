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

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

/**
 * Mutable Vec3 which uses integer math and retains uniform precision of 1/256th across its entire "covered" space. The
 * tradeoff is that the covered area is much smaller than a traditional Vec3d; however, this coverage is
 * about +/- 36,028,797,019,000,000 - far larger than the +/-29,999,984 Minecraft range.
 * 
 */
public class Vec3Fixed {
	protected static final int FRACT_BITS = 16; //6-byte int (approx. +/- 1.4e+14)
	protected static final long FRACT_SCALE = (long)Math.pow(2, FRACT_BITS);
	protected long x;
	protected long y;
	protected long z;
	
	public Vec3Fixed() {
		x=0;
		y=0;
		z=0;
	}
	
	public Vec3Fixed(double x, double y, double z) {
		setTo(x, y, z);
	}
	
	public double getX() {
		return x / (double)FRACT_SCALE;
	}
	
	public double getY() {
		return y / (double)FRACT_SCALE;
	}
	
	public double getZ() {
		return z / (double)FRACT_SCALE;
	}
	
	public long floorX() {
		return x >> FRACT_BITS;
	}
	
	public long floorY() {
		return y >> FRACT_BITS;
	}
	
	public long floorZ() {
		return z >> FRACT_BITS;
	}
	
	public Vec3Fixed setTo(double x, double y, double z) {
		this.x = (int)(x*FRACT_SCALE);
		this.y = (int)(y*FRACT_SCALE);
		this.z = (int)(z*FRACT_SCALE);
		return this;
	}
	
	public Vec3Fixed setTo(Vec3d vec) {
		return setTo(vec.x, vec.y, vec.z);
	}
	
	public Vec3Fixed setTo(Vec3i vec) {
		this.x = ((long)vec.getX()) << FRACT_BITS;
		this.y = ((long)vec.getY()) << FRACT_BITS;
		this.z = ((long)vec.getZ()) << FRACT_BITS;
		return this;
	}
	
	public Vec3Fixed add(double x, double y, double z) {
		this.x = this.x + (long)(x*FRACT_SCALE);
		this.y = this.y + (long)(y*FRACT_SCALE);
		this.z = this.z + (long)(z*FRACT_SCALE);
		return this;
	}
	
	public Vec3Fixed add(Vec3d other) {
		return add(other.x, other.y, other.z);
	}
	
	public Vec3Fixed add(Vec3Fixed other) {
		this.x += other.x;
		this.y += other.y;
		this.z += other.z;
		return this;
	}
	
	public Vec3Fixed scale(double scale) {
		this.x = (long)(this.x*scale);
		this.y = (long)(this.y*scale);
		this.z = (long)(this.z*scale);
		return this;
	}
	
	/** Special-case optimization of scale, will scale this vector by the specified amount, and return this vector for more chaining. */
	public Vec3Fixed scale(long scale) {
		this.x *= scale;
		this.y *= scale;
		this.z *= scale;
		return this;
	}
	
	/** Special-case optimization of scale, if you have an whole-number divisor but a fractional numerator, the divide will be faster. */
	public Vec3Fixed divide(long divisor) {
		this.x /= divisor;
		this.y /= divisor;
		this.z /= divisor;
		return this;
	}
	
	public Vec3Fixed normalize() {
		double magnitude = Math.sqrt(x*x + y*y + z*z); //Since we're already IN integer math, we could multiply by fastInvSqrt instead of dividing here, but I don't even know why I'm writing this class so let's just not.
		x /= magnitude;
		y /= magnitude;
		z /= magnitude;
		return this;
	}
	
	/** Returns a NEW non-normalized vector which represents the normalized direction from this vector to the specified coordinates */
	public Vec3Fixed pointAt(double x, double y, double z) {
		double dx = x-getX();
		double dy = y-getY();
		double dz = z-getZ();
		double magnitude = Math.sqrt(dx*dx + dy*dy + dz*dz);
		dx /= magnitude;
		dy /= magnitude;
		dz /= magnitude;
		return new Vec3Fixed(dx,dy,dz);
	}
	
	public Vec3Fixed pointAt(Vec3Fixed other) {
		//We can stay in bit-shifted units
		long dx = other.x-x;
		long dy = other.y-y;
		long dz = other.z-z;
		double magnitude = Math.sqrt(dx*dx + dy*dy + dz*dz);
		dx /= magnitude;
		dy /= magnitude;
		dz /= magnitude;
		Vec3Fixed result = new Vec3Fixed();
		result.x = dx;
		result.y = dy;
		result.z = dz;
		return result;
	}
	
	/** Returns a NEW vector which is the cross-product of this vector and the one specified */
	public Vec3Fixed crossProduct(Vec3Fixed other) {
		Vec3Fixed result = new Vec3Fixed();
		result.x = (this.y * other.z) - (this.z * other.y);
		result.y = (this.z * other.x) - (this.x * other.z);
		result.z = (this.x * other.y) - (this.y * other.x);
		return result;
	}
	
	public Vec3d toVec3d() {
		return new Vec3d(getX(), getY(), getZ());
	}
	
	public Vec3Fixed copy() {
		Vec3Fixed result = new Vec3Fixed();
		result.x = x;
		result.y = y;
		result.z = z;
		return result;
	}
	
	@Override
	protected Vec3Fixed clone() {
		return copy();
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof Vec3Fixed &&
			((Vec3Fixed)other).x == x &&
			((Vec3Fixed)other).y == y &&
			((Vec3Fixed)other).z == z;
			
	}
	
	@Override
	public int hashCode() {
		//Somewhat unrolled; the answer should be identical to Objects.hashCode(x,y,z) but with fewer objects involved.
		int result = 1;
		result = 31 * result + ((int)(x^(x>>>32)));
		result = 31 * result + ((int)(y^(y>>>32)));
		result = 31 * result + ((int)(z^(z>>>32)));
		
		return result;
	}
}
