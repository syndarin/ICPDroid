package com.syndarin.icpdroid;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ICPDroidActivity extends Activity implements OnClickListener {

	private final int REQUEST_SCAN_BARCODE = 1;
	private final String HWB_PATTERN = "\\d{3}-\\d{6}";

	private EditText hwbEdit;
	
	private Toast toast;
	private View toastView;
	private TextView toastTextView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		setContentView(R.layout.main);

		AgentData.init(this);
		
		setupWidgets();
	}

	private void setupWidgets() {
		hwbEdit = (EditText) findViewById(R.id.hwb_number);
		Typeface typeface = Typeface.createFromAsset(getAssets(), "upcil.ttf");
		hwbEdit.setTypeface(typeface);

		toast = new Toast(this);
		toastView = getLayoutInflater().inflate(R.layout.toast_view, null);
		toastTextView = (TextView)toastView.findViewById(R.id.toastMessage);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		
		ImageButton buttonSearch = (ImageButton) findViewById(R.id.button_search);
		buttonSearch.setOnClickListener(this);
		ImageButton buttonScan = (ImageButton) findViewById(R.id.button_scan);
		buttonScan.setOnClickListener(this);
		ImageButton buttonCall = (ImageButton) findViewById(R.id.button_call);
		buttonCall.setOnClickListener(this);
		ImageButton buttonEmail = (ImageButton) findViewById(R.id.button_email);
		buttonEmail.setOnClickListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_SCAN_BARCODE) {
			if (resultCode == Activity.RESULT_OK) {
				String qrContent = data.getStringExtra("SCAN_RESULT");
				hwbEdit.setText(qrContent);
			}
		}
	}

	private void sendHwbRequest() {
		String hwb = hwbEdit.getText().toString();
		Pattern pattern = Pattern.compile(HWB_PATTERN);
		Matcher matcher = pattern.matcher(hwb);

		if (matcher.matches()) {
			String url = getString(R.string.request_url) + hwb;
			RequestWorker worker = new RequestWorker(url, new ThreadHandler(this));
			Thread t = new Thread(worker);
			t.start();
		} else {
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
			
		case R.id.button_email:
			onEmailRequest();
			break;
			
		case R.id.button_call:
			onCallRequest();
			break;

		}
	}
	
	private void onEmailRequest(){
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				sendEmail(AgentData.getAgentEmail(which));
			}
		};
		
		DialogHelper.createCitiesListDialog(this, listener, getString(R.string.select_your_city)).show();
	}
	
	private void sendEmail(String email){
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("plain/text");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
		intent.putExtra(Intent.EXTRA_SUBJECT, "");
		intent.putExtra(Intent.EXTRA_TEXT, "");
		startActivity(intent);
	}
	
	private void onCallRequest(){
		if(isTelephonyEnabled()){
			DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					showPhonesForAgent(which);
				}
			};
			
			DialogHelper.createCitiesListDialog(this, listener, getString(R.string.select_your_city)).show();
		}else{
			showToast(getString(R.string.telephony_not_supported));
		}
	}
	
	private void showPhonesForAgent(final int position){
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				startDialActivity(AgentData.getAgentPhone(position, which));
			}
		};
		
		DialogHelper.createPhonesDialog(this, listener, AgentData.getAgentPhones(position), getString(R.string.select_phone_number)).show();
	}
	
	private void startDialActivity(String phone){
		Intent intent = new Intent(Intent.ACTION_DIAL);
		intent.setData(Uri.parse("tel:"+phone));
		startActivity(intent);
	}

	private void startScanning() {
		Intent scanIntent = new Intent("com.google.zxingmy.client.android.SCAN");
		scanIntent.putExtra("SCAN_MODE", "ONE_D_MODE");
		this.startActivityForResult(scanIntent, REQUEST_SCAN_BARCODE);
	}

	private void loadResults(ArrayList<HwbEvent> events) {
		if (events.size() > 0) {
			Intent intent = new Intent(this, ResultActivity.class);
			intent.putExtra("data", events);
			startActivity(intent);
		} else {
			showToast(getString(R.string.no_hwb_info));
		}

	}

	private static class ThreadHandler extends Handler{
		
		private WeakReference<ICPDroidActivity> parentActivity;

		private ThreadHandler(ICPDroidActivity activity) {
			super();
			parentActivity = new WeakReference<ICPDroidActivity>(activity);
		}

		@SuppressWarnings("unchecked")
		@Override
		public void dispatchMessage(Message msg) {
			if (msg.what == RequestWorker.RESULT_CODE_OK) {
				parentActivity.get().loadResults((ArrayList<HwbEvent>) msg.obj);
			} else {
				parentActivity.get().handleServerError((Integer) msg.obj);
			}
		}
	}
	
	private void handleServerError(int errorCode) {
		switch (errorCode) {
		case RequestWorker.ERROR_IO:
			showToast(getString(R.string.io_exception_error));
			break;
		case RequestWorker.ERROR_PARSE:
			showToast(getString(R.string.parse_exception_error));
			break;
		}
	}

	
	private void showToast(String message) {
		toastTextView.setText(message);
		toast.setView(toastView);
		toast.show();
	}
	
	private boolean isTelephonyEnabled(){
		TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		if(tm != null && tm.getSimState()==TelephonyManager.SIM_STATE_READY)
			return true;
		else
			return false;
	}
}