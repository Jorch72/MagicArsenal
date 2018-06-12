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

package com.elytradev.marsenal.client.star;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.lwjgl.opengl.GL11;

import com.elytradev.marsenal.client.Emitter;
import com.elytradev.marsenal.client.PartialTickTime;
import com.elytradev.marsenal.client.WorldEmitter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class StarFlinger {
	private static BufferBuilder buffer = null;
	private static int maxStars = 200;
	/* Another possible way to solve this problem, and I may switch to this for performance reasons later, would be to
	 * ping stars back and forth between two arrays like a double-buffer, and potentially use Arrays.fill() on the size
	 * difference to clear out any extra old entries so they can GC after two frames. Let's keep benchmarking, friends!
	 */
	private static Set<IStar> globalStars = new ConcurrentSkipListSet<>();
	private static Set<IStar> deadStars = new ConcurrentSkipListSet<>();
	private static Map<BlockPos, WorldEmitter> worldEmitters = new ConcurrentSkipListMap<>();
	//private static Set<BlockPos> orbs = new HashSet<>();
	private static float frameTime;
	
	public static BufferBuilder startBatch() {
		if (buffer!=null) {
			buffer.finishDrawing();
		}
		
		frameTime = PartialTickTime.getFrameTime();
		GlStateManager.disableLighting();
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.depthMask(false);
		GlStateManager.disableCull();
		GlStateManager.disableAlpha();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		
		Tessellator tess = Tessellator.getInstance();
		buffer = tess.getBuffer();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		
		return buffer;
	}
	
	/**
	 * Spawns an emitter at a given location. If an existing emitter is present at that location, refreshes its duration
	 * and updates its parameters.
	 */
	public static void spawnWorldEmitter(BlockPos pos, String id, NBTTagCompound tag) {
		if (worldEmitters.containsKey(pos)) {
			worldEmitters.get(pos).refreshAndUpdate(tag);
		} else {
			WorldEmitter emitter = Emitter.createWorldEmitter(id, tag);
			if (emitter!=null) {
				emitter.x = pos.getX()+0.5f;
				emitter.y = pos.getY()+0.5f;
				emitter.z = pos.getZ()+0.5f;
				
				worldEmitters.put(pos, emitter);
			}
		}
	}
	
	public static void paint(Collection<IStar> stars, boolean tick) {
		if (stars.size()<=0) return;
		
		//System.out.println("Painting "+stars.size()+" stars at frame interval "+frameTime);
		for(IStar star : stars) {
			if (buffer!=null) star.paint(buffer);
			if (tick) {
				star.tick(frameTime);
				if (star.isDead()) deadStars.add(star);
			}
		}
		
		if (tick) {
			stars.removeAll(deadStars);
			deadStars.clear();
		}
	}
	
	public static void paintAndTickGlobalStars() {
		paint(globalStars, true);
		
		WorldClient world = Minecraft.getMinecraft().world;
		if (world==null) return; //No world? No worldEmitters.
		HashSet<BlockPos> deadWorldEmitters = new HashSet<BlockPos>();
		for(Map.Entry<BlockPos, WorldEmitter> entry : worldEmitters.entrySet()) {
			
			if (!world.isAreaLoaded(entry.getKey(), 0)) {
				deadWorldEmitters.add(entry.getKey());
				continue;
			}
			
			if (buffer!=null) entry.getValue().paint(buffer);
			entry.getValue().tick(frameTime);
			if (entry.getValue().isDead()) deadWorldEmitters.add(entry.getKey());
		}
		for(BlockPos it : deadWorldEmitters) worldEmitters.remove(it);
	}
	
	public static void endBatch() {
		Tessellator.getInstance().draw();
		
		GlStateManager.enableCull();
		GlStateManager.depthMask(true);
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		//GlStateManager.enableLighting(); //Breaks Astral Sorcery effects!
		GlStateManager.disableLighting();
		GlStateManager.enableTexture2D();
		
		buffer = null;
	}
	
	private static int getEffectiveCap() {
		switch (Minecraft.getMinecraft().gameSettings.particleSetting) {
		case 0: return maxStars;
		case 1: return maxStars >> 1;
		case 2: return maxStars >> 2;
		default: return maxStars >> 3;
		}
	}
	
	public static void spawn(IStar star) {
		if(globalStars.size()>=getEffectiveCap()) return; //Refuse to spawn too many stars
		
		globalStars.add(star);
	}
}
