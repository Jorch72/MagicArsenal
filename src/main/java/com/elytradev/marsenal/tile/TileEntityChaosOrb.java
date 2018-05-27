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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.elytradev.marsenal.capability.impl.DeepEnergyStorage;
import com.elytradev.marsenal.client.star.StarFlinger;
import com.google.common.base.Predicates;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

public class TileEntityChaosOrb extends TileEntity implements ITickable {
	public static final DamageSource ORB_DAMAGE = new DamageSource("magicarsenal.orb").setDamageBypassesArmor().setDamageIsAbsolute();
	public static final float ORB_DAMAGE_AMOUNT = 1f;
	public static final float ENERGY_PER_HARDNESS = 1000;
	public static final int VAPORIZATION_PERIOD = 20*20;
	public static final int ENTITY_PERIOD = 10;
	
	private static final BigInteger RADIANCE_SCALE = BigInteger.valueOf(1_000_000L);
	private DeepEnergyStorage energy = new DeepEnergyStorage();
	private Set<BlockPos> resonatorCache = new HashSet<>();
	private Set<BlockPos> dead = new HashSet<>();
	private double radius = 0D;
	private double effectiveRadius = 0D;
	private int radiusTicks = 0;
	
	private List<BlockPos> vaporizationCache = new ArrayList<>();
	private int vaporizationTimer = (int)(Math.random()*VAPORIZATION_PERIOD);
	
	private int entityTimer = 0;
	
	public TileEntityChaosOrb() {
		energy.setEnergyLimitInternal(BigInteger.ZERO); //No capacity until connected
		energy.listen(this::markDirty);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey("Energy")) {
			energy.readFromNBT(compound.getTag("Energy"));
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("Energy", energy.writeToNBT());
		return compound;
	}
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock()!=newState.getBlock();
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability==CapabilityEnergy.ENERGY) return true;
		return super.hasCapability(capability, facing);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability==CapabilityEnergy.ENERGY) return (T) energy;
		return super.getCapability(capability, facing);
	}
	
	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setDouble("Radius", radius);
		
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
		StarFlinger.spawnWorldEmitter(pos, "chaosorb", tag);
	}
	
	@Override
	public void onLoad() {
		if (world==null) return;
		//if (world.isRemote) StarFlinger.spawnWorldEmitter(pos, "chaosorb", new NBTTagCompound());
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}
	
	@Override
	public void update() {
		if (!hasWorld() || world.isRemote) return; //We don't even need to tick on the client.
		
		for(BlockPos cur : resonatorCache) {
			if (!world.isAreaLoaded(cur, 0)) {
				dead.add(cur);
				continue;
			}
			
			TileEntity te = world.getTileEntity(cur);
			if (te instanceof TileEntityChaosResonator) {
				TileEntityChaosResonator resonator = (TileEntityChaosResonator)te;
				
				resonator.transferFromOrb(energy);
			} else {
				dead.add(cur);
			}
		}
		resonatorCache.removeAll(dead);
		if (resonatorCache.isEmpty()) {
			energy.setEnergyLimit(BigInteger.ZERO); //No resonators means no radiance and no storage.
		}
		
		//double logRadius = Math.log10(energy.getFullEnergyStored().doubleValue());
		double x = energy.getFullEnergyStored().doubleValue();
		radius = Math.log10((x/50_000_000)+1) * 2.3;
			
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
		
		if (effectiveRadius<radius) {
			double delta = radius - effectiveRadius;
			effectiveRadius += delta/16d;
		} else if (effectiveRadius>radius) {
			double delta = effectiveRadius - radius;
			effectiveRadius -= delta/16d;
		}
		
			//markDirty();
		if (vaporizationTimer<=0) {
			destroyBlocks();
		} else {
			if (vaporizationCache.isEmpty()) {
				drawVaporizationCache();
			}
			vaporizationTimer--;
		}
		
		if (entityTimer<=0) {
			entityTimer = ENTITY_PERIOD;
			
			int r = (int)effectiveRadius;
			AxisAlignedBB bb = new AxisAlignedBB(
					pos.getX()-r,
					pos.getY()-r,
					pos.getZ()-r,
					pos.getX()+r+1,
					pos.getY()+r+1,
					pos.getZ()+r+1);
			r += 1;
			
			List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, bb);
			for(Entity entity : entities) {
				double squareDistance = entity.getPositionVector().addVector(0, entity.height/2, 0).squareDistanceTo(pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5);
				if (squareDistance<=r*r) {
					sear(entity, Math.sqrt(squareDistance));
				}
			}
		} else {
			entityTimer--;
		}
	}
	
	public void drawVaporizationCache() {
		int r = (int)effectiveRadius;
		if (effectiveRadius%1!=0) r++;
		for(int z = -r; z<=r; z++) {
			for(int y = -r; y<=r; y++) {
				for(int x = -r; x<=r; x++) {
					if (x==0 && y==0 && z==0) continue;
					if (Math.sqrt(x*x+y*y+z*z)>r) continue;
					vaporizationCache.add(new BlockPos(x,y,z).add(pos));
				}
			}
		}
		Collections.shuffle(vaporizationCache);
	}
	
	public void destroyBlocks() {
		if (vaporizationCache.isEmpty()) {
			vaporizationTimer = VAPORIZATION_PERIOD;
			return;
		}
		
		BlockPos next = vaporizationCache.remove(vaporizationCache.size()-1);
		vaporize(next);
	}
	
	public void vaporize(BlockPos pos) {
		if (world.isAirBlock(pos)) return;
		
		IBlockState state = world.getBlockState(pos);
		
		float hardness = state.getBlockHardness(world, pos);
		if (hardness<0) {
			energy.extract((long)(10*ENERGY_PER_HARDNESS), false);
		}
		
		if (state.getBlock() instanceof BlockLiquid) {
			world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2 | 16);
		} else {
			//world.destroyBlock(pos, false); //NO UPDATEY
			world.playEvent(2001, pos, Block.getStateId(state));
			world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2 | 16);
		}
		energy.extract((long)(hardness*ENERGY_PER_HARDNESS), false);
	}
	
	public void sear(Entity entity, double distance) {
		if (entity instanceof EntityLivingBase) {
			if (entity instanceof EntityArmorStand) {
				entity.setDead();
				return;
			}
			
			double distanceFromEdge = (effectiveRadius+1) - distance;
			
			entity.attackEntityFrom(ORB_DAMAGE, (float)(distanceFromEdge*ORB_DAMAGE_AMOUNT));
			
		} else {
			
			entity.setDead();
		}
	}
	
	/**
	 * Notifies this Chaos Orb that a resonator is positioned to beam energy in and out of it.
	 */
	public void pingFromResonator(BlockPos pos) {
		resonatorCache.add(pos);
	}

	public void updateRadiance(int radiance) {
		energy.setEnergyLimit(BigInteger.valueOf(radiance).multiply(RADIANCE_SCALE));
	}
	
	public DeepEnergyStorage getEnergyStorage() {
		return energy;
	}

	public double getRadius() {
		return radius;
		
	}
}
