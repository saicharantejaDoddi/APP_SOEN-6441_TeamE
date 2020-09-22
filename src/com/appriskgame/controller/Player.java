package com.appriskgame.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.appriskgame.model.Card;
import com.appriskgame.model.Continent;
import com.appriskgame.model.Country;
import com.appriskgame.model.GameMap;
import com.appriskgame.model.GamePlayer;
import com.appriskgame.strategy.Aggressive;
import com.appriskgame.strategy.Benevolent;
import com.appriskgame.strategy.Cheater;
import com.appriskgame.strategy.Human;
import com.appriskgame.strategy.PlayerStrategy;
import com.appriskgame.strategy.RandomPlayer;

/**
 * This class contains methods which will take the input to add or remove
 * players , Populate countries to players randomly , to allocate armies to the
 * players initially and then place the remaining armies in round robin fashion.
 * Contains methods for Reinforcement, Attack and fortification phase.
 *
 * @author Sahana
 * @author Surya
 * @author Shruthi
 * @author Dolly
 * @author Sai
 */
public class Player {
	public BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	public ArrayList<GamePlayer> playersList = new ArrayList<GamePlayer>();
	public ArrayList<String> playerNames;
	public RoundRobinAllocator roundRobin;
	public MapOperations mapOperations = new MapOperations();
	public GamePlayer gameplayer = new GamePlayer();
	static int TWOPLAYERARMYCOUNT = 40;
	static int THREEPLAYERARMYCOUNT = 35;
	static int FOURPLAYERARMYCOUNT = 30;
	static int FIVEPLAYERARMYCOUNT = 25;
	static int SIXPLAYERARMYCOUNT = 20;
	public static int MINIMUM_REINFORCEMENT_ARMY = 3;
	public static int MINIMUM_NUM_OF_PLAYERS_COUNTRY = 9;

	public static ArrayList<Country> listOfContries = new ArrayList<Country>();
	public static ArrayList<Country> listOfCountriesOfPlayersContinent = new ArrayList<Country>();
	public static ArrayList<Continent> listOfPlayerContinents = new ArrayList<Continent>();

	public boolean doFortification = false;
	private List<Card> cardList = new ArrayList<>();

	/**
	 * This method returns the list of cards player has
	 *
	 * @return cardList list of cards
	 */
	public List<Card> getCardList() {
		return cardList;
	}

	/**
	 * This method sets the list of cards player has
	 *
	 * @param cardList Card List
	 */
	public void setCardList(List<Card> cardList) {
		this.cardList = cardList;
	}

	/**
	 * This method is used to set the player's list.
	 *
	 * @param playersList It is the player's List need to be set
	 */
	public void setPlayerList(ArrayList<GamePlayer> playersList) {
		this.playersList = playersList;
	}

	/**
	 * This method is used to get the player's list.
	 *
	 * @return ArrayList of Players
	 */
	public ArrayList<GamePlayer> getPlayerList() {
		return playersList;
	}

	/**
	 * This method starts the game by obtaining the number of players and taking the
	 * input from the players and initialize them.
	 *
	 * @param gameMap Object of GameMap which consists of Map details
	 * @throws Exception IO Exception
	 */
	public void gamePlay(GameMap gameMap) throws Exception {
		boolean proceed = false, populateFlag = false, mapFlag = true;
		playerNames = new ArrayList<String>();
		do {
			playerNames = playerCreation();
			if (playerNames.size() > 5 || playerNames.size() < 2) {
				System.out.println("Sorry! The numbers of players can be between 2 and 6.\n Current size is "
						+ playerNames.size() + "\nPlayers are : " + playerNames);
				proceed = true;
			} else {
				System.out.println("Great! Let's Play.");
				proceed = false;
			}
		} while (proceed);

		boolean humanOperator = false;
		for (String player : playerNames) {
			String[] data = player.split("-");
			GamePlayer gamePlayers = new GamePlayer();
			gamePlayers.setPlayerName(player);
			gamePlayers.setPlayerType(data[1]);
			if (data[1].equalsIgnoreCase("human")) {
				humanOperator = true;
			}
			playersList.add(gamePlayers);
		}
		gameMap.setPlayers(playersList);

		if (humanOperator) {
			do {
				populateFlag = false;
				System.out.println("Human Player Present in Game");
				System.out.println("Enter Command to Populate Country to Players");
				String input = br.readLine().trim();
				Pattern commandName = Pattern.compile("populatecountries");
				Matcher commandMatch = commandName.matcher(input);
				if (!commandMatch.matches() || input.isEmpty()) {
					System.out.println("\nIncorrect Command");
					populateFlag = true;
				}
				if (!populateFlag) {
					populateCountries(gameMap);
				}

			} while (populateFlag);
			defaultArmiesToPlayer();
			initialArmyAllocation(gameMap);

			do {
				System.out.println("Enter the Command to display Map");
				String mapCommand = br.readLine().trim();
				if (mapCommand.equalsIgnoreCase("showmap")) {
					showMap(gameMap);
					mapFlag = false;
				} else {
					System.out.println("Incorrect Command");
					mapFlag = true;
				}
			} while (mapFlag);
		} else {
			populateCountries(gameMap);
			defaultArmiesToPlayer();
			initialArmyAllocation(gameMap);
			showMap(gameMap);
		}

		roundRobin = new RoundRobinAllocator(playersList);
		System.out.println("Place army Individually");
		while (playersList.get(playersList.size() - 1).getNoOfArmies() > 0) {
			for (int round = 1; round <= playersList.size(); round++) {
				gameplayer = roundRobin.nextTurn();
				System.out.println("Name: " + gameplayer.getPlayerName());
				System.out.println("No of Armies remaining: " + gameplayer.getNoOfArmies());
				PlayerStrategy playerStrategy = null;
				playerStrategy = gameplayer.getPlayerType().equalsIgnoreCase("Aggressive") ? new Aggressive()
						: (gameplayer.getPlayerType().equalsIgnoreCase("Benevolent") ? new Benevolent()
								: (gameplayer.getPlayerType().equalsIgnoreCase("Cheater") ? new Cheater()
										: (gameplayer.getPlayerType().equalsIgnoreCase("Random") ? new RandomPlayer()
												: (gameplayer.getPlayerType().equalsIgnoreCase("Human") ? new Human()
														: null))));
				boolean placeallflag = false;
				if (gameplayer.getPlayerType().equalsIgnoreCase("Human")) {
					System.out.println("As a Human Player, Do you want to place army individually?");
					String choice = br.readLine().trim();
					while (!(choice.equalsIgnoreCase("Yes") || choice.equalsIgnoreCase("No") || choice == null)) {
						System.err.println("\nPlease enter the choice as either Yes or No:");
						choice = br.readLine().trim();
					}
					if (choice.equalsIgnoreCase("Yes")) {

					} else {
						System.out.println("Enter Command for Place all Armies : ");
						String command = br.readLine().trim();
						if (command.equalsIgnoreCase("placeall")) {
							placeallAmry();
							placeallflag = true;
							round = playersList.size();
						}
					}
				}
				if (!placeallflag) {
					playerStrategy.placeArmies(gameMap, gameplayer);
				}
			}
		}
		boolean gameContinue;

		do {
			for (int round = 0; round < playersList.size(); round++) {
				gameplayer = playersList.get(round);
				PlayerStrategy playerStrategy = null;
				playerStrategy = gameplayer.getPlayerType().equalsIgnoreCase("Aggressive") ? new Aggressive()
						: (gameplayer.getPlayerType().equalsIgnoreCase("Benevolent") ? new Benevolent()
								: (gameplayer.getPlayerType().equalsIgnoreCase("Cheater") ? new Cheater()
										: (gameplayer.getPlayerType().equalsIgnoreCase("Random") ? new RandomPlayer()
												: (gameplayer.getPlayerType().equalsIgnoreCase("Human") ? new Human()
														: null))));
				if (gameplayer.getPlayerCountries().size() > 0) {

					String playerName = gameplayer.getPlayerName();
					gameMap.setCurrentPlayer(playerName);
					gameMap.setGamePhase("Reinforcement Phase", "phase");
					gameMap.setActionMsg(
							"** Reinforcement Phase Begins for Player: " + gameplayer.getPlayerName() + " **",
							"action");
					System.out
							.println("** Reinforcement Phase Begins for Player: " + gameplayer.getPlayerName() + " **");
					System.out.println(gameplayer.getPlayerCountries());

					Continent playerContinent = gameplayer.getPlayerCountries().get(0).getPartOfContinent();
					int reInforceAmries = assignReinforcedArmies(gameplayer, playerContinent);
					gameplayer.setNoOfArmies(reInforceAmries);

					gameMap.setDomination(gameMap, "domination");
					while (gameplayer.getNoOfArmies() > 0) {
						playerStrategy.reinforcementPhase(gameplayer, gameMap);
					}
					gameMap.setActionMsg(
							"** Reinforcement Phase Ends for Player: " + gameplayer.getPlayerName() + " **", "action");
					System.out.println("** Reinforcement Phase Ends for Player: " + gameplayer.getPlayerName() + " **");
					System.out.println("Attack Begin");
					gameMap.setGamePhase("Attack Phase", "phase");
					gameMap.setActionMsg("** Attack Phase Begins for Player: " + gameplayer.getPlayerName() + " **",
							"action");
					playerStrategy.attackPhase(gameMap, gameplayer, playersList);
					gameMap.setActionMsg("** Attack Phase Ends for Player: " + gameplayer.getPlayerName() + " **",
							"action");
					System.out.println("Attack Ends");
					gameMap.setDomination(gameMap, "domination");

					System.out
							.println("** Fortification Phase Begins for Player: " + gameplayer.getPlayerName() + " **");
					gameMap.setGamePhase("Fortification Phase", "phase");
					gameMap.setActionMsg(
							"** Fortification Phase Begins for Player: " + gameplayer.getPlayerName() + " **",
							"action");
					playerStrategy.fortificationPhase(gameMap, gameplayer);
					System.out.println("** Fortification Phase Ends for Player: " + gameplayer.getPlayerName() + " **");
					gameMap.setActionMsg(
							"** Fortification Phase Ends for Player: " + gameplayer.getPlayerName() + " **", "action");

					if (gameplayer.getPlayerType().equalsIgnoreCase("human")) {
						System.out.println("Do you want to Save the Game? (Yes/No)");
						String choice = br.readLine().trim();

						while (!(choice.equalsIgnoreCase("Yes") || choice.equalsIgnoreCase("No") || choice == null)) {
							System.err.println("\nPlease enter the choice as either Yes or No:");
							choice = br.readLine().trim();
						}
						if (choice.equalsIgnoreCase("Yes")) {
							do {
								System.out.println("Enter the Command to display Map");
								String mapCommand = "showmap";
								if (mapCommand.equalsIgnoreCase("showmap")) {
									showMap(gameMap);
									mapFlag = false;
								} else {
									System.out.println("Incorrect Command");
									mapFlag = true;
								}
							} while (mapFlag);
							GameSaveLoad gameSaveLoad = new GameSaveLoad();
							gameSaveLoad.saveGame(gameMap);
						}
					}
				}
			}
			gameContinue = true;
		} while (gameContinue);
	}

