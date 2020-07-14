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
		for(int i = 100000; i < 300000; i++) {
			num = ""+i;
			prod = "" + (i * 2);
			srcList = num.chars().mapToObj(e -> (char)e).collect(Collectors.toList());
			prodList = prod.chars().mapToObj(e -> (char)e).collect(Collectors.toList());
			if(srcList.containsAll(prodList) && charsMatches(srcList, prodList)) {
				System.out.println("Result is: " + i + " * 2 = " + prod);
			}
		}
	}

	private static boolean charsMatches(List<Character> srcList, List<Character> prodList) {
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
