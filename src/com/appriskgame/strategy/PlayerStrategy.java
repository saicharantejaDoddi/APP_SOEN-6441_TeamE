package com.appriskgame.strategy;

import java.io.IOException;
import java.util.ArrayList;

import com.appriskgame.model.Country;
import com.appriskgame.model.GameMap;
import com.appriskgame.model.GamePlayer;

/**
 * This interface defines the player strategy to be used by all the types of
 * players.
 * 
 * @author Surya
 */
public interface PlayerStrategy {
	/**
	 * Place armies method that should implement the logic of allocating the armies
	 * 
	 * @param gameMap gameMap object
	 * @param player  current player
	 * @throws IOException input/output exception
	 */
	public void placeArmies(GameMap gameMap, GamePlayer player) throws IOException;

	/**
	 * Reinforcement of armies to the player owned countries
	 * 
	 * @param player  current player
	 * @param gameMap gameMap object
	 * @throws Exception input/output exception
	 */
	public void reinforcementPhase(GamePlayer player, GameMap gameMap) throws Exception;

	/**
	 * This method is used for Attack phase.
	 * 
	 * @param gameMap     gameMap object
	 * @param player      current player
	 * @param playersList List of player
	 * @return 1 if player wins, else 0
	 * @throws IOException input/output exception
	 */
	public int attackPhase(GameMap gameMap, GamePlayer player, ArrayList<GamePlayer> playersList) throws IOException;

	/**
	 * Fortifying armies from one country to its adjacent country, both of which are
	 * owned by the player
	 * 
	 * @param gameMap gameMap object
	 * @param player  current player
	 * @throws IOException input/output exception
	 */
	public void fortificationPhase(GameMap gameMap, GamePlayer player) throws IOException;
}
