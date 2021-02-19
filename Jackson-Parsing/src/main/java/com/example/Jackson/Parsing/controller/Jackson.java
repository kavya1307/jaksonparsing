package com.example.Jackson.Parsing.controller;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController

public class Jackson {
	
	@RequestMapping(path = "/parse", method = RequestMethod.GET)
	public ResponseEntity<?> viewRes() throws FileNotFoundException, IOException, ParseException {
		
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		List<Map<String, Object>> mainlo = new ArrayList<Map<String, Object>>();
		
		LinkedHashSet<Object> set = new LinkedHashSet<Object>();
		LinkedHashSet<Object> nameset = new LinkedHashSet<Object>();
		LinkedHashSet<Object> srcset = new LinkedHashSet<Object>();
		
		JSONParser parser = new JSONParser();
		
		Object object = parser.parse(new FileReader("src/main/resources/file.json"));
		JSONArray jsonObject = (JSONArray) object;
		
		JsonNode node = objectMapper.readTree(jsonObject.toJSONString());
		
		List<String> msgList = node.findValuesAsText("msg");
		
		int i =0;
		
		for(String s: msgList) {
			String[] str =  s.split("\\s+");
			i = str.length;
		}
		for(JsonNode onode:node) {
			ObjectNode objNode = (ObjectNode) onode;
			objNode.put("msgLength", i);
			objNode.remove("message");
			objNode.remove("@timestamp");
			objNode.remove("destPort");
			objNode.remove("srcUserId");
			objNode.remove("srcTransPort");
			objNode.remove("syslog5424_pri");
			objNode.remove("@version");
			objNode.remove("host");
			objNode.remove("connectionId");
			objNode.remove("destTransIp");
			objNode.remove("id");
			objNode.remove("srcUser");
			
		}
		
	//return return	
		for(String s : node.findValuesAsText("destTransPort")) {
			set.add(s);
		}
		for (String s : node.findValuesAsText("destInterfaceName")) {
			nameset.add(s);
		}
		for(String s: node.findValuesAsText("srcInterfaceName")) {
			srcset.add(s);
		}
		
		for(Object obsrc : srcset) {
			for(Object obname : nameset) {	
			
				for(Object obj :set) {
				
					Map<String, Object> map=new LinkedHashMap<String, Object>();
					List<Object> list = new LinkedList<Object>();
					int in = Integer.valueOf((String) obj);
			
					for(JsonNode nodes:node) {
					
						if(nodes.get("destTransPort").asInt()==in) {
						
							if(nodes.get("destInterfaceName").asText().equals(obname)) {
								if(nodes.get("srcInterfaceName").asText().equals(obsrc)) {
									list.add(nodes);
									
								}
							}
						}
					}	
					if(!(list.isEmpty())) {
					map.put("destTransPort", in);
					map.put("destInterfaceName",obname);
					map.put("srcInterfaceName", obsrc);
					map.put("topHits", list);
					mainlo.add(map);
					}
				}
			}
		}
		return ResponseEntity.ok(mainlo);
		
	}
}
		
