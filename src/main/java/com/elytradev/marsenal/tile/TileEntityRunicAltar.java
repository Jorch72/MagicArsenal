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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import com.elytradev.concrete.inventory.IContainerInventoryHolder;
import com.elytradev.concrete.inventory.ValidatedInventoryView;
import com.elytradev.concrete.inventory.ValidatedItemHandlerView;
import com.elytradev.concrete.inventory.Validators;
import com.elytradev.marsenal.MagicArsenal;
import com.elytradev.marsenal.capability.IRuneProducer;
import com.elytradev.marsenal.capability.impl.FlexibleItemHandler;
import com.elytradev.marsenal.capability.impl.ValidatedInventoryWrapperTakeTwo;
import com.elytradev.marsenal.compat.ProbeDataCompat;
import com.elytradev.marsenal.magic.SpellEffect;
import com.elytradev.marsenal.recipe.RunicAltarRecipes;
import com.elytradev.marsenal.recipe.ShapelessAltarRecipe;

import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

public class TileEntityRunicAltar extends TileEntity implements ITickable, IContainerInventoryHolder {
	private static final int NBTTAG_COMPOUND = 10;
	
	private static final double BLOCK_DIAGONAL = (Math.sqrt(3) * 0.5d) + (1/16d); //pythagorean theorem, but then add 1 chisel bit worth of wiggle room
	
	public static final int SLOT_OUTPUT = 6;
	private static final int MAX_DEPTH = 20;
	private static final int RESCAN_PERIOD = 20*10;
	private static final int EMC_TIMER = 20;
	private static final int CRAFTING_TIMER = (int)(20*2.5f);
	
	private FlexibleItemHandler storage = new FlexibleItemHandler(7)
			.setName("tile.magicarsenal.altar.name")
			.setCanExtract(false, false, false, false, false, false, true)
			.setMaxStackSize(1)
			.setValidators(Validators.ANYTHING, Validators.ANYTHING, Validators.ANYTHING, Validators.ANYTHING, Validators.ANYTHING, Validators.ANYTHING, Validators.NOTHING)
			;
	private int rescanTimer = 0;
	Set<BlockPos> transmitterCache = new HashSet<>();
	Set<BlockPos> lastProducerCache = new HashSet<>();
	Set<BlockPos> producerCache = new HashSet<>();
	Set<BlockPos> participantCache = new HashSet<>();
	Set<String> usedKeys = new HashSet<>();
	private int emc = 0;
	private int radiance = 0;
	
	private ShapelessAltarRecipe currentlyCrafting = null;
	private int emcCollected = 0;
	private int timer = 0;
	private int craftingTimer = 0;
	
