
import java.awt.List;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;

public class GameServer {

	private ServerSocket ss;
	private int numPlayers;

	private ServerSideConnection player1;
	private ServerSideConnection player2;

	private int turnsMade;

	static ArrayList<Integer> player1Positions = new ArrayList<Integer>();
	static ArrayList<Integer> player2Positions = new ArrayList<Integer>();

	// Check if the server is running
	private boolean serverRunning;
	
	// Check if the game is running two players in the lobby
	private boolean playersFound;

	public GameServer() {
		System.out.println("---- GameServer ----");
		numPlayers = 0;
		serverRunning = true;
		playersFound = false;
		try {
			ss = new ServerSocket(3124);
		} catch (Exception e) {
			System.out.println("IOException from GameServer Consutructor");
		}
	}

	public void acceptConnections() {
		try {
			System.out.println("Waiting for connections...");
			while (numPlayers < 2) {
				Socket s = ss.accept();
				numPlayers++;
				System.out.println("Player #" + numPlayers + " has connected.");
				ServerSideConnection ssc = new ServerSideConnection(s,  numPlayers);

				if (numPlayers == 1) {
					player1 = ssc;
				}else {
					player2 = ssc;
					playersFound = true;					
					player1.sendPlayersFound(playersFound);
				}
				Thread thread = new Thread(ssc);
				thread.start();
			}

			System.out.println("Lobby is now full.");
		} catch (Exception e) {
			System.out.println("IOException from acceptConnections() SSC");
		}
	}

	private class ServerSideConnection implements Runnable{
		private Socket socket;
		private DataInputStream dataIn;
		private DataOutputStream dataOut;
		private int playerID;

		public ServerSideConnection(Socket s, int id) {
			socket = s;
			playerID = id;

			try {
				dataIn = new DataInputStream(socket.getInputStream());
				dataOut = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				System.out.println("IOException from SSC Constructor");
			}
		}

		public void run() {
			try {
				dataOut.writeInt(playerID);
				dataOut.flush();

				while(true) {
					if(playerID == 1) {
						int selectedNum = dataIn.readInt();
						player1Positions.add(selectedNum);
						System.out.println("Player #1 clicked panel #" + selectedNum);
						player2.sendButtonNum(selectedNum);
					}else {
						int selectedNum = dataIn.readInt();
						player2Positions.add(selectedNum);
						System.out.println("Player #2 clicked panel #" + selectedNum);
						player1.sendButtonNum(selectedNum);
					}

					checkWinner();

					if(serverRunning == false) {
						System.out.println("Game over.");
						break;
					}
				}

				player1.closeConnection();
				player2.closeConnection();
			} catch (IOException e) {
				System.out.println("IOException from run() SSC");
			}
		}

		public void sendButtonNum(int n) {
			try {
				dataOut.writeInt(n);
				dataOut.flush();
			} catch (IOException e) {
				System.out.println("IOException from sendButtonNum() SSC");
			}
		}
		
		
		/* ----------------- WIP - DISALLOW BUTTONS ON BOTH ENDS IF THERE IS ONLY ONE PLAYER IN THE LOBBBY  ----------------- */
		
		public void sendPlayersFound(Boolean gameRunning) {
			try {
				dataOut.writeBoolean(gameRunning);
				dataOut.flush();
			} catch (IOException e) {
				System.out.println("IOException from sendPlayersFound() SSC");
			}
		}

		
		/* ----------------- WIP - DISALLOW BUTTONS ON BOTH ENDS IF THERE IS ONLY ONE PLAYER IN THE LOBBBY  ----------------- */

		
		public void closeConnection() {
			try {
				socket.close();
				System.out.println("--- CONNECTION CLOSED ---");
			} catch (Exception e) {
				System.out.println("IOException on closeConnection() SSC");
			}
		}

	}




	public void checkWinner() {

		ArrayList<Integer> topRow = new ArrayList<Integer>(Arrays.asList(1,2,3));
		ArrayList<Integer> midRow = new ArrayList<Integer>(Arrays.asList(4,5,6));
		ArrayList<Integer> bottomRow = new ArrayList<Integer>(Arrays.asList(7,8,9));

		ArrayList<Integer> leftCol = new ArrayList<Integer>(Arrays.asList(1,4,7));
		ArrayList<Integer> midCol = new ArrayList<Integer>(Arrays.asList(2,5,8));
		ArrayList<Integer> rightCol = new ArrayList<Integer>(Arrays.asList(3,6,9));

		ArrayList<Integer> diag1 = new ArrayList<Integer>(Arrays.asList(1,5,9));
		ArrayList<Integer> diag2 = new ArrayList<Integer>(Arrays.asList(7,5,3));


		ArrayList<ArrayList<Integer>> winningConditions = new ArrayList<ArrayList<Integer>>();
		winningConditions.add(topRow);
		winningConditions.add(midRow);
		winningConditions.add(bottomRow);
		winningConditions.add(leftCol);
		winningConditions.add(midCol);
		winningConditions.add(rightCol);
		winningConditions.add(diag1);
		winningConditions.add(diag2);

		for(ArrayList<Integer> l : winningConditions) {
			if(player1Positions.containsAll(l) || player2Positions.containsAll(l) || player1Positions.size() + player2Positions.size() == 9) {
				serverRunning = false;
			}
		}
	}


	public static void main(String[] args) {
		GameServer gs = new GameServer();
		gs.acceptConnections();
	}


}
