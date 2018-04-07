/*
 * MIT License
 *
 * Copyright (c) 2017 Isaac Ellingson (Falkreon) and contributors
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

package com.elytradev.marsinal.magic;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;

public class TargetData {
	EntityLivingBase caster;
	List<Entity> targets = new ArrayList<>();
	
	public EntityLivingBase getCaster() { return caster; }
	public List<Entity> getTargets() { return targets; }
	
	public TargetData(EntityLivingBase caster) {
		this.caster = caster;
	}
	
	/** Adds entities along the caster's line of sight to the target list. */
	public void targetLine(Predicate<Entity> shouldAdd) {
		TargetData result = new TargetData(caster);
		
	}
	
	
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
	}
}
