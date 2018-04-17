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
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Draw {
	public static final float PI  = (float)Math.PI;
	public static final float TAU = (float)(Math.PI*2);
	
	public static void line(double x1, double y1, double z1, double x2, double y2, double z2, float width, int color) {
		int a = (color >> 24) & 0xFF;
		int r = (color >> 16) & 0xFF;
		int g = (color >>  8) & 0xFF;
		int b = (color      ) & 0xFF;
		line(x1,y1,z1,x2,y2,z2,width,r,g,b,a);
	}
	
	public static void line(double x1, double y1, double z1, double x2, double y2, double z2, float width, int r, int g, int b, int a) {
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder vb = tess.getBuffer();

		GlStateManager.glLineWidth(width);
		vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
		
		vb.pos(x1, y1, z1).color(r,g,b,a).endVertex();
		vb.pos(x2, y2, z2).color(r,g,b,a).endVertex();
		tess.draw();
	}
	
	public static void _quad(BufferBuilder vb, double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4, int r, int g, int b, int a) {
		vb.pos(x1, y1, z1).color(r,g,b,a).endVertex();
		vb.pos(x2, y2, z2).color(r,g,b,a).endVertex();
		vb.pos(x3, y3, z3).color(r,g,b,a).endVertex();
		vb.pos(x4, y4, z4).color(r,g,b,a).endVertex();
	}
	
	public static void _quad(BufferBuilder buf, Vec3d va, Vec3d vb, Vec3d vc, Vec3d vd, int r, int g, int b, int a)  {
		buf.pos(va.x, va.y, va.z).color(r,g,b,a).endVertex();
		buf.pos(vb.x, vb.y, vb.z).color(r,g,b,a).endVertex();
		buf.pos(vc.x, vc.y, vc.z).color(r,g,b,a).endVertex();
		buf.pos(vd.x, vd.y, vd.z).color(r,g,b,a).endVertex();
	}
	
	public static void fakeLine(double x1, double y1, double z1, double x2, double y2, double z2, float width, int color, boolean endcap) {
		int a = (color >> 24) & 0xFF;
		int r = (color >> 16) & 0xFF;
		int g = (color >>  8) & 0xFF;
		int b = (color      ) & 0xFF;
		fakeLine(x1, y1, z1, x2, y2, z2, width, r, g, b, a, endcap);
	}
	
	public static void fakeLine(double x1, double y1, double z1, double x2, double y2, double z2, float width, int r, int g, int b, int a, boolean endcap) {
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder vb = tess.getBuffer();
		
		/* Labeling these vectors north, east, and up, doesn't really follow the right-hand rule, but helps us think
		 * about what we're drawing. Pretend it's a skinny box with a local coordinate system that puts the "north"
		 * axis line directly through the center of the box.
		 */
		Vec3d north = new Vec3d(x2-x1, y2-y1, z2-z1);
		Vec3d east;
		if (north.x==0 && north.z==0) { //Use a different vector than up
			east = north.crossProduct(new Vec3d(1, 0, 0)).normalize().scale(width/2d);
		} else {
			east = north.crossProduct(new Vec3d(0, 1, 0)).normalize().scale(width/2d);
		}
		Vec3d up = north.crossProduct(east).normalize().scale(width/2d);
		
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		
		//Pretend north (+z) is parallel to the line
		//This means
		Vec3d usw = new Vec3d(x1-east.x+up.x, y1-east.y+up.y, z1-east.z+up.z);
		Vec3d use = new Vec3d(x1+east.x+up.x, y1+east.y+up.y, z1+east.z+up.z);
		Vec3d unw = new Vec3d(x2-east.x+up.x, y2-east.y+up.y, z2-east.z+up.z);
		Vec3d une = new Vec3d(x2+east.x+up.x, y2+east.y+up.y, z2+east.z+up.z);
		Vec3d dsw = new Vec3d(x1-east.x-up.x, y1-east.y-up.y, z1-east.z-up.z);
		Vec3d dse = new Vec3d(x1+east.x-up.x, y1+east.y-up.y, z1+east.z-up.z);
		Vec3d dnw = new Vec3d(x2-east.x-up.x, y2-east.y-up.y, z2-east.z-up.z);
		Vec3d dne = new Vec3d(x2+east.x-up.x, y2+east.y-up.y, z2+east.z-up.z);
		/*//Original face order... *all* backwards? O_o
		_quad(vb, dsw, dnw, dne, dse, r, g, b, a);
		_quad(vb, usw, use, une, unw, r, g, b, a);
		_quad(vb, unw, usw, use, une, r, g, b, a);
		_quad(vb, use, dse, dne, une, r, g, b, a); */
		
		_quad(vb, dsw, dse, dne, dnw, r, g, b, a);
		_quad(vb, usw, unw, une, use, r, g, b, a);
		_quad(vb, usw, dsw, dnw, unw, r, g, b, a);
		_quad(vb, use, une, dne, dse, r, g, b, a);
		if (endcap) {
			_quad(vb, usw, use, dse, dsw, r, g, b, a);
			_quad(vb, une, dne, dnw, unw, r, b, b, a);
		}
		
		tess.draw();
	}
	
	public static void circle(double x, double y, double z, double radius, float accuracy, float width, int color) {
		if (accuracy<=0) accuracy = (TAU/16f);
		for(float f = 0; f<TAU; f+=accuracy) {
			double px1 = x + (radius * Math.cos(f));
			double py = y;
			double pz1 = z + (radius * Math.sin(f));
			double px2 = x + (radius * Math.cos(f+accuracy));
			double pz2 = z + (radius * Math.sin(f+accuracy));
			
			//line(px1, py, pz1, px2, py, pz2, width, color);
			fakeLine(px1, py, pz1, px2, py, pz2, width, color, true);
		}
	}
}
