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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.hjson.JsonValue;

/** Configuration options. Before connecting to a server, such as in the main menu, only LOCAL will be available. But
 * once a player is connected to the world, *RESOLVED should be used for everything* except cosmetic tweaks. This will
 * allow network resolution of the config later on.
 * 
 * <p>In the network-resolution scheme, the following possibilities could arise:
 * <li> We're playing SSP, and LOCAL represents the clientside config file. RESOLVED is a copy of the clientside config.
 * <li> We're playing a network game as the host (multiplayer using integrated server). LOCAL is the clientside
 *      config file, and RESOLVED was sent to ourselves in a packet, but is functionally just an extremely roundabout
 *      copy of the local config.
 * <li> We're a dedicated SMP server. RESOLVED is the same reference as LOCAL.
 * <li> We're a SMP client. LOCAL is loaded from our own config file, but RESOLVED is received as a packet from the
 *      server when we connect.
 */
public class ArsenalConfig {
	public static ArsenalConfig LOCAL = null;
	public static ArsenalConfig RESOLVED = null;
	
	
	public static void load(File f) {
		try {
			if (!f.exists()) {
				
			} else {
				JsonValue value = JsonValue.readHjson(new InputStreamReader(new FileInputStream(f)));
				
				value.asObject();
			}
		} catch (IOException e) {
			MagicArsenal.LOG.error("There was trouble reading the config file.", e);
		}
	}
}