	/**
	 * This method is used to place army individually to player's country
	 * 
	 * @param gameplayer  Current Player
	 * @param countryName Country Name
	 * @return true if successfully placed else false
	 */
	public boolean placearmyassigned(GamePlayer gameplayer, String countryName) {
		boolean ownCountryFlag = true;
		ArrayList<Country> playerCountries = gameplayer.getPlayerCountries();
		for (int i = 0; i < playerCountries.size(); i++) {
			if (playerCountries.get(i).getCountryName().equalsIgnoreCase(countryName)) {
				if (gameplayer.getNoOfArmies() > 0) {
					playerCountries.get(i).setNoOfArmies(playerCountries.get(i).getNoOfArmies() + 1);
					gameplayer.setNoOfArmies(gameplayer.getNoOfArmies() - 1);
					System.out.println("One Army is placed in " + playerCountries.get(i).getCountryName());
					ownCountryFlag = true;
					break;
				} else {
					System.out.println("All armies are placed.");
					ownCountryFlag = true;
					break;
				}
			} else {
				ownCountryFlag = false;
			}
		}
		if (!ownCountryFlag) {
			System.out.println("Please enter the Country that you Own");
		}
		return ownCountryFlag;
	}

	/**
	 * This method is used show the details of the countries and continents, armies
	 * on each country, ownership of each country.
	 *
	 * @param gameMap Object of GameMap which consists of Map details
	 */
	public void showMap(GameMap gameMap) {
		ArrayList<Country> print = gameMap.getCountries();
		for (Country country : print) {
			System.out.println(country);
		}
	}

