package iot.unipi.it;

import org.eclipse.californium.core.CoapServer;



public class MyServer extends CoapServer {

	public static void main(String[] args) {
		BinManager mybins = new BinManager();
		MyWebServer webServer = new MyWebServer(mybins);
		MyServer server = new MyServer();
		server.add(new CoaAPResourceRegister("register", mybins));
		
		server.start();
		webServer.start();
		
	}
	
}
