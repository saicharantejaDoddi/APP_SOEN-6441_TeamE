package com.appriskgame.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.appriskgame.model.Card;
import com.appriskgame.model.Deck;
import com.appriskgame.model.GameMap;
import com.appriskgame.model.GamePlayer;

/**
 * This class is used for Card allocation and exchange of cards for armies. It
 * also contains methods to check invalid cards and card types.
 *
 * @author Shruthi
 * @author Sahana
 *
 */
public class CardController {
	static int EXCHANGEARMY = 5;

	/**
	 * This method is used to get an instance of deck of cards.
	 */
	public void setDeckOfCards() {
		Deck.getInstance().setDeckOfCards();
	}

	/**
	 * This method allocates a card randomly to the player if the player wins the
	 * country
	 *
	 * @param player - the current player
	 */
	public void allocateCardToPlayer(GamePlayer player) {
		Card card = Deck.getInstance().draw();
		player.getCardList().add(card);
		System.out.println(player.getPlayerName() + " " + "has got the card" + " " + card.getType());
	}

	/**
	 * This method is called when the player exchange the cards for armies.
	 *
	 * @param player Current player
	 * @return Number of calculated armies.
	 * @throws Exception IOException
	 */
	public int exchangeCards(GamePlayer player) throws Exception {

		HashMap<String, Integer> cardCount = new HashMap<>();
		int armiesInExchange = 0;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int cardTypes = 0;
		boolean cardExchangePossible = false;
		String cardAppearingMoreThanThrice = null;

		if (player.getCardList().size() > 0) {
			for (Card card : player.getCardList()) {
				if (!cardCount.containsKey(card.getType())) {
					cardCount.put(card.getType(), 1);
					cardTypes++;
				} else {
					int c = cardCount.get(card.getType());
					c++;
					cardCount.put(card.getType(), c);
				}
			}
		}

		if (cardTypes == 3) {
			cardExchangePossible = true;
		}

		for (Entry<String, Integer> cardVal : cardCount.entrySet()) {
			if (cardVal.getValue() >= 3) {
				cardExchangePossible = true;
				cardAppearingMoreThanThrice = cardVal.getKey();
			}
		}

		if (cardExchangePossible) {
			if (player.getCardList().size() < 5) {
				System.out.println("Available Cards to the player are");
				for (int k = 0; k < player.getCardList().size(); k++) {
					System.out.print(k + 1 + "." + player.getCardList().get(k).getType() + "\n");
				}
				List<Card> exchangeCards = new ArrayList<>();
				List<Integer> cardNumbers = new ArrayList<>();
				if (player.getPlayerType() == "human") {
					System.out.println("\n"
							+ "Enter the numbers of card you want to exchange in the format exchangecards num num num \n");
					String[] cardsList = returnInput(br.readLine());

					if (cardsList.length == 4) {
						for (int k = 1; k < cardsList.length; k++) {
							cardNumbers.add(Integer.parseInt(cardsList[k]));
						}

						for (int c : cardNumbers) {
							exchangeCards.add(player.getCardList().get(c - 1));
						}

						if (!checkDiffTypesOfCards(exchangeCards, cardTypes)
								&& !checkCardSameType(exchangeCards, cardAppearingMoreThanThrice)) {
							System.out.println(
									"Please enter numbers of same cards appearing thrice or three cards which are different.");
							throw new Exception();
						}
					}
				} else {
					if (cardTypes == 3) {
						int countinf = 0, countart = 0, countcav = 0;
						for (int m = 0; m < player.getCardList().size(); m++) {
							if (player.getCardList().get(m).getType().equalsIgnoreCase("infantry") && countinf == 0) {
								exchangeCards.add(player.getCardList().get(m));
								countinf++;
							}
							if (player.getCardList().get(m).getType().equalsIgnoreCase("artillery") && countart == 0) {
								exchangeCards.add(player.getCardList().get(m));
								countart++;
							}
							if (player.getCardList().get(m).getType().equalsIgnoreCase("cavalry") && countcav == 0) {
								exchangeCards.add(player.getCardList().get(m));
								countcav++;
							}
						}
					} else {
						int countinf = 0, countart = 0, countcav = 0;
						for (int m = 0; m < player.getCardList().size(); m++) {
							if (player.getCardList().get(m).getType().equalsIgnoreCase("infantry")) {
								countinf++;
							}
							if (player.getCardList().get(m).getType().equalsIgnoreCase("artillery")) {
								countart++;
							}
							if (player.getCardList().get(m).getType().equalsIgnoreCase("cavalry")) {
								countcav++;
							}
						}
						if (countart == 3) {
							for (int m = 0; m < player.getCardList().size(); m++) {
								if (player.getCardList().get(m).getType().equalsIgnoreCase("artillery")) {
									exchangeCards.add(player.getCardList().get(m));
								}
							}
						}
						if (countinf == 3) {
							for (int m = 0; m < player.getCardList().size(); m++) {
								if (player.getCardList().get(m).getType().equalsIgnoreCase("infantry")) {
									exchangeCards.add(player.getCardList().get(m));
								}
							}
						}
						if (countcav == 3) {
							for (int m = 0; m < player.getCardList().size(); m++) {
								if (player.getCardList().get(m).getType().equalsIgnoreCase("cavalry")) {
									exchangeCards.add(player.getCardList().get(m));
								}
							}
						}
					}
				}
				int count = GameMap.getCardExchangeCountinTheGame();
				armiesInExchange = (count + 1) * EXCHANGEARMY;
				System.out.println("Succesfully exchanged the card");
				GameMap.cardExchangeCountinTheGame = GameMap.getCardExchangeCountinTheGame() + 1;
				for (int i = 0; i < exchangeCards.size(); i++) {
					int count1 = 0;
					String cardType = exchangeCards.get(i).getType();
					for (int k = 0; k < player.getCardList().size(); k++) {
						if (player.getCardList().get(k).getType().equalsIgnoreCase(cardType)) {
							if (count1 == 0) {
								player.getCardList().remove(k);
								count1 = count1 + 1;
							}
						}
					}
					count1 = 0;
				}
			}

			if (player.getCardList().size() >= 5) {
				System.out.println("Available Cards to the player are");
				for (int k = 0; k < player.getCardList().size(); k++) {
					System.out.print(k + 1 + "." + player.getCardList().get(k).getType() + "\n");
				}
				List<Card> exchangeCards = new ArrayList<>();
				List<Integer> cardNumbers = new ArrayList<>();
				if (player.getPlayerType() == "human") {
					System.out.println("You must have to exchange atleast 3 cards out of 5." + "\n"
							+ "Input in the format exchangecards num num num \n");
					String[] cardsList = returnInputForMoreThanFive(br.readLine());

					if (cardsList.length == 4) {

						for (int k = 1; k < cardsList.length; k++) {
							cardNumbers.add(Integer.parseInt(cardsList[k]));
						}

						for (int c : cardNumbers) {
							exchangeCards.add(player.getCardList().get(c - 1));
						}

						if (!checkDiffTypesOfCards(exchangeCards, cardTypes)
								&& !checkCardSameType(exchangeCards, cardAppearingMoreThanThrice)) {
							System.out.println(
									"Please enter numbers of same cards appearing thrice or three cards which are different.\n");
							throw new Exception();
						}
					}
				} else {
					if (cardTypes == 3) {
						int countinf = 0, countart = 0, countcav = 0;
						for (int m = 0; m < player.getCardList().size(); m++) {
							if (player.getCardList().get(m).getType().equalsIgnoreCase("infantry") && countinf == 0) {
								exchangeCards.add(player.getCardList().get(m));
								countinf++;
							}
							if (player.getCardList().get(m).getType().equalsIgnoreCase("artillery") && countart == 0) {
								exchangeCards.add(player.getCardList().get(m));
								countart++;
							}
							if (player.getCardList().get(m).getType().equalsIgnoreCase("cavalry") && countcav == 0) {
								exchangeCards.add(player.getCardList().get(m));
								countcav++;
							}
						}
					} else {
						int countinf = 0, countart = 0, countcav = 0;
						for (int m = 0; m < player.getCardList().size(); m++) {
							if (player.getCardList().get(m).getType().equalsIgnoreCase("infantry")) {
								countinf++;
							}
							if (player.getCardList().get(m).getType().equalsIgnoreCase("artillery")) {
								countart++;
							}
							if (player.getCardList().get(m).getType().equalsIgnoreCase("cavalry")) {
								countcav++;
							}
						}
						if (countart == 3) {
							for (int m = 0; m < player.getCardList().size(); m++) {
								if (player.getCardList().get(m).getType().equalsIgnoreCase("artillery")) {
									exchangeCards.add(player.getCardList().get(m));
								}
							}
						}
						if (countinf == 3) {
							for (int m = 0; m < player.getCardList().size(); m++) {
								if (player.getCardList().get(m).getType().equalsIgnoreCase("infantry")) {
									exchangeCards.add(player.getCardList().get(m));
								}
							}
						}
						if (countcav == 3) {
							for (int m = 0; m < player.getCardList().size(); m++) {
								if (player.getCardList().get(m).getType().equalsIgnoreCase("cavalry")) {
									exchangeCards.add(player.getCardList().get(m));
								}
							}
						}
					}
				}
				int count = GameMap.getCardExchangeCountinTheGame();
				armiesInExchange = (count + 1) * EXCHANGEARMY;
				System.out.println("Succesfully exchanged the card");
				GameMap.cardExchangeCountinTheGame = GameMap.getCardExchangeCountinTheGame() + 1;
				for (int i = 0; i < exchangeCards.size(); i++) {
					int count1 = 0;
					String cardType = exchangeCards.get(i).getType();
					for (int k = 0; k < player.getCardList().size(); k++) {
						if (player.getCardList().get(k).getType().equalsIgnoreCase(cardType)) {
							if (count1 == 0) {
								player.getCardList().remove(k);
								count1 = count1 + 1;
							}
						}
					}
					count1 = 0;
				}
			}
		} else {
			if (player.getCardList().size() > 0) {
				System.out.println("You only have" + " " + player.getCardList().size() + " " + "Cards");
			}
			System.out.println("Not enough cards to exchange ,continuing with the reinforcement phase");
		}
		return armiesInExchange;
	}

