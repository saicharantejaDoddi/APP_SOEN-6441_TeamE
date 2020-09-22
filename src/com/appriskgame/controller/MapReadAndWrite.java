package com.appriskgame.controller;

import java.io.IOException;

import com.appriskgame.model.GameMap;

/**
 * Map read and write interface
 * 
 * @author Sai
 *
 */
public interface MapReadAndWrite {

	public GameMap readGameMap(String inputGameMapName, String format) throws IOException;

	public void writeGameMap(String ouputGameMapName, String mapFileName, GameMap gameMap, String format)
			throws IOException;

}
