package com.appriskgame.strategy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.appriskgame.controller.CardController;
import com.appriskgame.controller.Player;
import com.appriskgame.model.Country;
import com.appriskgame.model.GameMap;
import com.appriskgame.model.GamePlayer;

/**
 * This class reinforces the strongest country, attacks until the army count of
 * the attacking country is one, fortifies the strongest country for the
 * aggressive player.
 * 
 * @author surya
 *
 */
public class Aggressive implements PlayerStrategy {

	Player playerController;

	/**
	 * This method places armies on the countries owned by Aggressive player
	 */
	@Override
	public void placeArmies(GameMap gameMap, GamePlayer player) {
		playerController = new Player();
		Random random = new Random();
		int countryNumber = random.nextInt(player.getPlayerCountries().size());
		String countryName = player.getPlayerCountries().get(countryNumber).getCountryName();
		playerController.placearmyassigned(player, countryName);
	}

	/**
	 * This method reinforces armies to the strongest country of the Aggressive
	 * player
	 */
	@Override
	public void reinforcementPhase(GamePlayer player, GameMap gameMap) throws Exception {
		playerController = new Player();
		playerController.startReinforcement(player, gameMap);
		Country country = getStrongestCountryWithAdjCountry(gameMap, player);
		int numOfarmies = player.getNoOfArmies();
		playerController.userAssignedArmiesToCountries(country, numOfarmies, player);

	}

	/**
	 * This method calls the Attack method of player class by passing the aggressive
	 * player's strongest country as the attacking country and the adjacent country
	 * of the strongest country which belongs to other player as the defender
	 * country.
	 */
	@Override
	public int attackPhase(GameMap gameMap, GamePlayer player, ArrayList<GamePlayer> playersList) {
		playerController = new Player();
		int numberOfArmies = 0;
		Country defenderCountryObject = null;
		gameMap.setActionMsg("Aggressive Player is Attacking", "action");
		Country attackCountryObject = getStrongestCountryWithAdjCountry(gameMap, player);
		numberOfArmies = attackCountryObject.getNoOfArmies();
		List<String> adjacentCountriesList = attackCountryObject.getNeighbourCountries();
		for (String adjCountry : adjacentCountriesList) {
			GamePlayer adjPlayer = playerController.getPlayerForCountry(gameMap, adjCountry);
			Country adjPlayerCountry = adjPlayer.getSelectedCountry(adjCountry);
			if (adjPlayerCountry.getNoOfArmies() <= numberOfArmies
					&& !(player.getPlayerName().equalsIgnoreCase(adjPlayer.getPlayerName()))) {
				numberOfArmies = adjPlayerCountry.getNoOfArmies();
				defenderCountryObject = adjPlayerCountry;
			}
		}
		if (defenderCountryObject != null) {
			while (attackCountryObject.getNoOfArmies() > 1 && defenderCountryObject.getNoOfArmies() != 0) {
				System.out.println(attackCountryObject.getCountryName() + " is attacking "
						+ defenderCountryObject.getCountryName());
				gameMap.setActionMsg(attackCountryObject.getCountryName() + " is attacking "
						+ defenderCountryObject.getCountryName(), "action");
				int attackerDices = playerController.maxAllowableAttackerDice(attackCountryObject.getNoOfArmies());
				int defenderDices = playerController.maxAllowableDefenderDice(defenderCountryObject.getNoOfArmies());
				if (attackerDices > 0 && defenderDices > 0) {
					playerController.attackingStarted(attackerDices, defenderDices, attackCountryObject,
							defenderCountryObject);
					if (playerController.isAttackerWon(defenderCountryObject)) {
						System.out.println("Card Phase");
						CardController cardController = new CardController();
						gameMap.setActionMsg("Player Card Allocation", "action");
						cardController.setDeckOfCards();
						cardController.allocateCardToPlayer(player);
						gameMap.setActionMsg("Player got a Card", "action");
						String removePlayerName = defenderCountryObject.getPlayer();
						int moveNumberOfArmies = attackCountryObject.getNoOfArmies() / 2;
						if (playerController.ableToMoveArmy(attackCountryObject, moveNumberOfArmies)) {
							playerController.removeOwnerAddNewOwner(playersList, player,
									defenderCountryObject.getCountryName());
							System.out.println("Moving " + moveNumberOfArmies + " Amries to "
									+ defenderCountryObject.getCountryName());
							defenderCountryObject.setPlayer(attackCountryObject.getPlayer());
							defenderCountryObject.setNoOfArmies(moveNumberOfArmies);
							attackCountryObject.setNoOfArmies(attackCountryObject.getNoOfArmies() - moveNumberOfArmies);
						}
						playerController.removePlayer(playersList, gameMap, removePlayerName);
						if (playerController.isPlayerWinner(player, gameMap)) {
							gameMap.setActionMsg(player.getPlayerName() + " won the Game!", "action");
							System.out.println(player.getPlayerName() + " won the Game!");
							if (gameMap.getMode().equalsIgnoreCase("Tournament")) {
								return 1;
							} else {
								System.exit(0);
							}

						}
						break;
					}
				}
			}
		}
		return 0;

	}

