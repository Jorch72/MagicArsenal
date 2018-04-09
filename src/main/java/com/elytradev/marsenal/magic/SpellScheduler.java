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

import java.util.ArrayList;

//Not synchronized. MUST called from main Minecraft thread.
public class SpellScheduler {
	private static long currentTick = 0L;
	
	private static ArrayList<Entry> near = new ArrayList<>();
	//private static ArrayList<Entry> far100 = new ArrayList<>();
	//private static ArrayList<Entry> far1k = new ArrayList<>();
	private static ArrayList<Entry> deadNear = new ArrayList<>();
	
	public static void tick() {
		for(Entry entry : near) {
			if (currentTick >= entry.targetTick) {
				int next = entry.tickable.tick();
				if (next<=0) {
					deadNear.add(entry);
				} else {
					entry.targetTick = currentTick + next;
				}
			}
		}
		
		for(Entry entry : deadNear) {
			near.remove(entry);
		}
		
		//HACKY HACK HACKING ALL THE WAY TO HACKTOWN
		//WILL ALSO ONLY BE NEEDED IF THE SERVER IS RUNNING FOR ROUGHLY 14623560433 YEARS AT A TIME
		if (currentTick==Long.MAX_VALUE) {
			for(Entry entry : near) entry.targetTick -= Long.MAX_VALUE;
		}
		currentTick++;
	}
	
	public static void schedule(IScheduledTickable effect) {
		near.add(new Entry(effect, currentTick+1));
	}
	
	public static void schedule(ISpellEffect effect) {
		schedule(effect::tick);
	}
	
	private static class Entry {
		long targetTick = 0L;
		IScheduledTickable tickable;
		
		public Entry(IScheduledTickable effect, long target) {
			targetTick = target;
			tickable = effect;
		}
	}
}
