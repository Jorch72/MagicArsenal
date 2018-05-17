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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FakeSlot extends Slot {
	private ItemStack stack;
	
	public FakeSlot(int index, int xPosition, int yPosition) {
		super(null, index, xPosition, yPosition);
	}
	
	public void setItemStack(ItemStack stack) {
		this.stack = stack;
	}
	
	
	
	//IMPL below
	
	
	
	@Override
	public void onSlotChange(ItemStack a, ItemStack b) {
		return;
	}
	
	@Override
	public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack) {
		return false;
	}
	
	@Override
	public ItemStack getStack() {
		return stack;
	}
	
	@Override
	public void putStack(ItemStack stack) {}
	
	@Override
	public void onSlotChanged() {}
	
	@Override
	public int getSlotStackLimit() {
		return 64;
	}
	
	public int getItemStackLimit(ItemStack stack) {
		return this.getSlotStackLimit();
	}
	
	@Override
	public ItemStack decrStackSize(int amount) {
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean isHere(IInventory inv, int slotIn) {
		return false;
	}
	
	@Override
	public boolean canTakeStack(EntityPlayer playerIn) {
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean isEnabled() {
		return false;
	}

	@Override
	public boolean isSameInventory(Slot other) {
		return false;
	}
}
