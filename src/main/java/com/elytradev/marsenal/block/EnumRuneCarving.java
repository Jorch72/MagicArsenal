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

package com.elytradev.marsenal.block;

import net.minecraft.util.IStringSerializable;

public enum EnumRuneCarving implements IStringSerializable {
	NONE("none"),
	
	FEHU("fehu"),
	URUZ("uruz"),
	THURISAZ("thurisaz"),
	ANSUZ("ansuz"),
	RAIDHO("raidho"),
	KENAZ("kenaz"),
	GEBO("gebo"),
	WUNJO("wunjo"),
	
	HAGALAZ("hagalaz"),
	NAUTHIZ("nauthiz"),
	ISA("isa"),
	JERA("jera"),
	EIHWAZ("eihwaz"),
	PERTHRO("perthro"),
	ELHAZ("elhaz"),
	SOWILO("sowilo"),
	
	TIWAZ("tiwaz"),
	BERKANO("berkano"),
	EHWAZ("ehwaz"),
	MANNAZ("mannaz"),
	LAGUZ("laguz"),
	INGWAZ("ingwaz"),
	DAGAZ("dagaz"),
	OTHALA("othala")
	
	/* Reserved: Down here goes non-canon carvings and extra cosmetic bits because who doesn't want to guss up their
	 * temple / hall / library with special shiny rocks
	 */
	;
	public static final EnumRuneCarving[] FIRST_16 = {
		NONE, FEHU, URUZ, THURISAZ, ANSUZ, RAIDHO, KENAZ, GEBO,
		WUNJO, HAGALAZ, NAUTHIZ, ISA, JERA, EIHWAZ, PERTHRO, ELHAZ
	};
	
	public static final EnumRuneCarving[] SECOND_16 = {
		SOWILO, TIWAZ, BERKANO, EHWAZ, MANNAZ, LAGUZ, INGWAZ, DAGAZ,
		OTHALA
	};
	
	private String name;
	EnumRuneCarving(String name) { this.name = name; }
	@Override
	public String getName() { return name; }
}
