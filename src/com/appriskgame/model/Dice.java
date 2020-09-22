package com.appriskgame.model;

import java.util.List;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Dice class is a model which gives dice information of player in the game.
 * 
 * @author sai
 * @author dolly
 *
 */

public class Dice implements Serializable{

	private int totalDices;
	private List<Integer> resultOfDices = new ArrayList<Integer>();

	/**
	 * This method gives the total number of dices.
	 * 
	 * @return number of dices
	 */
	public int getTotalDices() {
		return totalDices;
	}

	/**
	 * This method sets total number of dices.
	 * 
	 * @param totalDices total number of dices
	 */
	public void setTotalDices(int totalDices) {
		this.totalDices = totalDices;
	}

	/**
	 * This method gives the result of the dice value.
	 * 
	 * @return List of result of Dice values
	 */
	public List<Integer> getResultOfDices() {
		return resultOfDices;
	}

	/**
	 * This method sets the result for dice values.
	 * 
	 * @param resultOfDices Result of Dice values
	 */
	public void setResultOfDices(List<Integer> resultOfDices) {
		this.resultOfDices = resultOfDices;
	}

}
