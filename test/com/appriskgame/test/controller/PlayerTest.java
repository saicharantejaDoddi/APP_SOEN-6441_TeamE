package com.appriskgame.test.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.appriskgame.controller.Player;
import com.appriskgame.model.Continent;
import com.appriskgame.model.Country;
import com.appriskgame.model.GameMap;
import com.appriskgame.model.GamePlayer;

/**
 * Test Class for Player class Method of startup ,reinforcement and
 * fortification
 * 
 * @author Sahana
 * @author Shruthi
 * @author Dolly
 */
public class PlayerTest {
	private GamePlayer player1, player2;
	private Country country1, country2, country3, country4, country5, country6, country7, country8, country9, country10,
			country11, country12, country13, country14;
	private Continent continent;
	private Country country, toCountry, fromCountry, toCountry1, countryStart;
	private ArrayList<String> NeighbourCountries;

	Player player;
	GameMap gameMap;
	HashMap<String, Country> countrySet;
	ArrayList<Country> listOfCountries;
	ArrayList<GamePlayer> listOfPlayers;
	ArrayList<Country> listOfNeighbours;
	String savedFilePath;
	String savedFileName;

	/**
	 * This is the setup method for the pre-requisite values before the test cases
	 */
	@Before
	public void initializePlayerTest() {
		savedFilePath = "C:\\Users\\saich\\Desktop\\Saicharanteja_TournamentChanges/resources/savedgames/";
		savedFileName = "Content";
		player = new Player();
		continent = new Continent();
		player1 = new GamePlayer();
		player2 = new GamePlayer();
		listOfCountries = new ArrayList<Country>();
		listOfPlayers = new ArrayList<GamePlayer>();
		listOfNeighbours = new ArrayList<Country>();

		country1 = new Country();
		country1.setCountryName("Egypt");
		country1.setPlayer("player1");
		player1.setPlayerName("player1");

		player1.getPlayerCountries().add(country1);
		player2.getPlayerCountries().add(country1);
		continent.getListOfCountries().add(country1);

		country2 = new Country();
		country2.setCountryName("Libya");
		player1.getPlayerCountries().add(country2);
		player2.getPlayerCountries().add(country2);
		continent.getListOfCountries().add(country2);

		country3 = new Country();
		country3.setCountryName("Morocco");
		player1.getPlayerCountries().add(country3);
		continent.getListOfCountries().add(country3);
		continent.setContinentName("Northern Africa");
		continent.setContinentControlValue(4);

		NeighbourCountries = new ArrayList<String>();

		country = new Country();
		country.setCountryName("India");
		NeighbourCountries.add("India");

		country = new Country();
		country.setCountryName("Nepal");
		NeighbourCountries.add("Nepal");

		country = new Country();
		country.setCountryName("Srilanka");
		NeighbourCountries.add("Srilanka");

		country = new Country();
		country.setCountryName("China");
		NeighbourCountries.add("China");

		fromCountry = new Country();
		fromCountry.setCountryName("Bangladesh");
		fromCountry.setNoOfArmies(8);

		toCountry = new Country();
		toCountry.setCountryName("India");
		toCountry.setNoOfArmies(4);

		toCountry1 = new Country();
		toCountry1.setCountryName("Canada");
		toCountry1.setNoOfArmies(2);
		fromCountry.setNeighbourCountries(NeighbourCountries);

		countrySet = new HashMap<String, Country>();
		gameMap = new GameMap();
		player.getPlayerList().add(player1);
		player.getPlayerList().add(player2);
		countryStart = new Country();

		countryStart.setCountryName("China");
		countryStart.setCountryName("India");
		countrySet.put(countryStart.getCountryName(), countryStart);
		gameMap.setCountrySet(countrySet);

		country1.setNeighbourCountries(NeighbourCountries);
		listOfCountries.add(country);
		listOfCountries.add(country1);
		listOfCountries.add(country);
		listOfPlayers.add(player1);
		listOfNeighbours.add(country);
		gameMap.setPlayers(listOfPlayers);
		gameMap.setCountries(listOfCountries);
		country1.setNoOfArmies(5);

	}

	/**
	 * This method to validate the allocation of armies to each player as per the
	 * number of players.
	 */
	@Test
	public void testDefaultArmiesToPlayer() {
		player.defaultArmiesToPlayer();
		assertEquals(40, player1.getNoOfArmies());
	}

	/**
	 * Testing whether the count of reinforcement armies is proper
	 */
	@Test
	public void testassignReinforcedArmies() {
		int expected = 3;
		assertEquals(expected, player.assignReinforcedArmies(player1, continent));
	}

