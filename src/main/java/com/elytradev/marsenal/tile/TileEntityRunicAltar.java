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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import com.elytradev.concrete.inventory.ConcreteItemStorage;
import com.elytradev.concrete.inventory.IContainerInventoryHolder;
import com.elytradev.concrete.inventory.ValidatedInventoryView;
import com.elytradev.concrete.inventory.ValidatedItemHandlerView;
import com.elytradev.concrete.inventory.Validators;
import com.elytradev.marsenal.MagicArsenal;
import com.elytradev.marsenal.capability.IRuneProducer;
import com.elytradev.marsenal.compat.ProbeDataCompat;

import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

public class TileEntityRunicAltar extends TileEntity implements ITickable, IContainerInventoryHolder {
	public static final int SLOT_OUTPUT = 6;
	private static final int MAX_DEPTH = 20;
	private static final int RESCAN_PERIOD = 20*10;
	private ConcreteItemStorage storage = new ConcreteItemStorage(7)
			.withName("tile.magicarsenal.altar.name")
			.setCanExtract(0, false)
			.setCanExtract(1, false)
			.setCanExtract(2, false)
			.setCanExtract(3, false)
			.setCanExtract(4, false)
			.setCanExtract(5, false)
			.setCanExtract(6, true)
			.withValidators(
					Validators.ANYTHING, Validators.ANYTHING, Validators.ANYTHING, Validators.ANYTHING, Validators.ANYTHING, Validators.ANYTHING,
					Validators.NOTHING);
	private int rescanTimer = 0;
	Set<BlockPos> transmitterCache = new HashSet<>();
	Set<BlockPos> lastProducerCache = new HashSet<>();
	Set<BlockPos> producerCache = new HashSet<>();
	Set<String> usedKeys = new HashSet<>();
	private int emc = 0;
	private int radiance = 0;
	
	public TileEntityRunicAltar() {
		storage.listen(this::markDirty);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (tag.hasKey("Inventory")) {
			CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(storage, null, tag.getTag("Inventory"));
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setTag("Inventory", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(storage, null));
		
		return tag;
	}
	
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (MagicArsenal.CAPABILITY_PROBEDATA!=null && capability==MagicArsenal.CAPABILITY_PROBEDATA) return true;
		if (capability==CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return true;
		return super.hasCapability(capability, facing);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (MagicArsenal.CAPABILITY_PROBEDATA!=null && capability==MagicArsenal.CAPABILITY_PROBEDATA) {
			return (T) ProbeDataCompat.getProvider(this, null);
		}
		if (capability==CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return (T) new ValidatedItemHandlerView(storage);
		
		return super.getCapability(capability, facing);
	}
	
	
	public void scan() {
		if (world==null) return;
		
		transmitterCache.clear();
		producerCache.clear();
		List<TileEntity> tiles = new ArrayList<TileEntity>();
		List<TileEntity> nextTiles = new ArrayList<TileEntity>();
		
		
		//Find connected Raidho Stele
		tiles.add(this);
		
		int scanDepth = 0;
		while (!tiles.isEmpty() && scanDepth<MAX_DEPTH) {
			List<TileEntity> newCandidates = new ArrayList<TileEntity>();
			for(TileEntity toTe : tiles) {
				addNearbyTiles(world, toTe.getPos(), 1, newCandidates, it->
					it instanceof TileEntityRaidhoStele &&
					!transmitterCache.contains(it.getPos()) &&
					toTe.getPos().distanceSqToCenter(it.getPos().getX()+0.5d, it.getPos().getY()+0.5d, it.getPos().getZ()+0.5d)<(16*16)
				);
				for(TileEntity fromTe : newCandidates) {
					transmitterCache.add(fromTe.getPos());
					((TileEntityRaidhoStele)fromTe).setBeamTo(toTe.getPos(), scanDepth);
				}
				nextTiles.addAll(newCandidates);
				newCandidates.clear();
			}
			scanDepth++;
			tiles.clear();
			tiles.addAll(nextTiles);
			nextTiles.clear();
		}
		
		//Now find other stele near the altar or the transmitters
		tiles.clear();
		adoptProducersNear(getPos(), tiles);
		for(BlockPos pos : transmitterCache) {
			adoptProducersNear(pos, tiles);
		}
		
		//We have a fully-realized producer cache. Find out our radiance value.
		emc = 0;
		radiance = 0;
		usedKeys.clear();
		for(BlockPos pos : producerCache) {
			TileEntity te = world.getTileEntity(pos);
			if (te.hasCapability(MagicArsenal.CAPABILITY_RUNEPRODUCER, null)) {
				IRuneProducer producer = te.getCapability(MagicArsenal.CAPABILITY_RUNEPRODUCER, null);
				if (producer==null) continue;
				if (usedKeys.contains(producer.getProducerType())) continue;
				emc += producer.getEMCAvailable();
				radiance += producer.getProducerRadiance();
				usedKeys.add(producer.getProducerType());
			}
		}
	}
	
	public void adoptProducersNear(BlockPos pos, List<TileEntity> pony) {
		pony.clear();
		addNearbyTiles(world, pos, 1, pony, it->
			it instanceof INetworkParticipant &&
			!(it instanceof TileEntityRaidhoStele) &&
			!producerCache.contains(it.getPos()) &&
			pos.distanceSqToCenter(it.getPos().getX()+0.5d, it.getPos().getY()+0.5d, it.getPos().getZ()+0.5d)<(16*16)
		);
		for(TileEntity te : pony) {
			INetworkParticipant cur = (INetworkParticipant)te;
			if (lastProducerCache.contains(te.getPos())) {
				cur.pollNetwork(getPos(), pos);
				producerCache.add(te.getPos());
			} else {
				if (cur.canJoinNetwork(getPos())) {
					cur.joinNetwork(getPos(), pos);
					producerCache.add(te.getPos());
				}
			}
		}
	}
	
	
	public static void addNearbyTiles(World world, BlockPos center, int chunkRadius, Collection<TileEntity> list, Predicate<TileEntity> predicate) {
		Chunk originChunk = world.getChunkFromBlockCoords(center);
		int centerX = originChunk.x;
		int centerZ = originChunk.z;
		
		for(int z=centerZ-chunkRadius; z<=centerZ+chunkRadius; z++) {
			for(int x=centerX-chunkRadius; x<=centerX+chunkRadius; x++) {
				Chunk cur = world.getChunkFromChunkCoords(x, z);
				for(TileEntity te : cur.getTileEntityMap().values()) {
					if (predicate.test(te)) list.add(te);
				}
			}
		}
	}
	
	public int getEMC() { return emc; }
	public int getRadiance() { return radiance; }


	@Override
	public void update() {
		if (!this.hasWorld()) return;
		rescanTimer--;
		if (rescanTimer<=0) {
			scan();
			rescanTimer = RESCAN_PERIOD;
		}
	}

	@Override
	public IInventory getContainerInventory() {
		ValidatedInventoryView result = new ValidatedInventoryView(storage);
		if (!world.isRemote) {
			result.withField(0, this::getEMC);
			result.withField(1, this::getRadiance);
		}
		return result;
	}
}
