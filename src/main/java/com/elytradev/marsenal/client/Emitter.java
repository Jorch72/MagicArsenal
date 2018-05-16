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

package com.elytradev.marsenal.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import com.elytradev.marsenal.MagicArsenal;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

/**
 * Notes to implementors:
 * <li> Emitters MUST have a zero-arg constructor, and be registered with Emitter.
 */
public abstract class Emitter {
	private static HashMap<String, Class<? extends Emitter>> registry = new HashMap<>();
	public static void register(String key, Class<? extends Emitter> impl) { registry.put(key, impl); }
	@Nullable
	public static Emitter create(String key) {
		Class<? extends Emitter> clazz = registry.get(key);
		try {
			if (clazz!=null) {
				return clazz.getDeclaredConstructor().newInstance();
			}
		} catch (Throwable t) {
			MagicArsenal.LOG.warn("Unable to create emitter for class {}. Thrown: {}", clazz, t);
		}
		
		return null;
	}
	
	public World world;
	public float x = 0;
	public float y = 0;
	public float z = 0;
	public Entity source = null;
	public Entity entity = null;
	private boolean dead = false;
	
	public void init(World world, float x, float y, float z, Entity source, Entity target) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.source = source;
		this.entity = target;
	}
	
	public void kill() {
		dead = true;
	}
	
	/** Called once every world tick, so that new particles can be spawned or old particles can have their trajectories adjusted */
	public abstract void tick();
	/** Called once every frame, in case the emitter needs to draw an effect directly to the screen in worldspace. */
	public abstract void draw(float partialFrameTime, double dx, double dy, double dz);
	/** If this returns true at any time, the emitter will be removed from the scheduler and cease to be. */
	public boolean isDead() {
		return dead;
	}
	
	
	public static void drawStars(double dx, double dy, double dz, Collection<Star> stars) {
		drawStars(0, dx, dy, dz, stars, false);
	}
	
	private static List<Star> deadStars = new ArrayList<>();
	public static void drawStars(float partialFrameTime, double dx, double dy, double dz, Collection<Star> stars, boolean doPhysics) {
		GlStateManager.disableLighting();
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		
		for(Star star : stars) {
			star.paint(dx, dy, dz);
			if (doPhysics) {
				star.tick(partialFrameTime);
				if (star.lifetime<=0) deadStars.add(star);
			}
		}
		if (doPhysics) {
			stars.removeAll(deadStars);
			deadStars.clear();
		}
		GlStateManager.disableBlend();
		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
	}
}
