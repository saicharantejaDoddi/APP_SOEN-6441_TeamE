package com.appriskgame.strategy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import com.appriskgame.controller.Player;
import com.appriskgame.model.Country;
import com.appriskgame.model.GameMap;
import com.appriskgame.model.GamePlayer;

/**
 * This method reinforces its weakest countries, does not attack, fortifies by
 * moving armies to weaker countries
 * 
 * @author surya
 * @author sahana
 *
 */
public class Benevolent implements PlayerStrategy {

	Player playerController;

	/**
	 * This method places armies on the countries owned by Benevolent player
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
	 * This method reinforces armies to the weakest country of the Benevolent player
	 */
	@Override
	public void reinforcementPhase(GamePlayer player, GameMap gameMap) throws Exception {
		playerController = new Player();
		playerController.startReinforcement(player, gameMap);
		Country country = getWeakestCountry(gameMap, player);
		int numOfarmies = player.getNoOfArmies();
		playerController.userAssignedArmiesToCountries(country, numOfarmies, player);
	}

	/**
	 * This method is used for attack and the benevolent player cannot attack.
	 */
	@Override
	public int attackPhase(GameMap gameMap, GamePlayer player, ArrayList<GamePlayer> playersList) {
		System.out.println("Benevolent Never Attacks");
		return 0;

	}

	/**
	 * This method fortifies armies from the strongest country to the weakest
	 * country of the Benevolent player
	 */
	@Override
	public void fortificationPhase(GameMap gameMap, GamePlayer player) throws IOException {
		playerController = new Player();
		if (playerController.startGameFortification(player, gameMap)) {
			Country weakestCountry = getWeakestCountry(gameMap, player);
			Country strongestNeighCountryToFortify = null;
			int armycount = 0;
			for (String neighbourCountryName : weakestCountry.getNeighbourCountries()) {
				for (Country country : player.getPlayerCountries()) {
					if (country.getCountryName().equalsIgnoreCase(neighbourCountryName)) {
						if (country.getNoOfArmies() > armycount) {
							strongestNeighCountryToFortify = country;
							armycount = country.getNoOfArmies();
						}
					}
				}
			}
			if (strongestNeighCountryToFortify != null) {
				int fortifyArmiesToWeakestCountry = (strongestNeighCountryToFortify.getNoOfArmies()) / 2;
				playerController.moveArmies(strongestNeighCountryToFortify, weakestCountry,
						fortifyArmiesToWeakestCountry);
			}
		}
		System.out.println("Benevolent fortification complete");
	}

	/**
	 * this method gets the weakest country owned by the benevolent player
	 * 
	 * @param mapGraph GameMap object
	 * @param player   player object
	 * @return weakest country
	 */
	public Country getWeakestCountry(GameMap mapGraph, GamePlayer player) {
		int numberOfArmies = 0;
		Country weakestCountry = null;
		numberOfArmies = player.getPlayerCountries().get(0).getNoOfArmies();
		for (Country country : player.getPlayerCountries()) {
			if (country.getNoOfArmies() <= numberOfArmies) {
				numberOfArmies = country.getNoOfArmies();
				weakestCountry = country;
			}
		}
		return weakestCountry;
	}

	/**
	 * this method gets the strongest country owned by the benevolent player
	 * 
	 * @param mapGraph GameMap object
	 * @param player   player object
	 * @return strongest country
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
}
