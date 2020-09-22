package com.appriskgame.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

import com.appriskgame.view.PlayerView;
import com.appriskgame.view.WorldDominationView;

/**
 * This class stores the value associated to map.It holds the values required to
 * build the map.
 *
 * @author Surya
 * @author Sahana
 */
public class GameMap extends Observable implements Serializable {
	private ArrayList<Country> countries = new ArrayList<Country>();
	private ArrayList<Continent> continents = new ArrayList<Continent>();
	private ArrayList<GamePlayer> players = new ArrayList<GamePlayer>();
	private HashMap<String, Country> countrySet;
	public GameMap gameMap;
	public String gamePhaseName;
	public String currentPlayer;
	public String actionMsg;
	public String message;
	public static int cardExchangeCountinTheGame = 0;
	private String mode = "";
	private String format = "";

	public static int getCardExchangeCountinTheGame() {
		return cardExchangeCountinTheGame;
	}

	/**
	 * GameMap Constructor
	 */
	public GameMap() {
		WorldDominationView worldDominationView = new WorldDominationView();
		PlayerView playerView = new PlayerView();
		this.addObserver(worldDominationView);
		this.addObserver(playerView);
	}

	/**
	 * Attach the observable
	 *
	 */
	public void attach() {
		WorldDominationView worldDominationView = new WorldDominationView();
		PlayerView playerView = new PlayerView();
		this.addObserver(worldDominationView);
		this.addObserver(playerView);
	}

	/**
	 * Get the list of continents.
	 *
	 * @return list of continents.
	 */
	public ArrayList<Continent> getContinents() {
		return continents;
	}

	/**
	 * Set the list of continents
	 *
	 * @param continents To set the list of continents.
	 */
	public void setContinents(ArrayList<Continent> continents) {
		this.continents = continents;
	}

	/**
	 * Get the list of Countries.
	 *
	 * @return list of Countries.
	 */
	public ArrayList<Country> getCountries() {
		return countries;
	}

	/**
	 * Set the list of Countries
	 *
	 * @param countries To set the list of Countries.
	 */
	public void setCountries(ArrayList<Country> countries) {
		this.countries = countries;
	}

	/**
	 * Get the Country details.
	 *
	 * @return Country details
	 */
	public HashMap<String, Country> getCountrySet() {
		return countrySet;
	}

	/**
	 * Set the Country details.
	 *
	 * @param countrySet To set the Country details
	 */
	public void setCountrySet(HashMap<String, Country> countrySet) {
		this.countrySet = countrySet;
	}

	/**
	 * This method sets the message for observer of domination and knows them when
	 * it is changed.
	 * @param gameMap game map object
	 * @param message Action type message
	 */
	public void setDomination(GameMap gameMap, String message) {
		this.gameMap = gameMap;
		this.message = message;
		setChanged();
		notifyObservers();
	}

	/**
	 * Gets the current game map
	 * 
	 * @return gameMap object
	 */
	public GameMap getDomination() {
		return gameMap;
	}

	/**
	 * Gets the current game phase
	 * 
	 * @return the phase
	 */
	public String getGamePhase() {
		return gamePhaseName;
	}

	/**
	 * Sets the current game phase
	 * 
	 * @param gamePhase - the phase of the game
	 * @param message   action message
	 */
	public void setGamePhase(String gamePhase, String message) {
		this.gamePhaseName = gamePhase;
		this.message = message;
		setChanged();
		notifyObservers();
	}

	/**
	 * Gets the current player Name
	 * 
	 * @return current Player
	 */
	public String getCurrentPlayer() {
		return currentPlayer;
	}

	/**
	 * Sets the current game phase
	 * 
	 * @param currentPlayer - Current Player Name
	 */
	public void setCurrentPlayer(String currentPlayer) {
		this.currentPlayer = currentPlayer;
		setChanged();
		notifyObservers();
	}

	/**
	 * Gets the current action message
	 * 
	 * @return current action message
	 */
	public String getActionMsg() {
		return actionMsg;
	}

	/**
	 * Sets the current action
	 * 
	 * @param actionMsg - the phase of the game
	 * @param message   action message
	 */
	public void setActionMsg(String actionMsg, String message) {
		this.actionMsg = actionMsg;
		this.message = message;
		setChanged();
		notifyObservers();
	}

	/**
	 * Gets the list of the Player
	 *
	 * @return the arrayList of the Player
	 */
	public ArrayList<GamePlayer> getPlayers() {
		return players;
	}

	/**
	 * Sets the list of the Players
	 *
	 * @param players - the players array list
	 */
	public void setPlayers(ArrayList<GamePlayer> players) {
		this.players = players;
	}

	/**
	 * gets the mode of game
	 * 
	 * @return mode of game
	 */
	public String getMode() {
		return mode;
	}

	/**
	 * sets the mode of game
	 * 
	 * @param mode type of mode
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}

	/**
	 * get the format
	 * 
	 * @return format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * sets the format
	 * 
	 * @param format format
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	@Override
	public String toString() {
		return "[continents=" + continents + ", countries=" + countries + ", countrySet=" + countrySet + "]";
	}
}
