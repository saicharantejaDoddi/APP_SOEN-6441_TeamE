package com.appriskgame.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class contains all the details of Continent
 *
 * @author Shruthi
 * @author Sahana
 *
 */
public class Continent implements Serializable{
	private String continentName;
	private int continentControlValue;
	private ArrayList<Country> listOfCountries = new ArrayList<Country>();

	/**
	 * Get the continent name.
	 *
	 * @return continent's Name
	 */
	public String getContinentName() {
		return continentName;
	}

	/**
	 * Set the continent name.
	 *
	 * @param continentName To set the name of the continent
	 */
	public void setContinentName(String continentName) {
		this.continentName = continentName;
	}

	/**
	 * To get the Control value for each continent
	 *
	 * @return continentControlValue
	 */
	public int getContinentControlValue() {
		return continentControlValue;
	}

	/**
	 * To set the Control value for each continent
	 *
	 * @param continentControlValue Continent Control Value
	 */
	public void setContinentControlValue(int continentControlValue) {
		this.continentControlValue = continentControlValue;
	}

	/**
	 * Get the list of countries in the particular continent.
	 *
	 * @return listOfContries
	 */
	public ArrayList<Country> getListOfCountries() {
		return listOfCountries;
	}

	/**
	 * Set the list of countries in the particular continent.
	 *
	 * @param listOfCountries List containing all countries
	 */
	public void setListOfCountries(ArrayList<Country> listOfCountries) {
		this.listOfCountries = listOfCountries;
	}

	@Override
	public String toString() {
		return "Continents: \n[continentName=" + continentName + ", controlValue=" + continentControlValue
				+ ", listOfCountries=" + listOfCountries + "]/n";
	}
}