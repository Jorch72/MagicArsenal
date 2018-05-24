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

package com.elytradev.marsenal.tile;

/**
 * Tagging interface for Runic Altar network participants which aren't IRuneProducers or secondary transmitters;
 * instead, Auxiliary Participants are special beacons, batteries, and other devices whose magnitude are in some way
 * passively controlled by the altar network's Radiance.
 */
public interface IAuxNetworkParticipant extends INetworkParticipant {
	/**
	 * Get the participant's mutual exclusion keys. This namespace is shared with IRuneProducer's exclusion keys.
	 */
	public String getParticipantType();
	/**
	 * Notifies this Auxiliary Participant of the altar's radiance. This will happen once after every ping, once all the
	 * producers have been pinged and the total radiance is known.
	 */
	public void pollAuxRadiance(int radiance);
}
