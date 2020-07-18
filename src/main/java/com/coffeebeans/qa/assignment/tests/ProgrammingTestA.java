package com.coffeebeans.qa.assignment.tests;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ProgrammingTestA {

	public static void main(String[] args) {
		problemA();
	}

	private static void problemA() {
		String num;
		String prod;
		List<Character> srcList;
		List<Character> prodList;
		// start from 100000 and end at 300000
		for(int i = 100000; i < 300000; i++) {
			// convert the number and its product to strings
			num = ""+i;
			prod = "" + (i * 2);
			// get the characters of number and product as list so that we can compare them
			srcList = num.chars().mapToObj(e -> (char)e).collect(Collectors.toList());
			prodList = prod.chars().mapToObj(e -> (char)e).collect(Collectors.toList());
			// initial check to see if characters in product is available in number - not necessarily all characters in number is available in product
			// if so, then try to compare if all the characters match in each list - by sorting them and comparing each character one by one
			// we can remove "srcList.containsAll(prodList)" condition and the code still works.. but initial check will avoid sorting of numbers and then returning false
			// Note: since we are using if condition - if the first part fails then the second part is not evaluated - hence optimised execution
			if(srcList.containsAll(prodList) && charsMatches(srcList, prodList)) {
				System.out.println("Result is: " + i + " * 2 = " + prod);
			}
		}
	}

	private static boolean charsMatches(List<Character> srcList, List<Character> prodList) {
		// sort the list to be in natural ordering and check if all the characters match
		Collections.sort(srcList);
		Collections.sort(prodList);
		for(int i = 0; i < srcList.size(); i++) {
			if(!srcList.get(i).equals(prodList.get(i))) {
				return false;
			}
		}
		return true;
	}
}
