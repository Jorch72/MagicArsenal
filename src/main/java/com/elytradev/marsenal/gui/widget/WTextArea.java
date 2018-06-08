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

package com.elytradev.marsenal.gui.widget;

import com.elytradev.concrete.inventory.gui.widget.WWidget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.ITextComponent;

public class WTextArea extends WWidget {
	protected ITextComponent text;
	
	protected final int color;

	public static final int DEFAULT_TEXT_COLOR = 0xFF404040;
	public int scale = 2;

	public WTextArea(int color) {
		this(null, color);
	}
	
	public WTextArea() {
		this(null, DEFAULT_TEXT_COLOR);
	}
	
	public WTextArea(ITextComponent text, int color) {
		this.text = text;
		this.color = color;
	}

	public WTextArea(ITextComponent text) {
		this(text, DEFAULT_TEXT_COLOR);
	}

	public void setScale(int scale) {
		this.scale = scale;
	}
	
	public void setText(ITextComponent text) {
		this.text = text;
	}
	
	@Override
	public void paintBackground(int x, int y) {
		if (text==null) return;
		
		float scaleOverride = scale;
		/*
		if (scale==2) {
			int scaleFactor = new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
			
			if (scaleFactor%2 != 0) { //odd-numbered scale factors :o
				switch(scaleFactor) {
					case 1:
						scaleOverride = 3/2f; break;
					case 3:
						scaleOverride = 3/2f; break; //3/2 is the smallest fraction here, so the text is the biggest. Most useability testing should happen under 3 ("Large")
					case 5:
						scaleOverride = 5/3f; break;
					case 7:
						scaleOverride = 7/4f; break;
					case 9:
						scaleOverride = 9/5f; break;
					case 11:
						scaleOverride = 11/6f; break;
					case 13:
						scaleOverride = 13/7f; break;
					case 15:
						scaleOverride = 15/8f; break;
					default:*.
						/* If your screen is so huge that you need a bigger magnification than 15x to scale your gui,
						 * you can also afford to pay me for a better solution than this. Right now it's not worth
						 * generalizing.
						 * 
						 * The goal here - of the above fractions - is to converge on a scale of 2. That is, the largest
						 * fraction whose numerator is the magnification value, but the total fraction's value is less
						 * than or equal to 2. The higher the numerator, the finer we can tune the denominator and the
						 * closer we can get to 2. That's why I picked the fractions that I did.
						 */
						/*scaleOverride = 2; break;
				}
			}
			
			if (scaleOverride!=1f) GlStateManager.scale(1/(float)scaleOverride, 1/(float)scaleOverride, 1);
		}*/
		GlStateManager.scale(1/(float)scaleOverride, 1/(float)scaleOverride, 1);
		Minecraft.getMinecraft().fontRenderer.drawSplitString("§r"+text.getFormattedText(), (int)(x*scaleOverride), (int)(y*scaleOverride), (int)(this.getWidth()*scaleOverride), color);
		if (scale!=1) GlStateManager.scale(scaleOverride, scaleOverride, 1);
	}
	
	@Override
	public boolean canResize() {
		return true;
}
}
