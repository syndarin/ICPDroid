package com.syndarin.icpdroid;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.util.Log;

public class HwbEventsParser {
	private final static String TOTAL_EVENTS = "total";
	private final static String EVENTS_CONTAINER="events";
	private final static String INFO_OFFICE="office";
	private final static String INFO_DATE="date";
	private final static String INFO_STATE="state";

	public List<HwbEvent> parseEvents(InputStream rawXML) throws ParserConfigurationException, SAXException, IOException, DOMException, ParseException {
		List<HwbEvent> events = new ArrayList<HwbEvent>();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		DocumentBuilder builder = factory.newDocumentBuilder();
		
		Document dom = builder.parse(rawXML);
		
		Element root = dom.getDocumentElement();
		
		NodeList totalNodeList = root.getElementsByTagName(TOTAL_EVENTS);
		
		Node totalNode = totalNodeList.item(0);
		
		
		int total = Integer.valueOf(totalNode.getTextContent());
		Log.e("PARSE PROGRESS", "Total records is "+total);
		
		if (total > 0) {
			
			NodeList eventsNodeList=root.getElementsByTagName(EVENTS_CONTAINER);
			
			Node eventsNode=eventsNodeList.item(0);
			
			NodeList eventsContainer=eventsNode.getChildNodes();
			
			for(int i=0; i<eventsContainer.getLength(); i++){
				
				HwbEvent event=new HwbEvent();
				
				Node eventData=eventsContainer.item(i);
				
				NodeList eventProperties=eventData.getChildNodes();
				
				for(int j=0; j<eventProperties.getLength(); j++){
					Node property=eventProperties.item(j);
					if(property.getNodeName().equals(INFO_OFFICE)){
						event.setOffice(property.getTextContent());
					}else if(property.getNodeName().equals(INFO_DATE)){
						event.setDate(property.getTextContent());
					}else if(property.getNodeName().equals(INFO_STATE)){
						event.setState(property.getTextContent());
					}else{
						event.setComment(property.getTextContent());
					}	
				}	
				events.add(event);
			}	
			
		} else {
			return events;
		}

		return events;
	}

}
