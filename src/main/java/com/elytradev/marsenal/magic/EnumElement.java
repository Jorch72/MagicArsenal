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

package com.elytradev.marsenal.magic;

/**
 * Typically a spell has two Elements: One from the first four "classical" elements, and one from the next four
 * "governing" elements. This includes spells which do not cause damage, and even passive anti-elemental wards.
 * 
 * Anti-elemental wards, conversely, tend to only target the classical elements, especially as the convenience and
 * operational lifetime goes up.
 */
public enum EnumElement {
	/** Governs most straightforward, direct damage spells. Should be mitigated by fire resistance or eliminated by wyvern armor */
	FIRE,
	/** Governs most stun/sleep/cc and passive auras */
	FROST,
	/** Governs most poisons and bleeds, and about half of all healing spells */
	NATURE,
	/** Governs most remote or location-based spells */
	AIR,
	
	/** Governs most lifedrain and damage from artificial creatures */
	UNDEATH,
	/** Governs most far-reaching or global-effect spells */
	HOLY,
	/** Governs most spells which exclusively benefit the caster */
	ARCANE,
	/** Governs most uncontrollable effects and unintended spell consequences */
	CHAOS;
}