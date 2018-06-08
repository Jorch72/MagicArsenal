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
import com.elytradev.concrete.inventory.gui.widget.WGridPanel;
import com.elytradev.concrete.inventory.gui.widget.WItemSlot;
import com.elytradev.marsenal.gui.widget.WSprite;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class ContainerBeacon extends ConcreteContainer {
	
	public ContainerBeacon(IInventory player, IInventory container, TileEntity te) {
		super(player, container);
		
		this.setColor(0xFF155f5b);
		this.setBevelStrength(0.25f);
		this.setTitleColor(0xFF8cb5b3);
		
		WGridPanel root = new WGridPanel();
		setRootPanel(root);
		
		root.add(WItemSlot.of(container, 0), 3, 1);
		root.add(WItemSlot.of(container, 1), 5, 1);
		root.add(WItemSlot.of(container, 2), 1, 3);
		root.add(WItemSlot.of(container, 3), 7, 3);
		root.add(WItemSlot.of(container, 4), 3, 5);
		root.add(WItemSlot.of(container, 5), 5, 5);
		WSprite crystal = new WSprite(new ResourceLocation("magicarsenal:textures/guis/beacon.crystal.png"), 72, 54, 4, 3);
		crystal.setAnimation(new int[] {0, 3, 4, 5, 6, 7, 8, 9, 10, 11}, 10);
		crystal.setDelays(new int[] {10_000, 10, 10, 10, 10, 10, 10, 10, 10, 10}); //Ten seconds in between glints
		
		root.add(crystal, 4, 3, 1, 1);
		
		root.add(createPlayerInventoryPanel(), 0, 7);
	}

}
