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

package com.elytradev.marsenal.capability.impl;

import java.util.function.BiFunction;

import com.elytradev.marsenal.capability.IRuneProducer;

public class RuneProducer implements IRuneProducer {
	private String key;
	private Runnable listener;
	private int radiance = 0;
	private int emc = 0;
	private int lastRadiance = 0;
	private int lastEMC = 0;
	private BiFunction<Integer, Boolean, Integer> produceCallback;
	
	public RuneProducer() {
		this("nameless", null);
	}
	
	public RuneProducer(String key, BiFunction<Integer, Boolean, Integer> produceCallback) {
		this.key = key;
		this.produceCallback = produceCallback;
	}
	
	public void listen(Runnable r) {
		listener = r;
	}
	
	@Override
	public int getProducerRadiance() {
		return radiance;
	}

	@Override
	public int getEMCAvailable() {
		return emc;
	}
	
	protected void onChanged() {
		if (listener!=null) listener.run();
	}
	
	@Override
	public String getProducerType() {
		return key;
	}
	
	public void setRadiance(int radiance) {
		this.radiance = radiance;
		onChanged();
	}
	public void addRadiance(int radiance) {
		this.radiance += radiance;
		onChanged();
	}
	public void clearRadiance() {
		this.radiance = 0;
		onChanged();
	}
	
	public void setEMC(int emc) {
		this.emc = emc;
		onChanged();
	}
	public void addEMC(int emc) {
		this.emc += emc;
		onChanged();
	}
	public void clearEMC() {
		this.emc = 0;
		onChanged();
	}
	
	public boolean detectAndResetChanges() {
		boolean result = (radiance!=lastRadiance) || (emc!=lastEMC);
		lastRadiance = radiance;
		lastEMC = emc;
		return result;
	}

	@Override
	public int produceEMC(int requested, boolean simulate) {
		if (produceCallback==null) return 0;
		return produceCallback.apply(requested, simulate);
	}
	
	public void setRadianceFromEMC() {
		this.radiance = this.emc / 1000;
	}
}
