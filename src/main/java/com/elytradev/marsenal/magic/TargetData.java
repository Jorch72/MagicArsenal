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

package com.elytradev.marsenal.magic;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;

import com.google.common.base.Predicates;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class TargetData<T extends Entity> {
	//public static final Predicate<Entity> LIVING_ENTITIES = (it)->it instanceof EntityLivingBase;
	public static final Predicate<EntityLivingBase>  NON_HOSTILE = (it)-> !(it instanceof EntityMob);
	
	protected EntityLivingBase caster;
	protected Class<T> targetClass;
	
	public EntityLivingBase getCaster() { return caster; }
	
	public TargetData(EntityLivingBase caster, Class<T> targetClass) {
		this.caster = caster;
		this.targetClass = targetClass;
	}
	
	protected static RayTraceResult raycastEntity(Entity exclude, World world, Vec3d pos, Vec3d dir, int range, Predicate<Entity> rule) {
        Vec3d lookTarget = pos.add(dir.normalize().scale(range));
        RayTraceResult raytraceresult = world.rayTraceBlocks(pos, lookTarget, false, true, false);

        if (raytraceresult != null) {
        	lookTarget = new Vec3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);
        } else {
        	raytraceresult = new RayTraceResult(RayTraceResult.Type.MISS, lookTarget, null, new BlockPos(lookTarget));
        }

        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(exclude, new AxisAlignedBB(pos.x-range, pos.y-range, pos.z-range, pos.x+range, pos.y+range, pos.z+range).grow(1.0D));
        double nearestDistanceSq = 0.0D;

        for (Entity target : list) {
        	if (!rule.test(target)) continue;
            AxisAlignedBB axisalignedbb = target.getEntityBoundingBox().grow(0.30000001192092896D);
            RayTraceResult raytraceresult1 = axisalignedbb.calculateIntercept(pos, lookTarget);

            if (raytraceresult1 != null) {
                double distanceSq = pos.squareDistanceTo(raytraceresult1.hitVec);

                if (distanceSq < nearestDistanceSq || nearestDistanceSq == 0.0D) {
                	raytraceresult.hitVec = raytraceresult1.hitVec;
                	raytraceresult.entityHit = target;
                	raytraceresult.sideHit = raytraceresult1.sideHit;
                	raytraceresult.typeOfHit = RayTraceResult.Type.ENTITY;
                    nearestDistanceSq = distanceSq;
                }
            }
        }
        
        return raytraceresult;
	}
	
	protected static Entity raycastEntity(EntityLivingBase caster, int range, Predicate<Entity> rule) {
        World world = caster.world;
        Vec3d pos = new Vec3d(caster.posX, caster.posY+caster.getEyeHeight(), caster.posZ);
        Vec3d lookTarget = pos.add(caster.getLookVec().normalize().scale(range));
        RayTraceResult raytraceresult = world.rayTraceBlocks(pos, lookTarget, false, true, false);

        if (raytraceresult != null) {
        	lookTarget = new Vec3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);
        }

        Entity result = null;
        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(caster, caster.getEntityBoundingBox().expand(range, range, range).expand(-range, -range, -range).grow(1.0D));
        double nearestDistanceSq = 0.0D;

        for (Entity target : list) {
        	if (!rule.test(target)) continue;
            AxisAlignedBB axisalignedbb = target.getEntityBoundingBox().grow(0.30000001192092896D);
            RayTraceResult raytraceresult1 = axisalignedbb.calculateIntercept(pos, lookTarget);

            if (raytraceresult1 != null) {
                double distanceSq = pos.squareDistanceTo(raytraceresult1.hitVec);

                if (distanceSq < nearestDistanceSq || nearestDistanceSq == 0.0D) {
                    result = target;
                    nearestDistanceSq = distanceSq;
                }
            }
        }

        return result;
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends Entity> Predicate<Entity> downcastingPredicate(Predicate<T> predicate, Class<T> clazz) {
		return (it)->{
			if (!clazz.isAssignableFrom(it.getClass())) return false;
			
			T t = (T)it;
			return predicate.test(t);
		};
	}
	
	/*
	public static boolean isLookingAt(EntityLivingBase entity, Entity target) {
		if (entity.canEntityBeSeen(target)) return false;
		
		Vec3d lookVec = entity.getLook(1.0F).normalize();
		double targetHalfHeight = target.height / 2D;
		//Vector from the *eye position* of the caster to the *center of mass* of the target.
		//This is different from the "eye to eye" test that endermen do!
        Vec3d lookStraightAtEntityVec = new Vec3d(
        		target.posX - entity.posX,
        		target.getEntityBoundingBox().minY + targetHalfHeight - (entity.posY + entity.getEyeHeight()),
        		target.posZ - entity.posZ
        		);
        double dist = lookStraightAtEntityVec.lengthVector();
        lookStraightAtEntityVec = lookStraightAtEntityVec.normalize();
        double dot = lookVec.dotProduct(lookStraightAtEntityVec);
        if (dot > 1.0D - (0.025D / dist)) { //Dot will be 1.0 at 0 degrees difference, 0 at 90 degrees, and -1 at 180 degrees around the shortest axis. 0.025 is probably about 87.75 degrees at 0 meters,  43.8 degrees at 1m, 
        	return entity.canEntityBeSeen(target);
        } else {
        	return false;
        }
	}*/
	
	public static class Single<T extends Entity> extends TargetData<T> {
		private T target;
		
		public Single(EntityLivingBase caster, Class<T> targetClass) {
			super(caster, targetClass);
		}
		
		public T targetRaycast(int range) {
			targetRaycast(range, Predicates.alwaysTrue());
			return target;
		}
		
		/** On a raycast failure, the target becomes null */
		@SuppressWarnings("unchecked")
		public T targetRaycast(int range, Predicate<T> rule) {
			Predicate<Entity> casted = downcastingPredicate(rule, targetClass);
			
			Entity e = raycastEntity(caster, range, (it)->targetClass.isAssignableFrom(it.getClass()) && casted.test(it));
			if (!targetClass.isAssignableFrom(e.getClass())) {
				target = null;
				return null;
			}
			target = (T)e;
			return target;
		}
		
		/** Returns whatever entity or block we hit when we raycast towards our target. */
		public RayTraceResult raycastToExistingTarget(int range, Predicate<T> rule) {
			Predicate<Entity> casted = downcastingPredicate(rule, targetClass);
			Vec3d pos = new Vec3d(caster.posX, caster.posY+caster.getEyeHeight(), caster.posZ);
			Vec3d targetPos = new Vec3d(target.posX, target.posY+(target.height/2), target.posZ);
			return raycastEntity(caster, caster.getEntityWorld(), pos, targetPos.subtract(pos).normalize(), range, (it)->targetClass.isAssignableFrom(it.getClass()) && casted.test(it));
		}
		
		public T getTarget() { return target; }
		public void clearTarget() { target = null; }
		public boolean hasTarget() { return target!=null; }
		
		public static Single<EntityLivingBase> living(EntityLivingBase caster) {
			return new Single<>(caster, EntityLivingBase.class);
		}
	}
	
	public static class Multi<T extends Entity> extends TargetData<T> {
		private HashSet<T> targets = new HashSet<>();
		
		public Multi(EntityLivingBase caster, Class<T> targetClass) {
			super(caster, targetClass);
		}
		
		public void targetRaycast(int range) {
			targetRaycast(range, Predicates.alwaysTrue());
		}
		
		/** On a raycast failure, the target list is *unchanged*. */
		@SuppressWarnings("unchecked")
		public void targetRaycast(int range, Predicate<T> rule) {
			Predicate<Entity> casted = downcastingPredicate(rule, targetClass);
			
			Entity e = raycastEntity(caster, range, (it)->targetClass.isAssignableFrom(it.getClass()) && casted.test(it));
			if (!targetClass.isAssignableFrom(e.getClass())) {
				return;
			}
			targets.add((T)e); //Doublechecked because who knows
		}
		
		public Collection<T> getTargets() {
			return targets;
		}
		
		public void clearTargets() { targets.clear(); }
		public boolean hasTargets() { return !targets.isEmpty(); }
		
		public static Multi<EntityLivingBase> living(EntityLivingBase caster) {
			return new Multi<>(caster, EntityLivingBase.class);
		}
	}
}
