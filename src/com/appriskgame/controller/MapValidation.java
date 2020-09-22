package com.appriskgame.controller;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.appriskgame.model.Continent;
import com.appriskgame.model.Country;
import com.appriskgame.model.GameMap;

/**
 * This class is used to check the format of List of continents, List of
 * countries, adjacency between countries which are present in the uploaded file
 * and validate the map.
 *
 * @author Surya
 * @author Sai
 */
public class MapValidation {
	private GameMap gameMap;
	private ArrayList<Country> listOfCountries;
	private ArrayList<Continent> listOfContinent;
	private static String errorMessage, adjancencyError;

	/**
	 * This Method used to get the error message.
	 *
	 * @return errorMessage
	 */
	public static String getError() {
		return errorMessage;
	}

	/**
	 * This Method used to get the Game Map.
	 *
	 * @return gameMap
	 */
	public GameMap getMapGraph() {
		return gameMap;
	}

	/**
	 * This method is used to validate the list of continents which are present in
	 * the uploaded file and check the correct format.
	 *
	 * @param tagData Value of the continent tag
	 * @param type    map type
	 * @return flag to check if entered continents are valid
	 */
	public boolean validateContinents(String tagData, String type) {

		listOfContinent = new ArrayList<Continent>();
		HashMap<String, String> visitedContinent = new HashMap<String, String>();
		boolean duplicateData = true, formatData = true;
		String formatError = new String();
		String DuplicateError = new String();
		String splitValue;
		if (type.equalsIgnoreCase("domination")) {
			splitValue = "\\r\\n";
		} else {
			splitValue = "\\n";
		}
		String[] metaData = tagData.split(splitValue);
		if (metaData.length != 1) {
			for (int i = 1; i < metaData.length; i++) {
				String data = metaData[i].trim().toUpperCase();
				String patternvalue;
				String splitVal;
				if (type.equalsIgnoreCase("domination")) {
					patternvalue = "[a-zA-Z-]+ [0-9]+ [a-zA-Z0-9#]+";
					splitVal = " ";
				} else {
					patternvalue = "[a-zA-Z\\\\s-]+=[0-9]+";
					splitVal = "=";
				}
				Pattern pattern = Pattern.compile(patternvalue);
				if (!data.trim().isEmpty()) {
					Continent continent = new Continent();
					Matcher match = pattern.matcher(data.trim());
					if (!match.matches()) {
						formatError = formatError
								.concat("Invalid Continent Details. " + data.trim() + " is not the correct format.\n");
						formatData = false;
					} else {
						if (visitedContinent.containsKey(data.split(splitVal)[0])) {
							DuplicateError = DuplicateError
									.concat(data.split(splitVal)[0] + " is already defined. Duplicate Entry Found.\n");
							duplicateData = false;
						} else {
							String field = data.split(splitVal)[0];
							int value = Integer.parseInt(data.split(splitVal)[1]);
							continent.setContinentName(field);
							continent.setContinentControlValue(value);
							listOfContinent.add(continent);
							visitedContinent.put(field, data);
						}
					}
				}
			}
			if (duplicateData && formatData) {
				return true;
			} else {
				errorMessage = errorMessage.concat(formatError).concat(DuplicateError);
				return false;
			}
		} else {
			errorMessage = errorMessage.concat("No Continents defined under Countinent tag.\n");
			return false;
		}
	}

