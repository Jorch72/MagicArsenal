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

import com.elytradev.concrete.inventory.gui.ConcreteContainer;
import com.elytradev.concrete.inventory.gui.widget.WFieldedLabel;
import com.elytradev.concrete.inventory.gui.widget.WGridPanel;
import com.elytradev.concrete.inventory.gui.widget.WImage;
import com.elytradev.concrete.inventory.gui.widget.WItemSlot;
import com.elytradev.concrete.inventory.gui.widget.WPlainPanel;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class ContainerRunicAltar extends ConcreteContainer {
	public static final ResourceLocation BG_IMAGE = new ResourceLocation("magicarsenal","textures/guis/slot.eldritch.png");

	public ContainerRunicAltar(IInventory player, IInventory container, TileEntity te) {
		super(player, container);
		
		this.setColor(0xFF155f5b);
		this.setBevelStrength(0.25f);
		this.setTitleColor(0xFF8cb5b3);
		
		WGridPanel root = new WGridPanel();
		setRootPanel(root);
		WPlainPanel centerPanel = new WPlainPanel();
		int leftMargin = (int)(18*1.5f);
		int topMargin = 4;
		centerPanel.add(new WImage(BG_IMAGE), leftMargin, 0, 107, 75);
		//WGridPanel matchedGrid = new WGridPanel();
		//centerPanel.add(matchedGrid, (int)(18*1.25f) + 4, 0 + 4, 18*6 + 4, 18*4 + 6);
		
		
		centerPanel.add(WItemSlot.of(container, 0), leftMargin + 18*1 + 3, topMargin + 18*0 + 0, 18, 18);
		centerPanel.add(WItemSlot.of(container, 1), leftMargin + 18*3 + 1, topMargin + 18*0 + 0, 18, 18);
		centerPanel.add(WItemSlot.of(container, 2), leftMargin + 18*5 - 2, topMargin + 18*1 + 0, 18, 18);
		centerPanel.add(WItemSlot.of(container, 3), leftMargin + 18*4 - 2, topMargin + 18*3 - 3, 18, 18);
		centerPanel.add(WItemSlot.of(container, 4), leftMargin + 18*2 + 0, topMargin + 18*3 - 3, 18, 18);
		centerPanel.add(WItemSlot.of(container, 5), leftMargin + 18*0 + 3, topMargin + 18*2 - 3, 18, 18);
		
		centerPanel.add(WItemSlot.of(container, 6), leftMargin + 18*2 + 8, topMargin + 18*1 + 7, 18, 18);
		
		centerPanel.add(new WRadianceBar(container, 1).withTooltip("Radiance: %s"), 0, 18*5, 64, 8);
		centerPanel.add(new WFieldedLabel(container, 0, "EMC: %f", 0xFF8cb5b3),  18*5, 18*5, 18*4, 18);
		
		root.add(centerPanel, 0, 1, 9, 4);
		//root.add(new WImage(BG_IMAGE), 1, 1, 6, 4);
		
		root.add(createPlayerInventoryPanel(), 0, 7);
	}

}
