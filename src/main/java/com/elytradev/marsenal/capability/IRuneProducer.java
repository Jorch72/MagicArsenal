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

package com.elytradev.marsenal.capability;

/**
 * A RuneProducer is a participant in a spanning tree network of carved runestones. This produces two kinds of energy:
 * <ul>
 * <li>EMC, which is the common denominator for all natural and supernatural forces. EMC can be produced from matter,
 * from energy, from concepts, and from qualities. Typically some combination of these wind up being required.
 * 
 * <li>Runes passively channel energy through the network to its root, the runic altar. This standing energy is called
 * Radiance, and each crafting recipe requires a certain threshold of Radiance to allow it to be activated. Typically,
 * if a stele sees more EMC available to it, it will also produce more Radiance, as the host items are also a channeling
 * catalyst.
 * </ul>
 * RuneProducers also govern an environment. It is possible for two producers in the same network to govern the same
 * environment, but they are not permitted to have the same rune. It is possible for two producers with the same rune
 * to govern the same environment, but they are not permitted to be in the same network. If an arrangement violates
 * these constraints, the network will include as many producers as it can while staying valid.
 */
public interface IRuneProducer {
	/** Determines how much radiance is produced by the producer's governed environment.
	 * */
	int getProducerRadiance();
	
	/** Determines how much EMC is available within the producer's governed environment. */
	int getEMCAvailable();
	
	/** Produces the amount of EMC requested. May yield more EMC than requested, but must consume absolutely everything
	 * it can to try to produce *at least* that much. The result of a "simulate" request MUST be the result of an
	 * identical non-simulate call unless conditions in the stele's governed environment change, or the server is reset.
	 * To accomplish this, it's reccommended that producers desiring randomness hold a random seed and only advance it
	 * when a "real" produce happens. */
	int produceEMC(int requested, boolean simulate);
	
	/** Gets a String key identifying the type of producer. No two producers with the same key will be permitted in any
	 * network. */
	String getProducerType();
}