	/**
	 * This method is used to validate the list of countries which are present in
	 * the uploaded file and check the correct format.
	 *
	 * @param tagData Value of the country tag
	 * @param type    map type
	 * @return flag to check if entered countries are valid
	 */
	public boolean validateCountries(String tagData, String type) {
		listOfCountries = new ArrayList<Country>();
		HashMap<String, String> visited = new HashMap<String, String>();
		boolean duplicateData = true, formatData = true, continentData = true, adjacentData = true;
		ArrayList<String> neighbourcountries;
		String formatError = new String(), DuplicateError = new String(), continentError = new String(),
				adjacencyError = new String();
		;
		String splitValue;
		if (type.equalsIgnoreCase("domination")) {
			splitValue = "\\r\\n";
		} else {
			splitValue = "\\n";
		}
		String[] countryData = tagData.split(splitValue);
		for (int i = 1; i < countryData.length; i++) {
			String data = countryData[i].trim().toUpperCase();
			String patternvalue;
			String splitVal;
			int value;
			if (type.equalsIgnoreCase("domination")) {
				patternvalue = "[0-9]+ [a-zA-Z_-]+ [0-9]+ [0-9 ]*";
				splitVal = " ";
				value = 1;
			} else {
				patternvalue = "[a-zA-Z\\s-_]+,[0-9]+,[0-9]+,[a-zA-Z\\s-_]+(,[a-zA-Z\\s-_]+)*";
				splitVal = ",";
				value = 0;
			}
			Pattern pattern = Pattern.compile(patternvalue);
			if (!data.trim().isEmpty()) {
				Matcher match = pattern.matcher(data.trim());
				if (!match.matches()) {
					formatError = formatError.concat(
							"Invalid Country Details. " + data.trim() + " is not defined in required format.\n");
					formatData = false;
					continue;
				} else {
					if (visited.containsKey(data.split(splitVal)[value])) {
						DuplicateError = DuplicateError
								.concat(data.split(splitVal)[value] + " is already defined. Duplicate Entry Found.\n");
						duplicateData = false;
						continue;
					} else {
						Country country = new Country();
						String[] countrydetail = data.split(splitVal);
						String name = countrydetail[value];

						boolean continentAvailable = false;
						if (listOfContinent != null && !listOfContinent.isEmpty()) {
							Continent newcontinent;
							if (type.equalsIgnoreCase("domination")) {
								int continentValue = Integer.parseInt(data.split(" ")[2]);

								if (continentValue <= listOfContinent.size()) {
									newcontinent = listOfContinent.get(continentValue - 1);
									country.setPartOfContinent(newcontinent);
									continentAvailable = true;
									continentData = true;
								}
							} else {
								for (Continent continent : listOfContinent) {
									if (continent.getContinentName().equalsIgnoreCase(data.split(",")[3])) {
										newcontinent = continent;
										country.setPartOfContinent(newcontinent);
										continentAvailable = true;
										continentData = true;
									}

								}
							}
						} else {
							continentError = continentError.concat("No Valid continents available.\n");
							continentData = false;
							break;
						}
						if (!continentAvailable) {
							if (type.equalsIgnoreCase("domination")) {
								continentError = continentError
										.concat(data.split(" ")[2] + " is not defined as Continent.\n");
							} else {
								continentError = continentError
										.concat(data.split(",")[3] + " is not defined as Continent.\n");
							}
							continentData = false;
							continue;
						}
						if (!type.equalsIgnoreCase("domination")) {
							neighbourcountries = new ArrayList<>();
							if (countrydetail.length > 4) {
								for (int j = 4; j < countrydetail.length; j++) {
									neighbourcountries.add(countrydetail[j]);
								}
								country.setNeighbourCountries(neighbourcountries);
							} else {
								adjacencyError = adjacencyError
										.concat("!! No Adjacent Country defined for " + name + ".\n");
								adjacentData = false;
								continue;
							}
						}
						country.setCountryName(name);
						listOfCountries.add(country);
						visited.put(name, data);
					}
				}
			}
		}
		if (duplicateData == true && formatData == true && continentData == true && adjacentData == true) {
			return true;
		} else {
			errorMessage = errorMessage.concat(formatError).concat(DuplicateError).concat(continentError)
					.concat(adjacencyError);
			return false;
		}
	}

