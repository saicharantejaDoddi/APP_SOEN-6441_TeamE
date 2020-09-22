package com.appriskgame.strategy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import com.appriskgame.controller.Player;
import com.appriskgame.model.Country;
import com.appriskgame.model.GameMap;
import com.appriskgame.model.GamePlayer;

/**
 * This class is for cheater who used to reinforce the methods that doubles the
 * army count on all its countries, attack method automatically conquers all the
 * neighbors of all its countries, doubles the number of armies on its countries
 * that have neighbors that belong to other players.
 * 
 * @author sahana
 * @author surya
 */
public class Cheater implements PlayerStrategy {

	Player playerController;

	/**
	 * this method is used to place armies in the countries owned by the cheater
	 * player
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
	 * this method is used for the reinforcement of the cheater player's countries,
	 * it doubles the armies on each country
	 */
	@Override
	public void reinforcementPhase(GamePlayer player, GameMap gameMap) {
		playerController = new Player();
		ArrayList<Country> countryList = player.getPlayerCountries();
		for (int i = 0; i < countryList.size(); i++) {
			Country currentCountry = countryList.get(i);
			int reinforcementArmies = currentCountry.getNoOfArmies() * 2;
			currentCountry.setNoOfArmies(reinforcementArmies);
			System.out.println(currentCountry.getCountryName() + " reinforced with " + reinforcementArmies);
		}
		player.setNoOfArmies(0);
	}

	/**
	 * this method is for the attack of the cheater player. it attacks its neighbor
	 * countries and wins them.
	 */
	@Override
	public int attackPhase(GameMap gameMap, GamePlayer player, ArrayList<GamePlayer> playersList) {
		ArrayList<Country> cheatersWinningCountries = new ArrayList<Country>();
		int armyCount = 1;
		for (Country country : player.getPlayerCountries()) {
			for (String adjCountry : country.getNeighbourCountries()) {
				Country adjacentCountry = playerController.getAdjacentCountry(gameMap, adjCountry);
				GamePlayer adjacentPlayer = playerController.getPlayerForCountry(gameMap,
						adjacentCountry.getCountryName());
				if (!(adjacentPlayer.getPlayerName().equalsIgnoreCase(player.getPlayerName()))
						&& country.getNoOfArmies() > 1) {
					adjacentCountry.setNoOfArmies(armyCount);
					country.setNoOfArmies(country.getNoOfArmies() - armyCount);
					cheatersWinningCountries.add(adjacentCountry);
				}
			}
		}
		for (Country country : cheatersWinningCountries) {
			System.out.println(player.getPlayerName() + " (Cheater) has conquered " + country.getCountryName());
			playerController.removeOwnerAddNewOwner(playersList, player, country.getCountryName());
			String removePlayerName = country.getPlayer();
			country.setPlayer(player.getPlayerName());
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
		}
		return 0;
	}

	/**
	 * this method is used for fortifying the countries of the cheater player that
	 * are neighbors to countries owned by other players
	 */
	@Override
	public void fortificationPhase(GameMap gameMap, GamePlayer player) throws IOException {
		playerController = new Player();
		if (playerController.startGameFortification(player, gameMap)) {
			for (Country country : player.getPlayerCountries()) {
				for (String adjCountry : country.getNeighbourCountries()) {
					GamePlayer adjPlayer = playerController.getPlayerForCountry(gameMap, adjCountry);
					if (!player.getPlayerName().equalsIgnoreCase(adjPlayer.getPlayerName())) {
						country.setNoOfArmies(country.getNoOfArmies() * 2);
						System.out.println("Cheater fortified " + country.getCountryName() + " with "
								+ country.getNoOfArmies() * 2);
						if (!(gameMap.getMode().equalsIgnoreCase("tournament")
								|| gameMap.getMode().equalsIgnoreCase("test"))) {
							gameMap.setActionMsg("Cheater fortified " + country.getCountryName() + " with "
									+ country.getNoOfArmies() * 2, "action");
						}
						break;
					}
				}
			}
			System.out.println("Cheater fortification complete");
		}
	}
}
