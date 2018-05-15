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

import net.minecraft.util.math.BlockPos;

/* The key words "MUST", "MUST NOT", "REQUIRED", "SHALL", "SHALL NOT", "SHOULD", "SHOULD NOT", "RECOMMENDED",
 * "MAY", and "OPTIONAL" in this document are to be interpreted as described in RFC 2119. */

/**
 * Tagging and management class for members of a Stele (Runic Altar) network. This is not a Capability - network members
 * MUST be TileEntities, and this interface MUST be implemented by the TileEntity class itself (or an ancestor). Actual
 * Radiance/EMC interactions will then proceed through the {@link com.elytradev.marsenal.capability.IRuneProducer}
 * capability.
 * 
 * 
 */
public interface INetworkParticipant {
	/**
	 * Gets whether this prospective participant can join the specified network. A participant SHOULD have a five-second
	 * cooldown before switching network targets, reset whenever this participant is re-polled by the same controller.
	 * This prevents the same participant from propping up two networks at the same time. Called on both client and
	 * server.
	 */
	boolean canJoinNetwork(BlockPos controller);
	
	/**
	 * Join the network centered around the specified controller block. Called on both client and server. If called on
	 * the server side, this participant SHOULD also reevaluate its EMC and Radiance contributions in the same manner
	 * as it would for a poll.
	 * @param controller The location of the controller block (or proxy-controller, if this network spans dims)
	 * @param beamTo     The location of either the controller or a transport node that should be visually linked to.
	 */
	void joinNetwork(BlockPos controller, BlockPos beamTo);
	
	/**
	 * Requests recalculation of shared values. Participants not connected to any network MAY stop assessing their
	 * governed areas and avoid ticking completely, and MAY rely on this method as their sole motivator to prospect for
	 * EMC and update their radiance. Also triggered immediately before a crafting operation.
	 * @param controller The location of the controller block (or proxy-controller, if this network spans dims)
	 * @param beamTo     The location of either the controller or a transport node that should be visually linked to.
	 */
	void pollNetwork(BlockPos controller, BlockPos beamTo);
	
	/**
	 * Gets the previously-set beam-to location
	 * @nullable MAY return null if not participating in a network or if the beam-to is unknown. Null will also often
	 * cause probes and inspectors to report that the block is "sleeping".
	 */
	BlockPos getBeamTo();
}