	/**
	 * This method is used to validate the list of boundary countries which are
	 * present in the uploaded file and check the correct format.
	 *
	 * @param tagData Value of the boundary countries tag
	 * @return flag to check if entered boundary countries are valid
	 */
	public boolean validateBoundaries(String tagData) {
		HashMap<String, String> visited = new HashMap<String, String>();
		boolean duplicatedata = true, formatdata = true, adjacentdata = true, continentdata = true;
		ArrayList<String> adjacentcountries;
		String formaterror = new String(), Duplicateerror = new String(), continenterror = new String(),
				adjacencyerror = new String();
		String[] boundaryData = tagData.split("\\r\\n");
		if (boundaryData.length - 1 == listOfCountries.size()) {
			for (int i = 1; i < boundaryData.length; i++) {
				String data = boundaryData[i].trim();
				Pattern pattern = Pattern.compile("[0-9]+( [0-9]+)*");
				if (!data.trim().isEmpty()) {
					Matcher match = pattern.matcher(data.trim());
					if (!match.matches()) {
						formaterror = formaterror.concat(
								"Invalid Boundary Details. " + data.trim() + " is not defined in required format.\n");
						formatdata = false;
						continue;
					} else {
						if (visited.containsKey(data.split(" ")[0])) {
							Duplicateerror = Duplicateerror
									.concat(data.split(" ")[1] + " is already defined. Duplicate Entry Found .\n");
							duplicatedata = false;
							continue;
						} else {
							String[] countrydetail = data.split(" ");
							int countryIndex = Integer.parseInt(countrydetail[0]);
							String name = listOfCountries.get(countryIndex - 1).getCountryName();
							adjacentcountries = new ArrayList<>();
							if (!(countrydetail.length < 1)) {
								for (int j = 1; j < countrydetail.length; j++) {
									int adjcountryIndex = Integer.parseInt(countrydetail[j]);
									String adjCountryName = listOfCountries.get(adjcountryIndex - 1).getCountryName();
									adjacentcountries.add(adjCountryName);
								}
							} else {
								adjacencyerror = adjacencyerror
										.concat("No Adjacent Country defined for " + name + ".\n");
								adjacentdata = false;
								continue;
							}
							for (int k = 0; k < listOfCountries.size(); k++) {
								if (listOfCountries.get(k).getCountryName() == name) {
									listOfCountries.get(k).setNeighbourCountries(adjacentcountries);
								}
							}
							visited.put(name, data);
						}
					}
				}
			}
		} else {
			formaterror = formaterror.concat(" Invalid Number of Borders ");
			formatdata = false;
		}
		if (duplicatedata == true && formatdata == true && adjacentdata == true && continentdata == true) {
			return true;
		} else {
			errorMessage = errorMessage.concat(formaterror).concat(Duplicateerror).concat(continenterror)
					.concat(adjacencyerror);
			return false;
		}
	}

	/**
	 *
	 * This method validates the map which is loaded and stores the details of valid
	 * data in the form of GameMap object.
	 *
	 * @param fileName Name of file which will be loaded.
	 * @return flag To check if file content is valid.
	 * @throws IOException throws for input output
	 */
	public boolean validateMapConquest(String fileName) throws IOException {

		BufferedReader read = null;
		try {
			read = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			System.out.println("Error File Not found Exception");
		}
		String fileData = "";
		String tagerror = new String();
		try {
			fileData = new String(Files.readAllBytes(Paths.get(fileName)));
		} catch (IOException e) {
			System.out.println("Error Message : " + e.getMessage());
		}
		errorMessage = new String();

		if (!fileData.trim().isEmpty()) {
			gameMap = new GameMap();
			Pattern p = Pattern.compile("\\r\\n\\r\\n");
			String[] result = p.split(fileData);
			ArrayList<String> visitedtag = new ArrayList<String>();
			boolean invalidtag = true, validatecontinentdata = true, validatecountrydata = true, validatemapdata = true;

			for (String tagdetails : result) {
				String tag = tagdetails.split("\\n")[0].trim();
				if (tag.equalsIgnoreCase("[Map]") || tag.equalsIgnoreCase("[Continents]")
						|| tag.equalsIgnoreCase("[Territories]")) {
					if (tag.equalsIgnoreCase("[Map]")) {
						if (!visitedtag.contains(tag)) {
							if (validatemetadata(tagdetails)) {
								visitedtag.add(tag);
							} else
								validatemapdata = false;
						} else {
							errorMessage = errorMessage.concat("Duplicate Entry for [Map] Tag Found.\n");
							validatemapdata = false;
						}
					} else if (tag.equalsIgnoreCase("[Continents]")) {
						if (!visitedtag.contains(tag)) {
							if (validateContinents(tagdetails, "conquest")) {
								visitedtag.add(tag);
							} else
								validatecontinentdata = false;
						} else {
							errorMessage = errorMessage.concat("Duplicate Entry for [Continent] Tag Found.\n");
							validatecontinentdata = false;
						}
					} else if (tag.equalsIgnoreCase("[Territories]")) {
						if (!visitedtag.contains(tag)) {

							if (validateCountries(tagdetails, "conquest")) {
								visitedtag.add(tag);
							} else
								validatecountrydata = false;
						} else {
							errorMessage = errorMessage.concat("Duplicate Entry for [Country] Tag Found.\n");
							validatecountrydata = false;
						}
					}
				} else {
					tagerror = tagerror.concat("Invalid " + tag + " found.\n");
					invalidtag = false;
				}
			}
			if (invalidtag == true && validatemapdata == true && validatecontinentdata == true
					&& validatecountrydata == true) {
				ConnectedGraph connect = new ConnectedGraph();
				adjancencyError = connect.checkCountryAdjacency(listOfCountries, listOfContinent);
				if (adjancencyError.isEmpty()) {
					read.close();
					return true;
				} else {
					errorMessage = "Map have below error.\n";
					errorMessage = errorMessage.concat(adjancencyError);
					read.close();
					return false;
				}
			} else {
				errorMessage = tagerror.concat(errorMessage);
				errorMessage = "Map have below error\n" + errorMessage;
				read.close();
				return false;
			}
		} else {
			errorMessage = "File is Empty.\n";
			read.close();
			return false;
		}

	}

