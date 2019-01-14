package com.kevin.maven.Connect5Assignment.maven_connect5Assignment;


import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     * @throws Exception 
     */
	ContentHandler1 Handler;
	ServerClass server = new ServerClass();
	private int[][] connect5Board;
	private int boardRowNum;
	private int boardColumnNum;
    public AppTest( String testName ) throws Exception
    {
        super( testName );
        
        
    }
    protected void setUp() throws Exception {

    	super.setUp();
    	Handler = new ContentHandler1();
    	boardRowNum = Handler.getBoardRowNum();
    	boardColumnNum = Handler.getBoardColumnNum();
        
        server.startServer(Handler);

    }
    protected void tearDown() throws Exception {

    	super.tearDown();
    	server.stop();
    	

    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
        
    }

    /**
     * Rigourous Test :-)
     * @throws Exception 
     */
    
	public void testApp() throws Exception
    {
    	
    	Client ClientA = new Client();
    	Client ClientB = new Client();
    	Client ClientC = new Client();
    	Client ClientD = new Client();
    	Client ClientE = new Client();
    	
    	ClientA.start(new Scanner("ClientA"));
    	
    	String output = checkLobby().getContentAsString();
   	    assertTrue(output.equals("The players waiting to play are	ClientA	"
    			+ " \n\n Waiting for these players to make their move,\r\n"));
    	
    	ClientB.start(new Scanner("ClientB"));

    	ClientB.makeMove(new Scanner("3"));

    	ClientC.start(new Scanner("CLientC"));
    	
    	ClientA.makeMove(new Scanner("4"));

    	ClientD.start(new Scanner("ClientD"));
    	
    	ClientC.makeMove(new Scanner("7"));

    	ClientE.start(new Scanner("ClientE"));
    	
    	 
    	System.out.println(checkLobby().getContentAsString());
    	//TimeUnit.MILLISECONDS.sleep(100);
    	 output = checkLobby().getContentAsString();
    	//System.out.println("testappx: " + output);
    	assertTrue(output.equals("The players waiting to play are\tClientE\t \n\n Waiting for these players to make their move,ClientB,ClientD,\r\n"));
        assertTrue(true);
    }
    
    public void testDisconnect() throws Exception{
    	
    	Client ClientA = new Client();
    	Client ClientB = new Client();
    	Client ClientC = new Client();
    	Client ClientD = new Client();
    	
    	ClientA.start(new Scanner("ClientA"));
    	ClientB.start(new Scanner("ClientB"));
    	ClientC.start(new Scanner("CLientC"));
    	ClientB.makeMove(new Scanner("3"));
    	
    	ClientA.makeMove(new Scanner("3"));
    	System.out.println("sign");
    	Handler.disconnectOpponent("ClientA");
    	TimeUnit.MILLISECONDS.sleep(100);
    	System.out.println("sign2");
    	String output = checkLobby().getContentAsString();
    	System.out.println("testdisconenctX:" + output);
    				
    	assertTrue(output.equals(" \n\n Waiting for these players to make their move,CLientC,\r\n"));
    
    	output =  ClientB.inputLoop(new Scanner("3")).getContentAsString();
    	System.out.println("testdisconenctX2:" + output);
    	assertTrue((output.equals("You were disconencted due to inactivity \r\n")));
    	
    	ClientD.start(new Scanner("CLientD"));
    	
    	output = checkLobby().getContentAsString();
    	System.out.println("testdisconenct3:" + output);
    	assertTrue((output.equals(" \n\n Waiting for these players to make their move,CLientC,CLientD,\r\n")));
    	ClientD.makeMove(new Scanner("3"));
    	
    	output = checkLobby().getContentAsString();
    	assertTrue((output.equals(" \n\n Waiting for these players to make their move,CLientC,ClientB,\r\n")));
    	
    	
    	
    }
    public void testWin(){
	    //	assertSame(3,3);
	   	 assertFalse(Handler.gameEnd(new int[boardColumnNum][boardRowNum]));
	   	
	   	 connect5Board = new int[boardColumnNum][boardRowNum];
	   	 
	   
		for(int j = 0; j < 5; j++){
			assertFalse(Handler.gameEnd(connect5Board));
			connect5Board[0][j] = 1;
		}
		assertTrue(Handler.gameEnd(connect5Board));
		connect5Board = new int[boardColumnNum][boardRowNum];
	   	for (int i = 0;i< 5; i++){ 
	   		assertFalse(Handler.gameEnd(connect5Board));
	   		connect5Board[i][0] = 1;
	   	}
	   	assertTrue(Handler.gameEnd(connect5Board));
		connect5Board = new int[boardColumnNum][boardRowNum];
	   	for (int i = 0;i< 5; i++){ 
	   		assertFalse(Handler.gameEnd(connect5Board));
	   		connect5Board[i][i] = 1;
	   	}
	   	
	   	
   	    assertTrue(Handler.gameEnd(connect5Board));
   	 connect5Board = new int[boardColumnNum][boardRowNum];
   	int max = ((boardRowNum  < boardColumnNum) ? boardRowNum  :  boardColumnNum);
   	 for (int i = max - 1;i>= max-5; i--){ 
	   		assertFalse(Handler.gameEnd(connect5Board));
	   		connect5Board[i][i] = 1;
	   	}
	   	
	   	
	    assertTrue(Handler.gameEnd(connect5Board));
  
   }
    
   public void testColumnFull() throws Exception{
	   
	    Client ClientA = new Client();
   		Client ClientB = new Client();

   	
	   	ClientA.start(new Scanner("ClientA"));
	   	ClientB.start(new Scanner("ClientB"));
	   
	   	for(int i = 0; i < boardRowNum - 1; i++){
	   		if(i%2 == 0){
	   		ClientB.makeMove(new Scanner("3"));
	   		TimeUnit.MILLISECONDS.sleep(100);
	   		}else{
	   		ClientA.makeMove(new Scanner("3"));
		   	TimeUnit.MILLISECONDS.sleep(100);
	   		}
	   	}

	   	String output = ClientA.inputLoop(new Scanner("3")).getContentAsString();
	   	//System.out.println("outputfull: " + output);
	   	assertFalse(output.equals("Invalid Move! Try again\r\n")); 
	   	output = ClientB.inputLoop(new Scanner("3")).getContentAsString();
		assertTrue(output.equals("Invalid Move! Try again\r\n"));

	   	
   }
   
  public void testInvalidMove() throws Exception{
	  Client ClientA = new Client();
 		Client ClientB = new Client();

 	
	   	ClientA.start(new Scanner("ClientA"));
	   	ClientB.start(new Scanner("ClientB"));
	   
	   	String output = ClientB.inputLoop(new Scanner("-1")).getContentAsString();
	   	
	   	
    	assertTrue(output.equals("Invalid Move! Try again\r\n"));
    	
	  //.makeMove();
	  output = ClientB.inputLoop(new Scanner("11")).getContentAsString();
	  assertTrue(output.equals("Invalid Move! Try again\r\n"));
	  output = ClientB.inputLoop(new Scanner("2")).getContentAsString();
	  assertFalse(output.equals("Invalid Move! Try again\r\n")); 
  }
  
  public void testLobby() throws Exception{
  	
	  final HttpClient client = new HttpClient();
      client.start();
	  client.GET("http://localhost:2005/square/?name=" +  String.valueOf("ClientA"));
	  ContentResponse res =client.GET("http://localhost:2005/square/?lobby=0");
	  assertTrue(res.getContentAsString().equals("The players waiting to play are\tClientA\t \n\n Waiting for these players to make their move,\r\n"));
 	  client.GET("http://localhost:2005/square/?name=" +  String.valueOf("ClientB"));
 	  res =client.GET("http://localhost:2005/square/?lobby=0");
 	  assertTrue(res.getContentAsString().equals(" \n\n Waiting for these players to make their move,ClientB,\r\n"));
    
  }
    
    
    private ContentResponse checkLobby() throws Exception{
    	
   	 	final HttpClient client = new HttpClient();
        client.start();

       return client.GET("http://localhost:2005/square/?lobby=0");
    }
    
    
}
