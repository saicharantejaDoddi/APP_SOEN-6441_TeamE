package com.appriskgame.test.controller;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.appriskgame.controller.GameSaveLoad;

/**
 * This Class for test the save and load game map
 * @author Sai
 *
 */
public class SaveAndLoadTest {

	String savedFilePath;
	String savedFileName;
	GameSaveLoad gameSaveLoad;

	/**
	 * This is the setup method for the pre-requisite values before the test cases
	 */
	@Before
	public void initializePlayerTest() {
		savedFilePath = "C:\\Users\\saich\\Desktop\\Saicharanteja_TournamentChanges/resources/savedgames/";
		savedFileName = "Content";
		gameSaveLoad = new GameSaveLoad();
	}

	/**
	 * This method check whether saved map file exist or not
	 */
	@Test
	public void testSavedGameExists() {
		assertTrue(gameSaveLoad.isSavedGameExists(savedFilePath, savedFileName));
	}
}
