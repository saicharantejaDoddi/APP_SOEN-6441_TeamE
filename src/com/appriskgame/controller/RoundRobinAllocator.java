package com.appriskgame.controller;

import java.util.ArrayList;
import java.util.Iterator;

import com.appriskgame.model.GamePlayer;

/**
 * This class is used to iterate the Players Turns.
 * 
 * @author Sahana
 * @author Surya
 */
public class RoundRobinAllocator {
	private ArrayList<GamePlayer> listOfPlayers;
	private Iterator<GamePlayer> iterator;

	/**
	 * RoundRobinAllocator constructor
	 * 
	 * @param playersList List of players
	 */
	public RoundRobinAllocator(ArrayList<GamePlayer> playersList) {
		this.listOfPlayers = playersList;
		iterator = playersList.iterator();
	}

	/**
	 * This method is used to move on the turn to next Player.
	 * 
	 * @return Turn of the next player
	 */
	public GamePlayer nextTurn() {
		if (!iterator.hasNext()) {
			iterator = listOfPlayers.iterator();
		}
		return iterator.next();
	}
}
