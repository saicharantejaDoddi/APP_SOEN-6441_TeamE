package com.appriskgame.test.controller;

import org.junit.Test;
import org.junit.Before;

import com.appriskgame.controller.MapOperations;
import com.appriskgame.model.Country;
import com.appriskgame.model.GameMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * Test class for MapOperation class
 * 
 * @author Sahana
 * @author Sai
 */
public class MapOperationTest {

	private MapOperations mapOperations = new MapOperations();
	private MapOperations mapOperationsDemo = new MapOperations();
	GameMap gameMap = new GameMap();
	GameMap gameMapDemo = new GameMap();

	/**
	 * Initial setup for Map operations.
	 * 
	 * @throws IOException Input output exception
	 * 
	 */
	@Before
	public void intialize() throws IOException {
		String workingDir = System.getProperty("user.dir");
		String mapLocation = workingDir + "/resources/maps/" + "/ameroki.map";

		HashMap<String, Country> countrySet = new HashMap<>();

		String data = new String(Files.readAllBytes(Paths.get(mapLocation)));
		String[] requiredData = data.split("name");
		data = requiredData[1];
		String[] formattedData = data.split("\\r\\n\\r\\n");
		mapOperations.fillContinentsInGameMap(formattedData[2]);
		mapOperations.fillCountriesInGameMap(formattedData[3]);
		mapOperations.fillNeighboringCountriesInGameMap(formattedData[4]);
		gameMap.getCountries().forEach(country -> {
			countrySet.put(country.getCountryName(), country);
			gameMap.setCountrySet(countrySet);
		});

		String mapLocationDemo = workingDir + "/resources/maps/" + "/demofinaltest.map";
		HashMap<String, Country> countrySetdemo = new HashMap<>();
		String datademo = new String(Files.readAllBytes(Paths.get(mapLocationDemo)));
		String[] requiredDatademo = datademo.split("name");
		datademo = requiredDatademo[1];
		String[] formattedDatademo = datademo.split("\\r\\n\\r\\n");

		mapOperationsDemo.fillContinentsInGameMap(formattedDatademo[2]);
		mapOperationsDemo.fillCountriesInGameMap(formattedDatademo[3]);
		mapOperationsDemo.fillNeighboringCountriesInGameMap(formattedDatademo[4]);
		gameMapDemo = mapOperationsDemo.gameMap;
		gameMapDemo.getCountries().forEach(country -> {
			countrySetdemo.put(country.getCountryName(), country);
			gameMapDemo.setCountrySet(countrySetdemo);
		});
	}

	/**
	 * Test method for checking whether the continent is already defined in the
	 * continent list
	 */
	@Test
	public void testIsContinentUnique() {
		assertTrue(!mapOperations.doesContinentExit("North America"));
	}

	/**
	 * Test method for checking if the continent is not already defined in the list
	 */
	@Test
	public void testIsContinentNotUnique() {
		assertFalse(mapOperations.doesContinentExit("Europe"));
	}

	/**
	 * Test method for checking if the country is not already defined in the list
	 */
	@Test
	public void testCountyExist() {
		assertTrue(mapOperations.doesCountryExit("india"));
	}

	/**
	 * Test method to check whether map countries are connected inside continent
	 * 
	 * @throws IOException Input output exception
	 */
	@Test
	public void testConnectedGraph() throws IOException {

		assertTrue(mapOperations.isConnected(gameMap));

	}

	/**
	 * Test method to check whether map countries are connected inside continent
	 * 
	 * @throws IOException Input output exception
	 */
	@Test
	public void testDisConnectedGraph() throws IOException {

		assertFalse(mapOperationsDemo.isConnected(gameMapDemo));

	}

}
