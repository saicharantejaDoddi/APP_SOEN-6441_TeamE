package com.appriskgame.strategy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.appriskgame.controller.CardController;
import com.appriskgame.controller.Player;
import com.appriskgame.model.Country;
import com.appriskgame.model.GameMap;
import com.appriskgame.model.GamePlayer;

/**
 * This class is based on the normal human player for reinforcement, attack and
 * fortification phase.
 * 
 * @author surya
 */
public class Human implements PlayerStrategy {
	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	Player playerController;
	public static String playersChoice;
	public static List<String> playersChoiceList = new ArrayList<String>();

	/**
	 * This method is used to place the remaining armies to countries
	 */
	@Override
	public void placeArmies(GameMap gameMap, GamePlayer player) throws IOException {
		playerController = new Player();
		boolean placeArmyFlag = true;
		do {
			placeArmyFlag = false;
			System.out.println("Enter Command to place Army to Country");
			String input = br.readLine().trim();
			String[] data = input.split(" ");
			Pattern commandName = Pattern.compile("placearmy");
			Matcher commandMatch = commandName.matcher(data[0]);
			if (!commandMatch.matches() || input.isEmpty()) {
				System.out.println("\nIncorrect Command");
				placeArmyFlag = true;
			}
			if (!placeArmyFlag) {
				placeArmyFlag = !playerController.placearmyassigned(player, data[1]);
			}
		} while (placeArmyFlag);
	}

	/**
	 * This method is used to reinforce armies
	 */
	@Override
	public void reinforcementPhase(GamePlayer player, GameMap gameMap) throws Exception {
		playerController = new Player();
		playerController.startReinforcement(player, gameMap);

		while (player.getNoOfArmies() > 0) {
			System.out.println(" Player Name :" + player.getPlayerName());
			gameMap.setActionMsg("Armies available for Reinforcement: " + player.getNoOfArmies(), "action");
			System.out.println("Armies available for Reinforcement: " + player.getNoOfArmies());
			gameMap.setActionMsg("User entering reinforce Command", "action");
			System.out.println(
					"Please enter the country and number of armies to reinforcein the format: reinforce countryname num");
			playersChoice = br.readLine().trim();
			playersChoiceList = Arrays.asList(playersChoice.split(" "));
			if (!(playersChoiceList.size() == 3)) {
				System.out.println("Please enter the right format like : reinforce countryname num");
				gameMap.setActionMsg("Incorrect Command", "action");
				playersChoice = br.readLine().trim();
				playersChoiceList = Arrays.asList(playersChoice.split(" "));
			}
			String strreinforce = playersChoiceList.get(0);
			String countryName = playersChoiceList.get(1);
			String armyCount = playersChoiceList.get(2);
			Country countryNameObject = new Country();

			// Saving the players country in an object, so that all the country details will
			// be taken further
			for (Country country : player.getPlayerCountries()) {
				if (country.getCountryName().equalsIgnoreCase(countryName)) {
					countryNameObject = country;
				}
			}
			Pattern namePattern2 = Pattern.compile("reinforce");
			Matcher match2 = namePattern2.matcher(strreinforce);
			Pattern namePattern1 = Pattern.compile("[a-zA-Z-\\s]+");
			Matcher match1 = namePattern1.matcher(countryName);
			Pattern numberPattern = Pattern.compile("[0-9]+");
			Matcher match = numberPattern.matcher(armyCount);

			while (!match.matches() || armyCount.isEmpty() || !player.getPlayerCountries().contains(countryNameObject)
					|| !match1.matches() || !match2.matches()) {
				if (!player.getPlayerCountries().contains(countryNameObject) || !match1.matches()) {
					System.out.println("Please enter the country that you own in right format");
					playersChoice = br.readLine().trim();
					playersChoiceList = Arrays.asList(playersChoice.split(" "));
					if (!(playersChoiceList.size() == 3)) {
						System.out.println("Please enter the right format like : reinforce countryname num");
						playersChoice = br.readLine().trim();
						playersChoiceList = Arrays.asList(playersChoice.split(" "));
					}
					strreinforce = playersChoiceList.get(0);
					countryName = playersChoiceList.get(1);
					armyCount = playersChoiceList.get(2);
					for (Country country : player.getPlayerCountries()) {
						if (country.getCountryName().equalsIgnoreCase(countryName)) {
							countryNameObject = country;
						}
					}

					match1 = namePattern1.matcher(countryName);
					match2 = namePattern2.matcher(strreinforce);
					match = numberPattern.matcher(armyCount);
				}

				if (!match.matches() || armyCount.isEmpty()) {
					gameMap.setActionMsg("Incorrect Command", "action");
					System.out.println("\nPlease enter the correct army count in right format");
					playersChoice = br.readLine().trim();
					playersChoiceList = Arrays.asList(playersChoice.split(" "));
					if (!(playersChoiceList.size() == 3)) {
						gameMap.setActionMsg("Incorrect Command", "action");
						System.out.println("Please enter the right format like : reinforce countryname num");
						playersChoice = br.readLine().trim();
						playersChoiceList = Arrays.asList(playersChoice.split(" "));
					}

					countryName = playersChoiceList.get(1);
					armyCount = playersChoiceList.get(2);
					match = numberPattern.matcher(armyCount);
					for (Country country : player.getPlayerCountries()) {
						if (country.getCountryName().equalsIgnoreCase(countryName)) {
							countryNameObject = country;
						}
					}

					match1 = namePattern1.matcher(countryName);
					match2 = namePattern2.matcher(strreinforce);
					match = numberPattern.matcher(armyCount);
				}
				if (!match2.matches()) {
					System.out.println("\nPlease enter the right format like : reinforce countryname num");
					playersChoice = br.readLine().trim();
					playersChoiceList = Arrays.asList(playersChoice.split(" "));
					if (!(playersChoiceList.size() == 3)) {
						System.out.println("Please enter the right format like : reinforce countryname num");
						playersChoice = br.readLine().trim();
						playersChoiceList = Arrays.asList(playersChoice.split(" "));
					}
					strreinforce = playersChoiceList.get(0);
					countryName = playersChoiceList.get(1);
					armyCount = playersChoiceList.get(2);

					for (Country country : player.getPlayerCountries()) {

						if (country.getCountryName().equalsIgnoreCase(countryName)) {
							countryNameObject = country;
						}

					}
					match1 = namePattern1.matcher(countryName);
					match2 = namePattern2.matcher(strreinforce);
					match = numberPattern.matcher(armyCount);
				}
			}

			int numOfarmies = Integer.parseInt(armyCount);
			for (Country country : player.getPlayerCountries()) {
				if (country.getCountryName().equalsIgnoreCase(countryName)) {
					playerController.userAssignedArmiesToCountries(country, numOfarmies, player);
				}
			}
		}
	}