	/**
	 * To check exchanging cards are of different types
	 *
	 * @param exchangeCards exchange cards
	 * @param cardTypes     card types
	 * @return true or false depending on the types of cards
	 */
	public boolean checkDiffTypesOfCards(List<Card> exchangeCards, int cardTypes) {
		if (cardTypes < 3) {
			return false;
		}
		List<String> types = new ArrayList<>();
		for (Card card : exchangeCards) {
			types.add(card.getType());
		}
		if (!types.get(0).equals(types.get(1)) && !types.get(1).equals(types.get(2))
				&& !types.get(2).equals(types.get(0))) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if the given three types of cards are same
	 *
	 * @param exchangeCards       exchange cards
	 * @param cardAppearingThrice Card's name which is appearing thrice
	 * @return true if all are same else false
	 */
	public boolean checkCardSameType(List<Card> exchangeCards, String cardAppearingThrice) {
		int cardAppearingCount = 0;
		if (exchangeCards.size() < 3) {
			return false;
		} else {
			for (Card card : exchangeCards) {
				if (card.getType().equals(cardAppearingThrice)) {
					cardAppearingCount++;
				}
			}
			if (cardAppearingCount == 3) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method is to check the user input format for cards less than 5
	 *
	 * @param str User Input command
	 * @return List of Cards
	 * @throws Exception IOException
	 */
	public static String[] returnInput(String str) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		boolean rightInput = false;
		String[] cardsList = str.split(" ");
		while (!rightInput) {
			if (cardsList.length > 4) {
				System.out.print("\n"
						+ "Enter the numbers of card you want to exchange in the format exchangecards num num num");
				cardsList = br.readLine().split(" ");
			}
			if (cardsList.length < 2) {
				System.out.print("\n"
						+ "Enter the numbers of card you want to exchange in the format exchangecards num num num");
				cardsList = br.readLine().split(" ");
			}
			if (cardsList.length == 2) {
				Pattern namePattern2 = Pattern.compile("exchangecards");
				Matcher match1 = namePattern2.matcher(cardsList[0]);

				Pattern namePattern = Pattern.compile("-none");
				Matcher match2 = namePattern.matcher(cardsList[1]);
				if (!match1.matches() || !match2.matches()) {
					System.out.println(
							"To not to exchange, Please enter the command as : exchangecards -none OR exchangecards num num num");
					cardsList = br.readLine().split(" ");
				} else {
					rightInput = true;
					System.out.println("Exiting out of card exchange...");
					return cardsList;
				}
			}
			if (cardsList.length == 4) {
				Pattern namePattern2 = Pattern.compile("exchangecards");
				Matcher match1 = namePattern2.matcher(cardsList[0]);

				Pattern numberPattern = Pattern.compile("[0-9]+");
				Matcher match2 = numberPattern.matcher(cardsList[1]);
				Matcher match3 = numberPattern.matcher(cardsList[2]);
				Matcher match4 = numberPattern.matcher(cardsList[3]);
				while (!match1.matches() || !match2.matches() || !match3.matches() || !match4.matches()) {
					System.out.println("Enter in the format exchangecards num num num");
					cardsList = br.readLine().split(" ");
					match1 = namePattern2.matcher(cardsList[0]);
					match2 = numberPattern.matcher(cardsList[1]);
					match3 = numberPattern.matcher(cardsList[2]);
					match4 = numberPattern.matcher(cardsList[3]);
				}
				rightInput = true;
				return cardsList;

			}
		}
		return null;
	}

	/**
	 * This method is to check the user input format for cards more than 5
	 *
	 * @param str User Input Command
	 * @return List of Cards
	 * @throws IOException Input output Exception
	 */
	public static String[] returnInputForMoreThanFive(String str) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		boolean rightInput = false;
		String[] cardsList = str.split(" ");
		while (!rightInput) {
			if (cardsList.length > 4) {
				System.out.print(
						"\n" + "Enter atleast 3 cards you want to exchange in the format exchangecards num num num");
				cardsList = br.readLine().split(" ");
			}
			if (cardsList.length < 4) {
				System.out.print(
						"\n" + "Enter atleast 3 cards you want to exchange in the format exchangecards num num num");
				cardsList = br.readLine().split(" ");
			}
			if (cardsList.length == 4) {
				Pattern namePattern2 = Pattern.compile("exchangecards");
				Matcher match1 = namePattern2.matcher(cardsList[0]);

				Pattern numberPattern = Pattern.compile("[0-9]+");
				Matcher match2 = numberPattern.matcher(cardsList[1]);
				Matcher match3 = numberPattern.matcher(cardsList[2]);
				Matcher match4 = numberPattern.matcher(cardsList[3]);
				while (!match1.matches() || !match2.matches() || !match3.matches() || !match4.matches()) {
					System.out.println("Enter in the format exchangecards num num num");
					cardsList = br.readLine().split(" ");
					match1 = namePattern2.matcher(cardsList[0]);
					match2 = numberPattern.matcher(cardsList[1]);
					match3 = numberPattern.matcher(cardsList[2]);
					match4 = numberPattern.matcher(cardsList[3]);
				}
				rightInput = true;
				return cardsList;

			}
		}
		return null;
	}
}
