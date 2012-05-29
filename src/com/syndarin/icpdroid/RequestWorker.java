package com.syndarin.icpdroid;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

import android.os.Handler;
import android.os.Message;

public class RequestWorker implements Runnable {

	public final static int RESULT_CODE_OK=0;
	public final static int RESULT_CODE_ERROR=1;
	
	public final static int ERROR_IO=100;
	public final static int ERROR_PARSE=101;
	
	private String requestUrl;
	private Handler handler;
	
	public RequestWorker(String requestUrl, Handler handler) {
		this.requestUrl = requestUrl;
		this.handler = handler;
	}

	@Override
	public void run() {
		
		Message message=handler.obtainMessage();
		
		HttpClient httpClient=new DefaultHttpClient();
		HttpGet request=new HttpGet(requestUrl);
		
		try {
			HttpResponse response=httpClient.execute(request);
			
			HwbEventsParser parser=new HwbEventsParser();
			List<HwbEvent> events=parser.parseEvents(response.getEntity().getContent());
			
			message.what=RESULT_CODE_OK;
			message.obj=events;
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			message.what=RESULT_CODE_ERROR;
			message.arg1=ERROR_IO;
		} catch (IOException e) {
			e.printStackTrace();
			message.what=RESULT_CODE_ERROR;
			message.arg1=ERROR_IO;
		} catch (DOMException e) {
			e.printStackTrace();
			message.what=RESULT_CODE_ERROR;
			message.arg1=ERROR_PARSE;
		} catch (IllegalStateException e) {
			e.printStackTrace();
			message.what=RESULT_CODE_ERROR;
			message.arg1=ERROR_PARSE;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			message.what=RESULT_CODE_ERROR;
			message.arg1=ERROR_PARSE;
		} catch (SAXException e) {
			e.printStackTrace();
			message.what=RESULT_CODE_ERROR;
			message.arg1=ERROR_PARSE;
		} catch (ParseException e) {
			e.printStackTrace();
			message.what=RESULT_CODE_ERROR;
			message.arg1=ERROR_PARSE;
		}finally{
			handler.sendMessage(message);
		}
	}
}