	// startup phase methods
	/**
	 * This Method is used to Add and remove players to playerList.
	 *
	 * @return List of Player Names
	 */
	public ArrayList<String> playerCreation() {
		boolean flag;
		do {
			flag = false;
			System.out.println("Enter Command to add or remove player");
			String input = "";
			try {
				input = br.readLine().trim();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			Pattern multiPattern = Pattern.compile("[a-zA-z]+ [-[a-z]+ [a-zA-Z]+ ]*");
			Matcher multiCommandPattern = multiPattern.matcher(input);
			if (!multiCommandPattern.matches() || input.isEmpty()) {
				System.out.println("\nIncorrect Command");
				flag = true;
			}
			if (!flag) {
				ArrayList<String> multiCommand = multipleCommands(input);
				for (int p = 0; p < multiCommand.size(); p++) {
					String data = multiCommand.get(p);
					Pattern commandPattern = Pattern.compile("[a-zA-z]+ -[a-zA-z0-9\\s-]*");
					Pattern commandName = Pattern.compile("gameplayer");
					Matcher commandMatch = commandPattern.matcher(data);
					String[] command = data.split("-");
					Matcher commandNameMatch = commandName.matcher(command[0].trim());
					if (!commandMatch.matches() || input.isEmpty() || !commandNameMatch.matches()) {
						System.out.println("\nIncorrect Command");
						flag = true;
					}
					if (!flag) {
						if (data.contains("-add")) {
							String[] addData = data.split("-add ");
							for (int i = 1; i < addData.length; i++) {
								String[] splitData = addData[i].split(" ");
								String name = splitData[0].trim();
								String strategyType = splitData[1].trim();
								String[] validstrategyType = { "human", "aggressive", "benevolent", "cheater",
										"random" };
								List<String> validstrategyTypeList = Arrays.asList(validstrategyType);
								Pattern namePattern = Pattern.compile("[a-zA-z0-9]+");
								Matcher match = namePattern.matcher(name);
								if (!match.matches() || name.isEmpty()
										|| !validstrategyTypeList.contains(strategyType)) {
									System.out.println("\nPlease enter the correct player name and Strategy Type");
									flag = true;
								}
								if (!flag) {
									playerNames.add(name + "-" + strategyType);
								}
							}
						} else if (data.contains("-remove")) {
							String[] removeData = data.split("-remove ");
							for (int i = 1; i < removeData.length; i++) {
								String name = removeData[i].trim();
								Pattern namePattern = Pattern.compile("[a-zA-z0-9]+");
								Matcher match = namePattern.matcher(name);
								if (!match.matches() || name.isEmpty()) {
									System.out.println("\nPlease enter the correct player name");
									flag = true;
								}
								int check = 0;
								for (String player : playerNames) {
									String[] split = player.split("-");
									if (split[0].equals(name)) {
										playerNames.remove(player);
										check++;
										break;
									}
								}
								if (check == 0) {
									System.out.println(name + "doesn't exist");
								}

							}
						}

					}
				}
				System.out.println("Do you want to add/remove players? Yes/No");
				String choice = null;
				try {
					choice = br.readLine().trim();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				while (!(choice.equalsIgnoreCase("Yes") || choice.equalsIgnoreCase("No") || choice == null)) {
					System.err.println("\nPlease enter the choice as either Yes or No:");
					try {
						choice = br.readLine().trim();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (choice.equalsIgnoreCase("Yes")) {
					flag = true;
				} else {
					flag = false;
				}
			}
		} while (flag);
		return playerNames;
	}

	/**
	 * This method is used to assign countries to the players Random allocation of
	 * countries to players will take place in this method.
	 *
	 * @param gameMap Object of GameMap which consists of Map details
	 */
	public void populateCountries(GameMap gameMap) {
		int countryIndex;
		ArrayList<Country> countrySet = new ArrayList<>(gameMap.getCountrySet().values());
		while (countrySet.size() > 0) {
			for (int i = 0; i < playersList.size(); ++i) {
				if (countrySet.size() > 1) {
					countryIndex = new Random().nextInt(countrySet.size());
					playersList.get(i).addCountry(countrySet.get(countryIndex));
					countrySet.remove(countryIndex);

				} else if (countrySet.size() == 1) {
					playersList.get(i).addCountry(countrySet.get(0));
					countrySet.remove(0);
					break;
				} else {
					break;
				}
			}
		}
	}

	/**
	 * This method is used to allocate Armies to players which depends on the
	 * players count in the game.
	 *
	 */
	public void defaultArmiesToPlayer() {
		playersList.forEach(player -> {
			switch (playersList.size()) {
			case 2:
				player.setNoOfArmies(TWOPLAYERARMYCOUNT);
				break;
			case 3:
				player.setNoOfArmies(THREEPLAYERARMYCOUNT);
				break;
			case 4:
				player.setNoOfArmies(FOURPLAYERARMYCOUNT);
				break;
			case 5:
				player.setNoOfArmies(FIVEPLAYERARMYCOUNT);
				break;
			case 6:
				player.setNoOfArmies(SIXPLAYERARMYCOUNT);
				break;
			}
		});
	}

	/**
	 * This method is used to assign armies to the countries so that each country
	 * will get at least one army according to the game rule.
	 *
	 * @param gameMap Object of mapGraph which consists of map details
	 */
	public void initialArmyAllocation(GameMap gameMap) {

		gameMap.getCountrySet().values().forEach(country -> {
			country.setNoOfArmies(1);
		});

		gameMap.getCountrySet().values().forEach(country -> {
			playersList.forEach(player -> {
				if (player.getPlayerCountries().contains(country)) {
					country.setPlayer(player.getPlayerName());
				}
			});
		});

		playersList.forEach(player -> {
			player.setNoOfArmies(player.getNoOfArmies() - player.getPlayerCountries().size());
		});
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

		for (int i = 1; i < commandArrays.length && suspend == false; i = i + 1) {
			String[] cmdDetails = new String[4];
			String decider = commandArrays[i];

			switch (decider) {
			case "-add":
				cmdDetails[0] = "gameplayer";
				cmdDetails[1] = "-add";
				i = i + 1;
				cmdDetails[2] = commandArrays[i];
				i = i + 1;
				cmdDetails[3] = commandArrays[i];
				String addCoammand = mapOperations.singleCommandOperation(cmdDetails);
				splitCommands.add(addCoammand);
				suspend = false;
				break;
			case "-remove":
				cmdDetails = new String[3];
				cmdDetails[0] = "gameplayer";
				cmdDetails[1] = "-remove";
				i = i + 1;
				cmdDetails[2] = commandArrays[i];
				String removeCommand = mapOperations.singleCommandOperation(cmdDetails);
				splitCommands.add(removeCommand);
				suspend = false;
				break;
			}
		}
		return splitCommands;
	}

	/**
	 * This method is used place all the remaining armies randomly to each player in
	 * a round robin fashion
	 */
	public void placeallAmry() {
		for (int round = 1; round <= playersList.size(); round++) {
			gameplayer = roundRobin.nextTurn();
			while (gameplayer.getNoOfArmies() > 0) {
				int index = new Random().nextInt(gameplayer.getPlayerCountries().size());
				Country selectedCountry = gameplayer.getPlayerCountries().get(index);
				if (gameplayer.getNoOfArmies() > 0) {
					selectedCountry.setNoOfArmies(selectedCountry.getNoOfArmies() + 1);
					gameplayer.setNoOfArmies(gameplayer.getNoOfArmies() - 1);
				} else {
					System.out.println("All armies are placed.\n");
				}
			}
			System.out.println("Player Name: " + gameplayer.getPlayerName());
			System.out.println("Countries: " + gameplayer.getPlayerCountries());
			System.out.println("No of Armies remaining: " + gameplayer.getNoOfArmies());
		}
	}

	// reinforcement Methods

	/**
	 * This method asks the player to be continued with Reinforcement phase. If the
	 * player choose to continue this will call a method assigningReinforcedArmies
	 *
	 * @param player     - player details for reinforcement
	 * @param mapDetails -Object of map
	 * @throws Exception -IOException
	 */
	public void startReinforcement(GamePlayer player, GameMap mapDetails) throws Exception {
		Continent playerContinent = player.getPlayerCountries().get(0).getPartOfContinent();
		int sizeOfPlayerCountries = player.getPlayerCountries().size();
		for (int i = 0; i < sizeOfPlayerCountries; i++) {
			playerContinent = player.getPlayerCountries().get(i).getPartOfContinent();
			if (!listOfPlayerContinents.contains(playerContinent)) {
				listOfPlayerContinents.add(playerContinent);
			}
		}
		for (int i = 0; i < listOfPlayerContinents.size(); i++) {
			listOfCountriesOfPlayersContinent.addAll(listOfPlayerContinents.get(i).getListOfCountries());
		}

		ArrayList<Country> numOfcontries = mapDetails.getCountries();
		for (int i = 0; i < numOfcontries.size(); i++) {
			Country country = numOfcontries.get(i);
			if (country.getPartOfContinent().getContinentName().equalsIgnoreCase(playerContinent.getContinentName())) {
				listOfContries.add(country);
			}
		}
		CardController cardController = new CardController();
		if (!(mapDetails.getMode().equalsIgnoreCase("tournament") || mapDetails.getMode().equalsIgnoreCase("test"))) {
			player.setCurrentPlayer(player);
		}
		int exchangeInCard = cardController.exchangeCards(player);
		int reinforcementArmies = assignReinforcedArmies(player, playerContinent);
		player.setNoOfArmies((reinforcementArmies + exchangeInCard));
	}

	/**
	 *
	 * This method check the army count entered by the user and if it is less than
	 * the available, it assigned to the mentioned country
	 *
	 * @param country     - the country given to players
	 * @param armiesCount - the count of the armies player has
	 * @param player      - Current player object
	 */
	public void userAssignedArmiesToCountries(Country country, int armiesCount, GamePlayer player) {
		if (player.getPlayerCountries().contains(country)) {
			if ((player.getNoOfArmies()) > 0 && player.getNoOfArmies() >= armiesCount) {
				country.setNoOfArmies(country.getNoOfArmies() + armiesCount);
				player.setNoOfArmies(player.getNoOfArmies() - armiesCount);
				System.out.println(country.getCountryName() + " is reinforced with " + armiesCount);
			} else {
				System.out.println("Insufficient number of armies.\n");
			}
		} else {
			System.out.println("This country is not owned by you!\n");
		}
	}

	/**
	 * Based on Reinforcement conditions the player will be allocated with some
	 * armies to assign to countries
	 *
	 * @param player    - The player to whom armies will be allocated to
	 * @param continent - Continent to which the player belongs to
	 * @return armies to be assigned to any country of players choice
	 */
	public int assignReinforcedArmies(GamePlayer player, Continent continent) {
		int contriesPlyerOwns = player.getPlayerCountries().size();
		int reinformentArmiesAssigned;
		if (contriesPlyerOwns >= MINIMUM_NUM_OF_PLAYERS_COUNTRY) {
			reinformentArmiesAssigned = (int) Math.floor(contriesPlyerOwns / 3);
		} else {
			reinformentArmiesAssigned = MINIMUM_REINFORCEMENT_ARMY;
		}
		for (int i = 0; i < listOfPlayerContinents.size(); i++) {
			if (doesPlayerOwnAContinent(player, listOfPlayerContinents.get(i).getListOfCountries()))
				reinformentArmiesAssigned = reinformentArmiesAssigned
						+ listOfPlayerContinents.get(i).getContinentControlValue();
		}
		return reinformentArmiesAssigned;
	}

	/**
	 * This method is to check whether player owns whole continent
	 *
	 * @param player        - The player whose reinforcement turn is on
	 * @param countriesList - The count of countries a continent has
	 * @return True if a player own a continent
	 */
	public boolean doesPlayerOwnAContinent(GamePlayer player, ArrayList<Country> countriesList) {
		boolean flag = true;
		for (int i = 0; i < countriesList.size(); i++) {
			if (!player.getPlayerCountries().contains(countriesList.get(i)))
				flag = false;
		}
		return flag;
	}

	// Attack Phase methods

	/**
	 *
	 * This method checks whether the player is winner or not.
	 *
	 * @param player     Current player
	 * @param mapDetails Map details
	 * @return boolean true if the player is winner else false.
	 */
	public boolean isPlayerWinner(GamePlayer player, GameMap mapDetails) {

		if (mapDetails.getCountries().size() == player.getPlayerCountries().size()) {
			return true;
		}
		return false;
	}

	/**
	 *
	 * This method checks whether this country is present or not.
	 *
	 * @param currentCountry Current country name
	 * @param mapDetails     Game map details.
	 * @return boolean true if the country is present else false.
	 */
	public boolean isCountryPresent(String currentCountry, GameMap mapDetails) {
		for (int i = 0; i < mapDetails.getCountries().size(); i++) {
			if (mapDetails.getCountries().get(i).getCountryName().toString().equalsIgnoreCase(currentCountry)) {
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * This method checks whether the country which is being attacked is present or
	 * not.
	 *
	 * @param player         Current player
	 * @param currentCountry Current country name
	 * @param mapDetails     Game map details
	 * @return true if Attack country is present else false
	 */
	public boolean isCountryAttackPresent(GamePlayer player, String currentCountry, GameMap mapDetails) {
		for (int i = 0; i < mapDetails.getCountries().size(); i++) {
			if (mapDetails.getCountries().get(i).getCountryName().toString().equalsIgnoreCase(currentCountry)) {
				if (mapDetails.getCountries().get(i).getPlayer().equalsIgnoreCase(player.getPlayerName())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 *
	 * This method checks whether the country is adjacent or not.
	 *
	 * @param attackCountryObject Attack country object
	 * @param defenderCountry     Defend country object
	 * @param mapDetails          Game map details
	 * @return true if attacker country is adjacent to defender country else false
	 */
	public boolean isCountryAdjacent(Country attackCountryObject, String defenderCountry, GameMap mapDetails) {
		ArrayList<String> neighbourCountires = attackCountryObject.getNeighbourCountries();
		for (int i = 0; i < neighbourCountires.size(); i++) {
			if (neighbourCountires.get(i).equalsIgnoreCase(defenderCountry)) {
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * This method checks whether the defender dice is possible or not.
	 *
	 * @param DefenderArmies Number of defender armies
	 * @param defenderDices  Number of defender dices
	 * @return true if defender dice is possible else false
	 */
	public boolean isDefenderDicePossible(int DefenderArmies, int defenderDices) {
		if (defenderDices <= 2 && defenderDices <= DefenderArmies) {
			return true;
		}
		return false;
	}

	/**
	 *
	 * This method checks whether the attacker dice is possible or not.
	 *
	 * @param AttackerArmies Number of attacker armies
	 * @param attackDices    Number of attacker dices
	 * @return true if attacker dice is possible else false
	 */
	public boolean isAttackerDicePossible(int AttackerArmies, int attackDices) {

		if (attackDices <= 3 && attackDices <= AttackerArmies - 1) {
			return true;
		}
		return false;
	}

	/**
	 *
	 * This method checks that it is able to move country.
	 *
	 * @param attackCountryObject Attacker country
	 * @param moveNumberOfArmies  Number of armies to move
	 * @return true if able to move else false
	 */
	public boolean ableToMoveArmy(Country attackCountryObject, int moveNumberOfArmies) {
		if (moveNumberOfArmies <= 0) {
			return false;
		} else if (attackCountryObject.getNoOfArmies() - 1 >= moveNumberOfArmies) {
			return true;
		}
		return false;
	}

	/**
	 *
	 * This method checks that whether the attacker is winning or not.
	 *
	 * @param defenderCountryObject Defender country
	 * @return true if attacker wins else false
	 */
	public boolean isAttackerWon(Country defenderCountryObject) {
		if (defenderCountryObject.getNoOfArmies() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * This method removes the player from Game map details
	 *
	 * @param playersList List of players
	 * @param mapDetails  Game map object
	 * @param playerName  Defender player name
	 */
	public void removePlayer(ArrayList<GamePlayer> playersList, GameMap mapDetails, String playerName) {

		for (int i = 0; i < mapDetails.getPlayers().size(); i++) {
			if (mapDetails.getPlayers().get(i).getPlayerName().equalsIgnoreCase(playerName)) {
				if ((mapDetails.getPlayers().get(i).getPlayerCountries().size() == 0)) {
					mapDetails.getPlayers().remove(i);
					break;
				}
			}
		}
	}

	/**
	 *
	 * This method gets the country object.
	 *
	 * @param currentCountry Current country name
	 * @param mapDetails     Game map object
	 * @return Country object
	 */
	public Country getCountryObject(String currentCountry, GameMap mapDetails) {
		Country attackCountryObject = null;
		for (int i = 0; i < mapDetails.getCountries().size(); i++) {
			if (mapDetails.getCountries().get(i).getCountryName().toString().equalsIgnoreCase(currentCountry)) {
				attackCountryObject = mapDetails.getCountries().get(i);
			}
		}
		return attackCountryObject;
	}

	/**
	 *
	 * This method gives the outcome of dices.
	 *
	 * @param noOfDices Number of dices
	 * @return Result of dices
	 */
	public List<Integer> outComesOfDices(int noOfDices) {
		List<Integer> outComes = new ArrayList<Integer>();
		for (int i = 0; i < noOfDices; i++) {
			Random random = new Random();
			int result = random.nextInt(5) + 1;
			outComes.add(result);
		}
		return outComes;
	}

	/**
	 *
	 * This method gives the minimum battles.
	 *
	 * @param attackerDices Attacker dices
	 * @param defenderDices Defender dices
	 * @return Number of battles
	 */
	public int minimumBattles(int attackerDices, int defenderDices) {
		if (attackerDices < defenderDices) {
			return attackerDices;
		} else if (attackerDices > defenderDices) {
			return defenderDices;
		} else {
			return defenderDices;
		}
	}

	/**
	 *
	 * This method gives the reason for failed attacks.
	 *
	 * @param attackerArmies Number of attacker armies
	 * @param attackerDices  Number of attacker dices
	 */
	public void reasonForFailedAttack(int attackerArmies, int attackerDices) {
		if (attackerArmies == 1) {
			System.out.println("Attacking is not possible.As the Attacking Country has only 1 Army");
		} else if (attackerDices > 3) {
			System.out.println("Attacking Dice cannot be more than 3");
		} else {
			System.out.println(attackerArmies - 1 + " Attacking Armies should be more than or equal to Attacking Dices"
					+ attackerDices);
		}
	}

	/**
	 *
	 * This method gives the reason for failed defender.
	 *
	 * @param defenderArmies Number of defender armies
	 * @param defenderDices  Number of defender dices
	 */
	public void reasonForFailedDefender(int defenderArmies, int defenderDices) {
		if (defenderArmies == 0) {
			System.out.println("Defending is not possible.As the Defending Country has 0 Army");
		} else if (defenderArmies > 2) {
			System.out.println("Defending Army should be less than or equal to 2");
		} else {
			System.out.println(defenderArmies + " Defending Armies should be more than or equal to Defending Dices"
					+ defenderDices);
		}
	}

	/**
	 *
	 * This method gives maximum allowable attacker dice.
	 *
	 * @param attackerArmies Number of attacker armies
	 * @return Maximum allowable attacker dice
	 */
	public int maxAllowableAttackerDice(int attackerArmies) {
		if (attackerArmies >= 3) {
			return 3;
		} else {
			return attackerArmies - 1;
		}
	}

	/**
	 *
	 * This method gives the maximum allowable defender dice.
	 *
	 * @param DefenderArmies Number of defender armies
	 * @return Maximum allowable defender dice
	 */
	public int maxAllowableDefenderDice(int DefenderArmies) {
		if (DefenderArmies >= 2) {
			return 2;
		} else {
			return DefenderArmies;
		}
	}

	/**
	 *
	 * This method starts the attack.
	 *
	 * @param attackerDices         Number of attacker dices
	 * @param defenderDices         Number of defender dices
	 * @param attackCountryObject   Attacker country
	 * @param defenderCountryObject Defender country
	 */
	public void attackingStarted(int attackerDices, int defenderDices, Country attackCountryObject,
			Country defenderCountryObject) {
		List<Integer> attackerOutcomes = outComesOfDices(attackerDices);
		List<Integer> defenderOutcomes = outComesOfDices(defenderDices);
		Collections.sort(attackerOutcomes);
		Collections.reverse(attackerOutcomes);
		Collections.sort(defenderOutcomes);
		Collections.reverse(defenderOutcomes);
		int battles = minimumBattles(attackerDices, defenderDices);
		for (int i = 0; i < battles; i++) {
			System.out.println("Battle : " + (i + 1));
			System.out.println("Attacker value is: " + attackerOutcomes.get(i));
			System.out.println("Defender value is: " + defenderOutcomes.get(i));
			if (attackerOutcomes.get(i) > defenderOutcomes.get(i)) {
				System.out.println("Attacker won the battle");
				defenderCountryObject.setNoOfArmies(defenderCountryObject.getNoOfArmies() - 1);
			} else {
				System.out.println("Defender won the battle");
				attackCountryObject.setNoOfArmies(attackCountryObject.getNoOfArmies() - 1);
			}
		}
	}

	/**
	 *
	 * This method checks that army is moved to conquered country or not.
	 *
	 * @param playersList           List of players
	 * @param player                Current player
	 * @param attackCountryObject   Attacker country
	 * @param defenderCountryObject Defender country
	 * @throws IOException Input output Exception
	 */
	public void moveArmyToConquredCountry(ArrayList<GamePlayer> playersList, GamePlayer player,
			Country attackCountryObject, Country defenderCountryObject) throws IOException {
		String choice = "";
		do {
			try {
				System.out.println("Enter the Attack Move command? " + " Maximum allowable Armies to move is: "
						+ (attackCountryObject.getNoOfArmies() - 1));
				String userCommand = br.readLine().trim();
				String[] attackMoveDetails = userCommand.split(" ");
				int moveNumberOfArmies = Integer.parseInt(attackMoveDetails[1]);
				if (checkUserAttackMoveValidation(userCommand)
						&& ableToMoveArmy(attackCountryObject, moveNumberOfArmies)) {
					removeOwnerAddNewOwner(playersList, player, defenderCountryObject.getCountryName());

					defenderCountryObject.setPlayer(attackCountryObject.getPlayer());
					defenderCountryObject.setNoOfArmies(moveNumberOfArmies);
					attackCountryObject.setNoOfArmies(attackCountryObject.getNoOfArmies() - moveNumberOfArmies);
					choice = "";
				} else {
					if (!checkUserAttackMoveValidation(userCommand)) {
						System.out.println("Please enter the Attack Move Command in the below correct Format\n"
								+ "Format :attackmove num[num>0]\n");
					} else if (!ableToMoveArmy(attackCountryObject, moveNumberOfArmies)) {
						System.out.println("It is not possible to move" + moveNumberOfArmies);
					}
					BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
					System.out.println(
							"Do you want to still move the Army? Yes/No[Incase of 'No' By default 1 Army will be moved to the conqured Country");
					choice = br.readLine().trim();
				}
			} catch (Exception ex) {
				System.out.println("Please enter the Attack Move Command in the below correct Format\n"
						+ "Format :attackmove num[num>0]\n");
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				System.out.println(
						"Do you want to still move the Army? Yes/No[Incase of 'No' By default 1 Army will be moved to the conqured Country");
				choice = br.readLine().trim();
			}

			if (choice.equalsIgnoreCase("No")) {
				removeOwnerAddNewOwner(playersList, player, defenderCountryObject.getCountryName());
				defenderCountryObject.setPlayer(attackCountryObject.getPlayer());
				defenderCountryObject.setNoOfArmies(1);
				attackCountryObject.setNoOfArmies(attackCountryObject.getNoOfArmies() - 1);
				choice = "";
			}
		} while (choice.equalsIgnoreCase("Yes"));
	}

	/**
	 * This method checks for allOut.
	 *
	 * @param attackCountryObject   Attacker country
	 * @param defenderCountryObject Defender country
	 * @return true if all out else false
	 */
	public boolean isAllOut(Country attackCountryObject, Country defenderCountryObject) {
		return true;
	}

	/**
	 *
	 * This method checks user validation.
	 *
	 * @param userCommand User input command
	 * @return true if validated successfully else false
	 */
	public boolean checkUserValidation(String userCommand) {
		String[] attackDetails = userCommand.split(" ");
		int attackChoice = attackDetails.length;
		boolean validationOfUserCommand = true;
		switch (attackChoice) {
		case 2:
			validationOfUserCommand = checkNoAttackCommand(attackDetails);
			break;
		case 4:
			validationOfUserCommand = checkAttackCommand(attackDetails);
			break;
		default:
			validationOfUserCommand = false;
			break;
		}
		return validationOfUserCommand;
	}

	/**
	 *
	 * This method checks that it is no attack command.
	 *
	 * @param attackDetails Attacker input
	 * @return true if no attack command else false
	 */
	public boolean checkNoAttackCommand(String[] attackDetails) {
		String firstString = attackDetails[0];
		String secondString = attackDetails[1];
		if (firstString.equalsIgnoreCase("attack") && secondString.equalsIgnoreCase("-noattack")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 *
	 * This method checks that all out command.
	 *
	 * @param attackDetails Attacker input
	 * @return true if command is all out else false
	 */
	public boolean checkAllOutCommand(String[] attackDetails) {
		String firstString = attackDetails[0];
		String fourthString = attackDetails[3];
		if (firstString.equalsIgnoreCase("attack") && fourthString.equalsIgnoreCase("-allout")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 *
	 * This method checks single attack commands.
	 *
	 * @param attackDetails Attacker input
	 * @return true if it is single attack command else false
	 */
	public boolean checkSingleAttackCommand(String[] attackDetails) {
		try {
			String firstString = attackDetails[0];
			int diceValue = Integer.parseInt(attackDetails[3]);
			if (firstString.equalsIgnoreCase("attack") && diceValue >= 1) {
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * This method checks the attack command
	 *
	 * @param attackDetails Attacker input
	 * @return true if attack commands else false
	 */
	public boolean checkAttackCommand(String[] attackDetails) {
		String thridString = attackDetails[3];
		boolean validationOfUserCommand = true;
		if (thridString.equalsIgnoreCase("-allout")) {
			validationOfUserCommand = checkAllOutCommand(attackDetails);
		} else {
			validationOfUserCommand = checkSingleAttackCommand(attackDetails);
		}
		return validationOfUserCommand;
	}

	/**
	 *
	 * This method checks whether the defender command is valid or not.
	 *
	 * @param userCommand Defender input command.
	 * @return true if command is valid else false.
	 */
	public boolean checkUserDefenderValidation(String userCommand) {
		String[] defenderDetails = userCommand.split(" ");
		int defenderChoice = defenderDetails.length;
		boolean validationOfUserCommand = true;
		switch (defenderChoice) {
		case 2:
			validationOfUserCommand = checkDefenderCommand(defenderDetails);
			break;
		default:
			validationOfUserCommand = false;
			break;
		}
		return validationOfUserCommand;
	}

	/**
	 *
	 * This method checks defenders' commands.
	 *
	 * @param defenderDetails defender command details
	 * @return true if command valid else false
	 */
	public boolean checkDefenderCommand(String[] defenderDetails) {
		try {
			String firstString = defenderDetails[0];
			int diceValue = Integer.parseInt(defenderDetails[1]);
			if (firstString.equalsIgnoreCase("defend") && diceValue >= 1) {
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 *
	 * This method checks whether the move is validated or not.
	 *
	 * @param userCommand Attack move command
	 * @return true if it is valid or else false
	 */
	public boolean checkUserAttackMoveValidation(String userCommand) {
		String[] attackMoverDetails = userCommand.split(" ");
		int defenderChoice = attackMoverDetails.length;
		boolean validationOfUserCommand = true;
		switch (defenderChoice) {
		case 2:
			validationOfUserCommand = checkAttackMoveCommand(attackMoverDetails);
			break;
		default:
			validationOfUserCommand = false;
			break;
		}
		return validationOfUserCommand;
	}

	/**
	 * This method checks whether any move has taken place or not.
	 *
	 * @param attackMoverDetails Attack move command
	 * @return true if attack move command is valid else false
	 */
	public boolean checkAttackMoveCommand(String[] attackMoverDetails) {
		try {
			String firstString = attackMoverDetails[0];
			int diceValue = Integer.parseInt(attackMoverDetails[1]);
			if (firstString.equalsIgnoreCase("attackmove") && diceValue >= 1) {
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * This method adds new owner and removes old owner.
	 *
	 * @param playersList List of players
	 * @param player      Current player
	 * @param countryName Country name
	 */
	public void removeOwnerAddNewOwner(ArrayList<GamePlayer> playersList, GamePlayer player, String countryName) {
		// 1 Country owner list is removed with the lost country
		Country conquredCountry = null;
		for (int i = 0; i < playersList.size(); i++) {
			GamePlayer currentPlayer = playersList.get(i);
			for (int j = 0; j < currentPlayer.getPlayerCountries().size(); j++) {
				String currentCountryName = currentPlayer.getPlayerCountries().get(j).getCountryName();
				if (currentCountryName.equalsIgnoreCase(countryName)) {
					conquredCountry = currentPlayer.getPlayerCountries().get(j);
					currentPlayer.getPlayerCountries().remove(j);
					break;
				}
			}
		}
		// 2 Country owner list is updated with the conquered country
		if (conquredCountry != null) {
			player.getPlayerCountries().add(conquredCountry);
		}
	}
	// fortification methods

	/**
	 * This method is called from the Startup phase when the user opts to start the
	 * fortification. It internally calls the moveArmies method once all the
	 * validation with respect to fortification are performed.
	 *
	 * @param player  The player who is doing fortification.
	 * @param gameMap GameMap object
	 * @return true if fortification is possible else false
	 * @throws IOException throws Input-Output exception
	 * 
	 */
	public boolean startGameFortification(GamePlayer player, GameMap gameMap) throws IOException {
		if (player.getPlayerCountries().size() >= 2) {
			return true;
		} else {
			gameMap.setActionMsg("Sorry, Fortification is not possible", "action");
			System.out.println("Sorry, Fortification is not possible if the country owned is less than 2");
			return false;
		}
	}

	/**
	 * This method checks if the provided army count is sufficient for
	 * fortification.
	 *
	 * @param armyCount     Number of armies to move
	 * @param givingCountry Country name where armies are moved from
	 * @return true if the army count is insufficient or else false
	 */
	public boolean isArmyCountSufficient(int armyCount, Country givingCountry) {
		if (armyCount >= givingCountry.getNoOfArmies()) {
			System.out.println(
					"Insufficient armies available, fortification is not possible with asked number of armies.");
			return true;
		}
		if (givingCountry.getNoOfArmies() == 1) {
			System.out.println("Insufficient armies available, " + givingCountry.getCountryName()
					+ " should have more than 1 army to Move");
			return true;
		}
		if (armyCount < 0) {
			System.out.println("Army count should be a positive number");
			return true;
		}
		return false;

	}

	/**
	 * 
	 * This method takes the values for each player from the startFortification
	 * method and does the manipulation of armies and assign the armies
	 *
	 * @param fromCountry The country from where player want to move army
	 * @param toCountry   The country to where player want to move army
	 * @param armiesCount Count of armies player wish to move
	 * @return true if armies are moved else false
	 */
	public boolean moveArmies(Country fromCountry, Country toCountry, int armiesCount) {
		boolean neighbourCountries = false;
		if (fromCountry.getNoOfArmies() > 1 && (fromCountry.getNoOfArmies() - armiesCount) >= 1) {
			for (String country : fromCountry.getNeighbourCountries()) {
				if (country.equalsIgnoreCase(toCountry.getCountryName())) {
					int fromCountryArmy = fromCountry.getNoOfArmies();
					int toCountryArmy = toCountry.getNoOfArmies();
					fromCountry.setNoOfArmies(fromCountryArmy - armiesCount);
					toCountry.setNoOfArmies(toCountryArmy + armiesCount);
					neighbourCountries = true;
					doFortification = false;
					System.out.println("\nArmies successfully moved!");
					System.out.println("\nFortification phase ends!");
					break;
				}
			}
			if (!neighbourCountries) {
				System.out.println("Countries are not adjacent!");
				doFortification = true;
			}
			if (!doFortification) {
				System.out.println("Armies moved from " + fromCountry.getCountryName() + " to "
						+ toCountry.getCountryName() + " successfully!");
			}
		}
		return doFortification;

	}

	/**
	 * This method checks whether attack is possible or not
	 *
	 * @param player Current player
	 * @return true if attack is possible else false
	 */
	public boolean isAttackPossible(GamePlayer player) {
		for (int i = 0; i < player.getPlayerCountries().size(); i++) {
			Country currentCountryObject = player.getPlayerCountries().get(i);
			if (currentCountryObject.getNoOfArmies() > 1) {
				for (int j = 0; j < currentCountryObject.getNeighbourCountriesToAdd().size(); j++) {
					Country defenderCountryObject = currentCountryObject.getNeighbourCountriesToAdd().get(j);
					if (!defenderCountryObject.getPlayer().equalsIgnoreCase(player.getPlayerName())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * This method sets the player name.
	 * 
	 * @param players current players in the game
	 * @return name of the player
	 */
	public ArrayList<String> setplayList(ArrayList<GamePlayer> players) {
		ArrayList<String> playerNames = new ArrayList<String>();
		for (int i = 0; i < players.size(); i++) {
			playerNames.add(players.get(i).getPlayerName());
		}
		return playerNames;
	}

	/**
	 * This method is used for continuing the game.
	 * 
	 * @param gameMapcopy gameMap object
	 * @throws Exception input/output exception
	 */
	public void continueGame(GameMap gameMapcopy) throws Exception {

		GameMap gameMap = gameMapcopy;

		playerNames = new ArrayList<String>();

		playerNames = setplayList(gameMap.getPlayers());
		playersList = gameMap.getPlayers();
		boolean mapFlag = true;
		boolean gameContinue;
		gameMap.attach();

		do {
			for (int round = 0; round < playersList.size(); round++) {
				gameplayer = playersList.get(round);
				PlayerStrategy playerStrategy = null;
				playerStrategy = gameplayer.getPlayerType().equalsIgnoreCase("Aggressive") ? new Aggressive()
						: (gameplayer.getPlayerType().equalsIgnoreCase("Benevolent") ? new Benevolent()
								: (gameplayer.getPlayerType().equalsIgnoreCase("Cheater") ? new Cheater()
										: (gameplayer.getPlayerType().equalsIgnoreCase("Random") ? new RandomPlayer()
												: (gameplayer.getPlayerType().equalsIgnoreCase("Human") ? new Human()
														: null))));
				if (gameplayer.getPlayerCountries().size() > 0) {

					String playerName = gameplayer.getPlayerName();
					gameMap.setCurrentPlayer(playerName);
					gameMap.setGamePhase("Reinforcement Phase", "phase");
					gameMap.setActionMsg(
							"** Reinforcement Phase Begins for Player: " + gameplayer.getPlayerName() + " **",
							"action");
					System.out
							.println("** Reinforcement Phase Begins for Player: " + gameplayer.getPlayerName() + " **");
					System.out.println(gameplayer.getPlayerCountries());

					Continent playerContinent = gameplayer.getPlayerCountries().get(0).getPartOfContinent();
					int reInforceAmries = assignReinforcedArmies(gameplayer, playerContinent);
					gameplayer.setNoOfArmies(reInforceAmries);

					gameMap.setDomination(gameMap, "domination");
					while (gameplayer.getNoOfArmies() > 0) {
						playerStrategy.reinforcementPhase(gameplayer, gameMap);
					}
					gameMap.setActionMsg(
							"** Reinforcement Phase Ends for Player: " + gameplayer.getPlayerName() + " **", "action");
					System.out.println("** Reinforcement Phase Ends for Player: " + gameplayer.getPlayerName() + " **");
					System.out.println("Attack Begin");
					gameMap.setGamePhase("Attack Phase", "phase");
					gameMap.setActionMsg("** Attack Phase Begins for Player: " + gameplayer.getPlayerName() + " **",
							"action");
					playerStrategy.attackPhase(gameMap, gameplayer, playersList);
					gameMap.setActionMsg("** Attack Phase Ends for Player: " + gameplayer.getPlayerName() + " **",
							"action");
					System.out.println("Attack Ends");
					gameMap.setDomination(gameMap, "domination");

					System.out
							.println("** Fortification Phase Begins for Player: " + gameplayer.getPlayerName() + " **");
					gameMap.setGamePhase("Fortification Phase", "phase");
					gameMap.setActionMsg(
							"** Fortification Phase Begins for Player: " + gameplayer.getPlayerName() + " **",
							"action");
					playerStrategy.fortificationPhase(gameMap, gameplayer);
					System.out.println("** Fortification Phase Ends for Player: " + gameplayer.getPlayerName() + " **");
					gameMap.setActionMsg(
							"** Fortification Phase Ends for Player: " + gameplayer.getPlayerName() + " **", "action");

					if (gameplayer.getPlayerType().equalsIgnoreCase("human")) {
						System.out.println("Do you want to Save the Game? (Yes/No)");
						String choice = br.readLine().trim();

						while (!(choice.equalsIgnoreCase("Yes") || choice.equalsIgnoreCase("No") || choice == null)) {
							System.err.println("\nPlease enter the choice as either Yes or No:");
							choice = br.readLine().trim();
						}
						if (choice.equalsIgnoreCase("Yes")) {
							do {
								System.out.println("Enter the Command to display Map");
								String mapCommand = "showmap";
								if (mapCommand.equalsIgnoreCase("showmap")) {
									showMap(gameMap);
									mapFlag = false;
								} else {
									System.out.println("Incorrect Command");
									mapFlag = true;
								}
							} while (mapFlag);
							GameSaveLoad gameSaveLoad = new GameSaveLoad();
							gameSaveLoad.saveGame(gameMap);
						}
					}
				}
			}
			gameContinue = true;
		} while (gameContinue);
	}

	/**
	 * This method gets the adjacent country object for the entered country
	 * 
	 * @param mapGraph           The GameMap object
	 * @param attackerAdjCountry adjacent country name
	 * @return adjacent country object
	 */
	public Country getAdjacentCountry(GameMap mapGraph, String attackerAdjCountry) {
		for (GamePlayer player : mapGraph.getPlayers()) {
			for (Country country : player.getPlayerCountries()) {
				if (country.getCountryName().equalsIgnoreCase(attackerAdjCountry)) {
					return country;
				}
			}
		}
		return null;
	}

	/**
	 * This method returns the player who owns the given country
	 * 
	 * @param mapGraph    The GameMap object
	 * @param countryName country name
	 * @return current player object
	 */
	public GamePlayer getPlayerForCountry(GameMap mapGraph, String countryName) {
		for (GamePlayer player : mapGraph.getPlayers()) {
			for (Country country : player.getPlayerCountries()) {
				if (country.getCountryName().equalsIgnoreCase(countryName)) {
					return player;
				}
			}
		}
		return null;
	}

	/**
	 * This method is used to starts the tournament game mode.
	 * 
	 * @param gameMap gameMap object
	 * @param players players in the game
	 * @param turns   number of turns
	 * @return winner of the game
	 * @throws Exception input/output exception
	 */
	public String gamePlayTournament(GameMap gameMap, ArrayList<String> players, int turns) throws Exception {
		int maxTurns = turns;
		int currentTurn = 0;
		int winnerAnnoced = 0;
		gameMap.setMode("tournament");
		boolean proceed = false, populateFlag = false, mapFlag = true;
		playerNames = new ArrayList<String>();
		do {
			playerNames = players;
			if (playerNames.size() > 5 || playerNames.size() < 2) {
				System.out.println("Sorry! The numbers of players can be between 2 and 6.\n Current size is "
						+ playerNames.size() + "\nPlayers are : " + playerNames);
				proceed = true;
			} else {
				System.out.println("Great! Let's Play.");
				proceed = false;
			}
		} while (proceed);

		boolean humanOperator = false;
		for (String player : playerNames) {
			String[] data = player.split("-");
			GamePlayer gamePlayers = new GamePlayer();
			gamePlayers.setPlayerName(player);
			gamePlayers.setPlayerType(data[1]);
			if (data[1].equalsIgnoreCase("human")) {
				humanOperator = true;
			}
			playersList.add(gamePlayers);
		}
		gameMap.setPlayers(playersList);

		if (humanOperator) {
			do {
				populateFlag = false;
				System.out.println("Human Player Present in Game");
				System.out.println("Enter Command to Populate Country to Players");
				String input = br.readLine().trim();
				Pattern commandName = Pattern.compile("populatecountries");
				Matcher commandMatch = commandName.matcher(input);
				if (!commandMatch.matches() || input.isEmpty()) {
					System.out.println("\nIncorrect Command");
					populateFlag = true;
				}
				if (!populateFlag) {
					populateCountries(gameMap);
				}

			} while (populateFlag);
			defaultArmiesToPlayer();
			initialArmyAllocation(gameMap);

			do {
				System.out.println("Enter the Command to display Map");
				String mapCommand = br.readLine().trim();
				if (mapCommand.equalsIgnoreCase("showmap")) {
					showMap(gameMap);
					mapFlag = false;
				} else {
					System.out.println("Incorrect Command");
					mapFlag = true;
				}
			} while (mapFlag);
		} else {
			populateCountries(gameMap);
			defaultArmiesToPlayer();
			initialArmyAllocation(gameMap);
			showMap(gameMap);
		}

		roundRobin = new RoundRobinAllocator(playersList);
		System.out.println("Place army Individually");
		while (playersList.get(playersList.size() - 1).getNoOfArmies() > 0) {
			for (int round = 1; round <= playersList.size(); round++) {
				gameplayer = roundRobin.nextTurn();
				System.out.println("Name: " + gameplayer.getPlayerName());
				System.out.println("No of Armies remaining: " + gameplayer.getNoOfArmies());
				PlayerStrategy playerStrategy = null;
				playerStrategy = gameplayer.getPlayerType().equalsIgnoreCase("Aggressive") ? new Aggressive()
						: (gameplayer.getPlayerType().equalsIgnoreCase("Benevolent") ? new Benevolent()
								: (gameplayer.getPlayerType().equalsIgnoreCase("Cheater") ? new Cheater()
										: (gameplayer.getPlayerType().equalsIgnoreCase("Random") ? new RandomPlayer()
												: (gameplayer.getPlayerType().equalsIgnoreCase("Human") ? new Human()
														: null))));
				boolean placeallflag = false;
				if (gameplayer.getPlayerType().equalsIgnoreCase("Human")) {
					System.out.println("As a Human Player, Do you want to place army individually?");
					String choice = br.readLine().trim();
					while (!(choice.equalsIgnoreCase("Yes") || choice.equalsIgnoreCase("No") || choice == null)) {
						System.err.println("\nPlease enter the choice as either Yes or No:");
						choice = br.readLine().trim();
					}
					if (choice.equalsIgnoreCase("Yes")) {

					} else {
						System.out.println("Enter Command for Place all Armies : ");
						String command = br.readLine().trim();
						if (command.equalsIgnoreCase("placeall")) {
							placeallAmry();
							placeallflag = true;
							round = playersList.size();
						}
					}
				}
				if (!placeallflag) {
					playerStrategy.placeArmies(gameMap, gameplayer);
				}
			}
		}
		boolean gameContinue;

		do {
			for (int round = 0; round < playersList.size(); round++) {
				gameplayer = playersList.get(round);
				PlayerStrategy playerStrategy = null;
				playerStrategy = gameplayer.getPlayerType().equalsIgnoreCase("Aggressive") ? new Aggressive()
						: (gameplayer.getPlayerType().equalsIgnoreCase("Benevolent") ? new Benevolent()
								: (gameplayer.getPlayerType().equalsIgnoreCase("Cheater") ? new Cheater()
										: (gameplayer.getPlayerType().equalsIgnoreCase("Random") ? new RandomPlayer()
												: (gameplayer.getPlayerType().equalsIgnoreCase("Human") ? new Human()
														: null))));
				if (gameplayer.getPlayerCountries().size() > 0) {

					String playerName = gameplayer.getPlayerName();
					gameMap.setCurrentPlayer(playerName);
					gameMap.setGamePhase("Reinforcement Phase", "phase");
					gameMap.setActionMsg(
							"** Reinforcement Phase Begins for Player: " + gameplayer.getPlayerName() + " **",
							"action");
					System.out
							.println("** Reinforcement Phase Begins for Player: " + gameplayer.getPlayerName() + " **");
					System.out.println(gameplayer.getPlayerCountries());

					Continent playerContinent = gameplayer.getPlayerCountries().get(0).getPartOfContinent();
					int reInforceAmries = assignReinforcedArmies(gameplayer, playerContinent);
					gameplayer.setNoOfArmies(reInforceAmries);

					gameMap.setDomination(gameMap, "domination");
					while (gameplayer.getNoOfArmies() > 0) {
						playerStrategy.reinforcementPhase(gameplayer, gameMap);
					}
					gameMap.setActionMsg(
							"** Reinforcement Phase Ends for Player: " + gameplayer.getPlayerName() + " **", "action");
					System.out.println("** Reinforcement Phase Ends for Player: " + gameplayer.getPlayerName() + " **");
					System.out.println("Attack Begin");
					gameMap.setGamePhase("Attack Phase", "phase");
					gameMap.setActionMsg("** Attack Phase Begins for Player: " + gameplayer.getPlayerName() + " **",
							"action");
					winnerAnnoced = playerStrategy.attackPhase(gameMap, gameplayer, playersList);
					gameMap.setActionMsg("** Attack Phase Ends for Player: " + gameplayer.getPlayerName() + " **",
							"action");
					System.out.println("Attack Ends");
					gameMap.setDomination(gameMap, "domination");

					System.out
							.println("** Fortification Phase Begins for Player: " + gameplayer.getPlayerName() + " **");
					gameMap.setGamePhase("Fortification Phase", "phase");
					gameMap.setActionMsg(
							"** Fortification Phase Begins for Player: " + gameplayer.getPlayerName() + " **",
							"action");
					playerStrategy.fortificationPhase(gameMap, gameplayer);
					System.out.println("** Fortification Phase Ends for Player: " + gameplayer.getPlayerName() + " **");
					gameMap.setActionMsg(
							"** Fortification Phase Ends for Player: " + gameplayer.getPlayerName() + " **", "action");
				}
			}
			gameContinue = true;
			if (winnerAnnoced == 1) {
				return gameplayer.getPlayerName();
			}
			currentTurn = currentTurn + 1;
		} while (gameContinue && currentTurn <= maxTurns && winnerAnnoced == 0);
		return "Draw-Draw";
	}
}