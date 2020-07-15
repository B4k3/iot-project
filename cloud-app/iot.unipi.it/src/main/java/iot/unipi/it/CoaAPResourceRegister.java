package iot.unipi.it;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class CoaAPResourceRegister extends CoapResource {
	private BinManager myBins;
	
	public CoaAPResourceRegister (String name, BinManager binmng) {
		super (name);
		setObservable (true);
		this.myBins = binmng;
	}
	
	/*public void handleGET(CoapExchange exchange) {
		exchange.respond("hello world");
	}*/
	
	public void handlePOST(CoapExchange exchange) {
		//create resource
		//Response response = new Response(ResponseCode.CONTENT);
		String s;
		byte[] request = exchange.getRequestPayload();
		/*try {
			s = new String(request, "UTF-8");
		}
		catch(UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}*/
		InetAddress addr =  exchange.getSourceAddress();
		this.myBins.addBin(request, addr);
		exchange.respond("Registered!");
	}

}