	public TileEntityRunicAltar() {
		storage.listen(this::markDirty);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (tag.hasKey("Inventory", NBTTAG_COMPOUND)) {
			storage.deserializeNBT((NBTTagCompound) tag.getTag("Inventory"));
		}
	}
	

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("Inventory", storage.serializeNBT());
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
		participantCache.clear();
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
			if (te==null) continue;
			if (te.hasCapability(MagicArsenal.CAPABILITY_RUNEPRODUCER, null)) {
				IRuneProducer producer = te.getCapability(MagicArsenal.CAPABILITY_RUNEPRODUCER, null);
				if (producer==null) continue;
				if (usedKeys.contains(producer.getProducerType())) continue;
				emc += producer.getEMCAvailable();
				radiance += producer.getProducerRadiance();
				usedKeys.add(producer.getProducerType());
			}// else if (te instanceof IAuxNetworkParticipant) {
			//	participantCache.add(te.getPos());
			//} else {
			//	System.out.println("Whoops");
			//}
		}
		
		//producerCache.removeAll(participantCache);
		for(BlockPos pos : participantCache) {
			TileEntity te = world.getTileEntity(pos);
			if (te!=null && te instanceof IAuxNetworkParticipant) {
				System.out.println("Polling auxRadiance");
				((IAuxNetworkParticipant)te).pollAuxRadiance(radiance);
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
		Vec3d center = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
		for(TileEntity te : pony) {
			BlockPos target = te.getPos();
			
			Vec3d towardsBlock = new Vec3d(target.getX()-pos.getX(), target.getY()-pos.getY(), target.getZ()-pos.getZ()).normalize().scale(BLOCK_DIAGONAL); //Offset towards the target just enough that we don't collide with ourselves
			
			//Can we see the tile?
			RayTraceResult trace = world.rayTraceBlocks(center.add(towardsBlock), new Vec3d(te.getPos()).addVector(0.5, 0.5, 0.5), false);
			boolean blocked = false;
			if (trace!=null) {
				if (trace.typeOfHit==RayTraceResult.Type.BLOCK) {
					BlockPos hit = trace.getBlockPos();
					if (!hit.equals(target)) {
						blocked = true;
					}
				}
			} //else if trace is null, we hit air(?) and probably the trace is fine, we're just looking for a nonsolid block maybe.
			
			if (blocked) continue;
			
			
			if (te instanceof IAuxNetworkParticipant) {
				participantCache.add(te.getPos());
			} else {
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
		
		if (!world.isRemote) {
			if (currentlyCrafting!=null && storage.getStackInSlot(6).isEmpty()) {
				//IGNORE ALL OTHER CONCERNS - CRAFTING CAN HAPPEN
				if (emcCollected<currentlyCrafting.getEMC()) {
					timer--;
					if (timer<=0) {
						BlockPos pick = producerCache.toArray(new BlockPos[producerCache.size()])[(int)(Math.random()*producerCache.size())]; //No good way to yank a random member of a set
						TileEntity te = world.getTileEntity(pick);
						if (te.hasCapability(MagicArsenal.CAPABILITY_RUNEPRODUCER, null)) {
							IRuneProducer producer = te.getCapability(MagicArsenal.CAPABILITY_RUNEPRODUCER, null);
							int emcToRequest = Math.min(500, currentlyCrafting.getEMC()); //Request in 500EMC chunks, about 2 bookcases. Less if the crafting is cheap.
							emcCollected += producer.produceEMC(emcToRequest, false);
							this.markDirty();
						}
						
						timer = EMC_TIMER;
					}
					craftingTimer = CRAFTING_TIMER; //Set it up in case this finished it
					
					return;
				} else {
					if (craftingTimer == CRAFTING_TIMER) {
						//Spawn the in-world crafting effect
						SpellEffect.spawnEmitter("spellGather", null, world, pos);
					}
					craftingTimer--;
					if (craftingTimer<=0) {
						
						//TODO: BIG in-world effect. Crafting is over. No more crafting
						SpellEffect.spawnEmitter("coalesce", null, world, pos);
						storage.setStackInSlot(6, currentlyCrafting.getOutput().copy());
						currentlyCrafting = null;
						rescanTimer = 0; //We need to definitely update our EMC and radiance values
						//Fallthrough to lower code so that rescan happens now.
					} else {
						markDirty();
						return; //Don't rescan while the crafting effect is happening
					}
				}
				
				//Nothing can *actually* be crafted here. Let the rescans proceed.
			} else {
				if (storage.getStackInSlot(6).isEmpty()) {
					List<ShapelessAltarRecipe> matches = RunicAltarRecipes.findMatching(storage);
					if (!matches.isEmpty()) {
						Collections.shuffle(matches);
						for(ShapelessAltarRecipe recipe : matches) {
							if (recipe.getEMC()>this.emc || recipe.getRadiance()>this.radiance) continue;
							
							currentlyCrafting = matches.get(0);
							timer = EMC_TIMER;
							if (!currentlyCrafting.consumeIngredients(storage, false)) {
								currentlyCrafting = null;
								return;
							}
							emcCollected = 0;
							markDirty();
							//TODO: in-world effect
							return;
						}
						
						//Nothing can *actually* be crafted here. Let the rescans proceed.
					}
				}
			}
		}
		
		
		
		rescanTimer--;
		if (rescanTimer<=0) {
			scan();
			rescanTimer = RESCAN_PERIOD;
		}
	}

	@Override
	public IInventory getContainerInventory() {
		ValidatedInventoryView result = new ValidatedInventoryWrapperTakeTwo(storage);
		if (!world.isRemote) {
			result.withField(0, this::getEMC);
			result.withField(1, this::getRadiance);
		}
		return result;
	}
}
