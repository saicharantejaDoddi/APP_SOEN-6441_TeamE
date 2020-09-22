package com.appriskgame.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import com.appriskgame.model.GameMap;

/**
 * This Class is used for implementation of Tournament Mode
 * 
 * @author Sai
 */
public class Tournament {

	static int MINIMUMGAME = 1;
	static int MAXIMUMGAME = 5;
	static int MINUMUMTURNS = 10;
	static int MAXIMUMTURNS = 50;
	static int MAXIMUMSTRATEGYCOUNT = 18;
	static int MINIMUMSTRATEGYCOUNT = 17;
	static int MAXIMUMMAPCOUNT = 20;
	static int MINIMUMMAPCOUNT = 19;

	ArrayList<String> maps;
	ArrayList<String> playersStrategies;
	int noOfGames;
	int turns;
	MapOperations loadGameMap;
	String[][] results;
	private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

	ArrayList<String> preDefinedStrategies = new ArrayList<String>() {
		{
			add("aggressive-aggressive");
			add("benevolent-benevolent");
			add("random-random");
			add("cheater-cheater");
		}
	};

	/**
	 * Method used to start the tournament game
	 * 
	 * @throws Exception IO Exception
	 */
	public static void startTournament() throws Exception {
		System.out.println("Enter the Tournament command?");
		Tournament tournament = new Tournament();
		String tournamentDetails = br.readLine().trim();
		String[] tournamentList = tournamentDetails.split(" ");
		if (tournament.validateCommand(tournamentList)) {
			tournament.fillTournamentData(tournamentList[2], tournamentList[4], tournamentList[6], tournamentList[8]);
			if (!tournament.mapsValidation(tournament.maps)) {
				System.out.println("Maps should be Different");
			} else {
				tournament.results = new String[tournament.maps.size()][tournament.noOfGames];
				for (int i = 0; i < tournament.maps.size(); i++) {
					for (int j = 0; j < tournament.noOfGames; j++) {
						tournament.results[i][j] = tournament.startGame(j, tournament.maps.get(i),
								tournament.playersStrategies, tournament.turns);
					}
				}
				tournament.generateReport();
				System.exit(0);
			}
		}
	}

	/**
	 * Method used to validate the tournament command
	 * 
	 * @param tournamentList input command
	 * @return true if command is valid else false
	 */
	@SuppressWarnings("finally")
	public boolean validateCommand(String[] tournamentList) {
		boolean decision = false;
		try {
			String tournament = tournamentList[0];
			String maps = tournamentList[1];
			String stategies = tournamentList[3];
			String game = tournamentList[5];
			String turn = tournamentList[7];
			int games = Integer.parseInt(tournamentList[6]);
			int turns = Integer.parseInt(tournamentList[8]);
			if (tournament.equalsIgnoreCase("tournament") && game.equalsIgnoreCase("-G") && turn.equalsIgnoreCase("-D")
					&& maps.equalsIgnoreCase("-M") && stategies.equalsIgnoreCase("-P")) {
				if (games >= MINIMUMGAME && games <= MAXIMUMGAME) {
					if (turns >= MINUMUMTURNS && turns <= MAXIMUMTURNS) {
						decision = true;
						return decision;
					} else {
						System.out.println("Invalid Turns [10 to 50]");
						decision = false;
						return decision;
					}
				} else {
					System.out.println("Invalid Number of Games [1 to 5]");
					decision = false;
					return decision;
				}
			} else {
				System.out.println(
						"Invalid Command [Correct Syntax:tournament -M listofmapfiles -P listofplayerstrategies -G numberofgames -D maxnumberofturns]");
				decision = false;
				return decision;
			}
		} catch (Exception ex) {
			System.out.println(
					"Invalid Command [Correct Syntax:tournament -M listofmapfiles[1 to 5 different maps] -P listofplayerstrategies -G numberofgames -D maxnumberofturns  ]");
			decision = false;
		} finally {
			return decision;
		}
	}

	/**
	 * Method to generate the tournament game report
	 */
	public void generateReport() {
		System.out.print("                      ");
		printHeaders();
		printReport();
	}

	/**
	 * Method to print the header of report
	 */
	public void printHeaders() {
		for (int i = 0; i < this.noOfGames; i++) {
			System.out.print(getFormattedGameNumber(i + 1));
		}
	}

	/**
	 * Method to print the report
	 */
	public void printReport() {
		System.out.println();
		for (int i = 0; i < this.maps.size(); i++) {
			System.out.print(getFormattedMapName(this.maps.get(i)));
			for (int j = 0; j < this.noOfGames; j++) {
				System.out.print(getFormattedStrategry(this.results[i][j]));
			}
			System.out.println();
		}
	}

	/**
	 * Method to get the formatted strategy
	 * 
	 * @param strategy current strategy
	 * @return string of formatted strategy
	 */
	public String getFormattedStrategry(String strategy) {
		int currentLength = strategy.length();
		String formattedStrategy = " " + strategy;
		for (int i = currentLength; i < MAXIMUMSTRATEGYCOUNT; i++) {
			formattedStrategy = formattedStrategy + " ";
			if (i == MINIMUMSTRATEGYCOUNT) {
				formattedStrategy = formattedStrategy + " ";
			}
		}
		return formattedStrategy;
	}

