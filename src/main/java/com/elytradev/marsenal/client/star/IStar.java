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

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.Vec3d;

/**
 * Stars are participants in a particle system.
 * 
 * <h2>Timing and Units</h2>
 * All times are specified in ticks (20ths of a second). This goes for time-derivatives as well: velocity is meters per
 * tick, acceleration is meters per tick per tick, and so on. However, the simulation operates on a variable timestep,
 * so delta times can be anything a tiny fraction of a tick to {@code PartialTickTime.DISCONTINUITY_THRESHOLD} (default
 * is 3).
 * 
 */
public interface IStar {
	/** Run this Star's logic: Add {@code velocity * partial} to position, expire, and/or make decisions
	 * @param partial The number of ticks elapsed since last call to tick, in ticks (20ths of a second).
	 */
	void tick(float partial);
	void paint(BufferBuilder buffer);
	Vec3d getPosition();
	Vec3d getVelocity();
	boolean isDead();
}
