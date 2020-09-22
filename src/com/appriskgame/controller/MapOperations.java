package com.appriskgame.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.appriskgame.model.Continent;
import com.appriskgame.model.Country;
import com.appriskgame.model.GameMap;

/**
 * The MapOperations class is for loading the contents with Continents,
 * respective Countries and also for getting the map details for other classes
 *
 * @author Sai
 * @author Shruthi
 * @author Dolly
 */

public class MapOperations {
	private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	String workingDir = System.getProperty("user.dir");
	String mapLocation = workingDir + "/resources/maps/";
	public GameMap gameMap = new GameMap();
	static int connectableCountries = 0;
	String format = "Domination";

	/**
	 * This Method is to check whether the Map File exists.
	 * 
	 *
	 * @param mapFileName Input Map file Name.
	 * @return true if Map file exist or else false.
	 */
	public boolean isMapExists(String mapFileName) {
		String mapFileNameWithExtention = mapFileName + ".map";
		File mapFolder = new File(mapLocation);
		File[] listFiles = mapFolder.listFiles();
		for (int i = 0; i < listFiles.length; i++) {
			if (mapFileNameWithExtention.equals(listFiles[i].getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Method the get the file format
	 * 
	 * @param mapFileName input file name
	 * @return input data in string
	 * @throws IOException IOException
	 */
	public String getFileForamt(String mapFileName) throws IOException {
		String data = "";
		String mapFormat = "";
		data = new String(Files.readAllBytes(Paths.get(mapFileName)));
		boolean isName = data.contains("name");
		if (isName) {
			mapFormat = "Domination";
		} else {
			mapFormat = "Conquest";
		}
		return mapFormat;
	}

	/**
	 * This Method is to load the contents of Continents, Countries and neighboring
	 * countries.
	 *
	 * @param inputGameMapName FileName to read the Map.
	 * @return Game Map Object.
	 * @throws IOException FileNotFound Exception.
	 */

	public GameMap readGameMap(String inputGameMapName) throws IOException {

		ReadAndWrite readAndWrite = null;
		String mapFormat = getFileForamt(inputGameMapName);
		format = mapFormat;
		MapValidation validate = new MapValidation();

		if (mapFormat.equalsIgnoreCase("Domination")) {
			readAndWrite = new ReadAndWriteDomination();
		} else if (mapFormat.equalsIgnoreCase("Conquest")) {
			readAndWrite = new ReadAndWriteMapConquest();
		}

		MapAdapter mapAdapter = new MapAdapter(mapFormat);

		gameMap = mapAdapter.readGameMap(inputGameMapName, mapFormat);

		boolean res = isConnected(gameMap);
		if (res == true) {
			return gameMap;
		} else {
			System.out.println("Graph Not connected");
			gameMap = null;
			return new GameMap();
		}
	}

	/**
	 * This method is to fill the Continents Details in the Game Map.
	 *
	 * @param ContinentsString String of all Continent details.
	 */
	public void fillContinentsInGameMap(String ContinentsString) {
		String[] continentList = ContinentsString.split("\\r\\n");
		for (int i = 1; i < continentList.length; i++) {
			String[] ContinentDetails = continentList[i].split(" ");
			Continent continent = new Continent();
			continent.setContinentName(ContinentDetails[0]);
			continent.setContinentControlValue(Integer.parseInt(ContinentDetails[1]));
			gameMap.getContinents().add(continent);
		}
	}

	/**
	 * This Method returns the serial number of the given continent.
	 *
	 * @param continentName Name of the Continent for which serial number to be
	 *                      find.
	 * @return serial number of the given continent.
	 */
	public int getContinentNumber(String continentName) {
		int cotinenentNumber = 0;
		for (int i = 0; i < gameMap.getContinents().size()
				&& !gameMap.getContinents().get(i).getContinentName().equalsIgnoreCase(continentName); i++) {
			cotinenentNumber = i + 1;
		}
		int actualContinentNumber = cotinenentNumber + 1;
		return actualContinentNumber;
	}

	/**
	 * This Method is to get the respective ContinentName for a given Continent
	 * Number.
	 *
	 * @param continentNumber Serial number of the given continent for which name to
	 *                        be find.
	 * @return Continent Name.
	 */
	public String getContinentName(int continentNumber) {
		String cotinenentName = "";
		cotinenentName = gameMap.getContinents().get(continentNumber - 1).getContinentName();
		return cotinenentName;
	}

	/**
	 * This Method returns the serial number of the given Country.
	 *
	 * @param countryName Name of the Country for which serial number to be find.
	 * @return serial number of the given Country.
	 */
	public int getCountryNumber(String countryName) {
		int countryNumber = 0;
		for (int i = 0; i < gameMap.getCountries().size()
				&& !gameMap.getCountries().get(i).getCountryName().equalsIgnoreCase(countryName); i++) {
			countryNumber = i + 1;
		}
		int actualCountryNumber = countryNumber + 1;
		return actualCountryNumber;
	}

	/**
	 * This Method is to get the respective countryName for a given Country Number.
	 *
	 * @param countryNumber Serial number of the given Country for which name to be
	 *                      find.
	 * @return Country Name.
	 */
	public String getCountryName(int countryNumber) {
		String countryName = "";
		countryName = gameMap.getCountries().get(countryNumber).getCountryName();
		return countryName;
	}

	/**
	 * This method is to fill the Countries Details in the Game Map.
	 *
	 * @param CountriesString String of all Countries details.
	 */
	public void fillCountriesInGameMap(String CountriesString) {
		String[] countriesList = CountriesString.split("\\r\\n");

		for (int i = 1; i < countriesList.length; i++) {
			String[] countryDetails = countriesList[i].split(" ");
			String continentName = getContinentName(Integer.parseInt(countryDetails[2]));
			int continentNumber = Integer.parseInt(countryDetails[2]);
			Continent continent = gameMap.getContinents().get(continentNumber - 1);
			Country country = new Country();
			country.setCountryName(countryDetails[1]);
			country.setContinentName(continentName);
			country.setPartOfContinent(continent);
			continent.getListOfCountries().add(country);
			gameMap.getCountries().add(country);
		}
	}

	/**
	 * This method is to fill the Neighboring Countries Details in the Game Map.
	 *
	 * @param neighboringCountriesString String of all Neighboring Countries
	 *                                   details.
	 */
	public void fillNeighboringCountriesInGameMap(String neighboringCountriesString) {
		String[] neighbouringCountriesList = neighboringCountriesString.split("\\r\\n");

		for (int i = 1; i < neighbouringCountriesList.length; i++) {
			String[] arrayOfCountries = neighbouringCountriesList[i].split(" ");
			int currentCountryNumber = Integer.parseInt(arrayOfCountries[0]);
			for (int j = 1; j < arrayOfCountries.length; j++) {
				int neighbourCountryNumber = Integer.parseInt(arrayOfCountries[j]) - 1;
				Country neighbourCountry = gameMap.getCountries().get(neighbourCountryNumber);
				Country currentCountry = gameMap.getCountries().get(currentCountryNumber - 1);
				currentCountry.getNeighbourCountriesToAdd().add(neighbourCountry);
				currentCountry.getNeighbourCountries().add(neighbourCountry.getCountryName());
			}
		}
	}

	/**
	 * This Method is to write the Continents, Countries and boundaries details to
	 * the map file.
	 *
	 * @param ouputGameMapName full path of the file to write.
	 * @param mapFileName      File Name of the writing file.
	 * @throws IOException IO
	 */
	public void writeGameMap(String ouputGameMapName, String mapFileName) throws IOException {

		String mapFormat = format;

		try {
			ReadAndWrite readAndWrite = null;

			if (mapFormat.equalsIgnoreCase("Domination")) {
				readAndWrite = new ReadAndWriteDomination();

			}
			if (mapFormat.equalsIgnoreCase("Conquest")) {
				readAndWrite = new ReadAndWriteMapConquest();

			}

			readAndWrite.writeGameMap(ouputGameMapName, mapFileName, gameMap);
		}

		catch (IOException e) {
			System.err.println("File not found exception");
		}
	}

	/**
	 * This Method give standard file tags to the output file.
	 *
	 * @param ouputGameMapName output File Name.
	 * @return String of file Tags.
	 */
	public String getFileTags(String ouputGameMapName) {
		String mapNameDetails = "\r\n\r\nname " + ouputGameMapName + " Map";
		String fileTag = "\r\n\r\n[files]\r\n";
		String pic = "pic " + ouputGameMapName + "_pic.png";
		String fullFormat = mapNameDetails + fileTag + pic;
		return fullFormat;
	}

	/**
	 * This Method will give the continent details in a standard format.
	 *
	 * @return String consists of continent details in a standard format.
	 *
	 */
	public String getContinents() {
		String continentsDetails = "[continents]";
		for (int i = 0; i < gameMap.getContinents().size(); i++) {
			Continent continent = gameMap.getContinents().get(i);
			String continentDetails = continent.getContinentName() + " " + continent.getContinentControlValue() + " "
					+ "#99NoColor";
			continentsDetails = continentsDetails + "\r\n" + continentDetails;
		}
		return continentsDetails;
	}

	/**
	 * This Method will give the continent details in a standard format.
	 *
	 * @return String consists of continent details in a standard format.
	 *
	 */
	public String getCountries() {
		String countriesDetails = "[countries]";
		for (int i = 0; i < gameMap.getCountries().size(); i++) {
			Country country = gameMap.getCountries().get(i);
			String countryDetails = (i + 1) + " " + country.getCountryName() + " "
					+ getContinentNumber(country.getContinentName()) + " " + "99" + " " + "99";
			countriesDetails = countriesDetails + "\r\n" + countryDetails;
		}
		return countriesDetails;
	}

	/**
	 * This Method will give the borders details in a standard format.
	 *
	 * @return String consists of borders details in a standard format.
	 *
	 */
	public String getBoundaries() {
		String boundariesDetails = "[borders]";

		for (int i = 0; i < gameMap.getCountries().size(); i++) {
			String boundaryDetails = i + 1 + " ";
			for (int j = 0; j < gameMap.getCountries().get(i).getNeighbourCountriesToAdd().size(); j++) {
				boundaryDetails = boundaryDetails
						+ getCountryNumber(
								gameMap.getCountries().get(i).getNeighbourCountriesToAdd().get(j).getCountryName())
						+ " ";
			}
			boundariesDetails = boundariesDetails + "\r\n" + boundaryDetails;
		}
		return boundariesDetails;
	}

	/**
	 * This Method adds a Continent and it's Control Value to the Map.
	 *
	 * @param continentName         Continent Name to be Added.
	 * @param continentControlValue Continent Control Value to be Added.
	 */
	public void addContinentToGameMap(String continentName, int continentControlValue) {
		Continent continent = new Continent();
		continent.setContinentName(continentName);
		continent.setContinentControlValue(continentControlValue);
		gameMap.getContinents().add(continent);
	}

	/**
	 * This Method adds a Country to the Map.
	 *
	 * @param countryName   Country Name to be added.
	 * @param continentName Continent Name to which country to be added.
	 */
	public void addCountryToGameMap(String countryName, String continentName) {
		Country country = new Country();
		country.setCountryName(countryName);
		int continentIndex = getContinentNumber(continentName) - 1;
		country.setContinentName(continentName);
		Continent continent = gameMap.getContinents().get(continentIndex);
		country.setPartOfContinent(continent);
		gameMap.getCountries().add(country);
		gameMap.getContinents().get(continentIndex).getListOfCountries().add(country);
	}

	/**
	 * This Method adds a border between two countries to the Map.
	 *
	 * @param countryName         Country Name to which border to be added.
	 * @param neighborCountryName Country Name to which border to be added.
	 */
	public void addNeighborCountryToGameMap(String countryName, String neighborCountryName) {
		Country country = null;
		int countryIndex = getCountryNumber(countryName) - 1;
		country = gameMap.getCountries().get(countryIndex);
		Country neighborCountry = null;
		int neighborCountryIndex = getCountryNumber(neighborCountryName) - 1;
		neighborCountry = gameMap.getCountries().get(neighborCountryIndex);
		country.getNeighbourCountriesToAdd().add(neighborCountry);
		country.getNeighbourCountries().add(neighborCountry.getCountryName());
	}

	/**
	 * This Method removes a country from the Map.
	 *
	 * @param countryName Country Name which needs to be removed from map.
	 */
	public void removeCountryFromGameMap(String countryName) {
		// Get the country Object to be removed
		int removeCountryIndex = getCountryNumber(countryName) - 1;
		Country removeCountry = gameMap.getCountries().get(removeCountryIndex);
		// 1.Remove the country from the continent List
		int continentNumberIndex = getContinentNumber(removeCountry.getContinentName()) - 1;
		// remove country from continent list
		int removeCountryIndexInContinentList = 0;
		for (int i = 0; i < gameMap.getContinents().get(continentNumberIndex).getListOfCountries().size()
				&& !gameMap.getContinents().get(continentNumberIndex).getListOfCountries().get(i).getCountryName()
						.equalsIgnoreCase(countryName); i++) {
			removeCountryIndexInContinentList = i;
		}
		if (gameMap.getContinents().get(continentNumberIndex).getListOfCountries().size() >= 1) {
			gameMap.getContinents().get(continentNumberIndex).getListOfCountries()
					.remove(removeCountryIndexInContinentList);
		}

		// 2.Remove Country from neighbor Country List
		// Neighbor Details
		java.util.List<Country> neighborCountries = gameMap.getCountries().get(removeCountryIndex)
				.getNeighbourCountriesToAdd();
		String removeCountryName = removeCountry.getCountryName();
		for (int i = 0; i < neighborCountries.size(); i++) {
			Country neighborCountry = neighborCountries.get(i);
			int removeneighborIndex = 0;
			for (int j = 0; j < neighborCountry.getNeighbourCountriesToAdd().size() && !neighborCountry
					.getNeighbourCountriesToAdd().get(j).getCountryName().equalsIgnoreCase(removeCountryName); j++) {
				removeneighborIndex = j + 1;
			}
			// remove country from the neighbor country list
			neighborCountry.getNeighbourCountriesToAdd().remove(removeneighborIndex);
			neighborCountry.getNeighbourCountries().remove(removeCountryName);
		}
		// 3. Last remove the country from the country List
		if (gameMap.getCountries().size() >= 1) {
			gameMap.getCountries().remove(removeCountryIndex);
		}

	}

	/**
	 * This Method is to remove the neighboring country for a given country in the
	 * Map.
	 *
	 * @param countryName               Country Name for which neighbor needs to be
	 *                                  removed.
	 * @param neighborRemoveCountryName Country Name for which neighbor needs to be
	 *                                  removed.
	 */
	public void removeNeighborCountryFromGameMap(String countryName, String neighborRemoveCountryName) {
		int desiredCountryIndex = 0;
		int desiredNeighborIndex = 0;
		for (int i = 0; i < gameMap.getCountries().size()
				&& !gameMap.getCountries().get(i).getCountryName().equalsIgnoreCase(countryName); i++) {
			desiredCountryIndex = i + 1;
		}
		java.util.List<Country> neighborCountries = gameMap.getCountries().get(desiredCountryIndex)
				.getNeighbourCountriesToAdd();
		for (int i = 0; i < neighborCountries.size()
				&& !neighborCountries.get(i).getCountryName().equalsIgnoreCase(neighborRemoveCountryName); i++) {
			desiredNeighborIndex = i + 1;
		}
		gameMap.getCountries().get(desiredCountryIndex).getNeighbourCountriesToAdd().remove(desiredNeighborIndex);
		gameMap.getCountries().get(desiredCountryIndex).getNeighbourCountries().remove(neighborRemoveCountryName);
	}

	/**
	 * This Method is to remove a Continent from the Map.
	 *
	 *
	 * @param continentName Continent Name to be removed from Map.
	 */
	public void removeContinentFromGameMap(String continentName) {
		int removeContinentIndex = getContinentNumber(continentName) - 1;
		java.util.List<Country> removeCountries = gameMap.getContinents().get(removeContinentIndex)
				.getListOfCountries();
		ArrayList<String> removeCountriesNames = new ArrayList<String>();
		for (int i = 0; i < removeCountries.size(); i++) {
			removeCountriesNames.add(removeCountries.get(i).getCountryName());
		}
		for (int i = 0; i < removeCountriesNames.size(); i++) {
			removeCountryFromGameMap(removeCountriesNames.get(i));
		}
		gameMap.getContinents().remove(removeContinentIndex);
	}

	/**
	 * This Method is to check whether the Country exists.
	 * 
	 *
	 * @param countryName Country Name to Check.
	 * @return boolean true if exist or else false.
	 */
	public boolean doesCountryExit(String countryName) {
		for (int i = 0; i < gameMap.getCountries().size(); i++) {
			if (gameMap.getCountries().get(i).getCountryName().equalsIgnoreCase(countryName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This Method is to check whether the Continent exists.
	 * 
	 *
	 * @param continentName Continent Name to Check.
	 * @return boolean true if exist or else false.
	 */
	public boolean doesContinentExit(String continentName) {
		for (int i = 0; i < gameMap.getContinents().size(); i++) {
			if (gameMap.getContinents().get(i).getContinentName().equalsIgnoreCase(continentName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This Method display Map details to the user.
	 */
	public void showmapDetails() {
		System.out.println("Continents");
		System.out.println("----------");
		for (int i = 0; i < gameMap.getContinents().size(); i++) {
			System.out.println(gameMap.getContinents().get(i).getContinentName());
		}
		System.out.println();
		System.out.println("Countries");
		System.out.println("----------");
		for (int i = 0; i < gameMap.getCountries().size(); i++) {
			System.out.print(gameMap.getCountries().get(i).getCountryName());
			System.out.println(gameMap.getCountries().get(i).getNeighbourCountries());
		}
		System.out.println();
	}

	/**
	 * This Method is to check whether the Country is Unique.
	 *
	 * @param countryName Country Name to check.
	 * @return true if unique or else false.
	 */
	public boolean isCountryUnique(String countryName) {
		for (int i = 0; i < gameMap.getCountries().size(); i++) {
			if (gameMap.getCountries().get(i).getCountryName().equalsIgnoreCase(countryName)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * This Method is to check whether the Continent is Unique.
	 *
	 * @param continentName Continent Name to Check.
	 * @return true if unique or else false.
	 */
	public boolean isContinentUnique(String continentName) {
		for (int i = 0; i < gameMap.getContinents().size(); i++) {
			if (gameMap.getContinents().get(i).getContinentName().equalsIgnoreCase(continentName)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * This Method is to check whether the Border is Unique.
	 *
	 * @param countryName         Country Name to check.
	 * @param neighborCountryName neighbor Country Name to check.
	 * @return boolean true if unique or else false.
	 */
	public boolean isBorderUnique(String countryName, String neighborCountryName) {
		int desiredCountryIndex = getCountryNumber(countryName) - 1;
		Country currentCountry = gameMap.getCountries().get(desiredCountryIndex);
		java.util.List<Country> neighborCountries = currentCountry.getNeighbourCountriesToAdd();
		for (int i = 0; i < neighborCountries.size(); i++) {
			if (neighborCountries.get(i).getCountryName().equalsIgnoreCase(neighborCountryName)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * This Method is to check whether the Continent rule is Satisfied.
	 * 
	 *
	 * @return true if continent rule is satisfied or else false.
	 */
	public boolean isContinentRuleSatisfied() {
		if (gameMap.getContinents().size() > 2) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * validateMapDetails Validates the data of Countries, Continent, Boundaries and
	 * Map Connectivity
	 * 
	 * @return boolean true if validation is successful or else false.
	 * @throws IOException
	 */
	private boolean validateMapDetails() {
		MapValidation validate = new MapValidation();
		ConnectedGraph connect = new ConnectedGraph();
		String continentsData = getContinents();
		String countriesData = getCountries();
		String boundariesData = getBoundaries();
		ArrayList<Country> listCountries = new ArrayList<Country>();
		ArrayList<Continent> listContinent = new ArrayList<Continent>();
		listContinent.addAll(gameMap.getContinents());
		listCountries.addAll(gameMap.getCountries());
		String adjError = connect.checkCountryAdjacency(listCountries, listContinent);
		connectableCountries = 0;
		boolean res = isConnected(gameMap);
		if (!res) {
			adjError = adjError + "Map is not connected";
		}
		if (validate.validateContinents(continentsData, "domination") == true
				&& validate.validateCountries(countriesData, "domination") == true
				&& validate.validateBoundaries(boundariesData) == true && adjError.isEmpty()) {
			return true;
		} else {
			System.out.println(adjError);
			return false;
		}
	}

	/**
	 * Method to form the single command
	 *
	 * @param cmdDetails command string
	 * @return single command as string
	 */
	public String singleCommandOperation(String cmdDetails[]) {
		String command = "";
		for (int i = 0; i < cmdDetails.length; i++) {
			command = command + cmdDetails[i] + " ";
		}
		return command.trim();
	}

	/**
	 * This method is used to split the full command into single command of list
	 *
	 * @param fullCommand input command with multiple add and remove
	 * @return single command in arrayList
	 */
	public ArrayList<String> multipleCommands(String fullCommand) {

		String[] commandArrays = fullCommand.split(" ");
		boolean suspend = false;
		ArrayList<String> splitCommands = new ArrayList<String>();

		if (commandArrays[0].equalsIgnoreCase("editcontinent") || commandArrays[0].equalsIgnoreCase("editcountry")) {
			for (int i = 1; i < commandArrays.length && suspend == false; i = i + 1) {
				String[] cmdDetails = new String[4];
				String decider = commandArrays[i];

				switch (decider) {
				case "-add":
					cmdDetails[0] = commandArrays[0];
					cmdDetails[1] = "-add";
					i = i + 1;
					cmdDetails[2] = commandArrays[i];
					i = i + 1;
					cmdDetails[3] = commandArrays[i];
					String addCoammand = singleCommandOperation(cmdDetails);
					splitCommands.add(addCoammand);
					suspend = false;
					break;
				case "-remove":
					cmdDetails = new String[3];
					cmdDetails[0] = commandArrays[0];
					cmdDetails[1] = "-remove";
					i = i + 1;
					cmdDetails[2] = commandArrays[i];
					String removeCommand = singleCommandOperation(cmdDetails);
					splitCommands.add(removeCommand);
					suspend = false;
					break;
				}
			}
		}

		else if (commandArrays[0].equalsIgnoreCase("editneighbor")) {
			for (int i = 1; i < commandArrays.length && suspend == false; i = i + 1) {
				String[] cmdDetails = new String[4];
				String decider = commandArrays[i];

				switch (decider) {
				case "-add":
					cmdDetails[0] = "editneighbor";
					cmdDetails[1] = "-add";
					i = i + 1;
					cmdDetails[2] = commandArrays[i];
					i = i + 1;
					cmdDetails[3] = commandArrays[i];
					String addCoammand = singleCommandOperation(cmdDetails);
					splitCommands.add(addCoammand);
					suspend = false;
					break;
				case "-remove":
					cmdDetails = new String[4];
					cmdDetails[0] = "editneighbor";
					cmdDetails[1] = "-remove";
					i = i + 1;
					cmdDetails[2] = commandArrays[i];
					i = i + 1;
					cmdDetails[3] = commandArrays[i];
					String removeCommand = singleCommandOperation(cmdDetails);
					splitCommands.add(removeCommand);
					suspend = false;
					break;
				}
			}
		}

		return splitCommands;
	}

	/**
	 * editMap is to make the changes as per the player's choice. By adding or
	 * removing Continents,Countries and neighboring countries
	 *
	 * @return GameMap object
	 * @throws IOException IO
	 *
	 */
	public GameMap editMap() throws IOException {

		boolean flag = true;
		while (flag) {
			System.out.println("Enter the command : ");
			String command = br.readLine().trim();
			String[] cmdDetails = command.split(" ");
			ArrayList<String> commands = multipleCommands(command);
			String cmdType = cmdDetails[0];

			Pattern namePatternCmd = Pattern.compile("[a-zA-Z-\\s]+");
			Matcher matchCmd = namePatternCmd.matcher(cmdType);
			String[] userValidInputs = { "editcontinent", "editcountry", "editneighbor", "showmap", "validatemap" };
			List<String> userValidInputsList = Arrays.asList(userValidInputs);
			while (!matchCmd.matches() || !userValidInputsList.contains(cmdType)) {
				System.out.println(
						"\nPlease enter Command in right format :\n editcontinent -add continentname continentvalue -remove continentname\n"
								+ " " + "or\n" + " "
								+ "editcountry -add countryname continentname -remove countryname\n" + " " + "or\n"
								+ " "
								+ "editneighbor -add countryname neighborcountryname -remove countryname neighborcountryname\r\n"
								+ " " + "or\n" + "showmap\n" + "or\n" + "validatemap\n");
				command = br.readLine().trim();
				cmdDetails = command.split(" ");
				commands = multipleCommands(command);
				cmdType = cmdDetails[0];
				matchCmd = namePatternCmd.matcher(cmdType);

			}
			if (cmdType.equals("editcontinent")) {
				for (int i = 0; i < commands.size(); i++) {
					String[] cmdDetailsMulti = commands.get(i).split(" ");
					String opsType = "";
					if (cmdDetailsMulti.length > 1) {
						opsType = cmdDetailsMulti[1];
					}
					if (opsType.equals("-add")) {
						if (isContinentUnique(cmdDetailsMulti[2])) {
							addContinentToGameMap(cmdDetailsMulti[2], Integer.parseInt(cmdDetailsMulti[3]));
							flag = false;
						} else {
							System.out
									.println(cmdDetailsMulti[2] + "  " + "Continent is Already Present in the GameMap");
							flag = true;
						}

					} else if (opsType.equals("-remove")) {
						if (isContinentRuleSatisfied()) {
							if (doesContinentExit(cmdDetailsMulti[2])) {
								removeContinentFromGameMap(cmdDetailsMulti[2]);
								flag = false;
							} else {
								System.out
										.println(cmdDetailsMulti[2] + "  " + "Continent is not present in the GameMap");
								flag = true;
							}
						} else {
							System.out.println("There should be atleast 2 Continents in the GameMap");
						}
					}
				}
				if (!flag) {
					System.out.println("Do you want to perform other map operations? Yes/No");
					String choice = br.readLine().trim();
					while (!(choice.equalsIgnoreCase("Yes") || choice.equalsIgnoreCase("No") || choice == null)) {
						System.err.println("\nPlease enter the choice as either Yes or No:");
						choice = br.readLine().trim();
					}

					if (choice.equalsIgnoreCase("Yes")) {
						flag = true;
					} else {
						flag = false;
					}
				}
			} else if (cmdType.equals("editcountry")) {
				for (int i = 0; i < commands.size(); i++) {
					String[] cmdDetailsMulti = commands.get(i).split(" ");
					String opsType = "";
					if (cmdDetailsMulti.length > 1) {
						opsType = cmdDetailsMulti[1];
					}
					if (opsType.equals("-add")) {
						if (doesContinentExit(cmdDetailsMulti[3])) {
							if (isCountryUnique(cmdDetailsMulti[2])) {
								addCountryToGameMap(cmdDetailsMulti[2], cmdDetailsMulti[3]);
								flag = false;
							} else {
								System.out.println(
										cmdDetailsMulti[2] + "  " + "Country is Already Present in the GameMap");
								flag = true;
							}
						} else {
							System.out.println(cmdDetailsMulti[3] + " Continent is not present in the GameMap");
							flag = true;
						}
					} else if (opsType.equals("-remove")) {
						if (cmdDetailsMulti.length == 3) {
							if (doesCountryExit(cmdDetailsMulti[2])) {
								removeCountryFromGameMap(cmdDetailsMulti[2]);
								flag = false;
							} else {
								System.out.println(cmdDetailsMulti[2] + "  " + "Country is not present in the GameMap");
								flag = true;
							}
						}
					}
				}
				if (!flag) {
					System.out.println("Do you want to perform other map operations? Yes/No");
					String choice = br.readLine().trim();
					while (!(choice.equalsIgnoreCase("Yes") || choice.equalsIgnoreCase("No") || choice == null)) {
						System.err.println("\nPlease enter the choice as either Yes or No:");
						choice = br.readLine().trim();
					}

					if (choice.equalsIgnoreCase("Yes")) {
						flag = true;
					} else {
						flag = false;
					}
				}
			} else if (cmdType.equals("editneighbor")) {
				for (int i = 0; i < commands.size(); i++) {
					String[] cmdDetailsMulti = commands.get(i).split(" ");
					String opsType = "";
					if (cmdDetailsMulti.length > 1) {
						opsType = cmdDetailsMulti[1];
					}
					if (opsType.equals("-add")) {
						if (doesCountryExit(cmdDetailsMulti[2])) {
							if (doesCountryExit(cmdDetailsMulti[3])) {
								if (isBorderUnique(cmdDetailsMulti[2], cmdDetailsMulti[3])) {
									addNeighborCountryToGameMap(cmdDetailsMulti[2], cmdDetailsMulti[3]);
									addNeighborCountryToGameMap(cmdDetailsMulti[3], cmdDetailsMulti[2]);
									flag = false;
								} else {
									System.out.println(cmdDetailsMulti[2] + cmdDetailsMulti[3] + "  "
											+ "Boundary is Already present in the GameMap");
									flag = true;
								}
							} else {
								System.out.println(cmdDetailsMulti[3] + "  " + "Country is not present in the GameMap");
								flag = true;
							}
						} else {
							System.out.println(cmdDetailsMulti[2] + "  " + "Country is not present in the GameMap");
							flag = true;
						}

					} else if (opsType.equals("-remove")) {
						if (doesCountryExit(cmdDetailsMulti[2])) {
							if (doesCountryExit(cmdDetailsMulti[3])) {
								removeNeighborCountryFromGameMap(cmdDetailsMulti[2], cmdDetailsMulti[3]);
								// Remove the same in other way
								removeNeighborCountryFromGameMap(cmdDetailsMulti[3], cmdDetailsMulti[2]);
								flag = false;
							} else {
								System.out.println(cmdDetailsMulti[3] + "  " + "Country is not present in the GameMap");
								flag = true;
							}
						} else {
							System.out.println(cmdDetailsMulti[2] + "  " + "Country is not present in the GameMap");
							flag = true;
						}
					}
				}
				if (!flag) {
					System.out.println("Do you want to perform other map operations? Yes/No");
					String choice = br.readLine().trim();
					while (!(choice.equalsIgnoreCase("Yes") || choice.equalsIgnoreCase("No") || choice == null)) {
						System.err.println("\nPlease enter the choice as either Yes or No:");
						choice = br.readLine().trim();
					}
					if (choice.equalsIgnoreCase("Yes")) {
						flag = true;
					} else {
						flag = false;
					}
				}
			} else if (cmdType.equals("showmap")) {
				showmapDetails();
				System.out.println("Do you want to perform other map operations? Yes/No");
				String choice = br.readLine().trim();
				while (!(choice.equalsIgnoreCase("Yes") || choice.equalsIgnoreCase("No") || choice == null)) {
					System.err.println("\nPlease enter the choice as either Yes or No:");
					choice = br.readLine().trim();
				}
				if (choice.equalsIgnoreCase("Yes")) {
					flag = true;
				} else {
					flag = false;
				}
			} else if (cmdType.equals("validatemap")) {
				boolean result = validateMapDetails();
				if (result) {
					System.out.println("Map Validated Successfully");
					System.out.println("Do you want to perform other map operations? Yes/No");
					String choice = br.readLine().trim();
					while (!(choice.equalsIgnoreCase("Yes") || choice.equalsIgnoreCase("No") || choice == null)) {
						System.err.println("\nPlease enter the choice as either Yes or No:");
						choice = br.readLine().trim();
					}
					if (choice.equalsIgnoreCase("Yes")) {
						flag = true;
					} else {
						flag = false;
					}
				} else {
					System.out.println("Map Validation UnSuccessfully, Please Correct the Map");
					flag = true;
				}
			} else {
				System.out.println("Please Enter Map Operation Commands like below.\n Example :");
				System.out.println("editcontinent -add continentname continentvalue -remove continentname");
				System.out.println("editcountry -add countryname continentname -remove countryname");
				System.out.println(
						"editneighbor -add countryname neighborcountryname -remove countryname neighborcountryname");
				System.out.println("showmap");
				System.out.println("Do you want to perform other map operations? Yes/No");
				String choice = br.readLine().trim();
				while (!(choice.equalsIgnoreCase("Yes") || choice.equalsIgnoreCase("No") || choice == null)) {
					System.err.println("\nPlease enter the choice as either Yes or No:");
					choice = br.readLine().trim();
				}
				if (choice.equalsIgnoreCase("Yes")) {
					flag = true;
				} else {
					flag = false;
				}
			}
		}
		System.out.println("Do you want to Save the Map File? Yes/No");
		String choice = br.readLine().trim();
		while (!(choice.equalsIgnoreCase("Yes") || choice.equalsIgnoreCase("No") || choice == null)) {
			System.err.println("\nPlease enter the choice as either Yes or No:");
			choice = br.readLine().trim();
		}
		if (choice.equalsIgnoreCase("Yes")) {
			System.out.println("Enter the command to save the Map File");
			String command = br.readLine().trim();
			String[] cmdDetails = command.split(" ");

			String cmdType = cmdDetails[0];
			if (cmdType.equals("savemap")) {
				if (cmdDetails.length == 2) {
					String fileName = cmdDetails[1];
					String ouputGameMapName = mapLocation + fileName + ".map";
					try {
						writeGameMap(ouputGameMapName, fileName);
					} catch (IOException e) {
						System.out.println("File Not found Exception");
					}
					MapValidation validate = new MapValidation();
					boolean uploadSuccessful = false;
					try {

						String mapFormat = format;

						if (mapFormat.equalsIgnoreCase("Domination")) {
							uploadSuccessful = validate.validateMapDomination(ouputGameMapName);

						}
						if (mapFormat.equalsIgnoreCase("Conquest")) {
							uploadSuccessful = validate.validateMapConquest(ouputGameMapName);

						}

					} catch (IOException e) {
						System.out.println("File Not found Exception");
					}

					boolean res = isConnected(gameMap);
					if (!res) {
						uploadSuccessful = false;
						System.out.println("Map is not connected");
					}
					if (uploadSuccessful) {
						System.out.println("Successfully Saved");
					} else {
						File file = new File(ouputGameMapName);
						file.delete();
						System.out.println(MapValidation.getError());
						System.out.println("File is not saved and exited from edit operation.");
						flag = true;
					}
				} else {
					System.out.println("Incorrect command");
					flag = true;
				}
			} else {
				GameMap map = new GameMap();
				return map;
			}
		}
		return gameMap;
	}

	/**
	 * This Method is used to create the new file.
	 *
	 * @return Game Map with details
	 */
	public GameMap createFile() {
		GameMap map = null;
		try {
			map = editMap();
		} catch (Exception e) {
			System.out.println("Error Message:" + e.getMessage());
		}
		return map;
	}

	/**
	 * This Method is used to load the existing file.
	 *
	 * @return Game Map with details
	 * @throws IOException IO
	 */
	public GameMap loadFile() throws IOException {
		boolean flag = true;
		while (flag) {
			flag = false;
			System.out.println("Enter the command to Edit the existing Map File");
			String command = "";
			try {
				command = br.readLine().trim();
			} catch (IOException e) {
				System.out.println("Error Message:" + e.getMessage());
			}
			String[] cmdDetails = command.split(" ");
			String cmdType = cmdDetails[0];
			GameMap gameMapCheck = null;
			if (cmdType.equals("editmap")) {
				if (cmdDetails.length == 2) {
					String mapFileName = cmdDetails[1];
					if (isMapExists(mapFileName)) {
						String inputGameMapName = mapLocation + mapFileName + ".map";
						try {
							gameMapCheck = readGameMap(inputGameMapName);
						} catch (IOException e) {
							System.out.println("File Not found Exception");
						}
						if (gameMapCheck.getContinents().isEmpty()) {
							flag = true;
							System.out.println("Incorrect File");
						}
					} else {
						flag = true;
						System.out.println("InCorrect File!! \n Do you want to create a map from scratch? Yes/No");
						String choice = br.readLine().trim();
						while (!(choice.equalsIgnoreCase("Yes") || choice.equalsIgnoreCase("No") || choice == null)) {
							System.err.println("\nPlease enter the choice as either Yes or No:");
							choice = br.readLine().trim();
						}
						if (choice.equalsIgnoreCase("Yes")) {
							createFile();
						} else {
							loadFile();
							break;
						}
					}
					if (!flag) {
						System.out.println("Do you want to edit the loaded map? Yes/No");
						String choice = null;
						try {
							choice = br.readLine().trim();
						} catch (IOException e) {
							System.out.println("Error Message:" + e.getMessage());
						}
						while (!(choice.equalsIgnoreCase("Yes") || choice.equalsIgnoreCase("No") || choice == null)) {
							System.err.println("\nPlease enter the choice as either Yes or No:");
							try {
								choice = br.readLine().trim();
							} catch (IOException e) {
								System.out.println("Error Message:" + e.getMessage());
							}
						}
						if (choice.equalsIgnoreCase("Yes")) {
							GameMap map = editMap();
							return map;
						} else {
							return gameMap;
						}
					}
				}
			} else {
				System.out.println("Incorrect Command");
				flag = true;
			}
		}
		return gameMap;
	}

	/**
	 * This method checks for continent connectivity.
	 * 
	 * @param gameMap It is game Map object
	 * @return True if it is connected false if not connected.
	 */
	public boolean isConnected(GameMap gameMap) {

		for (int j = 0; j < gameMap.getContinents().size(); j++) {
			ArrayList<Country> listOfCountries = gameMap.getContinents().get(j).getListOfCountries();
			int[] visited = new int[listOfCountries.size()];
			for (int i = 0; i < listOfCountries.size(); i++) {
				depthFirstSearch(visited, i, listOfCountries);
				visited = new int[listOfCountries.size()];
				if (connectableCountries != listOfCountries.size()) {
					return false;
				} else {

				}
				connectableCountries = 0;
			}
		}

		return true;
	}

	/**
	 * This method is used to depth first search of a graph.
	 * 
	 * @param visited         Node visited value
	 * @param currentindex    Current index value
	 * @param listOfCountries List of countries.
	 */
	public void depthFirstSearch(int[] visited, int currentindex, ArrayList<Country> listOfCountries) {

		if (visited[currentindex] == 1) {
			return;
		}
		visited[currentindex] = 1;

		ArrayList<Integer> list = getNeighbourCountriesIntegerList(
				listOfCountries.get(currentindex).getNeighbourCountriesToAdd(), listOfCountries);

		for (int vert : list) {
			depthFirstSearch(visited, vert, listOfCountries);
		}
		connectableCountries = connectableCountries + 1;

	}

	/**
	 * This method gives neighbor countries' list in integer.
	 * 
	 * @param neighbourCountries List of neighbor countries.
	 * @param listOfCountries    List of countries.
	 * @return List of neighbor countries.
	 */
	public ArrayList<Integer> getNeighbourCountriesIntegerList(List<Country> neighbourCountries,
			ArrayList<Country> listOfCountries) {
		ArrayList<Integer> neighbourCountriesIntegerList = new ArrayList<Integer>();

		for (Integer i = 0; i < neighbourCountries.size(); i++) {
			String countryName = neighbourCountries.get(i).getCountryName();

			for (int j = 0; j < listOfCountries.size(); j++) {
				String checkName = listOfCountries.get(j).getCountryName();

				if (countryName.equalsIgnoreCase(checkName)) {
					neighbourCountriesIntegerList.add(j);
				}
			}
		}
		return neighbourCountriesIntegerList;
	}
}