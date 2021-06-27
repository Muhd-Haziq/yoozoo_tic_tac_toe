
import javax.print.attribute.standard.MediaSize.Other;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.event.*;

public class Player extends JFrame {
	private int width;
	private int height;
	private Container mainContainer;
	private JPanel menuPanel;
	private JPanel gameboardPanel;
	private JButton createBtn, joinBtn;
	private JTextPane messageText;

	// Variables for connection to server
	private ClientSideConnection csc;
	private int playerID;
	private int opponentPlayer;

	// ArrayList to store the actions taken by both players
	static ArrayList<Integer> player1Positions;
	static ArrayList<Integer> opponentPlayerPositions;

	// Buttons to be used in each grid
	private JButton top1, top2, top3;
	private JButton mid1, mid2, mid3;
	private JButton btm1, btm2, btm3;

	// Images for circle and crosses
	private Icon playerIcon;
	private Icon opponentIcon;

	// Variable to prevent opponent/player from acting when
	// it's not their turn
	private boolean buttonsEnabled;

	// Variable to check if there are enough players in the lobby
	private boolean playersFound;

	// Variables for registration and login
	private JPanel registrationPanel;
	private JPanel loginPanel;
	private JLabel title;

	// Variables for registration labels and textfields
	private JLabel lblRegisterUsername;
	private JTextField txtRegisterUsername;

	private JLabel lblRegisterPassword;
	private JPasswordField txtRegisterPassword;

	// Variables for login labels and textfields
	private JLabel lblLoginUsername;
	private JTextField txtLoginUsername;

	private JLabel lblLoginPassword;
	private JPasswordField txtLoginPassword;

	// Variables for buttons to login and register
	private JButton btnRegister;
	private JButton btnLogin;

	// Variables for buttons to transition between login and register
	private JButton btnToLogin;
	private JButton btnToRegister;

	// Variables for registration and login titles
	private JLabel registrationTitle;
	private JLabel loginTitle;

	// Variable for playerLoginID
	private int playerLoginID;
	
	// Variable for db entries
	private final String DB = "jdbc:mysql://localhost:3306/tic_tac_toe_db";
	private final String DB_USER = "root";
	private final String DB_PASSWORD = "";

	public Player(int w, int h) {
		width = w;
		height = h;
		playersFound = false;

		// Initialize Containers and JPanels
		mainContainer = this.getContentPane();

		registrationPanel = new JPanel();
		menuPanel = new JPanel();
		gameboardPanel = new JPanel();

		createBtn = new JButton("Create Game");
		joinBtn = new JButton("Join Game");

		messageText = new JTextPane();

		// Initialize ArrayLists used to track the moves of both players
		player1Positions = new ArrayList<Integer>();
		opponentPlayerPositions = new ArrayList<Integer>();

		// Initialize buttons used for the game
		top1 = new JButton("1");
		top2 = new JButton("2");
		top3 = new JButton("3");

		mid1 = new JButton("4");
		mid2 = new JButton("5");
		mid3 = new JButton("6");

		btm1 = new JButton("7");
		btm2 = new JButton("8");
		btm3 = new JButton("9");

		// Initialize registrationPanel and loginPanel
		registrationPanel = new JPanel();
		loginPanel = new JPanel();

		lblRegisterUsername = new JLabel("Username");
		lblRegisterPassword = new JLabel("Password");

		txtRegisterUsername = new JTextField();
		txtRegisterPassword = new JPasswordField();

		lblLoginUsername = new JLabel("Username");
		lblLoginPassword = new JLabel("Password");

		txtLoginUsername = new JTextField();
		txtLoginPassword = new JPasswordField();

		btnRegister = new JButton("Register");
		btnLogin = new JButton("Login");
		btnToLogin = new JButton("Have an account? Login here.");
		btnToRegister = new JButton("Don't have an account? Register here.");
	}

	private void createRegistrationMenu() {
		registrationPanel.setSize(width, height);
		registrationPanel.setLayout(null);

		// Add the username and password fields to the registrationPanel
		registrationPanel.add(lblRegisterUsername);
		registrationPanel.add(txtRegisterUsername);

		registrationPanel.add(lblRegisterPassword);
		registrationPanel.add(txtRegisterPassword);

		registrationPanel.add(btnRegister);
		registrationPanel.add(btnToLogin);

		// Set username location and size
		lblRegisterUsername.setLocation(100, 150);
		lblRegisterUsername.setSize(200, 35);
		txtRegisterUsername.setLocation(300, 150);
		txtRegisterUsername.setSize(200, 35);

		// Join button location and size
		lblRegisterPassword.setLocation(100, 250);
		lblRegisterPassword.setSize(200, 35);
		txtRegisterPassword.setLocation(300, 250);
		txtRegisterPassword.setSize(200, 35);

		// Register button location and size
		btnRegister.setLocation(200, 350);
		btnRegister.setSize(200, 35);

		// ToLogin button location and size
		btnToLogin.setLocation(150, 500);
		btnToLogin.setSize(300, 35);

		// Add the menuPanel to the mainContainer
		mainContainer.add(registrationPanel);

		// Set visibility as false as the player will not start in the registration screen
		registrationPanel.setVisible(false);

		setupRegistrationMenu();
	}

