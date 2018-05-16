package com.elytradev.marsenal.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionInfuseLife extends Potion {
	private static final ResourceLocation TEXTURE_INFUSE_LIFE = new ResourceLocation("magicarsenal", "textures/effects/infuselife.png");
	DamageSource DAMAGE_INFUSE_LIFE = new DamageSource("magicarsenal.infuselife");
	
	public PotionInfuseLife() {
		super(false, 0xFF74aa00);
		
		setPotionName("effect.magicarsenal.infuselife");
		setRegistryName("magicarsenal", "infuselife");
	}
	
	@SideOnly(Side.CLIENT)
    public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
		mc.renderEngine.bindTexture(TEXTURE_INFUSE_LIFE);
		GlStateManager.color(1, 1, 1);
		Gui.drawModalRectWithCustomSizedTexture(x+6, y+7, 0, 0, 18, 18, 18, 18);
	}
	
	@Override
	public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
		mc.renderEngine.bindTexture(TEXTURE_INFUSE_LIFE);
		GlStateManager.color(1, 1, 1, alpha);
		Gui.drawModalRectWithCustomSizedTexture(x+3, y+3, 0, 0, 18, 18, 18, 18);
	}
	
	@Override
	public void performEffect(EntityLivingBase entity, int amplifier) {
		float toHeal = 1;
		
		if (entity.isEntityUndead()) {
			entity.attackEntityFrom(DAMAGE_INFUSE_LIFE, toHeal);
		} else {
			entity.heal(toHeal);
		}
	}
	
	@Override
	public boolean isReady(int duration, int amplifier) {
		int ticks = 25 >> (amplifier+1);
		
        if (ticks > 0) {
            return duration % ticks == 0;
        } else {
            return true;
        }
	}
}
