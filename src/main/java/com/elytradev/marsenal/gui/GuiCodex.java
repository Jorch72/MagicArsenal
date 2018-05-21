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

import java.io.IOException;

import org.lwjgl.opengl.Display;

import com.elytradev.concrete.inventory.gui.client.ConcreteGui;
import com.elytradev.concrete.inventory.gui.client.GuiDrawing;
import com.elytradev.concrete.inventory.gui.widget.WPanel;
import com.elytradev.concrete.inventory.gui.widget.WWidget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

public class GuiCodex extends ConcreteGui {
	private ContainerCodex container;
	private int actualScale = 2;
	private int marginLeft = 0;
	private int marginTop = 0;
	private float toMinecraftUnits = 1.0f;
	private WWidget lastResponder;
	
	public GuiCodex(ContainerCodex container) {
		super(container);
		this.container = container;
	}
	
	protected void updateSize() {
		
		
		/* DO NOT TRY THIS AT HOME. Please make sure your arms are secure, your children are in their
		 * upright positions, and keep your seatbelts inside the vehicle at all times. We're in for a bumpy ride.
		 * 
		 * The above code *undoes Minecraft's GUI scale*. The below code is kind of like the "Auto" setting except that
		 * it doesn't try to make the gui absolutely gigantic. Consider combining this with Vise to get tooltips to a
		 * nice size too.
		 */
		int width = Display.getWidth();
		int height = Display.getHeight();
		//int width = Minecraft.getMinecraft().displayWidth;
		//int height = Minecraft.getMinecraft().displayHeight;
		
		//Aim to get the book to about 2/3 the screen width
		//float targetWidth = width * (3/4f);
		float targetWidth = width * (8/12f);
		float targetScale = targetWidth / 256.0f;
		//System.out.println("Width: "+width+", ThisWidth: "+this.width+", TargetWidth: "+targetWidth+", TargetScale: "+targetScale);
		
		//Find out how much "absolute" vertical room we have
		int hotbarMargin = (int)(height * (1/16f)); //Attempt to make the hotbar visible underneath the gui by setting a symmetric margin at the top and bottom.
		int availableHeight = height - (hotbarMargin*2);
		
		//Will we be too tall when rescaled?
		if (128 * targetScale > availableHeight) {
			//This is exactly how much room we have. #dealwithit
			targetScale = availableHeight / 128f;
		}
		
		//Now that we have a target, find the biggest *integer* targetScale that's divisible by 2. This is important to keep Minecraft's text renderer from looking like crap if half-sized.
		actualScale = (int)targetScale;
		if (actualScale%2!=0) actualScale--;
		if (actualScale<2) actualScale = 2; //Hard bottom limit for text readability.
		
		//Find width and height in gui coordinates
		int vWidth = width / actualScale;
		int vHeight = height / actualScale;
		
		//Center us by moving the left and top to half the total margins
		marginLeft = (vWidth - 256) / 2; if (marginLeft<0) marginLeft = 0;
		marginTop = (vHeight - 128) / 2; if (marginLeft<0) marginLeft = 0;
		
		//Transform our logical width and height into Minecraft's logical width and height so that JEI can wrap around
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		int scaleFactor = sr.getScaleFactor();
		
		//float toMinecraftUnits = scaleFactor / (float)actualScale;
		toMinecraftUnits = actualScale / (float)scaleFactor; //TODO: WHY DOES THIS WORK? IT'S BACKWARDS!
		
		int minecraftWidth = (int)(256 * toMinecraftUnits);
		int minecraftHeight = (int)(128 * toMinecraftUnits);
		int minecraftLeft = (int)(marginLeft * toMinecraftUnits);
		int minecraftTop = (int)(marginTop * toMinecraftUnits);
		
		//System.out.println("PreRescale Width: "+this.width+", Height: "+this.height+", Left: "+this.guiLeft+", Top: "+this.guiTop+", VanillaGuiScale: "+scaleFactor);
		//this.width = minecraftWidth;
		//this.height = minecraftHeight;
		this.guiLeft = minecraftLeft;
		this.guiTop = minecraftTop;
		this.xSize = minecraftWidth;
		this.ySize = minecraftHeight;
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		
		WPanel root = this.container.getRootPanel();
		if (root==null || inventorySlots==null) return;
		
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		int scaleFactor = sr.getScaleFactor();
		
		GlStateManager.scale(1/(float)scaleFactor, 1/(float)scaleFactor, 1/(float)scaleFactor);
		
		//updateSize();
		
		GlStateManager.scale(actualScale, actualScale, actualScale);
		
		root.paintBackground(marginLeft, marginTop);
		
		//GuiDrawing.rect(mcToLocal(mouseX)-4, mcToLocal(mouseY)-4, 9, 9, 0xFFFF0000); //Debug mouse
		
		GlStateManager.scale(1/(float)actualScale, 1/(float)actualScale, 1/(float)actualScale);
		
		GlStateManager.scale(scaleFactor, scaleFactor, scaleFactor);
		
		
	}
	
	@Override
	public void setWorldAndResolution(Minecraft mc, int width, int height) {
		super.setWorldAndResolution(mc, width, height);
		
		updateSize();
	}
	
	@Override
	public void onResize(Minecraft mcIn, int w, int h) {
		super.onResize(mcIn, w, h);
		
		updateSize();
	}
	
	protected int mcToLocal(int i) {
		return (int)(i / toMinecraftUnits);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		lastResponder = container.doMouseDown(mcToLocal(mouseX-guiLeft), mcToLocal(mouseY-guiTop), mouseButton);
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) { //Testing shows that STATE IS ACTUALLY BUTTON
		int containerX = mcToLocal(mouseX-guiLeft);
		int containerY = mcToLocal(mouseY-guiTop);
		WWidget responder = container.doMouseUp(containerX, containerY, state);
		if (responder!=null && responder==lastResponder) container.doClick(containerX, containerY, state);
		lastResponder = null;
	}
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		int containerX = mcToLocal(mouseX-guiLeft);
		int containerY = mcToLocal(mouseY-guiTop);
		if (containerX<0 || containerY<0 || containerX>=width || containerY>=height) return;
		container.doMouseDrag(containerX, containerY, clickedMouseButton);
	}
	
}