	/**
	 * This method is used to perform attack
	 */
	@Override
	public int attackPhase(GameMap mapDetails, GamePlayer player, ArrayList<GamePlayer> playersList)
			throws IOException {
		playerController = new Player();
		boolean gameContinue;

		do {
			boolean errorOccured = false;
			String errorDetails = "";
			gameContinue = false;
			playerController.showMap(mapDetails);
			boolean isAttackPossible = playerController.isAttackPossible(player);
			String userCommand = "";
			if (isAttackPossible) {
				mapDetails.setActionMsg("Player Entering Attack Commands", "action");
				System.out.println("Enter the Attacker command?\n" + "Player Name : " + player.getPlayerName());
				userCommand = br.readLine().trim();
			} else {
				mapDetails.setActionMsg("No Attack", "action");
				userCommand = "attack -noattack";
			}

			if (playerController.checkUserValidation(userCommand)) {
				String[] attackDetails = userCommand.split(" ");
				String attackCountry = attackDetails[1];
				if (attackCountry.equalsIgnoreCase("-noattack")) {
					break;
				}
				String defenderCountry = attackDetails[2];
				String decision = attackDetails[3];
				boolean attackerCountryPresent = playerController.isCountryAttackPresent(player, attackCountry,
						mapDetails);
				boolean defenderCountryPresent = playerController.isCountryPresent(defenderCountry, mapDetails);
				Country attackCountryObject = null;
				Country defenderCountryObject = null;
				if (!attackerCountryPresent) {
					errorDetails = attackCountry + " is not owned by the current player" + "\n";
					mapDetails.setActionMsg("Error : " + errorDetails, "action");
					errorOccured = true;
				}
				if (!defenderCountryPresent) {
					errorDetails = errorDetails + defenderCountry + "This is not in map" + "\n";
					mapDetails.setActionMsg("Error : " + errorDetails, "action");
					errorOccured = true;
				}
				boolean isAttackAndDefenderAdajacent = false;
				if (attackerCountryPresent && defenderCountryPresent) {
					attackCountryObject = playerController.getCountryObject(attackCountry, mapDetails);
					defenderCountryObject = playerController.getCountryObject(defenderCountry, mapDetails);

					isAttackAndDefenderAdajacent = playerController.isCountryAdjacent(attackCountryObject,
							defenderCountry, mapDetails);
				}
				if (defenderCountryPresent) {
					if (defenderCountryObject != null) {
						if (defenderCountryObject.getPlayer().equalsIgnoreCase(player.getPlayerName())) {
							errorDetails = defenderCountry + " is  owned by the current player" + "\n";
							mapDetails.setActionMsg("Error : " + errorDetails, "action");
							errorOccured = true;
						}
					}
				}
				if (!isAttackAndDefenderAdajacent) {
					if (attackerCountryPresent) {
						errorDetails = errorDetails + attackCountry + " is not adjacent to the " + defenderCountry;
						mapDetails.setActionMsg("Error : " + errorDetails, "action");
						errorOccured = true;
					}
				}
				if (decision.equalsIgnoreCase("-allout") && errorOccured == false) {
					while (attackCountryObject.getNoOfArmies() > 1 && defenderCountryObject.getNoOfArmies() != 0) {
						int attackerDices = playerController
								.maxAllowableAttackerDice(attackCountryObject.getNoOfArmies());
						int defenderDices = playerController
								.maxAllowableDefenderDice(defenderCountryObject.getNoOfArmies());
						if (attackerDices > 0 && defenderDices > 0) {
							playerController.attackingStarted(attackerDices, defenderDices, attackCountryObject,
									defenderCountryObject);
							if (playerController.isAttackerWon(defenderCountryObject)) {

								System.out.println("Card Phase");
								mapDetails.setActionMsg("Player Card Allocation", "action");
								CardController cardController = new CardController();
								cardController.setDeckOfCards();
								cardController.allocateCardToPlayer(player);
								mapDetails.setActionMsg("Player got a Card", "action");
								String removePlayerName = defenderCountryObject.getPlayer();
								playerController.moveArmyToConquredCountry(playersList, player, attackCountryObject,
										defenderCountryObject);
								playerController.removePlayer(playersList, mapDetails, removePlayerName);
								if (playerController.isPlayerWinner(player, mapDetails)) {
									mapDetails.setActionMsg(player.getPlayerName() + " won the Game!", "action");
									System.out.println(player.getPlayerName() + " won the Game!");
									System.exit(0);
								}
								break;
							}
						}
					}
				} else if (errorOccured == false) {
					int attackerDices = Integer.parseInt(attackDetails[3]);
					int attackerArmies = attackCountryObject.getNoOfArmies();
					if (playerController.isAttackerDicePossible(attackerArmies, attackerDices)) {
						mapDetails.setActionMsg("Defender Entering Defender Commands", "action");
						System.out.println("Enter the Defender command?");
						String name[] = defenderCountryObject.getPlayer().split("-");
						String defenderUserCommand;
						if (name[1].equalsIgnoreCase("human")) {
							defenderUserCommand = br.readLine().trim();
						} else {
							System.out.println("System entering Command");
							int defenderArmies = defenderCountryObject.getNoOfArmies();
							if (defenderArmies >= 2) {
								System.out.println("defend 2");
								defenderUserCommand = "defend 2";
							} else {
								System.out.println("defend 1");
								defenderUserCommand = "defend 1";
							}
						}
						if (playerController.checkUserDefenderValidation(defenderUserCommand)) {
							String[] defenderDetails = defenderUserCommand.split(" ");
							int defenderDices = Integer.parseInt(defenderDetails[1]);
							int defenderArmies = defenderCountryObject.getNoOfArmies();
							if (playerController.isDefenderDicePossible(defenderArmies, defenderDices)) {
								playerController.attackingStarted(attackerDices, defenderDices, attackCountryObject,
										defenderCountryObject);
								if (playerController.isAttackerWon(defenderCountryObject)) {
									System.out.println("Card Phase");
									CardController cardController = new CardController();
									mapDetails.setActionMsg("Player card Allocation", "action");
									cardController.setDeckOfCards();
									cardController.allocateCardToPlayer(player);
									mapDetails.setActionMsg("Player got a Card", "action");
									String removePlayerName = defenderCountryObject.getPlayer();
									playerController.moveArmyToConquredCountry(playersList, player, attackCountryObject,
											defenderCountryObject);
									playerController.removePlayer(playersList, mapDetails, removePlayerName);
									if (playerController.isPlayerWinner(player, mapDetails)) {
										mapDetails.setActionMsg(player.getPlayerName() + " won the Game!", "action");
										System.out.println(player.getPlayerName() + " won the Game!");
										System.exit(0);
									}
								}
							} else {
								playerController.reasonForFailedDefender(defenderArmies, defenderDices);
							}
						} else {
							mapDetails.setActionMsg("Defender Incorrect Command", "action");
							System.out.println("Please enter the defender Command in the below correct Format\n"
									+ "Format :defend numdice[numdice>0]\n");
						}
					} else {
						playerController.reasonForFailedAttack(attackerArmies, attackerDices);
					}
				} else if (errorOccured == true) {
					System.out.println(errorDetails);
					mapDetails.setActionMsg("Error: " + errorDetails, "action");
					errorDetails = "";
					errorOccured = false;
				}
			} else {
				mapDetails.setActionMsg("InCorrect Commands", "action");
				System.out.println("Please enter the attack Command in any one of the below correct Format\n"
						+ "Format 1:attack countrynamefrom countynameto numdice[numdice>0]\n"
						+ "Format 2:attack countrynamefrom countynameto  allout\n" + "Format 3:attack -noattack\n");
			}
			boolean isAttackPossibleAfter = playerController.isAttackPossible(player);
			String continueAttacking = "";
			if (isAttackPossibleAfter) {
				System.out.println("Do you want to attack again? Yes/No");
				continueAttacking = br.readLine().trim();
				mapDetails.setActionMsg("Player Attacking again", "action");
				while (!(continueAttacking.equalsIgnoreCase("Yes") || continueAttacking.equalsIgnoreCase("No")
						|| continueAttacking == null)) {
					System.err.println("\nPlease enter the choice as either Yes or No:");
					continueAttacking = br.readLine().trim();
					mapDetails.setActionMsg("Player Attacking again", "action");
				}
			} else {
				continueAttacking = "No";
			}
			if (continueAttacking.equalsIgnoreCase("Yes")) {
				gameContinue = true;
			} else {
				gameContinue = false;
				System.out.println("Attacking Phase is ended");
				mapDetails.setActionMsg("Attacking Phase is ended", "action");
			}
		} while (gameContinue);
		return 0;
	}

