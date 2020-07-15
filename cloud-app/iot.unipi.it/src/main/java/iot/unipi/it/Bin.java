package iot.unipi.it;

import java.net.InetAddress;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import spark.utils.IOUtils;



public class Bin {
	InetAddress addr;
	CoapClient bin_node;
	CoapObserveRelation relation;
	
	String id;
	BinType type;
	int capacity;
	int status;
	int locked;
	int percentage;
	
	public class bin_coap_handler implements CoapHandler {
			Bin bin;
			public bin_coap_handler(Bin bin) {
				this.bin = bin;
			}
			@Override public void onLoad(CoapResponse response) {
				String content = response.getResponseText(); 
				JSONObject status;
				try {
					status = (JSONObject) JSONValue.parseWithException(content);
				}
				catch (ParseException e) {
					e.printStackTrace();
					return;
				}
				this.bin.status = Integer.parseInt(status.get("status").toString());
				this.bin.locked = Integer.parseInt(status.get("locked").toString());
				int tmp = ((this.bin.status)*100)/(this.bin.capacity);
				
				if(tmp >= 100)  
					this.bin.percentage = 100;
				else
					this.bin.percentage = tmp;
					
				System.out.println("bin:"+ this.bin.id +" percentage-update:" + Integer.toString(tmp));
			}
			@Override public void onError() {
				System.err.println("-Failed--------"); 
			}
		
	}
	
	public Bin (String BinName) {
		this.id = BinName;
		
		this.capacity = -1;
		this.status = -1;
		this.type = BinType.initFromInt(1);
		this.locked = 0;
		this.percentage = 0;
		
		System.out.print("Bin "+this.id+" added!\n");
	}

	public Bin (byte[] s,InetAddress src) {
		JSONObject bin;
		try {
			String json = new String(s); //IOUtils.toString(s);
			bin = (JSONObject) JSONValue.parseWithException(json);
		}
		catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		
		this.id = bin.get("id").toString();
		this.capacity = Integer.parseInt(bin.get("capacity").toString());
		this.status = 0;
		this.type = BinType.initFromInt(Integer.parseInt(bin.get("type").toString()));
		this.locked = 0;
		this.percentage = 0;
		this.bin_node = new CoapClient("coap://["+ src.getHostAddress().toString() +"]/status");
		this.relation = bin_node.observe(new bin_coap_handler(this));
		this.addr = src;
		System.out.print("Bin "+this.id+" added!\n addr:" + src.getHostAddress().toString() +"\n");
	}
	
	public void empty () {
		CoapClient empty = new CoapClient("coap://["+ this.addr.getHostAddress().toString() +"]/lock");
		CoapResponse res = empty.post("", MediaTypeRegistry.APPLICATION_JSON);
		
		JSONObject response;
		try {
			
			response = (JSONObject) JSONValue.parseWithException(new String(res.getPayload()));
		}
		catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		
		System.out.print("3d!\n");
		if(response.get("result").toString().equals("OK")) {
			this.status = 0;
			this.percentage = 0;
			System.out.print("4!\n");
		}
	}
	
	public void lock () {
		CoapClient lock = new CoapClient("coap://["+ this.addr.getHostAddress().toString() +"]/lock");
		CoapResponse res = lock.post("", MediaTypeRegistry.APPLICATION_JSON);
		
		JSONObject response;
		try {
			
			response = (JSONObject) JSONValue.parseWithException(new String(res.getPayload()));
		}
		catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		
		if(response.get("result").toString().equals("OK")) {
			this.locked = 1;
		}
		
	}
	
	public void unlock () {
		CoapClient unlock = new CoapClient("coap://["+ this.addr.getHostAddress().toString() +"]/unlock");
		CoapResponse res = unlock.post("", MediaTypeRegistry.APPLICATION_JSON);
		
		JSONObject response;
		try {
			
			response = (JSONObject) JSONValue.parseWithException(new String(res.getPayload()));
		}
		catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		
		if(response.get("result").toString().equals("OK")) {
			this.locked = 0;
		}
		
	}
	
	public String getBinId () {
		return this.id;
	}
	
	public JSONObject getBinJsonData () {
		JSONObject dataset = new JSONObject();
		dataset.put("id",this.id);
		dataset.put("capacity", this.capacity);
		dataset.put("status", this.status);
		dataset.put("locked", this.locked);
		dataset.put("type",this.type.toString().toLowerCase());
		dataset.put("percentage", this.percentage);
		return dataset;
	}

}
