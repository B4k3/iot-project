package iot.unipi.it;



import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import static spark.Spark.*;

import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;



public class MyWebServer {
	private BinManager MyBins;
	
	public MyWebServer (BinManager myBins) {
		this.MyBins = myBins;
	}
	
	public void start() {
		port(8080);
		staticFiles.location("/template");
		
		get("/bins", (req, res) -> this.MyBins.getBinJsonStatus());
		
		get("/updatebins", (req, res) -> this.MyBins.getBinJsonStatus());
		
		post("/emptybin", (req, res) -> {
			JSONObject request;
			try {
				request = (JSONObject) JSONValue.parseWithException(new String(req.bodyAsBytes()));
			}
			catch (ParseException e) {
				e.printStackTrace();
				request = new JSONObject();
				request.put("result","ko");
				return request;
			}
			int ret = this.MyBins.emptyBin(Integer.parseInt(request.get("id").toString()));
			System.out.print("Empty bin id"+request.get("id").toString()+"\n");
			if(ret == 1)
				request.put("result", "ok");
			else
				request.put("result", "ko");
			return request.toString();
		});
		
		post("/lockbin", (req, res) -> {
			JSONObject request;
			try {
				
				request = (JSONObject) JSONValue.parseWithException(new String(req.bodyAsBytes()));
			}
			catch (ParseException e) {
				e.printStackTrace();
				request = new JSONObject();
				request.put("result","ko");
				return request;
			}
			this.MyBins.lockBin(Integer.parseInt(request.get("id").toString()));
			System.out.print("Lock bin id"+request.get("id").toString()+"\n");
			request.put("result", "ok");
			return request.toString();
		});
		
		post("/unlockbin", (req, res) -> {
			JSONObject request;
			try {
				request = (JSONObject) JSONValue.parseWithException(new String(req.bodyAsBytes()));
			}
			catch (ParseException e) {
				e.printStackTrace();
				request = new JSONObject();
				request.put("result","ko");
				return request;
			}
			this.MyBins.unlockBin(Integer.parseInt(request.get("id").toString()));
			System.out.print("unlock bin id"+request.get("id").toString()+"\n");
			request.put("result", "ok");
			return request.toString();
		});
		System.out.print("Webserver started on address http://127.0.0.1:8080 \n");

	}

}
