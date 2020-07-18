package com.coffeebeans.qa.assignment.tests;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class ProgrammingTestB {

	public static void main(String[] args) {
		String sentence = "All I need to find is a good answer.";
		//String sentence = "Bangalore is in India";
		// split the sentence by " " empty space to get the word by word
		for(String word : sentence.split(" ")) {
			parseCharCount(word);
		}
	}

	private static Map<Character, Integer> parseCharCount(String word) {
		// get the characters in the word as list to check the repeating characters
		// note that we are using toLowerCase() to make case-insensitive characters count
		List<Character> charList = word.toLowerCase().chars().mapToObj(e -> (char)e).collect(Collectors.toList());
		// hold the count of characters in a Map
		Map<Character, Integer> charCountMap = new HashMap<>();
		Integer count = null;
		for (Character character : charList) {
			count = charCountMap.get(character);
			if(count == null) {
				// for the first time when the character is encountered, the map does not contain the character and hence returns "null" count
				// put the count as 1 for first occurrence
				charCountMap.put(character, 1);
			} else {
				// character is repeating.. increment by 1
				charCountMap.put(character, count + 1);
			}
		}
		for(Entry<Character, Integer> e : charCountMap.entrySet()) {
			// loop through the entry set of the map
			if(e.getValue() > 1) {
				// if character had more that one count, then its repeating one.. print it
				System.out.println("Character \"" + e.getKey() + "\" repeats " + e.getValue() + " times in word \"" + word + "\"");
			}
		}
		return charCountMap;
	}
}
