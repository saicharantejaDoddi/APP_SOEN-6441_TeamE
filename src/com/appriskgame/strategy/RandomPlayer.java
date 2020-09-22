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
 * This class is for Random player who reinforces random country, attacks random
 * country a random number of times and fortifies a random country.
 * 
 * @author Surya
 */
public class RandomPlayer implements PlayerStrategy {

	Player playerController;

	/**
	 * this method place armies on a random country owned by the player.
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
	 * This method is for Reinforcement phase of the random player.
	 */
	@Override
	public void reinforcementPhase(GamePlayer player, GameMap gameMap) throws Exception {
		playerController = new Player();
		playerController.startReinforcement(player, gameMap);
		Country country = getRandomCountry(gameMap, player);
		int numOfarmies = player.getNoOfArmies();
		playerController.userAssignedArmiesToCountries(country, numOfarmies, player);
	}

	/**
	 * this method chooses two random adjacent countries and decide whether to
	 * attack or not. if it chooses to attack it randomly decide how many times to
	 * attack if the attack is possible
	 * 
	 */
	@Override
	public int attackPhase(GameMap gameMap, GamePlayer player, ArrayList<GamePlayer> playersList) {
		playerController = new Player();
		GamePlayer adjPlayer = null;
		Country attackCountryObject = null, defenderCountryObject = null;
		List<Country> countriesWithAdjCountries = new ArrayList<Country>();
		countriesWithAdjCountries.addAll(getRandomCountryWithAdjCountry(gameMap, player));
		if (countriesWithAdjCountries.size() > 1) {
			int random = new Random().nextInt(countriesWithAdjCountries.size());
			attackCountryObject = countriesWithAdjCountries.get(random);
		} else if (countriesWithAdjCountries.size() > 0) {
			attackCountryObject = countriesWithAdjCountries.get(0);
		}
		if (attackCountryObject != null) {
			do {
				int randomAdjCountry = new Random().nextInt(attackCountryObject.getNeighbourCountries().size());
				defenderCountryObject = playerController.getAdjacentCountry(gameMap,
						attackCountryObject.getNeighbourCountries().get(randomAdjCountry));
				adjPlayer = playerController.getPlayerForCountry(gameMap, defenderCountryObject.getCountryName());

			} while (player.getPlayerName().equalsIgnoreCase(adjPlayer.getPlayerName()));

		}

		if (defenderCountryObject != null) {
			System.out.println(
					attackCountryObject.getCountryName() + " is attacking " + defenderCountryObject.getCountryName());
			gameMap.setActionMsg(
					attackCountryObject.getCountryName() + " is attacking " + defenderCountryObject.getCountryName(),
					"action");
			while (attackCountryObject.getNoOfArmies() > 1 && defenderCountryObject.getNoOfArmies() != 0) {
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
	 * this method chooses two random adjacent countries and fortifies a random
	 * number of possible armies
	 */
	@Override
	public void fortificationPhase(GameMap gameMap, GamePlayer player) throws IOException {
		playerController = new Player();
		if (playerController.startGameFortification(player, gameMap)) {
			boolean countryFound = false;
			do {
				Country fromCountry = getRandomCountry(gameMap, player);
				Country toCountry = getRandomNeighbourCountry(fromCountry, gameMap);
				if (!fromCountry.getCountryName().equalsIgnoreCase(toCountry.getCountryName())) {
					countryFound = true;
				}
				if (countryFound) {
					int armiesCount = new Random().nextInt(fromCountry.getNoOfArmies()) + 1;
					if (armiesCount > 1 && (fromCountry.getNoOfArmies() - armiesCount) >= 1) {
						playerController.moveArmies(fromCountry, toCountry, armiesCount);
					}
				}
			} while (!countryFound);
		}
	}

	/**
	 * this method gets a random country owned by the Random player
	 * 
	 * @param mapGraph GameMap object
	 * @param player   current player
	 * @return randomCountry object
	 */
	public Country getRandomCountry(GameMap mapGraph, GamePlayer player) {
		int countryIndexAssignment = new Random().nextInt(player.getPlayerCountries().size());
		return player.getPlayerCountries().get(countryIndexAssignment);
	}

	/**
	 * this method gets a random neighbor country owned by the Random player
	 * 
	 * @param gamemap     GameMap object
	 * @param fromcountry Country Object
	 * @return random neighbor Country
	 */
	public Country getRandomNeighbourCountry(Country fromcountry, GameMap gamemap) {
		Country neighbourCountry = null;
		int neighbourIndex = new Random().nextInt(fromcountry.getNeighbourCountries().size());
		String countryName = fromcountry.getNeighbourCountries().get(neighbourIndex);
		for (Country country : gamemap.getCountries()) {
			if (country.getCountryName().equalsIgnoreCase(countryName)) {
				neighbourCountry = country;
				break;
			}
		}
		return neighbourCountry;
	}

	/**
	 * this method gets a random country of player with adjacent country belonging
	 * to a different player
	 * 
	 * @param mapGraph GameMapGraph object
	 * @param player   current player
	 * @return List of Adjacent Country
	 */
	public List<Country> getRandomCountryWithAdjCountry(GameMap mapGraph, GamePlayer player) {
		playerController = new Player();
		List<Country> countriesWithAdjCountries = new ArrayList<Country>();
		for (Country country : player.getPlayerCountries()) {
			for (String adjCountry : country.getNeighbourCountries()) {
				GamePlayer adjPlayer = playerController.getPlayerForCountry(mapGraph, adjCountry);
				if (!player.getPlayerName().equalsIgnoreCase(adjPlayer.getPlayerName())) {
					countriesWithAdjCountries.add(country);
				}
			}
		}
		return countriesWithAdjCountries;
	}
}
