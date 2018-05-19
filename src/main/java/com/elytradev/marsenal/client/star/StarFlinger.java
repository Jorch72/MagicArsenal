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
import java.util.Collection;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.elytradev.marsenal.client.PartialTickTime;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class StarFlinger {
	private static BufferBuilder buffer = null;
	private static int maxStars = 200;
	/* Another possible way to solve this problem, and I may switch to this for performance reasons later, would be to
	 * ping stars back and forth between two arrays like a double-buffer, and potentially use Arrays.fill() on the size
	 * difference to clear out any extra old entries so they can GC after two frames. Let's keep benchmarking, friends!
	 */
	private static List<IStar> globalStars = new ArrayList<>();
	private static List<IStar> deadStars = new ArrayList<>();
	private static float frameTime;
	
	public static BufferBuilder startBatch() {
		if (buffer!=null) {
			buffer.finishDrawing();
		}
		
		frameTime = PartialTickTime.getFrameTime();
		GlStateManager.disableLighting();
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.depthMask(false);
		GlStateManager.disableCull();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		
		Tessellator tess = Tessellator.getInstance();
		buffer = tess.getBuffer();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		
		return buffer;
	}
	
	public static void paint(Collection<IStar> stars, boolean tick) {
		if (stars.size()<=0) return;
		
		//System.out.println("Painting "+stars.size()+" stars at frame interval "+frameTime);
		for(IStar star : stars) {
			if (buffer!=null) star.paint(buffer);
			if (tick) {
				star.tick(frameTime);
				if (star.isDead()) deadStars.add(star);
			}
		}
		
		if (tick) {
			stars.removeAll(deadStars);
			deadStars.clear();
		}
	}
	
	public static void paintAndTickGlobalStars() {
		paint(globalStars, true);
	}
	
	public static void endBatch() {
		Tessellator.getInstance().draw();
		
		GlStateManager.enableCull();
		//GlStateManager.disableAlpha();
		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
		
		buffer = null;
	}
	
	private static int getEffectiveCap() {
		switch (Minecraft.getMinecraft().gameSettings.particleSetting) {
		case 0: return maxStars;
		case 1: return maxStars >> 1;
		case 2: return maxStars >> 2;
		default: return maxStars >> 3;
		}
	}
	
	public static void spawn(IStar star) {
		if(globalStars.size()>=getEffectiveCap()) return; //Refuse to spawn too many stars
		
		globalStars.add(star);
	}
}
