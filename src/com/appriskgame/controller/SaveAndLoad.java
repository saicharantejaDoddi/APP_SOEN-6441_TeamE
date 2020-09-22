package com.appriskgame.controller;

import java.io.IOException;

import com.appriskgame.model.GameMap;

/**
 * Interface for save and load game map object
 * 
 * @author Sai
 *
 */
public interface SaveAndLoad {

	public void saveGame(GameMap gameMap) throws IOException;

	public void readGame() throws Exception;
}
