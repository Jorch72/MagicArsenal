package com.elytradev.marsinal.capability;

import net.minecraft.util.ResourceLocation;

public interface IMagicResources {
	/** Recharges over time and is expended by general magic */
	public static final ResourceLocation RESOURCE_STAMINA = new ResourceLocation("magicarsenal", "stamina");
	/** Recharges when an entity dies near the player, and when the player activates a bleed effect */
	public static final ResourceLocation RESOURCE_BLOOD = new ResourceLocation("magicarsenal", "blood");
	/** Slowly recharges while in battle, and quickly recharges when damage is taken or dealt. */
	public static final ResourceLocation RESOURCE_RAGE = new ResourceLocation("magicarsenal", "rage");
	/** Hovers near zero, but rises as chaotic spells are cast, and causes more risky / detrimental effects the closer
	 * it gets to 100. At 100, it engulfs the caster entirely and spawns a short-lived chaos storm. */
	public static final ResourceLocation RESOURCE_CHAOS = new ResourceLocation("magicarsenal", "chaos");
	/** Instantly charges to ~40 whenever an attack is blocked while cooldown is up. Vengeance  decays by 1 each tick,
	 * and attacking while any Vengeance is present will consume it all and guarantee a critical hit. */
	public static final ResourceLocation RESOURCE_VENGEANCE = new ResourceLocation("magicarsenal", "vengeance");
	
	/**
	 * Gets the current amount of the magic resource. If the resource is unknown or does not exist, returns defaultAmount.
	 */
	int getResource(ResourceLocation id, int defaultAmount);
	
	/**
	 * Spends a magic resource.
	 * @param id             The magic resource to spend
	 * @param amount         The desired amount to spend
	 * @param defaultAmount  The amount to default to if the resource did not already exist
	 * @param requireAmount  If true, will refuse to spend any resource when there's not enough, and return zero instead.
	 * @return               The amount actually spent, or zero if nothing was spent.
	 */
	int spend(ResourceLocation id, int amount, int defaultAmount, boolean requireAmount);
	
	/**
	 * Sets a new value for a magic resource. If the resource didn't exist before, it does now.
	 */
	void set(ResourceLocation id, int amount);
	
	/**
	 * Returns the initial duration in ticks of the last global cooldown set. This is primarily intended for rendering
	 * visual cooldown indicators.
	 */
	int getMaxCooldown();
	
	/**
	 * Sets the maximum cooldown duration. Users typically do not need to call this; setGlobalCooldown will update this
	 * value if necessary.
	 */
	void setMaxCooldown(int max);
	
	/**
	 * Gets the amount of global cooldown remaining. It is reccommended to set at least 20 ticks of global cooldown
	 * after any magic activation, and prevent magic activation if the cooldown value is greater than zero. This
	 * prevents players from rapidly switching which item they hold to activate more magic in a given time period.
	 */
	int getGlobalCooldown();
	
	/**
	 * Sets the global cooldown, in ticks. If the global cooldown is already greater than this, the method must do
	 * nothing.
	 */
	void setGlobalCooldown(int ticks);
	
	/**
	 * Reduces the global cooldown by the specified amount of ticks. This could be because time passed, or an
	 * accelerator skill was activated.
	 */
	void reduceGlobalCooldown(int ticks);
}
