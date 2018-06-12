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

import java.util.List;
import java.util.function.Predicate;

import com.elytradev.concrete.inventory.IContainerInventoryHolder;
import com.elytradev.concrete.inventory.ValidatedInventoryView;
import com.elytradev.marsenal.capability.impl.FlexibleItemHandler;
import com.elytradev.marsenal.capability.impl.ValidatedInventoryWrapperTakeTwo;
import com.elytradev.marsenal.client.star.StarFlinger;
import com.elytradev.marsenal.item.IBeaconSigil;
import com.google.common.base.Predicates;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

public class TileEntityRadiantBeacon extends TileEntity implements IAuxNetworkParticipant, ITickable, IContainerInventoryHolder {
	protected BlockPos controller = null;
	protected long lastControllerPing = 0L;
	protected BlockPos beamTo = null;
	Predicate<ItemStack> BEACON_SIGIL = it->it.getItem() instanceof IBeaconSigil;
	
	protected double radius;
	protected double effectiveRadius;
	
	protected int timer = 0;
	protected int radiusTicks = 0;
	
	protected FlexibleItemHandler storage = new FlexibleItemHandler(6)
			.setName("tile.magicarsenal.beacon.name")
			.setCanExtract(true, true, true, true, true, true)
			.setValidators(BEACON_SIGIL, BEACON_SIGIL, BEACON_SIGIL, BEACON_SIGIL, BEACON_SIGIL, BEACON_SIGIL)
			.setMaxStackSize(1);
	
	public TileEntityRadiantBeacon() {
		storage.listen(this::markDirty);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		
		if (compound.hasKey("Inventory")) {
			storage.deserializeNBT(compound.getCompoundTag("Inventory"));
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagCompound tag = super.writeToNBT(compound);
		tag.setTag("Inventory", storage.serializeNBT());
		
		return tag;
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
		radius = Math.min(radiance, 100);
		if (world!=null) this.lastControllerPing = world.getTotalWorldTime();
	}
	
	@Override
	public void update() {
		if (world==null || world.isRemote) return;
		
		if (effectiveRadius<radius) {
			double delta = radius-effectiveRadius;
			effectiveRadius += delta/16d;
		} else if (effectiveRadius>radius) {
			double delta = effectiveRadius-radius;
			effectiveRadius -= delta/16d;
		}
		
		timer--;
		if (timer<=0) {
			timer = 20*10;
			
			AxisAlignedBB aabb = new AxisAlignedBB(pos.getX()+0.5-effectiveRadius, pos.getY()+0.5-effectiveRadius, pos.getZ()+0.5-effectiveRadius, pos.getX()+0.5+effectiveRadius, pos.getY()+0.5+effectiveRadius, pos.getZ()+0.5+effectiveRadius);
			List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, aabb, (it)->it.getDistanceSqToCenter(pos)<effectiveRadius*effectiveRadius);
			//System.out.println("Ticking beacon on "+entities.size()+" entities.");
			for(int i=0; i<storage.getSlots(); i++) {
				ItemStack stack = storage.getStackInSlot(i);
				if (stack.isEmpty()) continue;
				if (stack.getItem() instanceof IBeaconSigil) {
					for(Entity entity : entities) {
						((IBeaconSigil)stack.getItem()).applyEffect(entity, stack, pos);
					}
				}
			}
		}
		
		radiusTicks--;
		if (radiusTicks<=0) {
			radiusTicks = 20;
			if (world instanceof WorldServer) {
				WorldServer ws = (WorldServer)world;
				Chunk c = world.getChunkFromBlockCoords(pos);
				SPacketUpdateTileEntity updatePacket = getUpdatePacket();
				
				for (EntityPlayerMP player : getWorld().getPlayers(EntityPlayerMP.class, Predicates.alwaysTrue())) {
					if (ws.getPlayerChunkMap().isPlayerWatchingChunk(player, c.x, c.z)) {
						player.connection.sendPacket(updatePacket);
					}
				}
			}
		}
	}
	
	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setDouble("Radius", radius);
		
		//Mix hues in the most obnoxious way possible
		Vec3d result = new Vec3d(0,0,0);
		for(int i=0; i<6; i++) {
			ItemStack stack = storage.getStackInSlot(i);
			if (stack.isEmpty()) continue;
			if (stack.getItem() instanceof IBeaconSigil) {
				IBeaconSigil sigil = (IBeaconSigil) stack.getItem();
				float hue = sigil.getColorHue();
				Vec3d hueVec = Vec3d.fromPitchYaw(0, hue);
				result = result.add(hueVec);
			}
		}
		double finalHue = 0;
		if (result.x!=0 || result.z!=0) {
			finalHue = Math.atan2(result.z, result.x) * (360d/(2d*Math.PI)) + 180d;
		}
		
		tag.setDouble("Hue", finalHue);
		
		return tag;
	}
	
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
	}
	
	@Override
	public void onDataPacket(net.minecraft.network.NetworkManager net, net.minecraft.network.play.server.SPacketUpdateTileEntity pkt) {
		handleUpdateTag(pkt.getNbtCompound());
	}
	
	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		radius = tag.getDouble("Radius");
		if (effectiveRadius==0) effectiveRadius = radius;
		StarFlinger.spawnWorldEmitter(pos, "radiantbeacon", tag);
	}
	
	public float getRadius() {
		return (float)effectiveRadius;
	}
	
	@Override
	public IInventory getContainerInventory() {
		ValidatedInventoryView result = new ValidatedInventoryWrapperTakeTwo(storage);
		if (!world.isRemote) {
			result.withField(0, ()->(int)this.getRadius());
		}
		return result;
	}

	public FlexibleItemHandler getStorage() {
		return storage;
	}
}
