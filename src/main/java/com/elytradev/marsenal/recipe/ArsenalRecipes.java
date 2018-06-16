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

package com.elytradev.marsenal.recipe;

import com.elytradev.marsenal.tile.TileEntityBerkanoStele;
import com.elytradev.marsenal.tile.TileEntityFehuStele;
import com.elytradev.marsenal.tile.TileEntityJeraStele;
import com.elytradev.marsenal.tile.TileEntityWunjoStele;

import net.minecraft.init.Blocks;

public class ArsenalRecipes {
	public static void registerEMC() {
		EmcRegistry fehu = TileEntityFehuStele.REGISTRY;
		
		fehu.register(Blocks.DIAMOND_BLOCK,      73_728);
		fehu.register(Blocks.DIAMOND_ORE,         8_192);
		fehu.register(Blocks.EMERALD_BLOCK,      73_728);
		fehu.register(Blocks.EMERALD_ORE,         8_192);
		fehu.register(Blocks.GOLD_BLOCK,         18_432);
		fehu.register(Blocks.GOLD_ORE,            2_048);
		fehu.register(Blocks.IRON_BLOCK,          2_304);
		fehu.register(Blocks.IRON_BARS,              96);
		fehu.register(Blocks.IRON_ORE,              256);
		fehu.register(Blocks.LAPIS_BLOCK,         7_776);
		fehu.register(Blocks.LAPIS_ORE,             864);
		
		fehu.register("thermalfoundation:storage", 0, 2_304); //Copper   **Correct ProjectE value
		fehu.register("thermalfoundation:storage", 1, 2_048); //Tin
		fehu.register("thermalfoundation:storage", 2, 2_048); //Silver
		fehu.register("thermalfoundation:storage", 3, 2_048); //Lead
		fehu.register("thermalfoundation:storage", 4, 2_048); //Aluminum
		fehu.register("thermalfoundation:storage", 5, 2_048); //Nickel
		fehu.register("thermalfoundation:storage", 6, 2_304); //Platinum **Correct ProjectE value
		fehu.register("thermalfoundation:storage", 7, 9_216); //Iridium  **Correct ProjectE value
		fehu.register("thermalfoundation:storage", 8, 2_048); //Mithril
		
		fehu.register("draconicevolution:draconium_block", 10_000);
		fehu.register("draconicevolution:draconic_block",  40_000);
		
		//URUZ     NYI
		//THURISAZ NYI
		//ANSUZ    NYI
		//RAIDHO   DOES NOT SURVEY
		//KENAZ    COMPLETELY HEURISTIC
		//GEBO     DOES NOT SURVEY
		
		EmcRegistry wunjo = TileEntityWunjoStele.REGISTRY;
		//Uses additional heuristics to capture vanilla skulls
		wunjo.register("openblocks:trophy",      1_440);
		wunjo.register("twilightforest:trophy",  1_440);
		
		//HAGALAZ  NYI
		//NAUTHIZ  NYI
		//ISA      NYI
		
		EmcRegistry jera = TileEntityJeraStele.REGISTRY;
		//Uses additional heuristics to forward unusual plants into the following entries (usually WHEAT or DEADBUSH)
		jera.register(Blocks.BEETROOTS,            24); //From wheat
		jera.register(Blocks.BROWN_MUSHROOM,       32);
		jera.register(Blocks.BROWN_MUSHROOM_BLOCK, 32*9);
		jera.register(Blocks.CACTUS,                8);
		jera.register(Blocks.CARROTS,              24); //wheat
		jera.register(Blocks.CHORUS_FLOWER,       240); //  should be pretty expensive, 
		jera.register(Blocks.CHORUS_PLANT,          1); //  but then once you get it it's easy to grow
		jera.register(Blocks.COCOA,                 8); //less than wheat
		jera.register(Blocks.DEADBUSH,              1);
		jera.register(Blocks.DOUBLE_PLANT,         16); //From short flower
		jera.register(Blocks.FARMLAND,              2); //1 more than it would normally be to make EMC obtainable
		jera.register(Blocks.LIT_PUMPKIN,         144);
		jera.register(Blocks.MELON_BLOCK,         144);
		jera.register(Blocks.MELON_STEM,           16); //custom
		jera.register(Blocks.MYCELIUM,              3);
		jera.register(Blocks.NETHER_WART,           1); //Doesn't match our nature very well
		jera.register(Blocks.NETHER_WART_BLOCK,     1*9);//Ditto, just 9 times not-very-much
		jera.register(Blocks.POTATOES,             24); //wheat
		jera.register(Blocks.PUMPKIN,             144);
		jera.register(Blocks.PUMPKIN_STEM,         16); //custom
		jera.register(Blocks.RED_FLOWER,           16);
		jera.register(Blocks.RED_MUSHROOM,         32);
		jera.register(Blocks.RED_MUSHROOM_BLOCK,   32*9);
		jera.register(Blocks.REEDS,                 2); //1 more than it would normally be to make EMC obtainable
		jera.register(Blocks.SPONGE,             1000); //Potentially controversial
		jera.register(Blocks.TALLGRASS,             1);
		jera.register(Blocks.VINE,                  8);
		jera.register(Blocks.WATERLILY,            16);
		jera.register(Blocks.WHEAT,                24);
		jera.register(Blocks.YELLOW_FLOWER,        16);
		
		//EIHWAZ  NYI
		//PERTHRO NYI
		//ELHAZ   NYI
		//SOWILO  NYI
		//TIWAZ   NYI
		
		EmcRegistry berkano = TileEntityBerkanoStele.REGISTRY;
		//Uses additional heuristics to forward unusual logs and leaves into the following entries
		berkano.register(Blocks.GRASS,                 2);
		berkano.register(Blocks.LEAVES,                1);
		berkano.register(Blocks.LEAVES2,               1);
		berkano.register(Blocks.LOG,                  32);
		berkano.register(Blocks.LOG2,                 32);
		berkano.register(Blocks.SAPLING,               2);
		
		//EHWAZ  NYI
		//MANNAZ NYI
		//LAGUZ  NYI
		//INGWAZ NYI
		//DAGAZ  NYI
		//OTHALA NYI
	}
}
