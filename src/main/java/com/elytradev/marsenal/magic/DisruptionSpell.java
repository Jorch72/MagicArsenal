package com.elytradev.marsenal.magic;

import com.elytradev.marsenal.capability.IMagicResources;

import net.minecraft.entity.EntityLivingBase;

public class DisruptionSpell implements ISpellEffect {
	private EntityLivingBase caster;
	private int ticksExisted = 0;
	
	@Override
	public void activate(EntityLivingBase caster, IMagicResources res) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int tick() {
		if (ticksExisted==-1 || caster==null) {
			return 0;
		}
		
		// TODO Auto-generated method stub
		return 0;
	}

}
