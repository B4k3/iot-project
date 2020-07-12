package iot.unipi.it;

import org.eclipse.californium.core.CoapClient;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Bin {
	CoapClient bin_node;
	String id;
	BinType type;
	int capacity;
	int status;
	int locked;
	int percentage;
	
	
	public Bin (String BinName) {
		this.id = BinName;
		
		this.capacity = -1;
		this.status = -1;
		this.type = BinType.initFromInt(1);
		this.locked = 0;
		this.percentage = 0;
		
		System.out.print("Bin "+this.id+" added!\n");
	}
	
	public void empty () {
		
	}
	
	public void updatebin () {
		
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
