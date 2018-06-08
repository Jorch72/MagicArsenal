package com.elytradev.marsenal.gui.widget;

import com.elytradev.concrete.inventory.gui.client.GuiDrawing;
import com.elytradev.concrete.inventory.gui.widget.WWidget;

import net.minecraft.inventory.IInventory;
import net.minecraft.util.text.ITextComponent;

public class WLabel extends WWidget {
	public static final int DEFAULT_TEXT_COLOR = 0x404040;
	protected String text;
	protected int color;
	
	protected IInventory inventory;
	protected int field1 = -1;
	protected int field2 = -1;
	
	public WLabel(String text, int color) {
		this.text = text;
		this.color = color;
	}
	
	public WLabel(String text) {
		this(text, DEFAULT_TEXT_COLOR);
	}
	
	public WLabel withFields(IInventory inv, int field1, int field2) {
		this.inventory = inv;
		this.field1 = field1;
		this.field2 = field2;
		return this;
	}
	
	@Override
	public void paintBackground(int x, int y) {
		int field1Contents = 0;
		int field2Contents = 0;
		if (inventory!=null) {
			if (field1>=0) {
				field1Contents = inventory.getField(field1);
			}
			
			if (field2>=0) {
				field2Contents = inventory.getField(field2);
			}
		}
		
		@SuppressWarnings("deprecation")
		String formatted = net.minecraft.util.text.translation.I18n.translateToLocalFormatted(text, field1Contents, field2Contents);
		
		GuiDrawing.drawString(formatted, x, y, color);
	}

	@Override
	public boolean canResize() {
		return false;
	}
}