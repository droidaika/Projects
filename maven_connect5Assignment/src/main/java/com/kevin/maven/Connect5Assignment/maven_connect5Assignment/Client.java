package com.kevin.maven.Connect5Assignment.maven_connect5Assignment;

import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
public class Client {
		
	
	private  ContentResponse res;
	
	private   String myUserName;
	private  int MoveAttempts= 0;
	final HttpClient client = new HttpClient();
    public static void main(String[] args) throws Exception  {
    	Client thisClient = new Client();
    	Scanner scanner = new Scanner(System.in);
    	thisClient.start(scanner);
    	thisClient.makeMove(scanner);
    }
    
   public void start(final Scanner scanner) throws Exception{
	   //adds client to player pool.
        client.start();

        //sends you information about the server
        res = client.GET("http://localhost:2005/square/?lobby=0");
        
        System.out.println(res.getContentAsString());
        System.out.println("Please enter your username");
        
        
        myUserName = scanner.next();
        System.out.println( myUserName);
       
        //this tells the server your desired username. The client gets added to the waiting room or gets added into the game
        res = client.GET("http://localhost:2005/square/?name=" +  String.valueOf(myUserName));
        
        //scanner.close();
       //client.stop();
    }
   
    
    public  void makeMove(final Scanner scanner){
    	//Client tries to make a move every n seconds. It gets the server information.
    	//If server tells it to make a move then it proceeds.
    	 Timer t = new Timer();
         t.schedule(new TimerTask() {
             @Override
             public void run() {
             	try {
             	res = client.GET("http://localhost:2005/square/?lobby=0");
             	 
             	//if clients name is in data then proceed with move.
             	String[] data = res.getContentAsString().split(",");
             	System.out.println(res.getContentAsString());
             	//move attempts keep track of how many times you checked if you could attempt move. If this gets high enough
             	//then other client is disconnected due to inactivity
             	MoveAttempts++;
             	
             	String[] wait = res.getContentAsString().split("	");
             	int waitMax = wait.length -1;
             	for(int i = 1; i < waitMax; i++){
             		if(wait[i] == myUserName){
             			//resets moveAttemps counter if you are waiting to join a game. 
             			MoveAttempts = 0;
             		}
             	}
             	
             	int dataLength = data.length;
              	for(int i = 1; i<dataLength; i++){
              		//checks if its your turn
              		if(data[i].equals(myUserName)){//String.valueOf(myUserName) + "\r\n"){
//              			String serverFree ="";
//              			System.out.println("server: " + serverFree);
//              			while(!serverFree.equals("true\r\n\r\n")){
//              				res = client.GET("http://localhost:2005/square/?turn=" + myUserName);
//              				serverFree = res.getContentAsString();
//              				System.out.println("ser: " + serverFree);
//              				if(!serverFree.equals("true\r\n\r\n")){
//              					System.out.println("wait");
//              					TimeUnit.SECONDS.sleep(1);
//                  			}
//              			}
              			System.out.println("free");
              			
              			//resets to 0 as your move attempt was successful
              			MoveAttempts = 0;
              			//gets board state to tell client
              			res = client.GET("http://localhost:2005/square/?number=0" +  "," +String.valueOf(myUserName));
              			System.out.println(res.getContentAsString());
              			
              			
              	        
              	        do{
              	        	//gets input. repeats if invalid input entered such as out of bounds column number or if column is full
              	        	res = inputLoop(scanner);
              	        	
              	        }while(res.equals("Invalid Move! Try again"));
              	        
              	        //if the server waits to long for ur input. The other client disconnects you.
              	        //WHen you enter input the server readds you to waiting for game pool
              			if(res.equals("You were disconencted due to inactivity \r\n")){
              				client.GET("http://localhost:2005/square/?name=" +String.valueOf(myUserName));
              			}
 	       				
 	       	            System.out.println(res.getContentAsString());
 	                   	
 	
              		}
              	}
              	//12 * 10. 2 minutes to make move
              	if(MoveAttempts>12){
              		//disconnects opponent
              		res = client.GET("http://localhost:2005/square/?disconnect=" + String.valueOf(myUserName));
              		System.out.println("The other user has timed out.");
              		MoveAttempts = 0;
              	}
              	} catch (InterruptedException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				} catch (ExecutionException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				} catch (TimeoutException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
                  
                 
             }

			
         }, 0, 10000);
    	
    }
    public ContentResponse inputLoop(Scanner scanner) throws InterruptedException, ExecutionException, TimeoutException {
    	int myMove;
    	ContentResponse response;
        System.out.println("It's your move");
  		myMove = scanner.nextInt();
  		//posts move to server
		response = client.GET("http://localhost:2005/square/?number=" +  String.valueOf(myMove) + "," +String.valueOf(myUserName));
		System.out.println("myMove is: " + myMove);
		return response;
	}
	
}
