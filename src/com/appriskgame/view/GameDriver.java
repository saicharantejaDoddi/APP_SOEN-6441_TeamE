package com.appriskgame.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.appriskgame.controller.GameSaveLoad;
import com.appriskgame.controller.MapOperations;
import com.appriskgame.controller.Player;
import com.appriskgame.controller.Tournament;
import com.appriskgame.model.GameMap;

/**
 * GameDriver class launches the Risk Game and provided options for the users to
 * create a new map or load an existing map in-order to begin the game.
 * 
 * @author Surya
 * @author Sai
 * @author Sahana
 */
public class GameDriver {

	private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	private static MapOperations loadGameMap;
	private static GameMap gameMap;
	static String workingDir = System.getProperty("user.dir");
	static String mapLocation = workingDir + "/resources/maps/";

	/**
	 * This method sets up a console for the risk game by displaying the create a
	 * map, load a map and exit.
	 * 
	 * @throws Exception - ClassNotFoundException
	 */
	public static void setUp() throws Exception {
		boolean exit = true;
		while (exit) {
			exit = false;
			System.out.flush();
			System.out.println("\nWelcome to Risk Game!");
			System.out.println("\nChoose the below options\n");
			System.out.println("1. Create a Map.");
			System.out.println("2. Load an existing map");
			System.out.println("3. Enter the name of Saved Game Map");
			System.out.println("4. Tournament Mode");
			System.out.println("5. Exit");
			System.out.println("\nPlease enter your choice below:");
			Pattern pattern = Pattern.compile("[0-9]+");
			String option = br.readLine().trim();
			Matcher match = pattern.matcher(option.trim());
			while (!(match.matches())) {
				System.out.println("Please enter a valid option(number) from the game menu!");
				option = br.readLine().trim();
				match = pattern.matcher(option.trim());
			}
			switch (Integer.parseInt(option)) {
			case 1:
				loadGameMap = new MapOperations();
				gameMap = loadGameMap.createFile();
				startGame();
				exit = true;
				break;
			case 2:
				loadGameMap = new MapOperations();
				gameMap = loadGameMap.loadFile();
				if (gameMap.getContinents() == null) {
					System.out.println("Thank You!!");
				} else {
					startGame();
					exit = true;
				}
				break;
			case 3:
				GameSaveLoad gameSaveLoad = new GameSaveLoad();
				gameSaveLoad.readGame();
			case 4:
				Tournament.startTournament();
			case 5:
				exit = false;
				System.out.println("Thank You!!");
				System.exit(0);
				break;
			default:
				System.out.println("Invalid option. Please choose the correct option.");
				exit = true;
				System.out.flush();
			}
		}
	}

	/**
	 * This is the method where the game begins after the creation of new map or
	 * loading of existing map.
	 * 
	 * @throws Exception - NoSuchMethodException
	 */
	private static void startGame() throws Exception {
		System.out.println("Do you want to start the game? (Yes or No)");
		try {
			GameMap createMapGraph = new GameMap();
			String choice = br.readLine().trim();
			while (!(choice.equalsIgnoreCase("Yes") || choice.equalsIgnoreCase("No") || choice == null)) {
				System.err.println("\nPlease enter the choice as either Yes or No:");
				System.out.flush();
				choice = br.readLine().trim();
			}
			if (choice.equalsIgnoreCase("Yes")) {

				boolean loadFlag;
				do {
					loadFlag = false;
					System.out.println("Please enter the Load File Command");
					String command = br.readLine().trim();
					Pattern commandPattern = Pattern.compile("[a-z]+ [a-zA-z0-9]+");
					Matcher matchPattern = commandPattern.matcher(command);
					if (!matchPattern.matches() || command.isEmpty()) {
						System.out.println("\nIncorrect Command");
						loadFlag = true;
					}
					if (!loadFlag) {
						String[] cmdDetails = command.split(" ");
						String cmdType = cmdDetails[0];
						String inputGameMapName = mapLocation + cmdDetails[1] + ".map";
						if (cmdType.equals("loadmap")) {
							loadGameMap = new MapOperations();
							if (loadGameMap.isMapExists(cmdDetails[1])) {
								Player start = new Player();
								createMapGraph = loadGameMap.readGameMap(inputGameMapName);
								if (!createMapGraph.getContinents().isEmpty()) {
									start.gamePlay(createMapGraph);
								} else {
									System.out.println("Incorrect File");
									loadFlag = true;
								}
							} else {
								System.out.println("File Doesn't Exist");
								loadFlag = true;
							}
						} else {
							System.out.println("Incorrect Command");
							loadFlag = true;
						}
					}
				} while (loadFlag);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This is the main method which launches the Game.
	 * 
	 * @param args main arguments
	 * @throws Exception - NoSuchMethodException
	 */
	public static void main(String[] args) throws Exception {
		setUp();
	}
}
