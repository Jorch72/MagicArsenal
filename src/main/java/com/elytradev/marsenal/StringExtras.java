package com.elytradev.marsenal;

import java.util.ArrayList;
import java.util.List;

public class StringExtras {
	public static List<String> WordWrap(String str, int width) {
		List<String> result = new ArrayList<>();
		String[] words = str.split(" ");

		String line = "";
		for (String word : words) {
			
			if (line.length() + word.length() > width) {
				//If the current line won't fit with the new word appended, emit it and try the word on its own
				if (!line.isEmpty()) {
					result.add(line.trim());
					line = "";
				}
				
				// If the current word is too long to fit even on its own, split it up
				while (word.length() > width) {
					String subword = word.substring(0, width - 1) + "-";
					result.add(subword.trim());
					word = word.substring(width - 1);
					line = ""; //unnecessary but you never know
				}
			}
			line += " "+word;
		}
		if (!line.isEmpty()) result.add(line.trim());
		
		return result;
	}
}
