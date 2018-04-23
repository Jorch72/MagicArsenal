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

import com.elytradev.marsenal.entity.EntityWillOWisp;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public strictfp class RenderWillOWisp extends Render<EntityWillOWisp> {
	protected RenderWillOWisp(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityWillOWisp entity) {
		
		return null;
	}
	
	@Override
	public void doRender(EntityWillOWisp entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
		GlStateManager.disableLighting();
		GlStateManager.disableTexture2D();
		
		int max = 12;
		for(int i=0; i<max; i++) {
			float progress = i/(float)max;
			float radianProgress = progress*Draw.PI;
			float radius = (float)Math.sin(radianProgress);
			float height = 0.4f + (float)Math.cos(radianProgress)*0.4f;
			Draw.circle(x, y+height, z, 0.4f*radius, Draw.TAU/10, 0.02f, 0xFFffff77);
			//Draw.circle(x, y+0.25f, z, 0.5f, Draw.TAU/10, 0.04f, 0xFF7777FF);
		
		}
		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
	}
}
