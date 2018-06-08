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

import com.elytradev.concrete.inventory.ValidatedInventoryView;
import com.elytradev.concrete.inventory.ValidatedSlot;
import com.elytradev.concrete.inventory.gui.ConcreteContainer;
import com.elytradev.concrete.inventory.gui.client.GuiDrawing;
import com.elytradev.concrete.inventory.gui.widget.WItemSlot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WFlexibleItemSlot extends WItemSlot {
	protected IInventory inventory;
	protected ConcreteContainer container;
	protected int startIndex = 0;
	protected int slotsWide = 1;
	protected int slotsHigh = 1;
	protected boolean big = false;
	
	public WFlexibleItemSlot(IInventory inventory, int startIndex, int slotsWide, int slotsHigh, boolean big) {
		super(inventory, startIndex, slotsWide, slotsHigh, big, false);
	}
	/*
	@Override
	public void createPeers(ConcreteContainer c) {
		this.container = c;
		int index = startIndex;

		for (int y = 0; y < slotsHigh; y++) {
			for (int x = 0; x < slotsWide; x++) {
				ValidatedSlot slot = new ValidatedSlot(inventory, index, this.getAbsoluteX() + (x * 18), this.getAbsoluteY() + (y * 18));
				slot.
				c.addSlotPeer(slot);
				index++;
			}
		}
	}*/
	
	@SideOnly(Side.CLIENT)
	@Override
	public void paintBackground(int x, int y) {
		for (int xi = 0; xi < slotsWide; xi++) {
			for (int yi = 0; yi < slotsHigh; yi++) {
				int lo = GuiDrawing.colorAtOpacity(0x000000, 0.72f);
				int bg = GuiDrawing.colorAtOpacity(0x000000, 0.29f);
				int hi = GuiDrawing.colorAtOpacity(0xFFFFFF, 1.0f);
				if (container!=null) {
					lo = GuiDrawing.colorAtOpacity(0x000000, container.getBevelStrength());
					bg = GuiDrawing.colorAtOpacity(0x000000, container.getBevelStrength()/2.4f);
					hi = GuiDrawing.colorAtOpacity(0xFFFFFF, container.getBevelStrength());
				}
				
				if (big) {
					GuiDrawing.drawBeveledPanel((xi * 18) + x - 4, (yi * 18) + y - 4, 24, 24,
							lo, bg, hi);
				} else {
					GuiDrawing.drawBeveledPanel((xi * 18) + x - 1, (yi * 18) + y - 1, 18, 18,
							lo, bg, hi);
				}
			}
		}
	}
	/*
	
	@Override
	public int getWidth() {
		return slotsWide * 18;
	}
	
	@Override
	public int getHeight() {
		return slotsHigh * 18;
	}
	
	public class ValidatedSlot extends Slot {

		public ValidatedSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}
		
		@Override
		public boolean isItemValid(ItemStack stack) {
			if (inventory instanceof ValidatedInventoryView) {
				return ((ValidatedInventoryView) inventory).getValidator(getSlotIndex()).test(stack);
			} else {
				return super.isItemValid(stack);
			}
		}
		
		@Override
		public int getItemStackLimit(ItemStack stack) {
			if (inventory instanceof ValidatedInventoryView) {
				((ValidatedInventoryView)inventory).getSlotLimit(this.getSlotIndex());
			}
			// TODO Auto-generated method stub
			return super.getItemStackLimit(stack);
		}
	}*/
}
