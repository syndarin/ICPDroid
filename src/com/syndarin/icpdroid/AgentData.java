package com.syndarin.icpdroid;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import com.google.gson.Gson;

import android.content.Context;

public class AgentData {

	private final static String AGENTS_DATA_FILE = "agents.json";

	private static Agent[] agents;
	
	public static void init(Context context) {
		try {
			String rawJSON = readAgentsAssetFile(context);
			agents = new Gson().fromJson(rawJSON, Agent[].class);
		} catch (IOException e) {
			e.printStackTrace();
			agents = new Agent[0];
		}
	}

	private static String readAgentsAssetFile(Context context) throws IOException {
		InputStream is = context.getAssets().open(AGENTS_DATA_FILE);
		Scanner scanner = new Scanner(is);
		StringBuilder builder = new StringBuilder();
		while (scanner.hasNextLine())
			builder.append(scanner.nextLine());
		scanner.close();
		return builder.toString();
	}
	
	public static String[] getCities(){
		String[] cities = new String[agents.length];
		for(int i=0; i<agents.length; i++)
			cities[i] = agents[i].getCity();
		
		return cities;
	}
	
	public static String[] getAgentPhones(int position){
		return agents[position].getPhones();
	}
	
	public static String getAgentEmail(int position){
		return agents[position].getEmail();
	}
	
	public static String getAgentPhone(int agentPosition, int phonePosition){
		return agents[agentPosition].getPhones()[phonePosition];
	}
	
	public static class Agent{
		private String agent;
		private String city;
		private String email;
		private String[] phones;

		public String getAgent() {
			return agent;
		}

		public void setAgent(String agent) {
			this.agent = agent;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String[] getPhones() {
			return phones;
		}

		public void setPhones(String[] phones) {
			this.phones = phones;
		}
	}
}
