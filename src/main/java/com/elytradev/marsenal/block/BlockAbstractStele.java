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

package com.elytradev.marsenal.block;

import java.util.Random;

import com.elytradev.marsenal.client.ParticleVelocity;
import com.elytradev.marsenal.tile.INetworkParticipant;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockAbstractStele extends BlockSimple implements ITileEntityProvider {
	public BlockAbstractStele(String id) {
		super("stele."+id);
		this.setLightLevel(0.5f);
	}

	@Override
	@SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World world, BlockPos pos, Random rand) {
		TileEntity te = world.getTileEntity(pos);
		if (te!=null && te instanceof INetworkParticipant) {
			BlockPos target = ((INetworkParticipant)te).getBeamTo();
			if (target!=null) {
				spawnBeamToEffect(world, pos, target, rand);
			}
		}
    }
	
	@SideOnly(Side.CLIENT)
	public static void spawnBeamToEffect(World world, BlockPos self, BlockPos target, Random rand) {
		float px = (float)(self.getX()+0.5f);
		float py = (float)(self.getY()+0.5f);
		float pz = (float)(self.getZ()+0.5f);
		
		Vec3d vec = new Vec3d(target.getX()-self.getX(), target.getY()-self.getY(), target.getZ()-self.getZ()).scale(1/50d);
		
		Particle particle = new ParticleVelocity(world,
				px, py, pz,
				(float)vec.x, (float)vec.y, (float)vec.z
				);
		particle.setMaxAge(100);
		particle.setParticleTextureIndex(225+rand.nextInt(25)); //Anywhere in the galactic alphabet is fine.
		particle.setRBGColorF(0.6f, 0.6f, 0.9667f);
		
		Minecraft.getMinecraft().effectRenderer.addEffect(particle);
	}
}
