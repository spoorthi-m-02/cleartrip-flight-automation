package com.coffeebeans.qa.assignment.tests;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class ProgrammingTestB {
	
	public static void main(String[] args) {
		String input = "All I need to find is a good answer.";
//		String input = "Bangalore is in India";
		for(String s : input.split(" ")) {
			parseCharCount(s);
		}
	}

	private static Map<Character, Integer> parseCharCount(String s) {
		List<Character> charList = s.toLowerCase().chars().mapToObj(e -> (char)e).collect(Collectors.toList());
		Map<Character, Integer> charCountMap = new HashMap<>();
		Integer count = null;
		for (Character character : charList) {
			count = charCountMap.get(character);
			if(count == null) {
				charCountMap.put(character, 1);
			} else {
				charCountMap.put(character, count + 1);
			}
		}
		for(Entry<Character, Integer> e : charCountMap.entrySet()) {
			if(e.getValue() > 1) {
				System.out.println("Character \"" + e.getKey() + "\" repeats " + e.getValue() + " times in word \"" + s + "\"");
			}
		}
		return charCountMap;
	}
}
