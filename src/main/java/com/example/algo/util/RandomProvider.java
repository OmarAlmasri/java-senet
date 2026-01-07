package com.example.algo.util;

/*
 * DEV_NOTES: I added this class , to take the random numbers via a class 
 * 			instead of a random variable inside each funcion and so on . 
 * NOTE2  : the returns are wrong , just naming functions .
 */
public class RandomProvider {
	public int nextInt(int bound) {
		return this.nextInt(0, bound);
	}
	public int nextInt(int start , int end) {
		// add the random code here not in the first function 
		// the first function represent the default value for the function. 
		// where it takes from 0 to the current value .
		return 0; 
	}
	
	public boolean nextBoolean() {
		return false;
	}
}
