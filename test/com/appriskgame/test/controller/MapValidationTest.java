package com.appriskgame.test.controller;

import org.junit.Test;

import com.appriskgame.controller.MapValidation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import org.junit.Before;

/**
 * Test Class for ReadAndWriteMap Class
 * 
 * @author Surya
 * @author Sai
 */
public class MapValidationTest {
	MapValidation mapValidation;
	private String validMap, validMapConquest, invalidMap, invalidMapConquest, notConnectedGraph;
	private String validCountryData, invalidCountryData, validCountryDataConquest, invalidCountryDataConquest;
	private String validContinentData, invalidContinentData, validContinentDataConquest, invalidContinentDataConquest;
	private String validBoundaryData, invalidBoundaryData;

	/**
	 * Initial setup for Map Validation Test.
	 */
	@Before
	public void initialize() {
		mapValidation = new MapValidation();
		validMap = "resources\\maps\\Valid1.map";
		validMapConquest = "resources\\maps\\Valid1Conquest.map";
		invalidMap = "resources\\maps\\invalid2.map";
		invalidMapConquest = "resources\\maps\\inValid1Conquest.map";
		notConnectedGraph = "resources\\maps\\testinvalidcontinent.map";
		validCountryData = "[countries]\r\n1 siberia 1 99 99\r\n2 worrick 2 99 99\r\n3 yazteck 1 99 99";
		invalidCountryData = "[countries]\r\n1 siberia\r\n2 worrick 2 99 99";
		validCountryDataConquest = "[countries]\r\n siberia,100,100,azio,worrick,yazteck";
		invalidCountryDataConquest = "[countries]\r\n1 siberia\r\n2 worrick 2 99 99";
		validContinentData = "[continents]\r\nSouth-America 5 yellow\r\nameroki 10 #99NoColor";
		invalidContinentData = "[continents]\nAsia=";
		validContinentDataConquest = "[continents]\r\n azio=5";
		invalidContinentData = "[continents]\nAsia=";
		invalidContinentDataConquest = "[continents]\nAsia=";
		validBoundaryData = "[borders]\r\n1 2 3\r\n2 1\r\n3 1";
		invalidBoundaryData = "[borders]\nasia";

	}

	/**
	 * Test method for testing whether the Domination map file is valid.
	 * 
	 * @throws IOException - the input output exception
	 */
	@Test
	public void testIsValidMap() throws IOException {
		assertTrue(mapValidation.validateMapDomination(validMap));
	}

	/**
	 * Test method for testing whether the Conquest map file is valid.
	 * 
	 * @throws IOException - the input output exception
	 */
	@Test
	public void testIsValidConquestMap() throws IOException {
		assertTrue(mapValidation.validateMapConquest(validMapConquest));
	}

	/**
	 * Test method for testing whether the map file is invalid.
	 * 
	 * @throws IOException - the input output exception
	 */
	@Test
	public void testIsInvalidMap() throws IOException {
		assertFalse(mapValidation.validateMapDomination(invalidMap));
	}

	/**
	 * Test method for testing whether the Conquest map file is invalid.
	 * 
	 * @throws IOException - the input output exception
	 */
	@Test
	public void testIsInvalidMapConquest() throws IOException {
		assertFalse(mapValidation.validateMapConquest(invalidMapConquest));
	}

	/**
	 * Test method for testing whether the map connected or not.
	 * 
	 * @throws IOException - the input output exception
	 */
	@Test
	public void testnotConnectedGraph() throws IOException {
		assertFalse(mapValidation.validateMapDomination(notConnectedGraph));
	}

	/**
	 * Test method for validating the valid country data.
	 */
	@Test
	public void testIsValidCountryData() {
		mapValidation.validateContinents(validContinentData, "domination");
		assertTrue(mapValidation.validateCountries(validCountryData, "domination"));
	}

	/**
	 * Test method for validating the invalid country data.
	 */
	@Test
	public void testIsInvalidCountryData() {
		mapValidation.validateContinents(validContinentData, "domination");
		assertFalse(mapValidation.validateCountries(invalidCountryData, "domination"));
	}

	/**
	 * Test method for validating the valid country data.
	 */
	@Test
	public void testIsValidCountryDataConquest() {
		mapValidation.validateContinents(validContinentDataConquest, "Conquest");
		assertTrue(mapValidation.validateCountries(validCountryDataConquest, "Conquest"));
	}

	/**
	 * Test method for validating the invalid country data.
	 */
	@Test
	public void testIsInvalidCountryDataConquest() {
		mapValidation.validateContinents(validContinentDataConquest, "Conquest");
		assertFalse(mapValidation.validateCountries(invalidCountryDataConquest, "Conquest"));
	}

	/**
	 * Test method for validating the valid continent data.
	 */
	@Test
	public void testIsValidContinentData() {
		assertTrue(mapValidation.validateContinents(validContinentData, "domination"));
	}

	/**
	 * Test method for validating the invalid continent data.
	 */
	@Test
	public void testIsInvalidContinentData() {
		assertFalse(mapValidation.validateContinents(invalidContinentData, "domination"));
	}

	/**
	 * Test method for validating the valid continent data.
	 */
	@Test
	public void testIsValidContinentDataConquest() {
		assertTrue(mapValidation.validateContinents(validContinentDataConquest, "conquest"));
	}

	/**
	 * Test method for validating the invalid continent data.
	 */
	@Test
	public void testIsInvalidContinentDataConquest() {
		assertFalse(mapValidation.validateContinents(invalidContinentDataConquest, "conquest"));
	}

	/**
	 * Test method for validating the valid border data.
	 */
	@Test
	public void testIsValidBorderData() {
		mapValidation.validateContinents(validContinentData, "domination");
		mapValidation.validateCountries(validCountryData, "domination");
		assertTrue(mapValidation.validateBoundaries(validBoundaryData));
	}

	/**
	 * Test method for validating the invalid border data.
	 */
	@Test
	public void testIsInvalidBorderData() {
		mapValidation.validateContinents(validContinentData, "domination");
		mapValidation.validateCountries(validCountryData, "domination");
		assertFalse(mapValidation.validateBoundaries(invalidBoundaryData));
	}
}
