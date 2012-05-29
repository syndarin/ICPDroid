package com.syndarin.icpdroid;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


public class ICPDroidActivity extends Activity implements OnClickListener {
	
	private final int REQUEST_SCAN_BARCODE=1;
	private final String HWB_PATTERN="\\d{3}-\\d{6}";
	
	private EditText hwbEdit;
	private ListView resultList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		
		setContentView(R.layout.main);

		hwbEdit = (EditText) findViewById(R.id.hwb_number);
		resultList=(ListView)findViewById(R.id.result_list);
		
		Button buttonSearch = (Button) findViewById(R.id.button_search);
		buttonSearch.setOnClickListener(this);
		Button buttonScan = (Button) findViewById(R.id.button_scan);
		buttonScan.setOnClickListener(this);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==REQUEST_SCAN_BARCODE){
			if (resultCode == Activity.RESULT_OK) {
				String qrContent = data.getStringExtra("SCAN_RESULT");
				hwbEdit.setText(qrContent);
			}
		}
	}

	private void sendHwbRequest(){
		String hwb=hwbEdit.getText().toString();
		Pattern pattern=Pattern.compile(HWB_PATTERN);
		Matcher matcher=pattern.matcher(hwb);
		
		if(matcher.matches()){
			String url=getString(R.string.request_url)+hwb;
			RequestWorker worker=new RequestWorker(url, handler);
			Thread t=new Thread(worker);
			t.start();
		}else{
			showToast(getString(R.string.hwb_format_error));
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.button_search:
			sendHwbRequest();
			break;
			
		case R.id.button_scan:
			startScanning();
			break;
			
		}
	}
	
	private void startScanning(){
		Intent scanIntent = new Intent("com.google.zxingmy.client.android.SCAN");
		scanIntent.putExtra("SCAN_MODE", "ONE_D_MODE");
		this.startActivityForResult(scanIntent, REQUEST_SCAN_BARCODE);
	}
	
	private void loadResults(List<HwbEvent> events){
		if(events.size()>0){
			ResultListAdapter adapter=new ResultListAdapter(events);
			resultList.setAdapter(adapter);
		}else{
			showToast(getString(R.string.no_hwb_info));
		}
		
	}
	
	private Handler handler=new Handler(){
		@SuppressWarnings("unchecked")
		@Override
		public void dispatchMessage(Message msg) {
			if(msg.what==RequestWorker.RESULT_CODE_OK){
				loadResults((List<HwbEvent>)msg.obj);
			}else{
				handleServerError((Integer)msg.obj);
			}
		}
	};
	
	private void handleServerError(int errorCode){
		switch(errorCode){
		case RequestWorker.ERROR_IO:
			showToast(getString(R.string.io_exception_error));
			break;
		case RequestWorker.ERROR_PARSE:
			showToast(getString(R.string.parse_exception_error));
			break;
		}
	}
	
	private void showToast(String message){
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
}