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

import com.elytradev.marsenal.magic.HealingCircleSpell;

import net.minecraft.client.renderer.GlStateManager;

public class HealingSphereEmitter extends Emitter {
	public static final float RADIUS = HealingCircleSpell.RADIUS;
	private int ticksRemaining = 400;
	private float wubTime = 0f;
	
	@Override
	public void tick() {
		ticksRemaining--;
		if (ticksRemaining<=0) kill();
	}

	@Override
	public void draw(float partialFrameTime, double dx, double dy, double dz) {
		GlStateManager.disableLighting();
		GlStateManager.disableTexture2D();
		
		
		wubTime += partialFrameTime*64f;
		double wub = Math.sin(wubTime) * 0.3d;
		double wub2 = Math.sin(wubTime+(Draw.TAU/4f)*1) * 0.3d;
		double wub3 = Math.sin(wubTime+(Draw.TAU/4f)*2) * 0.3d;
		
		Draw.circle(this.x-dx, this.y-dy, this.z-dz, RADIUS+wub, Draw.TAU/32f, 9, 0xFF33FF33);
		Draw.circle(this.x-dx, (this.y-dy)+0.5d, this.z-dz, RADIUS+wub2, Draw.TAU/32f, 7, 0xFF33FF33);
		Draw.circle(this.x-dx, (this.y-dy)+1.0d, this.z-dz, RADIUS+wub3, Draw.TAU/32f, 5, 0xFF33FF33);
		
		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
	}

}
