package iot.unipi.it;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class BinManager {
	List<Bin> myBins;
	
	public BinManager () {
		myBins = new ArrayList<Bin>();
	}
	
	public void addBin (String BinName) {
		this.myBins.add(new Bin(BinName));
	}
	
	public void rmBin () {
		
	}
	
	public void getBin () {
		
	}
	
	public void updateBin () {
		
	}
	
	public String getBinJsonStatus () {
		JSONArray dataset = new JSONArray();
		this.myBins.forEach(bin -> {
			JSONObject bindata = bin.getBinJsonData();
			dataset.add(bindata);
		});
		return dataset.toString();
	}

}
