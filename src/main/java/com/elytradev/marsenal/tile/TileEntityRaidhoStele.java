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

import com.elytradev.marsenal.MagicArsenal;
import com.elytradev.marsenal.compat.ProbeDataCompat;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;

public class TileEntityRaidhoStele extends TileEntity implements INetworkParticipant {
	private BlockPos beamTo;
	private int beamDepth;
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (MagicArsenal.CAPABILITY_PROBEDATA!=null && capability==MagicArsenal.CAPABILITY_PROBEDATA) return true;
		
		return super.hasCapability(capability, facing);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (MagicArsenal.CAPABILITY_PROBEDATA!=null && capability==MagicArsenal.CAPABILITY_PROBEDATA) {
			return (T) ProbeDataCompat.getProvider(this, null);
		}
		
		return super.getCapability(capability, facing);
	}
	
	public void setBeamTo(BlockPos pos, int depth) {
		this.beamTo = pos;
		this.beamDepth = depth;
	}
	
	public int getBeamDepth() {
		return beamDepth;
	}

	@Override
	public boolean canJoinNetwork(BlockPos controller) {
		return true;
	}

	@Override
	public void joinNetwork(BlockPos controller, BlockPos beamTo) {}

	@Override
	public void pollNetwork(BlockPos controller, BlockPos beamTo) {}
	
	@Override
	public BlockPos getBeamTo() {
		return beamTo;
	}
}
