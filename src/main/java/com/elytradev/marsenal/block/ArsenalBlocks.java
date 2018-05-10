package com.elytradev.marsenal.block;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class ArsenalBlocks {
	private static List<Block> blocksForItems = new ArrayList<>();
	
	public static BlockPoisonDoublePlant CROP_WOLFSBANE = null;
	
	@SubscribeEvent
	public static void onRegisterBlocks(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();
		
		CROP_WOLFSBANE = block(r, new BlockPoisonDoublePlant("wolfsbane"));
		
		
	}
	
	public static Iterable<Block> blocksForItems() {
		return blocksForItems;
	}
	
	public static <T extends Block> T block(IForgeRegistry<Block> registry, T t) {
		registry.register(t);
		blocksForItems.add(t);
		return t;
	}
}
