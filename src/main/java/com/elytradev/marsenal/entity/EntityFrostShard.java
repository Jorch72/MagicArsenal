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

package com.elytradev.marsenal.entity;

import java.util.List;
import java.util.Random;

import com.elytradev.marsenal.ArsenalConfig;
import com.elytradev.marsenal.SpellEvent;
import com.elytradev.marsenal.client.ParticleVelocity;
import com.elytradev.marsenal.magic.EnumElement;
import com.elytradev.marsenal.magic.SpellDamageSource;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityFrostShard extends EntityThrowable {
	private static final float WIDTH = 1.0f;
	private static final float HEIGHT = 0.5f;
	
	private Random random = new Random();
	
	private int maxAge = 40;
	private int age = 0;
	
	public EntityFrostShard(World world) {
		super(world);
		
		this.width = WIDTH;
		this.height = HEIGHT;
		this.setNoGravity(true);
	}
	
	public EntityFrostShard(World world, EntityLivingBase thrower) {
		super(world, thrower);
		
		this.width = WIDTH;
		this.height = HEIGHT;
		this.setNoGravity(true);
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		if (!this.world.isRemote) {
            if (result.entityHit != null && this.thrower!=null) {
            	if (result.entityHit instanceof EntityLivingBase) {
            		SpellEvent.DamageEntity event = new SpellEvent.DamageEntity("frostShards", this.thrower, (EntityLivingBase)result.entityHit, EnumElement.ARCANE, EnumElement.FROST)
            				.setDamage(ArsenalConfig.get().spells.frostShards.potency);
            		
            		if (!event.isCanceled()) {
            			result.entityHit.attackEntityFrom(new SpellDamageSource(this.thrower, "frostShards", EnumElement.ARCANE, EnumElement.FROST), event.getDamage());
            		}
            	}
            }

            this.setDead();
        }
	}
	
	/**
     * Sets throwable heading based on an entity that's throwing it. Copied from EntityThrowable
     */
	/*
    public void shoot(Entity entityThrower, float velocity, float inaccuracy) {
    	float rotationYawIn = entityThrower.rotationYaw;
    	float rotationPitchIn = entityThrower.rotationPitch;
    	
    	
        float f = -MathHelper.sin(rotationYawIn * 0.017453292F) * MathHelper.cos(rotationPitchIn * 0.017453292F);
        float f1 = -MathHelper.sin(rotationPitchIn * 0.017453292F);
        float f2 = MathHelper.cos(rotationYawIn * 0.017453292F) * MathHelper.cos(rotationPitchIn * 0.017453292F);
        this.shoot((double)f, (double)f1, (double)f2, velocity, inaccuracy);
        this.motionX += entityThrower.motionX;
        this.motionZ += entityThrower.motionZ;

        if (!entityThrower.onGround) {
            this.motionY += entityThrower.motionY;
        }
    }*/

    /**
     * Similar to setArrowHeading, it's point the throwable entity to a x, y, z direction. Copied in from EntityThrowable
     */
	/*
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        float f = MathHelper.sqrt(x * x + y * y + z * z);
        x = x / (double)f; //normalize
        y = y / (double)f;
        z = z / (double)f;
        x = x + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy; //fuzz
        y = y + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
        z = z + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
        x = x * (double)velocity; //scale to velocity so we get a motion vector
        y = y * (double)velocity;
        z = z * (double)velocity;
        this.motionX = x; //set our velocity to this vector
        this.motionY = y;
        this.motionZ = z;
        
        //What is the *horizontal* component of our velocity? We need to find the ratio between the vertical and horizontal components to get a pitch angle
        float f1 = MathHelper.sqrt(x * x + z * z);
        
        //Fudge some euler angles from our motion vector
        this.rotationYaw = (float)(MathHelper.atan2(x, z) * (180D / Math.PI));
        this.rotationPitch = (float)(MathHelper.atan2(y, (double)f1) * (180D / Math.PI));
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
    }*/
	
	
	@Override
	public void onUpdate() {
		//super.onUpdate();
		throwableUpdate();
		
		
		if (this.world!=null && this.world.isRemote) {
			for(int i=0; i<3; i++) {
				float px = (float)(posX + random.nextGaussian()*0.5d);
				float py = (float)(posY);
				float pz = (float)(posZ + random.nextGaussian()*0.5d);
				
				Particle particle = new ParticleVelocity(world,
						px, py, pz,
						0f, -0.6f, 0f
						);
				particle.setParticleTextureIndex(5); //Midway through redstone
				particle.setRBGColorF(0.6f, 0.6f, 0.9667f);
				
				Minecraft.getMinecraft().effectRenderer.addEffect(particle);
			}
		}
		
		
		
		
		this.age++;
		if (this.age>maxAge) this.setDead();
	}
	
	private void throwableUpdate() {
        this.lastTickPosX = this.posX;
        this.lastTickPosY = this.posY;
        this.lastTickPosZ = this.posZ;
        super.onUpdate();

        if (this.throwableShake > 0) {
            --this.throwableShake;
        }

        Vec3d vec3d = new Vec3d(this.posX, this.posY, this.posZ);
        Vec3d vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        RayTraceResult raytraceresult = this.world.rayTraceBlocks(vec3d, vec3d1);
        vec3d = new Vec3d(this.posX, this.posY, this.posZ);
        vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

        if (raytraceresult != null) {
            vec3d1 = new Vec3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);
        }

        Entity entity = null;
        List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(1.0D));
        double d0 = 0.0D;
        boolean flag = false;

        for (int i = 0; i < list.size(); ++i) {
            Entity entity1 = list.get(i);

            if (entity1.canBeCollidedWith()) {
                if (entity1 == this.ignoreEntity) {
                    flag = true;
                }
                else if (this.thrower != null && this.ticksExisted < 2 && this.ignoreEntity == null) {
                    this.ignoreEntity = entity1;
                    flag = true;
                }
                else {
                    flag = false;
                    AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow(0.30000001192092896D);
                    RayTraceResult raytraceresult1 = axisalignedbb.calculateIntercept(vec3d, vec3d1);

                    if (raytraceresult1 != null) {
                        double d1 = vec3d.squareDistanceTo(raytraceresult1.hitVec);

                        if (d1 < d0 || d0 == 0.0D) {
                            entity = entity1;
                            d0 = d1;
                        }
                    }
                }
            }
        }

        if (entity != null) {
            raytraceresult = new RayTraceResult(entity);
        }

        if (raytraceresult != null) {
            if (raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK && this.world.getBlockState(raytraceresult.getBlockPos()).getBlock() == Blocks.PORTAL) {
                this.setPortal(raytraceresult.getBlockPos());
            } else {
                if(!net.minecraftforge.common.ForgeHooks.onThrowableImpact(this, raytraceresult))
                this.onImpact(raytraceresult);
            }
        }

        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));

        for (this.rotationPitch = (float)(MathHelper.atan2(this.motionY, (double)f) * (180D / Math.PI)); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {}

        while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
            this.prevRotationPitch += 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
            this.prevRotationYaw -= 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
            this.prevRotationYaw += 360.0F;
        }

        this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
        this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
        float f1 = 0.99F;
        float f2 = this.getGravityVelocity();

        if (this.isInWater()) {
            for (int j = 0; j < 4; ++j) {
                float f3 = 0.25F;
                this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * 0.25D, this.posY - this.motionY * 0.25D, this.posZ - this.motionZ * 0.25D, this.motionX, this.motionY, this.motionZ);
            }

            f1 = 0.8F;
        }

        this.motionX *= (double)f1;
        this.motionY *= (double)f1;
        this.motionZ *= (double)f1;

        this.setPosition(this.posX, this.posY, this.posZ);
	}
	
	/*
	@Override
	protected boolean isFireballFiery() {
		return false;
	}*/

}
