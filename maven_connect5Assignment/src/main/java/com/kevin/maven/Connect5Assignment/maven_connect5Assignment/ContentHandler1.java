package com.kevin.maven.Connect5Assignment.maven_connect5Assignment;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

class ContentHandler1 extends AbstractHandler {

	private String playersJoined = "";
	private ArrayList<String> nameList = new ArrayList<String>();
	//turn tracker keeps track of whose turn it is. Each number in turntracker corresponds to 2 player in active player.
	//2 * turnTracker index + turntracker[index], turntacker[3] = 1,    3* 2 + 1 => 7 
	//i.e. turntracker = [1,0,1] corresponds to active player 1,2,5. 
	private ArrayList<Integer> turnTracker = new ArrayList<Integer>();
	private HashMap<String, int[][]> Games = new HashMap<String, int[][]>();
	private int[][] connect5Board;
	private ArrayList<String> activePlayers = new ArrayList<String>();
	private String TurnString;
	private String clientOnServer = "";
	private int boardColumnNum = 9;
	private int boardRowNum = 6;

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		try {
			response.setContentType("text/html;charset=utf-8");
			response.setStatus(HttpServletResponse.SC_OK);
			baseRequest.setHandled(true);
			
			
			if (request.getParameter("name") != null) {
				//enters client into waiting for game pool
				nameList.add(request.getParameter("name"));
				System.out.println(request.getParameter("name"));
			}

			if (request.getParameter("number") != null) {
				//makes move. takes client name and column number as input
				response.getWriter().println( makeMove(request.getParameter("number")));
			
			}
			
			
			if (request.getParameter("lobby") != null) {
				//if lobby has 2 or more people waiting for game then it pairs them off and creates games for them
				response.getWriter().println(enterLobby());
				


			}
			if (request.getParameter("disconnect") != null) {
				//disconencts opponent. Removes both players from game and adds stilla ctive client to waiting for game lobby
				disconnectOpponent(request.getParameter("disconnect"));

			}
//			if (request.getParameter("turn") != null) {
//				String user = request.getParameter("turn");
//				if (clientOnServer == "" || clientOnServer.equals(user)) {
//
//					clientOnServer = user;
//					System.out.println("client: " + clientOnServer);
//					response.getWriter().println(true);
//				} else {
//					System.out.println("client: " + clientOnServer);
//					response.getWriter().println(false);
//				}
//
//			}

			clientOnServer = "";
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String makeMove(String moveInput) {
		String stringResponse = new String();
		//moveInput is in the form moveNum,Username
		String[] data = moveInput.split(",", 2);
		//System.out.println("data[0] " + data[0]);
		int move = Integer.parseInt(data[0]) - 1; // Pankaj
		//System.out.println("data[1] " + data[1]);
		String User = data[1]; // New York,USA
		
		// moveInput 0 corresponds to send board state. 1-n represents columns.
		//any other input is out of bounds and client is prompted to enter valid input
		if (move < -1 || move > boardColumnNum - 1){
			return "Invalid Move! Try again";
		}
		
			//gets user's playing board
			connect5Board = (int[][]) Games.get(User);
			//move above 0 represents actual column move
			if (move >= 0) {
				//if Games.get(User) == null then user has been disconnected
				//The server has no ongoing games for client. As the client sent a move message it thinks its in a game. Thus
				//it must have been disconnected
				if (Games.get(User) == null){
					
					nameList.add(User);
					return "You were disconencted due to inactivity ";
				}
				
				int index = activePlayers.indexOf(User);
				
				Boolean full = true;
				//checks if column on board has been filled to top
				//it also changes column value to 1 or 2. move number translates to column number.
				for (int i = boardRowNum - 1; i >= 0; i--) {
					if (connect5Board[move][i] == 0) {
						System.out.println(i);
						//changes board from 0(empty) to 1(player 1 coin) or 2(player 2 coin).
						//it does this based on what number the player represents in turntracker
						connect5Board[move][i] = 1 + turnTracker.get((int) Math.floor(index / 2));
						full = false;
						break;
					}

				}
				// prompts user 
				if(full){
					return "Invalid Move! Try again";
				}
				//turn tracker keeps track of whose turn it is. Each number in turntracker corresponds to 2 player in active player.
				//2 * turnTracker index + turntracker[index], turntacker[3] = 1,    3* 2 + 1 => 7 
				//i.e. turntracker = [1,0,1] corresponds to active player 1,2,5. 
				if (index % 2 == 1) {
					turnTracker.set((int) Math.floor(index / 2),
							turnTracker.get((int) Math.floor(index / 2)) - 1);
				} else {
					turnTracker.set((int) Math.floor(index / 2),
							turnTracker.get((int) Math.floor(index / 2)) + 1);
				}
				//returns oppoennets user name.
				TurnString = getWhoseTurn();

				
				

			}
			//prints board state
			String boardLayout = "";
			for (int i = 0; i < boardRowNum; i++) {
				for (int j = 0; j < boardColumnNum; j++) {
					boardLayout += " [" + String.valueOf(connect5Board[j][i]) + "] ";
				}
				boardLayout += "\n";
			}
			String instruction = " Please enter column 1 - " + boardColumnNum;
			System.out.println(boardLayout + TurnString);
			//checks if any game victory conditions are reached
			if (gameEnd(connect5Board)) {
				stringResponse = boardLayout + " " + User + " wins";
				//kicks out loser and re-adds victor to waiting to play lobby.
				disconnectOpponent(User);
			} else {
				stringResponse = boardLayout + instruction + TurnString;
			}

		
		
		
		return stringResponse;
	}

	public void disconnectOpponent(String string) {
		int index = activePlayers.indexOf(string);
		//removes game data from turnTracker
		turnTracker.remove((int) Math.floor(index / 2));
		//removes games player reference. It finds index of games by finding turntracker's corresponding games
		if (index % 2 == 1) {
			
			Games.remove(activePlayers.remove(index));
			Games.remove(activePlayers.remove(index - 1));

		} else {

			Games.remove(activePlayers.remove(index + 1));
			Games.remove(activePlayers.remove(index));

		}
		//returns user to lobby
		nameList.add(string);
		
	}

	public String enterLobby(){
		//if more than 1 player waiting to play then we can add them to game
		while (nameList.size() > 1) {
			
			int[][] newConnect5Board = new int[boardColumnNum][boardRowNum];
			System.out.println("nameList: " +nameList.toString());
			//if two clients cause this code passage to run simultanously then errors can occur.
			//Both clients would be doing the same task. However it can interfere with each other.
			//Try catch knocks one client out so the other client can proceed.
			try{
			activePlayers.add(nameList.get(0));
			Games.put(nameList.remove(0), newConnect5Board);
			
			activePlayers.add(nameList.get(0));
			Games.put(nameList.remove(0), newConnect5Board);
			turnTracker.add(1);
		     }
		      catch (IndexOutOfBoundsException e) { 
		
		     }
	
		}
		//players joined is outputted. This is to inform client that they are waiting or that they have been disconencted 
		//from old game
		playersJoined = "";
		
		if (nameList.size() > 0) {
			playersJoined = "The players waiting to play are	" + nameList.get(0)+ "	";
		}
		
		//String activePlayersString = "The current people playing are: ";		
		//this tells the user whose turn it is
		TurnString = getWhoseTurn();

		//outputs response to client
		return playersJoined + " \n" + TurnString;
	}
	private String getWhoseTurn() {
		
		String whoseTurnString = "\n Waiting for these players to make their move,";
		//finds out whose turn it is based on turnTracker
		//turntracker implementation explained up top in declerations
		for (int i = 0; i < turnTracker.size(); i++) {
			whoseTurnString += activePlayers.get(turnTracker.get(i) + i * 2) + ",";
		}
		return whoseTurnString;

	}

	public boolean gameEnd(int[][] connect5Board) {
		//checks to see if there are any 5 in a rows
		//counterNum runs the loop twice to account for both types of discs being in a row.
		for(int counterNum = 1; counterNum < 3; counterNum++){
			//vertice runs twice as some values are different depending on whether its checking horizontally or vertically.
			//this is because it must read board in different directions(horzontally/vertically).
			for(int vertice = 0; vertice < 2; vertice++){
				int maxJ = 0;
				int maxI = 0;
				if(vertice == 0){
					maxJ = connect5Board.length;
					maxI = connect5Board[0].length;
				}else{
					maxJ = connect5Board[0].length;
					maxI = connect5Board.length;
				}
				
				for (int j = 0; j < maxJ; j++) {
					int counter1 = 0;
					
					//checks if there are any vertical 5 in a row
					//counter 1 counts player 1 coins. counter2 counts player 2 coins
					for (int i = maxI - 1; i >= 0; i--) {
						int compare = 0;
						if(vertice == 0){
							compare = connect5Board[j][i] ;
						}else{
							compare = connect5Board[i][j] ;
						}
						//checks if counter type is there. If it is then select the next one beside it.
						//if theres five in a row print true, otherwise print false
						if (compare == counterNum) {
							counter1++;
							if (counter1 > 4) {
								return true;
							}
						} else {
							counter1 = 0;
						}
						
					}
				}
			}
		}

		
		
		//get horizontal length and minus 4 as it needs 4 extra spaces to get 5 in a row.
		// ie Theres no point checking further accross as getting five in a row starting in this position facing that direction is impossible
		for (int i = 0; i < boardColumnNum - 4; i++) {
			for (int j = boardRowNum - 5; j > -1; j--) {
			
				if (CheckDiaganol(i, j, connect5Board) == true) return true;	
			}
			
		}

		return false;
	}

	private boolean CheckDiaganol(int x, int y, int[][] connect5Board) {
		
		//finds out if there is more space horizontally or vertically down from this position as this is the limiting factor.
		//this is to prevent it tryna check for a disc out of bounds.
		int pivot = ((boardRowNum  - y < boardColumnNum - x) ? boardRowNum  - y  :  boardColumnNum - x);

		
		int counter = 0;
		
		for(int counterNum = 1; counterNum < 3; counterNum++){
			//vertice swaps it from LtR(left to right) to RtL(right to left). I.e. it takes the top left position and 
			//then checks for a diaganol from top left to bottom right. When vertice is swapped it takes the top right position
			//and checks for a diaganol from top right to bottom left.
			for(int vertice = 0; vertice< 2; vertice++){
				//checks along the diaganol
				for (int j = 0; j < pivot; j++) {
					int compare = 0;
					if(vertice == 0){
						compare = connect5Board[j + x][j + y] ;
					}else{
						compare = connect5Board[boardColumnNum - 1 - j - x][boardRowNum - 1  - j -y] ;
					}
					if (compare == counterNum) {
						counter++;
						if (counter > 4) {
							return true;
						}
					} else {
						counter = 0;
					}
					
				}
			}
		}
		
		return false;

	}
	public int getBoardColumnNum() {
		//this is to allow unitTests to work with different board sizes
		return boardColumnNum;
	}
	public int getBoardRowNum() {
		//this is to allow unitTests to work with different board sizes
		return boardRowNum;
	}


}