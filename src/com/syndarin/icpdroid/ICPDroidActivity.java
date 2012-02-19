package com.syndarin.icpdroid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


//main activity
public class ICPDroidActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */
	
	private final String HWB_REGEXP="\\d{3}-\\d{6}";
	private final String REQUEST_URL="http://intercitypost.com/manifester/index.php/MobileManifester/getHwbRoute/";
	
	private EditText hwbEdit;
	private Resources resources;
	private ArrayList<HwbEvent> events;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		hwbEdit = (EditText) findViewById(R.id.hwb_number);
		
		resources=getResources();
		
		events=new ArrayList<HwbEvent>();

		Button buttonSearch = (Button) findViewById(R.id.button_search);
		buttonSearch.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.button_search:
			events.clear();
			String hwbNumber=hwbEdit.getText().toString().trim();
			Pattern pattern=Pattern.compile(HWB_REGEXP);
			Matcher matcher=pattern.matcher(hwbNumber);
			if(matcher.matches()){
				String requestString=REQUEST_URL+hwbNumber;
				HttpClient httpClient=new DefaultHttpClient();
				HttpGet request=new HttpGet(requestString);
				try {
					HttpResponse response=httpClient.execute(request);
					HttpEntity entity=response.getEntity();
					
					try{
						events=HwbEventsParser.parseEvents(entity.getContent());
						if(events!=null){
							for(HwbEvent event:events){
								Toast.makeText(getApplicationContext(), event.toString(), Toast.LENGTH_SHORT).show();
							}
						}else{
							Toast.makeText(getApplicationContext(), "No info!", Toast.LENGTH_SHORT).show();
						}
						
						
					}catch(Exception e){
						e.printStackTrace();
						String error_message=resources.getString(R.string.parse_exception_error)+e.getMessage();
						Toast.makeText(getApplicationContext(), error_message, Toast.LENGTH_LONG).show();
					}
					
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					String errorMessage=resources.getString(R.string.protocol_exception_error)+"\n"+e.getMessage();
					Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
					Log.e("CLIENT PROTOCOL EXCEPTION", e.getMessage());
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					String errorMessage=resources.getString(R.string.io_exception_error)+"\n"+e.getMessage();
					Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
					Log.e("IO EXCEPTION", e.getMessage());
					e.printStackTrace();
				}
			}else{
				String errorMessage=resources.getString(R.string.hwb_format_error);
				Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;

		}

	}
}