	/**
	 * This method aims validate list of map tag data available in the uploaded file
	 * and checks if they are in correct format.
	 * 
	 * @param tagData Value of the map tag data
	 * @return flag of type boolean determines if the entered map tags are valid
	 */
	public boolean validatemetadata(String tagData) {

		boolean checkdata = true, duplicatedata = true, formatdata = true;
		String formaterror = "", validateerror = "", Duplicateerror = "";
		errorMessage = new String();
		HashMap<String, String> visited = new HashMap<String, String>();
		String[] metaData = tagData.split("\\n");
		for (int i = 1; i < metaData.length; i++) {
			String data = metaData[i].trim().toUpperCase();
			Pattern pattern = Pattern.compile("[a-zA-Z0-9\\s]+=[a-zA-Z0-9\\s\\.]+");
			if (!data.trim().isEmpty()) {
				Matcher match = pattern.matcher(data.trim());
				if (!match.matches()) {
					formaterror = formaterror.concat("!! Invalid Metadata. " + data.trim()
							+ " is not defined in required format. Format required is: Field=Value.\n");
					formatdata = false;
				} else {
					if (visited.containsKey(data.split("=")[0])) {
						Duplicateerror = Duplicateerror
								.concat("!! " + data.split("=")[0] + " is already defined. Duplicate Entry Found as "
										+ visited.get(data.split("=")[0]) + " and " + data + ".\n");
						duplicatedata = false;
					} else {
						String field = data.split("=")[0];
						String value = data.split("=")[1];
						if (field.equalsIgnoreCase("Author")) {
							Pattern check = Pattern.compile("[a-zA-Z0-9\\s]+");
							if (check.matcher(value).matches()) {
								visited.put(field, data);
							} else {
								validateerror = validateerror.concat("!! Author Name is not Defined properly\n");
								checkdata = false;
							}
						} else if (field.equalsIgnoreCase("Image")) {
							Pattern check = Pattern.compile("[a-zA-Z0-9]+[_-]*.BMP");
							if (check.matcher(value).matches()) {
								visited.put(field, data);
							} else {
								validateerror = validateerror.concat(
										"!! Image Name is not Defined properly in proper format. required format- 'imagename.bmp'\n");
								checkdata = false;
							}

						} else if (field.equalsIgnoreCase("Warn")) {

							if (value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("No")) {
								visited.put(field, data);
							} else {
								validateerror = validateerror.concat(
										"!! Warn is not Defined properly, Values should be either Yes or No.\n");
								checkdata = false;
							}

						} else if (field.equalsIgnoreCase("Wrap")) {
							if (value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("No")) {
								visited.put(field, data);
							} else {
								validateerror = validateerror.concat(
										"!! Wrap is not Defined properly, Values should be either Yes or No.\n");
								checkdata = false;
							}

						} else if (field.equalsIgnoreCase("Scroll")) {
							if (value.equalsIgnoreCase("Horizontal") || value.equalsIgnoreCase("Vertical")
									|| value.equalsIgnoreCase("None")) {
								visited.put(field, data);
							} else {
								validateerror = validateerror.concat(
										"!! Scroll is not Defined properly, Values should be either Horizontal or Vertical.\n");
								checkdata = false;
							}
						} else {
							validateerror = validateerror.concat("!! " + data + " is not a Valid Metadata.\n");
							checkdata = false;
						}
					}
				}
			}
		}
		if (duplicatedata == true && checkdata == true && formatdata == true) {
			return true;
		} else {
			errorMessage = errorMessage.concat(validateerror).concat(Duplicateerror).concat(formaterror);
			return false;
		}
	}

