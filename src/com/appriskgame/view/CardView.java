package com.appriskgame.view;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.appriskgame.model.GamePlayer;

import java.util.Observable;
import java.util.Observer;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

/**
 * This Class is to check how many cards does player own to exchange and
 * implemented using observer pattern
 *
 * @author Shruthi
 * @author Surya
 *
 */
public class CardView implements Observer {
	JFrame frmPlayerCardView = new JFrame("Player Card View");
	int numOfInfantry = 0;
	int numOfCavalry = 0;
	int numOfArtillery = 0;
	String player = "";
	JTextField textField;
	JTextPane textPane;
	JLabel lblAvailableCardsFor = new JLabel("Available Cards for Players - ");
	JLabel lblNewLabel_1 = new JLabel("Infantry");
	JLabel lblNewLabel_2 = new JLabel("Cavalry");
	JLabel lblNewLabel_3 = new JLabel("Artillery");
	JTextField textField_1 = new JTextField();
	JTextField textField_2 = new JTextField();
	JTextField textField_3 = new JTextField();
	String info = "Cards in Player Hand";
	JTextField textField_4 = new JTextField();

	/**
	 * Method to initialize the frame.
	 * 
	 */
	public void initialize() {
		textField_4.setHorizontalAlignment(SwingConstants.CENTER);
		textField_4.setEditable(false);
		textField_4.setBounds(42, 270, 318, 130);
		textField_4.setColumns(10);
		textField_3.setEditable(false);
		textField_3.setBounds(116, 210, 25, 20);
		textField_3.setColumns(10);
		textField_2.setEditable(false);
		textField_2.setBounds(116, 172, 25, 20);
		textField_2.setColumns(10);
		textField_1.setEditable(false);
		textField_1.setBounds(116, 135, 25, 20);
		textField_1.setColumns(10);
		frmPlayerCardView.setTitle("Player Card View");

		frmPlayerCardView.setSize(500, 500);
		frmPlayerCardView.setLocation(500, 200);
		frmPlayerCardView.getContentPane().setLayout(null);

		JLabel lblNewLabel = new JLabel("Player Name");
		lblNewLabel.setBounds(42, 31, 99, 14);
		frmPlayerCardView.getContentPane().add(lblNewLabel);

		textField = new JTextField();
		textField.setEditable(false);
		textField.setBounds(154, 28, 206, 20);
		textField.setText(player);
		frmPlayerCardView.getContentPane().add(textField);
		textField.setColumns(10);
		lblAvailableCardsFor.setBounds(42, 92, 166, 35);

		frmPlayerCardView.getContentPane().add(lblAvailableCardsFor);
		lblNewLabel_1.setBounds(42, 138, 46, 14);

		frmPlayerCardView.getContentPane().add(lblNewLabel_1);
		lblNewLabel_2.setBounds(42, 175, 46, 14);

		frmPlayerCardView.getContentPane().add(lblNewLabel_2);
		lblNewLabel_3.setBounds(42, 213, 46, 14);

		frmPlayerCardView.getContentPane().add(lblNewLabel_3);

		frmPlayerCardView.getContentPane().add(textField_1);
		textField_1.setText(String.valueOf(numOfInfantry));

		frmPlayerCardView.getContentPane().add(textField_2);
		textField_2.setText(String.valueOf(numOfCavalry));

		frmPlayerCardView.getContentPane().add(textField_3);
		textField_3.setText(String.valueOf(numOfArtillery));

		frmPlayerCardView.getContentPane().add(textField_4);
		textField_4.setText(info);
		/*
		 * textPane = new JTextPane(); textPane.setEditable(false);
		 * textPane.setBounds(42, 250, 371, 173); textPane.setText(info);
		 * frmPlayerCardView.getContentPane().add(textPane);
		 */
		frmPlayerCardView.setVisible(true);
	}

	/**
	 * Method to update the card view screen
	 */
	@Override
	public void update(Observable o, Object arg) {
		numOfInfantry = 0;
		numOfCavalry = 0;
		numOfArtillery = 0;
		GamePlayer gamePlayer = (GamePlayer) o;
		GamePlayer currentPlayer = gamePlayer.current;
		info = "Cards in Player Hand - ";

		if (!frmPlayerCardView.isVisible()) {
			initialize();
		}

		for (int k = 0; k < currentPlayer.getCardList().size(); k++) {
			String type = currentPlayer.getCardList().get(k).getType();
			if (type.equalsIgnoreCase("Infantry")) {
				numOfInfantry++;
			}
			if (type.equalsIgnoreCase("Cavalry")) {
				numOfCavalry++;
			}
			if (type.equalsIgnoreCase("Artillery")) {
				numOfArtillery++;
			}
		}

		textField_1.setText(String.valueOf(numOfInfantry));
		textField_2.setText(String.valueOf(numOfCavalry));
		textField_3.setText(String.valueOf(numOfArtillery));

		for (int k = 0; k < currentPlayer.getCardList().size(); k++) {
			info = info + "  " + (k + 1) + "." + currentPlayer.getCardList().get(k).getType();
		}

		player = currentPlayer.getPlayerName().toUpperCase();
		textField.setText(player);
		textField_4.setText(info);
		frmPlayerCardView.revalidate();
		frmPlayerCardView.repaint();
	}
}