	/**
	 * Method to get the formatted map name
	 * 
	 * @param mapName map name
	 * @return formatted map name
	 */
	public String getFormattedMapName(String mapName) {
		int currentLength = mapName.length();
		String formattedMapName = " " + mapName;
		for (int i = currentLength; i < MAXIMUMMAPCOUNT; i++) {
			formattedMapName = formattedMapName + " ";
			if (i == MINIMUMMAPCOUNT) {
				formattedMapName = formattedMapName + " ";
			}
		}
		return formattedMapName;
	}

	/**
	 * Method to get the formatted game number
	 * 
	 * @param number game number
	 * @return formatted game number
	 */
	public String getFormattedGameNumber(int number) {
		String formattedGame = " " + "Game " + number + "             ";
		return formattedGame;
	}

	/**
	 * Method to start the individual tournament game
	 * 
	 * @param gameNumber       game number
	 * @param mapName          map name
	 * @param playerStrategies player strategy
	 * @param turns            number of turns
	 * @return result of the game
	 * @throws Exception IO Exception
	 */
	public String startGame(int gameNumber, String mapName, ArrayList<String> playerStrategies, int turns)
			throws Exception {
		String workingDir = System.getProperty("user.dir");
		String mapLocation = workingDir + "/resources/maps/";
		GameMap createMapGraph = new GameMap();
		String inputGameMapName = mapLocation + mapName + ".map";
		loadGameMap = new MapOperations();
		if (loadGameMap.isMapExists(mapName)) {
			createMapGraph = loadGameMap.readGameMap(inputGameMapName);
			if (!createMapGraph.getContinents().isEmpty()) {
				for (int i = 0; i < playerStrategies.size(); i++) {
					if (preDefinedStrategies.contains(playerStrategies.get(i))) {
					} else {

						return "Invalid Strategy";
					}
				}
				return getGameResult(gameNumber, inputGameMapName, playerStrategies, turns);
			} else {
				return "Incorrect Map";
			}
		} else {
			return "File Not Found";
		}
	}

	/**
	 * Method to get the tournament game result
	 * 
	 * @param gameNumber       game number
	 * @param mapName          map name
	 * @param playerStrategies player strategy
	 * @param turns            number of turns
	 * @return string with tournament result
	 * @throws Exception IO Exception
	 */
	public String getGameResult(int gameNumber, String mapName, ArrayList<String> playerStrategies, int turns)
			throws Exception {
		Player p = new Player();
		MapOperations mp = new MapOperations();
		GameMap gm = mp.readGameMap(mapName);
		gm.setMode("tournament");
		String result = p.gamePlayTournament(gm, playerStrategies, turns);
		String[] formattedResult = result.split("-");
		return formattedResult[1];
	}

	/**
	 * Method to validate the game map
	 * 
	 * @param maps list of game map
	 * @return true if valid game map else false
	 */
	public boolean mapsValidation(List<String> maps) {
		List<String> uniqueMaps = new ArrayList<String>();
		for (int i = 0; i < maps.size(); i++) {
			if (uniqueMaps.contains(maps.get(i).toString())) {
				return false;
			}
			uniqueMaps.add(maps.get(i).toString());
		}
		return true;
	}

	/**
	 * Method to fill the tournament data
	 * 
	 * @param maps          map name
	 * @param Strategies    strategy
	 * @param numberOfGames number of games
	 * @param turns         number of turns
	 */
	public void fillTournamentData(String maps, String Strategies, String numberOfGames, String turns) {
		this.maps = getMaps(maps);
		this.playersStrategies = getPlayersStrategies(Strategies);
		this.noOfGames = getnoOfGames(numberOfGames);
		this.turns = getTurns(turns);
	}

	/**
	 * Method to get number of turns
	 * 
	 * @param turns number of turns
	 * @return integer turns
	 */
	public int getTurns(String turns) {
		return Integer.parseInt(turns.trim());
	}

	/**
	 * Method to get number of games
	 * 
	 * @param noOfGames string number of games
	 * @return integer number of games
	 */
	public int getnoOfGames(String noOfGames) {
		return Integer.parseInt(noOfGames.trim());
	}

	/**
	 * Method to get list of player strategies
	 * 
	 * @param playersStrategy player strategy
	 * @return list of player strategies
	 */
	public ArrayList<String> getPlayersStrategies(String playersStrategy) {
		String[] playersStrategyList = playersStrategy.split(",");
		ArrayList<String> listOfStrategies = new ArrayList<String>();
		for (int i = 0; i < playersStrategyList.length; i++) {
			listOfStrategies.add(playersStrategyList[i] + "-" + playersStrategyList[i]);
		}
		return listOfStrategies;
	}

	/**
	 * Method to get the maps
	 * 
	 * @param maps map names
	 * @return List of maps
	 */
	public ArrayList<String> getMaps(String maps) {
		String[] mapsList = maps.split(",");
		ArrayList<String> listOfMaps = new ArrayList<String>();
		for (int i = 0; i < mapsList.length; i++) {
			listOfMaps.add(mapsList[i]);
		}
		return listOfMaps;
	}

}
