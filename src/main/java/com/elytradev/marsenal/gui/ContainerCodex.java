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
import com.elytradev.concrete.inventory.gui.widget.WImage;
import com.elytradev.concrete.inventory.gui.widget.WPlainPanel;
import com.elytradev.marsenal.block.ArsenalBlocks;
import com.elytradev.marsenal.item.ArsenalItems;
import com.elytradev.marsenal.item.EnumSpellFocus;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class ContainerCodex extends ConcreteContainer {
	public static CodexPage[] CODEX_PAGES;
	
	public static void initCodex() {
		CODEX_PAGES = new CodexPage[] {
			new CodexPage(
				new ResourceLocation("magicarsenal:textures/guis/codex/intro.png"),
				new TextComponentTranslation("codex.magicarsenal.page.1.left"),
				null,
				null,
				new TextComponentTranslation("codex.magicarsenal.page.1.right")
			),
			
			new CodexPage(
				null,
				new TextComponentTranslation("codex.magicarsenal.page.2.left"),
				new ItemStack(ArsenalItems.ROOT_WOLFSBANE),
				new ResourceLocation("magicarsenal:textures/guis/codex/feature.poison.png"),
				new TextComponentTranslation("codex.magicarsenal.page.2.right")
			),

			new CodexPage(
				null,
				new TextComponentTranslation("codex.magicarsenal.page.3.left"),
				new ItemStack(ArsenalBlocks.RUNESTONE1),
				null,
				new TextComponentTranslation("codex.magicarsenal.page.3.right")
			),
			
			new CodexPage(
				null,
				new TextComponentTranslation("codex.magicarsenal.page.4.left"),
				new ItemStack(ArsenalBlocks.STELE_UNCARVED),
				null,
				new TextComponentTranslation("codex.magicarsenal.page.4.right")
			),
			
			new CodexPage(
				null,
				new TextComponentTranslation("codex.magicarsenal.page.5.left"),
				null,
				new ResourceLocation("magicarsenal:textures/guis/codex/feature.runes.png"),
				null
			),
			
			new CodexPage(
				new ResourceLocation("magicarsenal:textures/guis/codex/header.kenaz.png"),
				new TextComponentTranslation("codex.magicarsenal.page.6.left"),
				new ItemStack(ArsenalBlocks.STELE_KENAZ),
				new ResourceLocation("magicarsenal:textures/guis/codex/feature.kenaz.png"),
				new TextComponentTranslation("codex.magicarsenal.page.6.right")
			),
			
			new CodexPage(
				null,
				new TextComponentTranslation("codex.magicarsenal.page.7.left"),
				new ItemStack(ArsenalBlocks.RUNIC_ALTAR),
				new ResourceLocation("magicarsenal:textures/guis/codex/feature.altar.png"),
				new TextComponentTranslation("codex.magicarsenal.page.7.right")
			),
			
			new CodexPage(
				null,
				new TextComponentTranslation("codex.magicarsenal.page.8.left"),
				null,
				null,
				new TextComponentTranslation("codex.magicarsenal.page.8.right")
			),
		};
	}
	
	private int pages = 1;
	private WSwappableImage header;
	private WTextArea leftPage;
	private WItemDisplay spotlight;
	private WSwappableImage feature;
	private WTextArea rightPage;
	private WButton prevPage = new WButton();
	private WButton nextPage = new WButton();
	private int curPage = 0;
	
	public ContainerCodex(IInventory player, IInventory container, TileEntity te) {
		this(player, 1);
	}
	
	public ContainerCodex(IInventory player, int numPages) {
		super(player, null);
		
		pages = numPages;
		
		//P A G E  L A Y O U T
		this.setColor(0xFFcbbf90);
		WPlainPanel root = new WPlainPanel();
		this.setRootPanel(root);
		this.setDrawPanel(false);
		
		root.add(new WImage(new ResourceLocation("magicarsenal","textures/guis/codex/bg.png")), 0, 0, 256, 128);
		
		header = new WSwappableImage();
		root.add(header, 10, 10, 100, 24);
		
		leftPage = new WTextArea();
		root.add(leftPage, 10, 10 + 3 + 24, 100, (100-(3+24)));
		
		feature = new WSwappableImage();
		root.add(feature, 136, 13, 100, 100); //Added here because of intended Z-order
		
		spotlight = new WItemDisplay();
		spotlight.setItemStack(new ItemStack(ArsenalItems.SPELL_FOCUS, 1, EnumSpellFocus.RECOVERY.ordinal()));
		root.add(spotlight, 136, 13, 18, 18);
		
		rightPage = new WTextArea();
		root.add(rightPage, 136, 13+18+2, 100, 100-(13+18-2));
		
		prevPage.setImage(new ResourceLocation("magicarsenal:textures/guis/codex/previous.png"));
		prevPage.setOnClick(this::previous);
		root.add(prevPage, 8, 103);
		
		nextPage.setImage(new ResourceLocation("magicarsenal:textures/guis/codex/next.png"));
		nextPage.setOnClick(this::next);
		root.add(nextPage, 231, 103);
		
		curPage = 0;
		setCurPage();
	}
	
	public int getpageCount() {
		return pages;
	}
	
	public String getLocalizedName() {
		return "";
	}
	
	public void setCurPage() {
		if (CODEX_PAGES.length<1) return; //Just in case I do something REALLY dumb
		if (curPage>=CODEX_PAGES.length) curPage = CODEX_PAGES.length-1;
		if (curPage<0) curPage=0;
		setToPage(CODEX_PAGES[curPage]);
		checkButtons();
	}
	
	private void setToPage(CodexPage page) {
		header.setImage(page.header);
		leftPage.setText(page.leftPage);
		if (page.header==null) {
			leftPage.setLocation(leftPage.getX(), 13);
		} else {
			leftPage.setLocation(leftPage.getX(), 10 + 3 + 24);
		}
		
		spotlight.setItemStack(page.spotlight);
		rightPage.setText(page.rightPage);
		if (page.spotlight==null || page.spotlight.isEmpty()) {
			rightPage.setLocation(rightPage.getX(), 13);
		} else {
			rightPage.setLocation(rightPage.getX(), 13+18+2);
		}
		
		feature.setImage(page.feature);
	}
	
	public void checkButtons() {
		nextPage.setEnabled(curPage<pages);
		prevPage.setEnabled(curPage>0);
	}
	
	public void previous() {
		curPage--;
		setCurPage();
	}
	
	public void next() {
		curPage++;
		setCurPage();
	}
	
	public static class CodexPage {
		/** A 50x16 image to be displayed across the top of the left page */
		private ResourceLocation header = null;
		/** Text to be displayed on the left page */
		private ITextComponent leftPage;
		/** A 50x50 image to be displayed on the full right page */
		private ItemStack spotlight = null;
		private ResourceLocation feature = null;
		/** Text to be displayed on the right page. Bottom-aligned if there's a spotlight. */
		private ITextComponent rightPage;
		
		public CodexPage(ITextComponent left, ITextComponent right) {
			this(null, left, null, null, right);
		}
		
		public CodexPage(ResourceLocation header, ITextComponent left, ItemStack spotlight, ResourceLocation feature, ITextComponent right) {
			this.header = header;
			this.leftPage = left;
			this.spotlight = spotlight;
			this.feature = feature;
			this.rightPage = right;
		}
	}
}
