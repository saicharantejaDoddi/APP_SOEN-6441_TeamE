package com.appriskgame.test.strategy;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.appriskgame.model.Continent;
import com.appriskgame.model.Country;
import com.appriskgame.model.GameMap;
import com.appriskgame.model.GamePlayer;
import com.appriskgame.strategy.RandomPlayer;

/**
 * This class aims to test the Random Player class
 * 
 * @author Sahana
 */
public class RandomPlayerTest {
	private static RandomPlayer randomPlayer;

	private static GameMap mapGraph;
	private static GamePlayer player, player1;
	private static ArrayList<GamePlayer> players;
	private static ArrayList<Country> getMyCountries, getMyCountries1;
	private static Country country, country1, country2, country3;

	/**
	 * Initial setup for Random player test.
	 */
	@Before
	public void initialize() {
		getMyCountries = new ArrayList<Country>();
		country = new Country();
		country.setCountryName("Canada");
		country.setNoOfArmies(2);
		country.setContinentName("America");
		country.setPlayer("attacker");

		country1 = new Country();
		country1.setCountryName("United states");
		country1.setNoOfArmies(4);
		country1.setContinentName("America");
		country1.setPlayer("attacker");

		getMyCountries.add(country);
		getMyCountries.add(country1);

		getMyCountries1 = new ArrayList<Country>();

		country2 = new Country();
		country2.setCountryName("Egypt");
		country2.setNoOfArmies(5);
		country2.setContinentName("Africa");
		country2.setPlayer("defender");

		country3 = new Country();
		country3.setCountryName("Cameron");
		country3.setNoOfArmies(5);
		country3.setContinentName("Africa");
		country3.setPlayer("defender");

		getMyCountries1.add(country2);
		getMyCountries1.add(country3);

		player = new GamePlayer();
		player.setPlayerCountries(getMyCountries);
		player.setNoOfArmies(2);
		player.setPlayerName("attacker");

		player1 = new GamePlayer();
		player1.setPlayerCountries(getMyCountries1);
		player1.setNoOfArmies(1);
		player1.setPlayerName("defender");

		mapGraph = new GameMap();
		mapGraph.setMode("test");

		players = new ArrayList<GamePlayer>();
		players.add(player);
		players.add(player1);
		mapGraph.setPlayers(players);
		mapGraph.setCountries(new ArrayList<Country>());
		mapGraph.getCountries().add(country);
		mapGraph.getCountries().add(country1);
		mapGraph.getCountries().add(country2);
		mapGraph.getCountries().add(country3);

		randomPlayer = new RandomPlayer();

		Continent continent = new Continent();
		continent.setContinentName("America");
		continent.setContinentControlValue(3);
		Continent continent1 = new Continent();
		continent1.setContinentName("Africa");
		continent1.setContinentControlValue(4);

		mapGraph.getContinents().add(continent);
		mapGraph.getContinents().add(continent1);

		country.setPartOfContinent(continent);
		country1.setPartOfContinent(continent);
		country2.setPartOfContinent(continent1);
		country3.setPartOfContinent(continent1);

		country.setNeighbourCountries(new ArrayList<String>());
		country.getNeighbourCountries().add("United states");

		country1.setNeighbourCountries(new ArrayList<String>());
		country1.getNeighbourCountries().add("Canada");
		country1.getNeighbourCountries().add("cameron");

		country2.setNeighbourCountries(new ArrayList<String>());
		country2.getNeighbourCountries().add("cameron");

		country3.setNeighbourCountries(new ArrayList<String>());
		country3.getNeighbourCountries().add("egypt");
		country3.getNeighbourCountries().add("united states");
	}

	/**
	 * This method tests the random country for Random Player
	 * 
	 * @throws Exception IO Exception
	 */
	@Test
	public void randomCountryTest() throws Exception {
		Country result = randomPlayer.getRandomCountry(mapGraph, player);
		assertTrue(player.getPlayerCountries().contains(result));
	}

}
