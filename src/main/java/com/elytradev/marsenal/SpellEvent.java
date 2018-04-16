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

package com.elytradev.marsenal;

import java.util.Collection;
import java.util.EnumSet;

import javax.annotation.Nullable;

import com.elytradev.marsenal.magic.EnumElement;
import com.elytradev.marsenal.magic.TargetData;
import com.google.common.collect.ImmutableSet;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Fired at the instant before a spellcast, when the targets are known.
 */
@Cancelable
public class SpellEvent extends Event {
	private final String spellId;
	private final EntityLivingBase caster;
	private final EnumSet<EnumElement> elements;
	private final World world;
	private final BlockPos origin;
	
	public SpellEvent(String spellId, EntityLivingBase caster, Collection<EnumElement> elements) {
		this.caster = caster;
		this.world = caster.getEntityWorld();
		this.origin = caster.getPosition();
		this.spellId = spellId;
		this.elements = EnumSet.noneOf(EnumElement.class);
		this.elements.addAll(elements);
	}
	
	public SpellEvent(String spellId, EntityLivingBase caster, EnumElement... elements) {
		this.caster = caster;
		this.world = caster.getEntityWorld();
		this.origin = caster.getPosition();
		this.spellId = spellId;
		this.elements = EnumSet.noneOf(EnumElement.class);
		for(EnumElement elem : elements) this.elements.add(elem);
	}
	
	/** This constructor should *only* be used if there is no living caster entity */
	public SpellEvent(String spellId, World world, BlockPos origin, Collection<EnumElement> elements) {
		this.caster = null;
		this.world = world;
		this.origin = origin;
		this.spellId = spellId;
		this.elements = EnumSet.noneOf(EnumElement.class);
		this.elements.addAll(elements);
	}
	
	/** This constructor should *only* be used if there is no living caster entity */
	public SpellEvent(String spellId, World world, BlockPos origin, EnumElement... elements) {
		this.caster = null;
		this.world = world;
		this.origin = origin;
		this.spellId = spellId;
		this.elements = EnumSet.noneOf(EnumElement.class);
		for(EnumElement elem : elements) this.elements.add(elem);
	}

	/** Returns the spell ID for this spell. This is a unique, case-sensitive String identifying the spell being cast. */
	public String getSpellId() {
		return spellId;
	}
	
	/** Returns the entity that activated this spell. If an entity did not activate this spell, may be null. */
	@Nullable
	public EntityLivingBase getCaster() {
		return caster;
	}
	
	/** Gets the world the spell was cast in. */
	public World getWorld() {
		return world;
	}
	
	/** Returns the location where the spell was cast (usually that of the spellcaster), regardless of where the area
	 * of effect is. This is especially relevant for wards.
	 */
	public BlockPos getOrigin() {
		return origin;
	}
	
	/** Returns an immutable set view of the elements present in this spell. */
	public ImmutableSet<EnumElement> getElements() {
		return ImmutableSet.copyOf(elements);
	}
	
	/** Modify this spellcast, adding an element to its nature. */
	public void addElement(EnumElement elem) {
		elements.add(elem);
	}
	

	public static class CastProjectile extends SpellEvent {
		public CastProjectile(String spellId, EntityLivingBase caster, Collection<EnumElement> elements) {
			super(spellId, caster, elements);
		}
		
		public CastProjectile(String spellId, EntityLivingBase caster, EnumElement... elements) {
			super(spellId, caster, elements);
		}
		
		public CastProjectile(String spellId, World world, BlockPos origin, Collection<EnumElement> elements) {
			super(spellId, world, origin, elements);
		}
		
		public CastProjectile(String spellId, World world, BlockPos origin, EnumElement... elements) {
			super(spellId, world, origin, elements);
		}
	}

	public static class CastOnEntity extends SpellEvent {
		private Entity target;
		
		public CastOnEntity(String spellId, EntityLivingBase caster, Entity target, EnumElement...elements) {
			super(spellId, caster, elements);
			this.target = target;
		}
		
		public CastOnEntity(String spellId, World world, BlockPos origin, Entity target, EnumElement...elements) {
			super(spellId, world, origin, elements);
			this.target = target;
		}
		
		public CastOnEntity(String spellId, TargetData.Single<? extends Entity> targetData, EnumElement... elements) {
			super(spellId, targetData.getCaster(), elements);
			this.target = targetData.getTarget();
		}
		
		public Entity getTarget() {
			return target;
		}
		
		public void changeTarget(Entity newTarget) {
			target = newTarget;
		}
	}
	
	public static class CastOnArea extends SpellEvent {
		private BlockPos target;
		private float radius;
		
		public CastOnArea(String spellId, EntityLivingBase caster, BlockPos target, float radius, EnumElement...elements) {
			super(spellId, caster, elements);
			this.target = target;
			this.radius = radius;
		}
		
		public CastOnArea(String spellId, World world, BlockPos origin, BlockPos target, float radius, EnumElement...elements) {
			super(spellId, world, origin, elements);
			this.target = target;
			this.radius = radius;
		}
		
		public BlockPos getTarget() {
			return target;
		}
		
		public float getRadius() {
			return radius;
		}
		
	}
}
