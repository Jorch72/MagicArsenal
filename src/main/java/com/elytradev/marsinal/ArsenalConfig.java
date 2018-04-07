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

package com.elytradev.marsinal;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.hjson.JsonValue;

import com.google.gson.GsonBuilder;

/** Configuration options. Before connecting to a server, such as in the main menu, only LOCAL will be available. But
 * once a player is connected to the world, *RESOLVED should be used for everything* except cosmetic tweaks. This will
 * allow network resolution of the config later on.
 * 
 * <p>In the network-resolution scheme, the following possibilities could arise:
 * <li> We're playing SSP or a network game as the host. LOCAL is the clientside config file, and RESOLVED was sent to
 *      ourselves in a packet, but is functionally just an extremely roundabout copy of the local config.
 * <li> We're a dedicated SMP server. RESOLVED is the same reference as LOCAL.
 * <li> We're a SMP client. LOCAL is loaded from our own config file, but RESOLVED is received as a packet from the
 *      server when we connect.
 */
public class ArsenalConfig {
	public static ArsenalConfig LOCAL = null;
	public static ArsenalConfig RESOLVED = null;
	
	
	public static ArsenalConfig load(File f) {
		try {
			if (!f.exists()) {
				ArsenalConfig result = new ArsenalConfig();
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
				out.write("# Default config file generated for version "+MagicArsenal.VERSION+"\n");
				out.write("# This file is hjson. Commas are optional, and comments can be added in #config format, //java format, or /* c format */\n");
				new GsonBuilder()
					.setPrettyPrinting()
					.create()
					.toJson(result, out);
				out.close();
				
				return result;
			} else {
				String value = JsonValue.readHjson(new InputStreamReader(new FileInputStream(f))).toString();
				
				return new GsonBuilder()
					
					.create()
					.fromJson(value, ArsenalConfig.class);
			}
		} catch (IOException e) {
			MagicArsenal.LOG.error("There was trouble reading the config file.", e);
			return new ArsenalConfig();
		}
	}
	
	public static class SpellEntry {
		public int potency = 10;
		public int cost = 10;
		public int cooldown = 10;
		
		public SpellEntry() {}
		
		public SpellEntry(int potency, int cost, int cooldown) {
			this.potency = potency;
			this.cost = cost;
			this.cooldown = cooldown;
		}
	}
	
	public static class SpellsSection {
		public SpellEntry healingWave = new SpellEntry(10, 10, 20*1);
		public SpellEntry regenArea   = new SpellEntry(10, 10, 20*5);
		public SpellEntry recovery    = new SpellEntry( 5,  5, 20*3);
		public SpellEntry drainLife   = new SpellEntry( 5,  5, 20*3);
		public SpellEntry oblation    = new SpellEntry( 5,  5, 20*3);
	}
	
	public SpellsSection spells = new SpellsSection();
}
