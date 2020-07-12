package iot.unipi.it;



import java.util.HashMap;
import java.util.Map;

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
		//staticFiles.location("/public");
		staticFiles.location("/template");
		get("/login", (req, res) -> this.MyBins.getBinJsonStatus());
		
		post("/login", (req, res) -> this.MyBins.getBinJsonStatus());
		
		get("/bins", (req, res) -> this.MyBins.getBinJsonStatus());
		
		get("/binmanager", (req, res) -> {
			Map<String, Object> model = new HashMap<>();
			return new ModelAndView(model, "index.html");
		}, new VelocityTemplateEngine());
		
		get("/updatebins", (req, res) -> this.MyBins.getBinJsonStatus());
		
		post("/emptybin", (req, res) -> this.MyBins.getBinJsonStatus());
		
		post("/lockbin", (req, res) -> this.MyBins.getBinJsonStatus());
		
		post("/unlockbin", (req, res) -> this.MyBins.getBinJsonStatus());
	}

}