	private void setupRegistrationMenu() {
		btnRegister.addActionListener(new ActionListener() {

			// Setup login button
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					Class.forName("com.mysql.jdbc.Driver");
					Connection c = DriverManager.getConnection(DB, DB_USER, DB_PASSWORD);
					Statement stmt = (Statement) c.createStatement();
					String sql = "INSERT INTO players (username, password) VALUES('" + txtRegisterUsername.getText() + "', '" + txtRegisterPassword.getText() + "')";
					int rs = stmt.executeUpdate(sql);
					

					if(rs == 1) {
						registrationPanel.setVisible(false);
						menuPanel.setVisible(true);
						
						Statement stmtId = (Statement) c.createStatement();
						String sqlId = "SELECT * FROM players WHERE username = '" + txtRegisterUsername.getText() + "' AND password = '" + txtRegisterPassword.getText() + "'";

						ResultSet resultSetId = stmtId.executeQuery(sqlId);
						
						while(resultSetId.next()) {
							playerLoginID = (int) resultSetId.getLong(1);
							System.out.println(String.valueOf(playerLoginID));
						}
					}else {
						JOptionPane.showMessageDialog(null, "Username/Password Error", "ERROR", JOptionPane.ERROR_MESSAGE);
						txtRegisterUsername.setText(null);
						txtRegisterPassword.setText(null);
						txtRegisterUsername.requestFocusInWindow();
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});


		// Setup button to login screen
		btnToLogin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				registrationPanel.setVisible(false);
				loginPanel.setVisible(true);
			}
		});
	}

	private void createLoginMenu() {
		loginPanel.setSize(width, height);
		loginPanel.setLayout(null);

		// Add the username and password fields to the loginPanel
		loginPanel.add(lblLoginUsername);
		loginPanel.add(txtLoginUsername);

		loginPanel.add(lblLoginPassword);
		loginPanel.add(txtLoginPassword);

		loginPanel.add(btnLogin);

		loginPanel.add(btnToRegister);

		// Set username location and size
		lblLoginUsername.setLocation(100, 150);
		lblLoginUsername.setSize(200, 35);
		txtLoginUsername.setLocation(300, 150);
		txtLoginUsername.setSize(200, 35);

		// Join button location and size
		lblLoginPassword.setLocation(100, 250);
		lblLoginPassword.setSize(200, 35);
		txtLoginPassword.setLocation(300, 250);
		txtLoginPassword.setSize(200, 35);

		// Register button location and size
		btnLogin.setLocation(200, 350);
		btnLogin.setSize(200, 35);

		// ToRegister button location and size
		btnToRegister.setLocation(150, 500);
		btnToRegister.setSize(300, 35);

		// Add the menuPanel to the mainContainer
		mainContainer.add(loginPanel);

		// Set visibility as true as the player will start in the menu screen
		loginPanel.setVisible(true);

		setupLoginMenu();
	}

	private void setupLoginMenu() {

		// Setup login button
		btnLogin.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					Class.forName("com.mysql.jdbc.Driver");
					Connection c = DriverManager.getConnection(DB, DB_USER, DB_PASSWORD);
					Statement stmt = (Statement) c.createStatement();
					String sql = "SELECT * FROM players WHERE username = '" + txtLoginUsername.getText() + "' AND password = '" + txtLoginPassword.getText() + "'";
					ResultSet rs = stmt.executeQuery(sql);

					if(rs.next()) {
						loginPanel.setVisible(false);
						menuPanel.setVisible(true);
						
						playerLoginID = (int) rs.getLong(1);
					}else {
						JOptionPane.showMessageDialog(null, "Either the username or password is wrong.", "ERROR", JOptionPane.ERROR_MESSAGE);
						txtRegisterUsername.setText(null);
						txtRegisterPassword.setText(null);
						txtRegisterUsername.requestFocusInWindow();
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});

		// Setup button to register screen
		btnToRegister.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				loginPanel.setVisible(false);
				registrationPanel.setVisible(true);
			}
		});
	}

	private void createMainMenu() {
		menuPanel.setSize(width, height);
		menuPanel.setLayout(null);

		// Add the Create and Join buttons to the menuPanel
		menuPanel.add(createBtn);
		menuPanel.add(joinBtn);

		// Create button location and size
		createBtn.setLocation(200, 200);
		createBtn.setSize(200, 35);

		// Join button location and size
		joinBtn.setLocation(200, 300);
		joinBtn.setSize(200, 35);

		// Message text area location and size
		messageText.setLocation(50, 500);
		messageText.setSize(500, 35);
		messageText.setEditable(false);
		messageText.setAlignmentX(CENTER_ALIGNMENT);
		messageText.setAlignmentY(CENTER_ALIGNMENT);

		// Add the menuPanel to the mainContainer
		mainContainer.add(menuPanel);

		// Set visibility as true as the player will start in the login screen
		menuPanel.setVisible(false);

		setupMenuButtons();
	}

	private void setupMenuButtons() {

		// Create game button
		createBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				menuPanel.setVisible(false);
				connectToServer();
				createGameboard();
				Player.this.setTitle("Tic Tac Toe - Player #" + playerID);		
				setGameboardPlayers();

				gameboardPanel.setVisible(true);

				setupButtons();
			}
		});

		// Join game button
		joinBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				menuPanel.setVisible(false);
				connectToServer();
				createGameboard();
				Player.this.setTitle("Tic Tac Toe - Player #" + playerID);		
				setGameboardPlayers();

				gameboardPanel.setVisible(true);

				setupButtons();
			}
		});
	}

	private void createGameboard() {
		gameboardPanel.setSize(width, height);
		gameboardPanel.setLayout(new GridLayout(4,3));

		// Adding the game panel where the game will be held
		gameboardPanel.add(top1);
		gameboardPanel.add(top2);
		gameboardPanel.add(top3);
		gameboardPanel.add(mid1);
		gameboardPanel.add(mid2);
		gameboardPanel.add(mid3);
		gameboardPanel.add(btm1);
		gameboardPanel.add(btm2);
		gameboardPanel.add(btm3);
		gameboardPanel.add(messageText);

		// Add the gameboard to the mainContainer
		mainContainer.add(gameboardPanel);

		// Set visibility as false as the player will not start in the game
		gameboardPanel.setVisible(false);
	}

	public void setGameboardPlayers() {
		if(playerID == 1) {
			messageText.setText("You are Player #1. \nWaiting for another player to join...");
			opponentPlayer = 2;
			buttonsEnabled = false;

			// Set icons depending on the player
			playerIcon = new ImageIcon("images/circle_120.png");
			opponentIcon = new ImageIcon("images/cross_120.png");
		}else {
			messageText.setText("You are Player #2. Wait for your turn.");
			opponentPlayer = 1;
			buttonsEnabled = false;

			// Set icons depending on the player
			playerIcon = new ImageIcon("images/cross_120.png");
			opponentIcon = new ImageIcon("images/circle_120.png");

			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					updateTurn();
				}
			});

			t.start();
		}

		toggleButtons();

		while(true) {
			if(playerID == 1 && !playersFound) {
				playersFound = csc.receivePlayersFound();

				if(playersFound) {
					messageText.setText("Player found. Please begin your turn.");
					buttonsEnabled = true;
					toggleButtons();
					break;
				}
			}else if(playerID == 2) {
				break;
			}
		}
	}


	public void createGUI() {
		this.setSize(width, height);
		this.setTitle("Tic Tac Toe - Yoozoo");		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);		

		// Create login menu
		createLoginMenu();

		// Create registration menu
		createRegistrationMenu();

		// Create the main menu
		createMainMenu();

		// Create the gameboard
		createGameboard();
	}

	public void connectToServer() {
		csc = new ClientSideConnection();
	}

	public void setupButtons() {
		ActionListener al = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JButton button = (JButton) e.getSource();

				// If statement to prevent player from clicking a button that
				// has already been clicked by either the player or opponent
				if(!button.getText().equals("")) {
					// Retrieve the number of the button that was clicked
					int clickNum = Integer.parseInt(button.getText());

					// Displays message that the player's turn is over
					messageText.setText("Please wait for Player #" + opponentPlayer + " to act.");

					// Sets buttons to be disabled after clicking on the button
					// to ensure the end of their turn
					buttonsEnabled = false;
					toggleButtons();

					// Adds own move to playerPositions Arraylist
					player1Positions.add(clickNum);

					// Sends that same position to the server,
					// which will be retrieved by the opponent player
					csc.sendButtonNum(clickNum);

					// Replaces the icon for the player's current move
					button.setIcon(playerIcon);
					button.setText("");

					// After every turn, check for a winner with the
					// checkWinner() method below.
					// If winner is found, end the game
					if(checkWinner()) {
						buttonsEnabled = false;
						toggleButtons();
						csc.closeConnection();
					} else {
						Thread t = new Thread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								updateTurn();
							}
						});
						t.start();
					}
				}
			}
		};


		// Assign action listeners to each of the Tic-Tac-Toe buttons
		top1.addActionListener(al);
		top2.addActionListener(al);
		top3.addActionListener(al);
		mid1.addActionListener(al);
		mid2.addActionListener(al);
		mid3.addActionListener(al);
		btm1.addActionListener(al);
		btm2.addActionListener(al);
		btm3.addActionListener(al);
	}

	public void toggleButtons() {
		top1.setEnabled(buttonsEnabled);
		top2.setEnabled(buttonsEnabled);
		top3.setEnabled(buttonsEnabled);
		mid1.setEnabled(buttonsEnabled);
		mid2.setEnabled(buttonsEnabled);
		mid3.setEnabled(buttonsEnabled);
		btm1.setEnabled(buttonsEnabled);
		btm2.setEnabled(buttonsEnabled);
		btm3.setEnabled(buttonsEnabled);
	}

	public void updateTurn() {
		int n = csc.receiveButtonNum();
		messageText.setText("Opponent selected panel #" + n + ". Please make your move.");
		opponentPlayerPositions.add(n);
		System.out.println("Your opponent has selected the following panels: " + opponentPlayerPositions.toString());

		// Check for winner after opponent's turn
		if(checkWinner()) {
			buttonsEnabled = false;
			toggleButtons();
			csc.closeConnection();
		} else {
			buttonsEnabled = true;
		}

		// Checking for opponent opponent placement,
		// and replacing it with the opponent icon
		if(top1.getText().equals(String.valueOf(n))) {
			top1.setIcon(opponentIcon);
			top1.setText("");
		}else if(top2.getText().equals(String.valueOf(n))) {
			top2.setIcon(opponentIcon);
			top2.setText("");
		}else if(top3.getText().equals(String.valueOf(n))) {
			top3.setIcon(opponentIcon);
			top3.setText("");
		}else if(mid1.getText().equals(String.valueOf(n))) {
			mid1.setIcon(opponentIcon);
			mid1.setText("");
		}else if(mid2.getText().equals(String.valueOf(n))) {
			mid2.setIcon(opponentIcon);
			mid2.setText("");
		}else if(mid3.getText().equals(String.valueOf(n))) {
			mid3.setIcon(opponentIcon);
			mid3.setText("");
		}else if(btm1.getText().equals(String.valueOf(n))) {
			btm1.setIcon(opponentIcon);
			btm1.setText("");
		}else if(btm2.getText().equals(String.valueOf(n))) {
			btm2.setIcon(opponentIcon);
			btm2.setText("");
		}else if(btm3.getText().equals(String.valueOf(n))) {
			btm3.setIcon(opponentIcon);
			btm3.setText("");
		}

		toggleButtons();
	}

	/* ---------------------------- CLIENT CONNECTION - START ---------------------------- */

	// Client connection
	private class ClientSideConnection {
		private Socket socket;
		private DataInputStream dataIn;
		private DataOutputStream dataOut;

		public ClientSideConnection() {
			System.out.println("---- Client ----");
			try {
				socket = new Socket("localhost", 3124);
				dataIn = new DataInputStream(socket.getInputStream());
				dataOut = new DataOutputStream(socket.getOutputStream());

				playerID = dataIn.readInt();
				System.out.println("Connected to server as Player #" + playerID + ".");


			} catch (Exception e) {
				System.out.println("IOException from CSC Constructor");
			}
		}

		// Method to send selected button to server
		public void sendButtonNum(int n) {
			try {
				dataOut.writeInt(n);
				dataOut.flush();
			} catch (IOException e) {
				System.out.println("IOException from sendButtonNum() CSC");
			}
		}

		// Method to retrieve opponent's selected button when the opponent's
		// turn is over
		public int receiveButtonNum() {
			int n = -1;
			try {
				n = dataIn.readInt();
				System.out.println("Player #" + opponentPlayer + " clicked panel #" + n);
			} catch (IOException e) {
				// TODO: handle exception
				System.out.println("IOException from receiveButtonNum() CSC");
			}
			return n;
		}

		// Method to return true only when there are two players in the lobby
		public boolean receivePlayersFound() {
			boolean isRunning = false;
			try {
				isRunning = dataIn.readBoolean();
				System.out.println("Player found.");
			} catch (IOException e) {
				// TODO: handle exception
				System.out.println("IOException from receivePlayersFound() CSC");
			}
			return isRunning;
		}

		// Method to close the connection after the game is done
		public void closeConnection() {
			try {
				socket.close();
				System.out.println("--- CONNECTION CLOSED ---");
			} catch (Exception e) {
				System.out.println("IOException on closeConnection() CSC");
			}
		}

	}

	/* ---------------------------- CLIENT CONNECTION - END -------------------------------------------------------- */


	// Method to check for the winner
	public boolean checkWinner() {
		boolean winnerDecided = false;

		// Array list for top, middle and bottom row winning conditions (if they are in the same line)
		ArrayList<Integer> topRow = new ArrayList<Integer>(Arrays.asList(1,2,3));
		ArrayList<Integer> midRow = new ArrayList<Integer>(Arrays.asList(4,5,6));
		ArrayList<Integer> bottomRow = new ArrayList<Integer>(Arrays.asList(7,8,9));

		// Array list for left, mid and right columns winning conditions (if they are in the same line)
		ArrayList<Integer> leftCol = new ArrayList<Integer>(Arrays.asList(1,4,7));
		ArrayList<Integer> midCol = new ArrayList<Integer>(Arrays.asList(2,5,8));
		ArrayList<Integer> rightCol = new ArrayList<Integer>(Arrays.asList(3,6,9));

		// Array list for both diagonal winning conditions (if they are in the same line)
		ArrayList<Integer> diag1 = new ArrayList<Integer>(Arrays.asList(1,5,9));
		ArrayList<Integer> diag2 = new ArrayList<Integer>(Arrays.asList(7,5,3));

		ArrayList<ArrayList<Integer>> winningConditions = new ArrayList<ArrayList<Integer>>();
		// Add each of those conditions in another ArrayList to easily loop through them
		winningConditions.add(topRow);
		winningConditions.add(midRow);
		winningConditions.add(bottomRow);
		winningConditions.add(leftCol);
		winningConditions.add(midCol);
		winningConditions.add(rightCol);
		winningConditions.add(diag1);
		winningConditions.add(diag2);

		// Check the winning conditions for both the player and opponent
		// by checking if their moves match any of the above conditions
		for(ArrayList<Integer> l : winningConditions) {
			if(player1Positions.containsAll(l)) {
				messageText.setForeground(Color.blue);
				messageText.setText("Congratulations! You won!");
				winnerDecided = true;
				updateWins();
			}else if(opponentPlayerPositions.containsAll(l)) {
				messageText.setForeground(Color.red);
				messageText.setText("You lost! Better luck next time!");
				winnerDecided = true;
				updateLoss();
			}else if(player1Positions.size() + opponentPlayerPositions.size() == 9 && winnerDecided == false) {
				// If both the opponent and player's moves == 9, meaning if the 3x3 grid is filled,
				// and without a winner, the match will be considered a draw
				messageText.setText("It's a draw!");
				winnerDecided = true;
				updateDraw();
			}
		}

		return winnerDecided;
	}
	
	/* ----------------- UPDATING DB WITH WINS/LOSS/DRAWS - START ----------------- */
	
	// Update playern wins
	public void updateWins() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection c = DriverManager.getConnection(DB, DB_USER, DB_PASSWORD);
			Statement stmt = (Statement) c.createStatement();
			String sql = "UPDATE players SET wins = wins + 1 WHERE id = '" + playerLoginID + "'";
			int rs = stmt.executeUpdate(sql);

		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}
	
	// update player losses
	public void updateLoss() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection c = DriverManager.getConnection(DB, DB_USER, DB_PASSWORD);
			Statement stmt = (Statement) c.createStatement();
			String sql = "UPDATE players SET loss = loss + 1 WHERE id = '" + playerLoginID + "'";
			int rs = stmt.executeUpdate(sql);

		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}
	
	// update player draws
	public void updateDraw() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection c = DriverManager.getConnection(DB, DB_USER, DB_PASSWORD);
			Statement stmt = (Statement) c.createStatement();
			String sql = "UPDATE players SET draw = draw + 1 WHERE id = '" + playerLoginID + "'";
			int rs = stmt.executeUpdate(sql);

		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}
	
	/* ----------------- UPDATING DB WITH WINS/LOSS/DRAWS - END ---------------------------------- */

	// Main Method
	public static void main(String[] args) {
		Player player = new Player(600, 600);
		player.createGUI();
	}

}