	/**
	 * This method is used for fortifying armies in the strongest armies
	 */
	@Override
	public void fortificationPhase(GameMap gameMap, GamePlayer player) throws IOException {
		playerController = new Player();
		Country fromCountry = null;
		int armiesCount = 0;
		if (playerController.startGameFortification(player, gameMap)) {
			Country strongestCountry = getStrongestCountryWithAdjCountry(gameMap, player);
			Country strongestNeighCountryToFortify = null;
			int armycount = 0;
			for (String neighbourCountryName : strongestCountry.getNeighbourCountries()) {
				for (Country country : player.getPlayerCountries()) {
					if (country.getCountryName().equalsIgnoreCase(neighbourCountryName)) {
						if (country.getNoOfArmies() > armycount) {
							strongestNeighCountryToFortify = country;
							armycount = country.getNoOfArmies();
						}
					}
				}
			}
			fromCountry = strongestNeighCountryToFortify;
			armiesCount = (armycount - 1) / 2;
			if (fromCountry != null && strongestCountry != null) {
				playerController.moveArmies(fromCountry, strongestCountry, armiesCount);
			}

		}
		System.out.println("Aggressive fortification complete");

	}

	/**
	 * Method to get the strongest country from the Aggressive player's countries
	 * list
	 * 
	 * @param mapGraph GameMapGraph object
	 * @param player   current player which is the aggressive player
	 * @return strongestCountry strongest country of aggressive player
	 */
	public Country getStrongestCountry(GameMap mapGraph, GamePlayer player) {
		int numberOfArmies = 0;
		Country strongestCountry = null;
		for (Country country : player.getPlayerCountries()) {
			if (country.getNoOfArmies() >= numberOfArmies) {
				numberOfArmies = country.getNoOfArmies();
				strongestCountry = country;
			}
		}
		return strongestCountry;
	}

	/**
	 * This method is used to get the strongest country
	 * 
	 * @param mapGraph GameMapGraph object
	 * @param player   current player which is the aggressive player
	 * @return strongestCountry strongest country of aggressive player
	 */
	public Country getStrongestCountryWithAdjCountry(GameMap mapGraph, GamePlayer player) {
		int numberOfArmies = 0;
		Country strongestCountry = null;
		playerController = new Player();
		List<Country> countriesWithAdjCountries = new ArrayList<Country>();
		for (Country country : player.getPlayerCountries()) {
			for (String adjCountry : country.getNeighbourCountries()) {
				GamePlayer adjPlayer = playerController.getPlayerForCountry(mapGraph, adjCountry);
				if (!player.getPlayerName().equalsIgnoreCase(adjPlayer.getPlayerName())) {
					countriesWithAdjCountries.add(country);
					break;
				}
			}
		}

		for (Country country : countriesWithAdjCountries) {
			if (country.getNoOfArmies() >= numberOfArmies) {
				numberOfArmies = country.getNoOfArmies();
				strongestCountry = country;
			}
		}
		if (strongestCountry == null) {
			strongestCountry = getStrongestCountry(mapGraph, player);
		}
		return strongestCountry;
	}

}
