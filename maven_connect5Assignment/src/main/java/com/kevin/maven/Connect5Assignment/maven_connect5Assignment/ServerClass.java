package com.kevin.maven.Connect5Assignment.maven_connect5Assignment;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
public class ServerClass {
	
	
	Server server;
    public void startServer(ContentHandler1 Handler) throws Exception{
        int port=2005;
        //Server server= new Server(port); 
        server = new Server(port);

        ContextHandler context = new ContextHandler();
        context.setContextPath("/square");
        context.setAllowNullPathInfo(true);
        context.setResourceBase(".");
        context.setClassLoader(Thread.currentThread().getContextClassLoader());
        context.setHandler(Handler);
        server.setHandler(context);

        server.start();
        
    }



    public static void main(String []args) throws Exception{
    	ServerClass server= new ServerClass();
        server.startServer(new ContentHandler1());
    }



	public void stop() throws Exception {
		// TODO Auto-generated method stub
		server.stop();
	}

}