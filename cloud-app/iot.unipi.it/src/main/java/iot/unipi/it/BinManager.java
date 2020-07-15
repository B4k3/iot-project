package iot.unipi.it;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class BinManager {
	List<Bin> myBins;
	
	public BinManager () {
		myBins = new ArrayList<Bin>();
	}
	
	public void addBin (byte[] s, InetAddress src) {
		Bin b = new Bin(s, src);
		String id = b.getBinId();
		final int[] bin_exist = {0};
		this.myBins.forEach(c -> {
			if(Objects.equals(c.getBinId(), id))
				bin_exist[0] = 1;
		});
		if(bin_exist[0] == 0)
			this.myBins.add(b);
		else
			System.out.print("Bin already added!\n");
	}
	
	public void emptyBin (int id) {
		this.myBins.forEach(c -> {
			if(Objects.equals(c.getBinId(), Integer.toString(id))) {
				c.empty();
			}
		});
	}
	
	public void lockBin (int id) {
		this.myBins.forEach(c -> {
			if(Objects.equals(c.getBinId(), Integer.toString(id)))
				c.lock();
		});
	}
	
	public void unlockBin (int id) {
		this.myBins.forEach(c -> {
			if(Objects.equals(c.getBinId(), Integer.toString(id)))
				c.unlock();
		});
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
