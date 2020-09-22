package com.appriskgame.view;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.appriskgame.controller.Player;
import com.appriskgame.model.Continent;
import com.appriskgame.model.Country;
import com.appriskgame.model.GameMap;
import com.appriskgame.model.GamePlayer;

/**
 * This Class is used for implementation of World Domination View. Gets Notified
 * whenever there is an update to the game player
 * 
 * @author Surya
 */
public class WorldDominationView implements Observer {
	public JTextArea info = new JTextArea(50, 50);
	JFrame frame = new JFrame("World Domination Map");
	String output;

	/**
	 * Method to initialize the frame.
	 * 
	 */
	public void initialize() {
		frame.getContentPane().add(info);
		info.setText(output);
		JScrollPane scroll = new JScrollPane(info);
		frame.getContentPane().add(scroll);
		frame.setSize(500, 500);
		frame.setLocation(500, 200);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	/**
	 * This method is used to update the domination view
	 */
	@Override
	public void update(Observable o, Object arg) {
		GameMap gameMap = (GameMap) o;

		if (!gameMap.getMode().equalsIgnoreCase("tournament")) {
			if (!frame.isVisible()) {
				initialize();
			}
			DecimalFormat df = new DecimalFormat("#.##");
			float totalCountries = gameMap.getCountrySet().size();
			output = "";
			for (GamePlayer player : gameMap.getPlayers()) {
				float playerCountries = player.getPlayerCountries().size();
				String mapPercent = df.format((playerCountries * 100) / totalCountries);
				List<Country> countryList = player.getPlayerCountries();
				int countryArmies = 0;
				for (Country country : countryList) {
					countryArmies = countryArmies + country.getNoOfArmies();
				}
				ArrayList<String> continentsOccupiedName = new ArrayList<String>();
				ArrayList<Continent> listOfPlayerContinents = new ArrayList<Continent>();
				Player p = new Player();
				Continent playerContinent = player.getPlayerCountries().get(0).getPartOfContinent();
				int sizeOfPlayerCountries = player.getPlayerCountries().size();
				for (int i = 0; i < sizeOfPlayerCountries; i++) {
					playerContinent = player.getPlayerCountries().get(i).getPartOfContinent();
					if (!listOfPlayerContinents.contains(playerContinent)) {
						listOfPlayerContinents.add(playerContinent);
					}
				}
				for (int i = 0; i < listOfPlayerContinents.size(); i++) {
					if (p.doesPlayerOwnAContinent(player, listOfPlayerContinents.get(i).getListOfCountries()))
						continentsOccupiedName.add(listOfPlayerContinents.get(i).getContinentName());
				}
				String continents = continentsOccupiedName.toString();
				continents = continents.substring(1, continents.length() - 1);

				output = output + "\nPlayer Name = " + player.getPlayerName() + "\nPercentage of Map Contolled = "
						+ mapPercent + "\nTotal Number of Armies = " + countryArmies + "\nContinents Controlled = "
						+ (continents.isEmpty() ? "None" : continents) + "\n";

			}

			String newInfo = "*******-------*******" + " " + output;
			info.setText(newInfo);
			frame.revalidate();
			frame.repaint();
		}
	}
}
