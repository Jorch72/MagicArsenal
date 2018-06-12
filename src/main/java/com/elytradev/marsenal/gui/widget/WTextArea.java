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
		
		GlStateManager.scale(1/(float)scale, 1/(float)scale, 1);
		Minecraft.getMinecraft().fontRenderer.drawSplitString("§r"+text.getFormattedText(), (int)(x*scale), (int)(y*scale), (int)(this.getWidth()*scale), color);
		if (scale!=1) GlStateManager.scale(scale, scale, 1);
	}
	
	@Override
	public boolean canResize() {
		return true;
	}
}
