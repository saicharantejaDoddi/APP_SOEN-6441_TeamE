package com.appriskgame.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class allows the creation of deck of 42 risk cards
 *
 * @author Shruthi
 *
 */
public class Deck implements Serializable{

	private static Deck deck;

	private String[] typeOfCards;
	public ArrayList<Card> deckOfCards;
	private Card drawCard;

	/**
	 * Creates all cards, one for each territory. Each card has either a type of
	 * Infantry, Cavalry, or Artillery.
	 *
	 */
	public void setDeckOfCards() {

		typeOfCards = new String[] { "Infantry", "Cavalry", "Artillery" };

		deckOfCards = new ArrayList<Card>();

		for (int i = 1; i <= 14; i++) {
			Card card = new Card();
			card.settype(typeOfCards[0]);
			deckOfCards.add(card);
		}
		for (int i = 1; i <= 14; i++) {

			Card card = new Card();
			card.settype(typeOfCards[1]);
			deckOfCards.add(card);
		}
		for (int i = 1; i <= 14; i++) {

			Card card = new Card();
			card.settype(typeOfCards[2]);
			deckOfCards.add(card);
		}
		Collections.shuffle(deckOfCards);
	}

	/**
	 * To get the instance of the Deck
	 *
	 * @return It is returning of type deck
	 */
	public static Deck getInstance() {
		if (null == deck) {
			deck = new Deck();
		}
		return deck;
	}

	/**
	 * Removing a card from the deck and returning it
	 *
	 * @return card object
	 */
	public Card draw() {

		drawCard = deckOfCards.get(0);
		deckOfCards.remove(0);

		return drawCard;
	}

	/**
	 * This method adds a card to the deck
	 *
	 * @param card object of the card which is to be added to deck
	 */
	public void add(Card card) {

		deckOfCards.add(card);
	}

	/**
	 * This method shuffles the deck of cards
	 */
	public void shuffle() {

		Collections.shuffle(deckOfCards);
	}

}
