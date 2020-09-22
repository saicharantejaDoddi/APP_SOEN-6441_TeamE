package com.appriskgame.view;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.appriskgame.model.Country;
import com.appriskgame.model.GameMap;
import javax.swing.JTextField;

/**
 * This Class is used to display Player information, Game phase and actions
 * taking place in the each phase.
 * 
 * @author Sahana
 *
 */
public class PlayerView implements Observer {
	JFrame frame = new JFrame("Player View");
	String phaseName = "";
	String playerName = "";
	String mapinfo = "";
	JTextArea textInfo;
	JTextArea textArea;
	JTextField textGamePhase;
	JTextField textPlayerName;
	JScrollPane scrollPane;
	JScrollPane scrollPane_1;

	/**
	 * Method to initialize the frame.
	 * 
	 */
	public void initialize() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 650);
		frame.setLocation(500, 200);
		frame.getContentPane().setLayout(null);

		JLabel lblGamePhase = new JLabel("Game Phase");
		lblGamePhase.setBounds(20, 53, 73, 25);
		frame.getContentPane().add(lblGamePhase);

		textGamePhase = new JTextField();
		textGamePhase.setEditable(false);
		textGamePhase.setBounds(128, 55, 176, 20);
		textGamePhase.setText(phaseName);
		frame.getContentPane().add(textGamePhase);
		textGamePhase.setColumns(10);

		JLabel lblPlayerName = new JLabel("Player Name");
		lblPlayerName.setBounds(20, 89, 73, 25);
		frame.getContentPane().add(lblPlayerName);

		textPlayerName = new JTextField();
		textPlayerName.setEditable(false);
		textPlayerName.setColumns(10);
		textPlayerName.setBounds(128, 91, 176, 20);
		textPlayerName.setText(playerName);
		frame.getContentPane().add(textPlayerName);

		JLabel lblNewLabel = new JLabel("Map Information :");
		lblNewLabel.setBounds(20, 135, 105, 25);
		frame.getContentPane().add(lblNewLabel);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(20, 158, 438, 193);
		frame.getContentPane().add(scrollPane);

		textInfo = new JTextArea();
		scrollPane.setViewportView(textInfo);
		textInfo.setColumns(10);

		JLabel actionNewLabel = new JLabel("Actions :");
		actionNewLabel.setBounds(20, 362, 46, 14);
		frame.getContentPane().add(actionNewLabel);

		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(20, 387, 436, 191);
		frame.getContentPane().add(scrollPane_1);

		textArea = new JTextArea();
		scrollPane_1.setViewportView(textArea);
		textArea.setColumns(10);

		frame.setVisible(true);
	}

	/**
	 * This method is used to update the player view screen
	 */
	@Override
	public void update(Observable o, Object arg) {

		GameMap gameMap = (GameMap) o;
		if (!gameMap.getMode().equalsIgnoreCase("tournament")) {
			if (!frame.isVisible()) {
				initialize();
			}
			ArrayList<Country> print = gameMap.getCountries();
			mapinfo = "";
			for (Country country : print) {
				mapinfo = mapinfo + "\n" + country;
			}
			String CurrentPhase = textGamePhase.getText();
			if ("phase".equals(gameMap.message)) {
				phaseName = gameMap.getGamePhase();
				textGamePhase.setText(phaseName);
				String appendLog = "";
				textArea.setText(appendLog);
				textInfo.setText(mapinfo);
			}

			playerName = gameMap.getCurrentPlayer();
			textPlayerName.setText(playerName.toUpperCase());

			if ("domination".equals(gameMap.message)) {
				textInfo.setText(mapinfo);
			}

			if ("action".equals(gameMap.message)) {
				if (CurrentPhase.equals(gameMap.getGamePhase())) {
					String newLog = gameMap.getActionMsg();
					String currentTxt = textArea.getText();
					String appendLog = newLog + "\n" + currentTxt;
					textArea.setText(appendLog);
					textInfo.setText(mapinfo);
				} else {
					String appendLog = gameMap.getActionMsg();
					textArea.setText(appendLog);
					textInfo.setText(mapinfo);
				}
			}

			frame.revalidate();
			frame.repaint();
		}

	}

}