	/**
	 * This method is used for fortification.
	 */
	@Override
	public void fortificationPhase(GameMap gameMap, GamePlayer player) throws IOException {
		playerController = new Player();
		List<String> playersCommandList = new ArrayList<String>();
		String strUser;
		String strfromCountry = "";
		String strtoCountry = "";
		String countryNumToPlace = "";
		String cmd;
		int countOfArmies = 0;

		if (playerController.startGameFortification(player, gameMap)) {
			boolean doFortification = true;
			boolean doFortificationNone = false;
			while (doFortification && !doFortificationNone) {
				doFortification = true;
				doFortificationNone = false;
				Country givingCountry = null;
				Country receivingCountry = null;
				do {
					doFortification = false;
					doFortificationNone = false;
					gameMap.setActionMsg("Displaying List of Countries", "action");
					System.out.println("\nPlayer has the following list of countries with armies: \n");
					for (Country country : player.getPlayerCountries()) {
						System.out.println("* " + country.getCountryName() + ":" + country.getNoOfArmies() + "\n");
					}
					System.out.println("Enter the Command for fortification");
					gameMap.setActionMsg("Player entering fortification Command", "action");
					strUser = br.readLine().trim();
					playersCommandList = Arrays.asList(strUser.split(" "));
					if (playersCommandList.size() == 2) {
						playersCommandList = Arrays.asList(strUser.split(" "));
						String none = playersCommandList.get(1);
						cmd = playersCommandList.get(0);
						if (none.equalsIgnoreCase("none") && cmd.equalsIgnoreCase("fortify")) {
							gameMap.setActionMsg("No Move in Forification Phase", "action");
							System.out.println("No Move in Forification Phase");
							doFortificationNone = true;
							doFortification = true;
						} else {
							gameMap.setActionMsg("Incorrect Command", "action");
							System.out.println("Please enter the right format like : fortify none");
							doFortification = true;
						}
					}
					if (!doFortification) {
						if (!(playersCommandList.size() == 4)) {
							System.out
									.println("Please enter the right format like : fortify fromcountry tocountry num");
							strUser = br.readLine().trim();
							playersCommandList = Arrays.asList(strUser.split(" "));
						}
						strfromCountry = playersCommandList.get(1);
						strtoCountry = playersCommandList.get(2);
						countryNumToPlace = playersCommandList.get(3);
						cmd = playersCommandList.get(0);
						Pattern cmdPattern = Pattern.compile("fortify");
						Matcher cmdMatch = cmdPattern.matcher(cmd);
						Pattern namePattern = Pattern.compile("[a-zA-Z-_]+");
						Matcher matchFromCountry = namePattern.matcher(strfromCountry);
						Matcher matchToCountry = namePattern.matcher(strtoCountry);
						Pattern numberPattern = Pattern.compile("[0-9]+");
						Matcher match1 = numberPattern.matcher(countryNumToPlace);
						while (!matchFromCountry.matches() || strfromCountry.isEmpty() || !matchToCountry.matches()
								|| strtoCountry.isEmpty() || !match1.matches() || countryNumToPlace.isEmpty()
								|| !cmdMatch.matches()) {
							if (!matchFromCountry.matches() || strfromCountry.isEmpty()) {
								System.out.println("\nInCorrect fromcountry name, please enter the command again:");
								strUser = br.readLine().trim();
								playersCommandList = Arrays.asList(strUser.split(" "));
								strfromCountry = playersCommandList.get(1);
								strtoCountry = playersCommandList.get(2);
								countryNumToPlace = playersCommandList.get(3);
							}
							if (!matchToCountry.matches() || strtoCountry.isEmpty()) {
								System.out.println("\nInCorrect tocountry name, please enter the command again:");
								strUser = br.readLine().trim();
								playersCommandList = Arrays.asList(strUser.split(" "));
								strfromCountry = playersCommandList.get(1);
								strtoCountry = playersCommandList.get(2);
								countryNumToPlace = playersCommandList.get(3);
							}
							if (!match1.matches() || countryNumToPlace.isEmpty()) {
								System.out.println("\nInCorrect Army Count, please enter the command again:");
								strUser = br.readLine().trim();
								playersCommandList = Arrays.asList(strUser.split(" "));
								strfromCountry = playersCommandList.get(1);
								strtoCountry = playersCommandList.get(2);
								countryNumToPlace = playersCommandList.get(3);
								match1 = numberPattern.matcher(countryNumToPlace);
							}
							if (!cmdMatch.matches()) {
								System.out.println("\nInCorrect Command, please enter the command again:");
								strUser = br.readLine().trim();
								playersCommandList = Arrays.asList(strUser.split(" "));
								cmd = playersCommandList.get(0);
								strfromCountry = playersCommandList.get(1);
								strtoCountry = playersCommandList.get(2);
								countryNumToPlace = playersCommandList.get(3);
								cmdMatch = cmdPattern.matcher(cmd);
							}
						}
						for (Country country : player.getPlayerCountries()) {
							if (country.getCountryName().equalsIgnoreCase(strfromCountry)) {
								givingCountry = country;
							}
						}
						for (Country country : player.getPlayerCountries()) {
							if (country.getCountryName().equalsIgnoreCase(strtoCountry)) {
								receivingCountry = country;
							}
						}
						if (player.getPlayerCountries().contains(givingCountry)
								&& player.getPlayerCountries().contains(receivingCountry)) {
							doFortification = false;
						} else {
							gameMap.setActionMsg("Entered countries doesn't exist in player's owned country list",
									"action");
							System.out.println(
									"Entered countries doesn't exist in player's owned country list, please enter country names again\n");
							doFortification = true;
						}
					}
				} while (doFortification && !doFortificationNone);
				if (!doFortification) {
					countOfArmies = Integer.parseInt(countryNumToPlace);
					if (playerController.isArmyCountSufficient(countOfArmies, givingCountry)) {
						doFortification = true;
					}
				}
				if (!doFortification) {
					boolean fortify = false;
					for (Country country : player.getPlayerCountries()) {
						for (String temp : country.getNeighbourCountries()) {
							if (temp.equalsIgnoreCase(strfromCountry) || temp.equalsIgnoreCase(strtoCountry)) {
								fortify = true;
							}
						}
					}
					if (fortify) {
						doFortification = playerController.moveArmies(givingCountry, receivingCountry, countOfArmies);
					} else {
						doFortification = true;
						gameMap.setActionMsg("None of the players' countries are adjacent", "action");
						System.out
								.println("None of the players' countries are adjacent\n Fortification phase ends..!!");
					}
				}
			}
		}
	}
}