	/**
	 * This method is used to test the count of reinforcement armies
	 */
	@Test
	public void testassignReinforcedArmies2() {
		int expected = 4;
		player2.getPlayerCountries().add(country4);
		player2.getPlayerCountries().add(country5);
		player2.getPlayerCountries().add(country6);
		player2.getPlayerCountries().add(country7);
		player2.getPlayerCountries().add(country8);
		player2.getPlayerCountries().add(country9);
		player2.getPlayerCountries().add(country10);
		player2.getPlayerCountries().add(country11);
		player2.getPlayerCountries().add(country12);
		player2.getPlayerCountries().add(country13);
		player2.getPlayerCountries().add(country14);
		assertEquals(expected, player.assignReinforcedArmies(player2, continent));
	}

	/**
	 * Test method to validate the number of armies present in the fromCountry and
	 * the toCountry after the player moves the armies between the adjacent
	 * fromCountry and toCountry.
	 */
	@Test
	public void isFortificationComplete() {
		player.moveArmies(fromCountry, toCountry, 2);
		assertEquals(6, fromCountry.getNoOfArmies());
		assertEquals(6, toCountry.getNoOfArmies());
	}

	/**
	 * Test method to validate the number of armies present in the fromCountry and
	 * the toCountry after the player moves the armies between fromCountry and
	 * toCountry which are not adjacent.
	 */
	@Test
	public void isFortificationNotComplete() {
		player.moveArmies(fromCountry, toCountry1, 2);
		assertEquals(8, fromCountry.getNoOfArmies());
		assertEquals(2, toCountry1.getNoOfArmies());
	}

	/**
	 * This method to validate the initial allocation of armies to each country
	 * owned by players.
	 */
	@Test
	public void testIntialArmyAllocation() {
		player.initialArmyAllocation(gameMap);
		assertEquals(1, countryStart.getNoOfArmies());
	}

	/**
	 * This method is used to check if the player country is present in the game
	 * map.
	 */
	@Test
	public void testisCountryPresent() {
		assertTrue(player.isCountryPresent("China", gameMap));

	}

	/**
	 * This method is used to check if the player country is present in the game
	 * map.
	 */
	@Test
	public void testisCountryNotPresent() {
		assertFalse(player.isCountryPresent("Morocco", gameMap));
	}

	/**
	 * This method is used to check if the player attack country is present in the
	 * game map.
	 */
	@Test
	public void testisCountryAttackPresent() {
		assertTrue(player.isCountryAttackPresent(player1, "Egypt", gameMap));
	}

	/**
	 * This method is used to check if the player attack country is not present in
	 * the game map.
	 */
	@Test
	public void testisCountryAttackNotPresent() {
		assertFalse(player.isCountryAttackPresent(player1, "India", gameMap));
	}

	/**
	 * This method is used to check if the attacker country is adjacent to Defender
	 * country
	 * 
	 */
	@Test
	public void testisCountryAdjacent() {
		assertTrue(player.isCountryAdjacent(country1, "India", gameMap));
	}

	/**
	 * This method is used to check if the attacker country is not adjacent to
	 * Defender country
	 */
	@Test
	public void testisCountryNotAdjacent() {
		assertFalse(player.isCountryAdjacent(country1, "Pakistan", gameMap));
	}

	/**
	 * This method is used to check if the player has won all the countries in the
	 * map
	 */
	@Test
	public void testisPlayerWinner() {
		System.out.println("Player 1 has won " + player1.getPlayerCountries().size() + " countries");
		System.out.println();
		System.out.println("Total Countries in the Map are " + gameMap.getCountries().size() + " countries");
		assertTrue(player.isPlayerWinner(player1, gameMap));
	}

	/**
	 * This method is used to check if the player has not won all the countries in
	 * the map
	 */
	@Test
	public void testisPlayerNotWinner() {
		System.out.println("Player 2 has won " + player2.getPlayerCountries().size() + " countries");
		System.out.println();
		System.out.println("Total Countries in the Map are " + gameMap.getCountries().size() + " countries");
		assertFalse(player.isPlayerWinner(player2, gameMap));
	}

	/**
	 * This method is used to check if the player is able to move army if number of
	 * armies are zero or less than zero
	 */
	@Test
	public void testableToMoveArmy() {
		assertTrue(player.ableToMoveArmy(country1, 1));
	}

	/**
	 * This method is used to check if the player is able to move army if number of
	 * armies are zero or less than zero
	 */
	@Test
	public void testNotableToMoveArmy() {
		assertFalse(player.ableToMoveArmy(country1, 0));
	}

}
