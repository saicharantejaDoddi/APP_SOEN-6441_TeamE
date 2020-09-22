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

import com.appriskgame.model.GameMap;
import com.appriskgame.model.GamePlayer;

/**
 * This Class is used for saving and loading game map
 * 
 * @author Sai
 *
 */
public class GameSaveLoad implements SaveAndLoad {
	public BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

	/**
	 * Method for saving the game map object
	 */
	@Override
	public void saveGame(GameMap gameMap) throws IOException {
		ArrayList<GamePlayer> changedOrder = new ArrayList<GamePlayer>();
		changedOrder = getCorrectPlayList(gameMap);
		gameMap.setPlayers(changedOrder);
		String workingDir = System.getProperty("user.dir");
		String mapLocation = workingDir + "/resources/savedgames/";
		FileOutputStream savefile = null;

		String savedFilePath = mapLocation;
		ObjectOutputStream objFile = null;
		String fullPath = "";
		boolean repeat = true;
		do {
			System.out.println("Please enter the command to save Game?");
			String command = br.readLine().trim();
			String fileName = "";
			String[] cmdDetails = command.split(" ");

			if (checkSaveCommand(cmdDetails)) {
				fileName = cmdDetails[1];
				fullPath = savedFilePath + fileName + ".txt";
				repeat = false;

			} else {
				repeat = true;
			}
		} while (repeat);

		File savingFile = new File(fullPath);

		savingFile.setReadable(true);
		savingFile.setExecutable(true);
		savingFile.setWritable(true);

		savefile = new FileOutputStream(fullPath);
		objFile = new ObjectOutputStream(savefile);

		objFile.writeObject(gameMap);
		objFile.close();
		System.exit(0);

	}

	/**
	 * This method is for reading the game map object
	 */
	@Override
	public void readGame() throws Exception {
		boolean repeat = true;
		do {
			System.out.println("Please the enter the command for the saved Game?");
			String workingDir = System.getProperty("user.dir");
			String mapLocation = workingDir + "/resources/savedgames/";
			String savedFilePath = mapLocation;
			String command = br.readLine().trim();
			String[] cmdDetails = command.split(" ");

			if (checkloadCommand(cmdDetails)) {
				String fileName = cmdDetails[1];
				if (isSavedGameExists(savedFilePath, fileName)) {
					String fullPath = savedFilePath + fileName + ".txt";
					FileInputStream getFile = new FileInputStream(fullPath);
					ObjectInputStream backup = new ObjectInputStream(getFile);
					GameMap gameMap = (GameMap) backup.readObject();
					backup.close();
					Player p = new Player();
					p.continueGame(gameMap);
					repeat = false;
				} else {
					System.out.println("File Name Doesn't exist!");
					repeat = true;
				}

			} else {
				repeat = true;
			}
		} while (repeat);

	}

	/**
	 * This method is used to check if the save map exist or not
	 * 
	 * @param filepath saved file path
	 * @param fileName saved file name
	 * @return true if file exist else false
	 */
	public boolean isSavedGameExists(String filepath, String fileName) {
		String mapFileNameWithExtention = fileName + ".txt";
		File mapFolder = new File(filepath);
		File[] listFiles = mapFolder.listFiles();
		for (int i = 0; i < listFiles.length; i++) {
			if (mapFileNameWithExtention.equals(listFiles[i].getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method is used to get the correct player List
	 * 
	 * @param gameMap game map object
	 * @return list of players
	 */
	public ArrayList<GamePlayer> getCorrectPlayList(GameMap gameMap) {
		ArrayList<GamePlayer> correctOrderPlayList = new ArrayList<GamePlayer>();
		int currentIndex = 0;
		for (int i = 0; i < gameMap.getPlayers().size(); i++) {
			GamePlayer current = gameMap.getPlayers().get(i);
			if (current.getPlayerName().toString().equalsIgnoreCase(gameMap.getCurrentPlayer())) {
				currentIndex = i;

				for (int j = currentIndex + 1; j < gameMap.getPlayers().size(); j++) {
					GamePlayer nextGame = gameMap.getPlayers().get(j);
					correctOrderPlayList.add(nextGame);
				}
			}
		}

		for (int i = 0; i <= currentIndex; i++) {
			GamePlayer current = gameMap.getPlayers().get(i);
			correctOrderPlayList.add(current);
		}
		return correctOrderPlayList;
	}

	/**
	 * This Method is used to validate the save command
	 * 
	 * @param saveDetails save command
	 * @return true if command is valid else false
	 */
	public boolean checkSaveCommand(String[] saveDetails) {
		try {
			String save = saveDetails[0].trim();
			if (save.equalsIgnoreCase("savegame") && saveDetails.length == 2) {
				return true;
			} else {
				System.out.println("Please enter correct format: savegame filename");
				return false;
			}
		} catch (Exception ex) {
			System.out.println("Please enter correct format: savegame filename");
			return false;
		}
	}

	/**
	 * This Method is used to validate the load command
	 * 
	 * @param saveDetails load command
	 * @return true if command is valid else false
	 */
	public boolean checkloadCommand(String[] saveDetails) {
		try {
			String save = saveDetails[0].trim();
			if (save.equalsIgnoreCase("loadgame") && saveDetails.length == 2) {
				return true;
			} else {
				System.out.println("Please enter correct format: loadgame filename");
				return false;
			}
		} catch (Exception ex) {
			System.out.println("Please enter correct format: loadgame filename");
			return false;
		}
	}

}