	/**
	 *
	 * This method validates the map which is loaded and stores the details of valid
	 * data in the form of GameMap object.
	 *
	 * @param fileName - Name of file which will be loaded.
	 * @return flag - To check if file content is valid.
	 * @throws IOException -throws for input output
	 */
	public boolean validateMapDomination(String fileName) throws IOException {

		BufferedReader read = null;
		try {
			read = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			System.out.println("Error File Not found Exception");
		}
		String fileData = "";
		String tagerror = new String();
		try {
			fileData = new String(Files.readAllBytes(Paths.get(fileName)));
		} catch (IOException e) {
			System.out.println("Error Message : " + e.getMessage());
		}
		String[] requiredData = fileData.split("files");
		fileData = "[files" + requiredData[1];
		errorMessage = new String();

		if (!fileData.trim().isEmpty()) {
			gameMap = new GameMap();
			Pattern p = Pattern.compile("\\r\\n\\r\\n");
			String[] result = p.split(fileData);
			ArrayList<String> visitedtag = new ArrayList<String>();
			boolean invalidtag = true, validatecontinentdata = true, validatecountrydata = true,
					validateboundarydata = true, validatefilesdata = true;

			for (String tagdetails : result) {
				String tag = tagdetails.split("\\r\\n")[0].trim();
				if (tag.equalsIgnoreCase("[continents]") || tag.equalsIgnoreCase("[countries]")
						|| tag.equalsIgnoreCase("[borders]") || tag.equalsIgnoreCase("[files]")) {
					if (tag.equalsIgnoreCase("[files]")) {
						if (!visitedtag.contains(tag)) {
						} else {
							errorMessage = errorMessage.concat("Duplicate Entry for [files] Tag Found.\n");
							validatefilesdata = false;
						}
					}
					if (tag.equalsIgnoreCase("[continents]")) {
						if (!visitedtag.contains(tag)) {
							if (validateContinents(tagdetails, "domination")) {
								visitedtag.add(tag);
							} else
								validatecontinentdata = false;
						} else {
							errorMessage = errorMessage.concat("Duplicate Entry for [continents] Tag Found.\n");
							validatecontinentdata = false;
						}
					} else if (tag.equalsIgnoreCase("[countries]")) {
						if (!visitedtag.contains(tag)) {

							if (validateCountries(tagdetails, "domination")) {
								visitedtag.add(tag);
							} else
								validatecountrydata = false;
						} else {
							errorMessage = errorMessage.concat("Duplicate Entry for [countries] Tag Found.\n");
							validatecountrydata = false;
						}
					} else if (tag.equalsIgnoreCase("[borders]")) {
						if (!visitedtag.contains(tag)) {
							if (validateBoundaries(tagdetails)) {
								visitedtag.add(tag);
							} else
								validateboundarydata = false;
						} else {
							errorMessage = errorMessage.concat("Duplicate Entry for [borders] Tag Found.\n");
							validatecountrydata = false;
						}
					}
				} else {
					tagerror = tagerror.concat("Invalid " + tag + " found.\n");
					invalidtag = false;
				}
			}
			if (invalidtag == true && validatefilesdata == true && validatecontinentdata == true
					&& validatecountrydata == true && validateboundarydata == true) {
				ConnectedGraph connect = new ConnectedGraph();
				adjancencyError = connect.checkCountryAdjacency(listOfCountries, listOfContinent);
				if (adjancencyError.isEmpty()) {
					read.close();
					return true;
				} else {
					errorMessage = "Map have below error.\n";
					errorMessage = errorMessage.concat(adjancencyError);
					read.close();
					return false;
				}
			} else {
				errorMessage = tagerror.concat(errorMessage);
				errorMessage = "Map have below error\n" + errorMessage;
				read.close();
				return false;
			}
		} else {
			errorMessage = "File is Empty.\n";
			read.close();
			return false;
		}
	}

}
