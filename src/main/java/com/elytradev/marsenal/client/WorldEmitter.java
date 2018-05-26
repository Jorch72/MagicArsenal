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

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.nbt.NBTTagCompound;

public abstract class WorldEmitter extends Emitter {
	private float tickBuffer = 0;
	public void refreshAndUpdate(NBTTagCompound tag) {
		//Do nothing by default
	}
	
	@Override
	public void draw(float partialFrameTime, double dx, double dy, double dz) {}
	
	public void tick(float partialTicks) {
		tickBuffer += partialTicks;
		if (tickBuffer>20) {
			tickBuffer -= 20f;
			tick();
		}
	}
	
	public abstract void paint(BufferBuilder buffer); //Swap out paint for a better paint
}
