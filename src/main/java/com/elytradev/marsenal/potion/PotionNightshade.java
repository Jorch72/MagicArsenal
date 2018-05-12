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

package com.elytradev.marsenal.potion;

import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionNightshade extends Potion {
	private static final ResourceLocation TEXTURE_WOLFSBANE = new ResourceLocation("magicarsenal", "textures/effects/nightshade.png");
	DamageSource DAMAGE_NIGHTSHADE = new DamageSource("magicarsenal.nightshade");
	
	public PotionNightshade() {
		super(true, 0x2a0e49);
		
		setPotionName("effect.magicarsenal.nightshade");
		setRegistryName("magicarsenal", "nightshade");
	}

	@SideOnly(Side.CLIENT)
    public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
		mc.renderEngine.bindTexture(TEXTURE_WOLFSBANE);
		GlStateManager.color(1, 1, 1);
		Gui.drawModalRectWithCustomSizedTexture(x+6, y+7, 0, 0, 18, 18, 18, 18);
	}
	
	@Override
	public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
		mc.renderEngine.bindTexture(TEXTURE_WOLFSBANE);
		GlStateManager.color(1, 1, 1, alpha);
		Gui.drawModalRectWithCustomSizedTexture(x+3, y+3, 0, 0, 18, 18, 18, 18);
	}
	
	@Override
	public void performEffect(EntityLivingBase entity, int amplifier) {
		if (entity.isEntityUndead()) {
			entity.heal(1.0f);
			return;
		}
		
		float toDamage = 1;
		
		if (entity instanceof EntityDragon || entity.getName().toLowerCase(Locale.ROOT).contains("dragon")) {
			toDamage *= 2;
			//Nightshade is especially potent against dragons, whose magic aptitude makes them sensitive to nerve poisons.
			//Nightshade can also provide the killing blow against these creatures.
		} else {
			if (entity.getHealth()<toDamage) toDamage = entity.getHealth()-1.0f;
		}
		
		if (toDamage > 0f) {
            entity.attackEntityFrom(DAMAGE_NIGHTSHADE, toDamage);
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
