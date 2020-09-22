package com.appriskgame.controller;

import java.util.ArrayList;
import com.appriskgame.model.Continent;
import com.appriskgame.model.Country;

/**
 * This Class containing the method to check whether a map is a connected graph
 * or not
 * 
 * @author Surya
 * @author Sahana
 * 
 */
public class ConnectedGraph {
	String adjancencyError = "";

	/**
	 * This method is used to check adjacency between countries in the uploaded file
	 * and check for correct format.
	 * 
	 * @param listOfCountries contains all countries
	 * @param listOfContinent contains all Continent
	 * @return flag returns boolean value if adjacency are defined correctly.
	 */
	public String checkCountryAdjacency(ArrayList<Country> listOfCountries, ArrayList<Continent> listOfContinent) {
		String adjError = new String(), contError = new String(), connectedError = new String();
		ArrayList<String> adjacentCountries = new ArrayList<>();
		ArrayList<String> availableContinent = new ArrayList<>();
		ArrayList<String> connectivity = new ArrayList<>();
		boolean flag = false, continentFlag = true, adjacencyFlag = true, connectedFlag = true;

		if (listOfCountries != null && !listOfCountries.isEmpty()) {
			for (Country country1 : listOfCountries) {
				adjacentCountries = country1.getNeighbourCountries();
				if (!adjacentCountries.isEmpty()) {
					for (String adjCountryName : adjacentCountries) {
						for (Country country2 : listOfCountries) {
							if (country2.getCountryName().equals(adjCountryName)) {
								if (country2.getNeighbourCountries().contains(country1.getCountryName())) {
									flag = true;
									// To check for Connectivity of the Graph adding the connected continents in the
									// list
									if (country1.getPartOfContinent() != null
											&& country2.getPartOfContinent() != null) {
										if (!country2.getPartOfContinent().getContinentName()
												.equals(country1.getPartOfContinent().getContinentName())) {
											connectivity.add(country2.getPartOfContinent().getContinentName());
											connectivity.add(country1.getPartOfContinent().getContinentName());
										}
									}
									break;
								} else
									flag = false;
							} else
								flag = false;
						}
						if (!flag) {
							adjacencyFlag = false;
							adjError = adjError.concat(country1.getCountryName() + " and " + adjCountryName
									+ " are not defined properly as adjacent countries.\n");
						}
					}
				} else {
					adjacencyFlag = false;
					adjError = adjError.concat(country1.getCountryName()
							+ " does not have any Adjacents Countries. There should be atleast one adjacent country\n");
				}
			}
		}
		if (listOfContinent.size() < 2) {
			continentFlag = false;
			contError = "Minimum number of continents should be two to play the games.\n";
		}
		if (listOfCountries != null && !listOfCountries.isEmpty()) {
			listOfCountries.forEach(country -> {
				if (!availableContinent.contains(country.getCountryName())) {
					availableContinent.add(country.getPartOfContinent().getContinentName());
				}
			});
		}
		for (Continent continent : listOfContinent) {
			boolean flagConnected = true;
			for (String continentname : availableContinent) {
				if (continent.getContinentName().equalsIgnoreCase(continentname)) {
					flagConnected = false;
				}
			}
			if (flagConnected) {
				continentFlag = false;
				contError = contError.concat(continent.getContinentName()
						+ " does not have any defined Country. Should have atleast one country.\n");
			}
			flagConnected = true;
			for (String connectedContinent : connectivity) {
				if (connectedContinent.equalsIgnoreCase(continent.getContinentName())) {
					flagConnected = false;
				}
			}
			if (flagConnected) {
				connectedFlag = false;
				connectedError = connectedError.concat("Countries from " + continent.getContinentName()
						+ " are not connected to any of the countries of the other " + (listOfContinent.size() - 1)
						+ " available continents.\n");
			}
		}
		if (flag && continentFlag && adjacencyFlag && connectedFlag) {
			return adjancencyError;
		} else {
			adjancencyError = "\n".concat(contError).concat(adjError);
			if (!connectedFlag) {
				connectedError = "\n Map Graph is not Connected - \n".concat(connectedError);
				adjancencyError = adjancencyError.concat(connectedError);
			}
			return adjancencyError;
		}
	}
}
