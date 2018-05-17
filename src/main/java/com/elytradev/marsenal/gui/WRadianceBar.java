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

package com.elytradev.marsenal.gui;

import com.elytradev.concrete.inventory.gui.client.GuiDrawing;
import com.elytradev.concrete.inventory.gui.widget.WBar;

import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WRadianceBar extends WBar {
	private static final ResourceLocation BG = new ResourceLocation("magicarsenal","textures/guis/radiance_bg.png");
	private static final ResourceLocation FG = new ResourceLocation("magicarsenal","textures/guis/radiance_fg.png");
	
	private static final int[] LOG_SPECTRUM = {
		0x13968e, 0x57e074, 0x57e074, 0xfff839, 0xffbb04, 0xff7800, 0xff0000, 0xff33a6, 0xdc73ff, 0xab55fb, 0x5c06ea, 0x0c00ff, 0x0027ba, 0x00334b, 0x00474b, 0x155f5b
	};

	protected IInventory inventory;
	protected int field;
	protected Direction dir;
	
	public WRadianceBar(IInventory inventory, int field) {
		this(inventory, field, Direction.RIGHT);
	}
	
	public WRadianceBar(IInventory inventory, int field, Direction dir) {
		super(null, FG, inventory, field, 100, dir);
		this.inventory = inventory;
		this.field = field;
		this.dir = dir;
	}

	float getLogScaled(int input) {
		Math.log10(input);
		return 0;
	}
	
	
	@SideOnly(Side.CLIENT)
	@Override
	public void paintBackground(int x, int y) {
		GuiDrawing.rect(BG, x, y, getWidth(), getHeight(), 0xFFFFFFFF);
		
		float value = inventory.getField(field);
		value = (value>=1) ? (float)Math.log10(inventory.getField(field)) : 0;
		int numBars = (int)value;
		float percent = value-numBars;
		
		if (percent < 0) percent = 0f;
		if (percent > 1) percent = 1f;
		
		int barMax = getWidth()-2;
		if (dir == Direction.DOWN || dir == Direction.UP) barMax = getHeight()-2;
		
		int tmp = numBars+(LOG_SPECTRUM.length-1);
		int lastBarColor = LOG_SPECTRUM[tmp%LOG_SPECTRUM.length] | 0xFF000000;
		int thisBarColor = LOG_SPECTRUM[numBars%LOG_SPECTRUM.length] | 0xFF000000;
		if (numBars>0) {
			GuiDrawing.rect(x+1, y+1, getWidth()-2, getHeight()-2, lastBarColor);
		}
		
		percent = ((int) (percent * barMax)) / (float) barMax; //Quantize to bar size
		
		int barSize = (int) (barMax * percent);
		barSize = barSize-(int)(Math.random()*5);
		
		percent = (barSize/(float)barMax);
		
		if (barSize <= 0) return;
		
		switch(dir) { //anonymous blocks in this switch statement are to sandbox variables
			case UP: {
				int left = x;
				int top = y + getHeight()-1;
				top -= barSize;
				GuiDrawing.rect(FG, left+1, top, getWidth()-2, barSize, 0, 0, 1, 1, thisBarColor);
				break;
			}
			case RIGHT: {
				GuiDrawing.rect(FG, x+1, y+1, barSize, getHeight()-2, 0, 0, 1, 1, thisBarColor);
				break;
			}
			case DOWN: {
				GuiDrawing.rect(FG, x, y, getWidth()-2, barSize, 0, 0, 1, 1, thisBarColor);
				break;
			}
			case LEFT: {
				int left = x + getWidth()-1;
				int top = y;
				left -= barSize;
				GuiDrawing.rect(FG, left+1, top+1, barSize, getHeight()-2, 0, 0, 1, 1, thisBarColor);
				break;
			}
		}
	}
}
