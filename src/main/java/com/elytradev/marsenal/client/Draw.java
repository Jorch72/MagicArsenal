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


import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Draw {
	public static final float PI  = (float)Math.PI;
	public static final float TAU = (float)(Math.PI*2);
	
	public static void line(double x1, double y1, double z1, double x2, double y2, double z2, int width, int color) {
		int a = (color >> 24) & 0xFF;
		int r = (color >> 16) & 0xFF;
		int g = (color >>  8) & 0xFF;
		int b = (color      ) & 0xFF;
		line(x1,y1,z1,x2,y2,z2,width,r,g,b,a);
	}
	
	public static void line(double x1, double y1, double z1, double x2, double y2, double z2, int width, int r, int g, int b, int a) {
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder vb = tess.getBuffer();

		GlStateManager.glLineWidth(width);
		vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
		
		vb.pos(x1, y1, z1).color(r,g,b,a).endVertex();
		vb.pos(x2, y2, z2).color(r,g,b,a).endVertex();
		tess.draw();
	}
	
	public static void circle(double x, double y, double z, double radius, float accuracy, int width, int color) {
		if (accuracy<=0) accuracy = (TAU/16f);
		for(float f = 0; f<TAU; f+=accuracy) {
			double px1 = x + (radius * Math.cos(f));
			double py = y;
			double pz1 = z + (radius * Math.sin(f));
			double px2 = x + (radius * Math.cos(f+accuracy));
			double pz2 = z + (radius * Math.sin(f+accuracy));
			
			line(px1, py, pz1, px2, py, pz2, width, color);
		}
	}
}
