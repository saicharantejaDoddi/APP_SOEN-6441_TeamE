package com.appriskgame.model;

import java.io.Serializable;
import java.util.ArrayList;

import com.appriskgame.controller.Player;

/**
 * Card definition, to have different types of cards
 *
 * @author Shruthi
 */

public class Card implements Serializable {

	private String type;

	private Country country;

	public final static String INFANTRY = "infantry";

	public final static String CAVALRY = "cavalry";

	public final static String ARTILLERY = "artillery";

	private Player currentPlayer;

	/**
	 * Card Constructor
	 */
	public Card() {
	}

	/**
	 * This method returns the country name and card type
	 *
	 * @return name of country and type
	 */
	public String getName() {
		return country.getCountryName() + ", " + type;
	}

	/**
	 * Gets the card type
	 *
	 * @return Card type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the card type
	 *
	 * @param cardType - the card type
	 */
	public void settype(String cardType) {
		this.type = cardType;
	}

	/**
	 * This method returns name of the country
	 *
	 * @return string country object
	 */
	public Country getCountry() {
		return country;
	}

	/**
	 * Gets the current player
	 *
	 * @return the player object
	 */
	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	/**
	 * Sets the current player
	 *
	 * @param currentPlayer - the player object
	 */
	public void setCurrentPlayer(Player currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

	/**
	 * This method creates an arrayList of cards
	 *
	 * @return the list of card
	 */
	public ArrayList<String> totalCardType() {
		ArrayList<String> cardTypes = new ArrayList<>();
		cardTypes.add(ARTILLERY);
		cardTypes.add(CAVALRY);
		cardTypes.add(INFANTRY);
		return cardTypes;
	}

}