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
import java.util.HashMap;
import java.util.List;

import com.appriskgame.model.Continent;
import com.appriskgame.model.Country;
import com.appriskgame.model.GameMap;

/**
 * This class is used for read and write of domination file
 * 
 * @author Sai
 * @author Shruthi
 *
 */
public class ReadAndWriteDomination implements ReadAndWrite {

	private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	String workingDir = System.getProperty("user.dir");
	String mapLocation = workingDir + "/resources/maps/";
	public GameMap gameMap = new GameMap();
	static int connectableCountries = 0;

	/**
	 * This Method is to load the contents of Continents, Countries and neighboring
	 * countries.
	 *
	 * @param inputGameMapName FileName to read the Map.
	 * @return Game Map Object.
	 * @throws IOException FileNotFound Exception.
	 */
	public GameMap readGameMap(String inputGameMapName) throws IOException {
		HashMap<String, Country> countrySet = new HashMap<>();
		MapValidation validate = new MapValidation();
		gameMap = new GameMap();
		connectableCountries = 0;
		String GameMapName = inputGameMapName;
		boolean uploadSuccessful = false;
		String data = "";
		uploadSuccessful = validate.validateMapDomination(GameMapName);
		if (uploadSuccessful) {
			try {
				data = new String(Files.readAllBytes(Paths.get(GameMapName)));
				String[] requiredData = data.split("name");
				data = requiredData[1];
			} catch (IOException e) {
				e.printStackTrace();
			}
			String[] formattedData = data.split("\\r\\n\\r\\n");
			fillContinentsInGameMap(formattedData[2]);
			fillCountriesInGameMap(formattedData[3]);
			fillNeighboringCountriesInGameMap(formattedData[4]);
			gameMap.getCountries().forEach(country -> {
				countrySet.put(country.getCountryName(), country);
				gameMap.setCountrySet(countrySet);
			});
			gameMap.setFormat("Domination");
			boolean res = isConnected(gameMap);
			if (res == true) {
				return gameMap;
			} else {
				System.out.println("Graph Not connected");
				gameMap = null;
				return new GameMap();
			}
		} else {
			System.out.println(MapValidation.getError());
			System.out.println("\nPlease rectify all the above mentioned issues.");
		}

		return new GameMap();
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

	/**
	 * This Method is to write the Continents, Countries and boundaries details to
	 * the map file.
	 *
	 * @param ouputGameMapName full path of the file to write.
	 * @param mapFileName      File Name of the writing file.
	 * @throws IOException IO
	 */
	public void writeGameMap(String ouputGameMapName, String mapFileName, GameMap gameMap) throws IOException {
		this.gameMap = gameMap;
		File GameMapName = new File(ouputGameMapName);
		FileWriter fw = new FileWriter(GameMapName);
		BufferedWriter bw = new BufferedWriter(fw);
		String fileData = getFileTags(mapFileName);
		try {
			bw.write(fileData);
			bw.write("\r\n\r\n");
			String continentsData = getContinents();
			bw.write(continentsData);
			bw.write("\r\n\r\n");
			String countriesData = getCountries();
			bw.write(countriesData);
			bw.write("\r\n\r\n");
			String boundariesData = getBoundaries();
			bw.write(boundariesData);
			bw.close();
		} catch (IOException e) {
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
}