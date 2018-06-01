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

import java.util.function.Predicate;

import com.elytradev.concrete.inventory.IContainerInventoryHolder;
import com.elytradev.concrete.inventory.ValidatedInventoryView;
import com.elytradev.marsenal.capability.impl.FlexibleItemHandler;
import com.elytradev.marsenal.capability.impl.ValidatedInventoryWrapperTakeTwo;
import com.elytradev.marsenal.item.IBeaconSigil;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

public class TileEntityRadiantBeacon extends TileEntity implements IAuxNetworkParticipant, ITickable, IContainerInventoryHolder {
	protected BlockPos controller = null;
	protected long lastControllerPing = 0L;
	protected BlockPos beamTo = null;
	Predicate<ItemStack> BEACON_LENS = it->it.getItem() instanceof IBeaconSigil;
	
	protected float radius;
	protected float effectiveRadius;
	
	protected FlexibleItemHandler storage = new FlexibleItemHandler(6)
			.setName("tile.magicarsenal.beacon.name")
			.setCanExtract(true, true, true, true, true, true)
			.setValidators(BEACON_LENS, BEACON_LENS, BEACON_LENS, BEACON_LENS, BEACON_LENS, BEACON_LENS)
			.setMaxStackSize(1);
	
	public TileEntityRadiantBeacon() {
		
	}
	
	@Override
	public boolean canJoinNetwork(BlockPos controller) {
		if (this.controller==null || this.controller.equals(controller)) return true;
		
		long now = world.getTotalWorldTime();
		return now-lastControllerPing > 20*5; //Has it been five seconds since the last poll?
	}

	@Override
	public void joinNetwork(BlockPos controller, BlockPos beamTo) {
		this.controller = controller;
		this.beamTo = beamTo;
	}

	@Override
	public void pollNetwork(BlockPos controller, BlockPos beamTo) {
		if (world!=null) lastControllerPing = world.getTotalWorldTime();
		this.beamTo = beamTo;
	}

	@Override
	public BlockPos getBeamTo() {
		return beamTo;
	}

	@Override
	public String getParticipantType() {
		return "beacon";
	}

	@Override
	public void pollAuxRadiance(int radiance) {
		radius = radiance;
	}
	
	@Override
	public void update() {
		
	}
	
	public float getRadius() {
		return effectiveRadius;
	}
	
	@Override
	public IInventory getContainerInventory() {
		ValidatedInventoryView result = new ValidatedInventoryWrapperTakeTwo(storage);
		if (!world.isRemote) {
			result.withField(0, ()->(int)this.getRadius());
		}
		return result;
	}
}
