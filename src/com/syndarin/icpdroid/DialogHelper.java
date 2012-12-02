package com.syndarin.icpdroid;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.widget.BaseAdapter;

public class DialogHelper {

	public static AlertDialog createCitiesListDialog(Context context, OnClickListener elementClickListener, String title){
		
		Builder builder = new Builder(context);
		
		builder.setTitle(title);
		
		builder.setItems(AgentData.getCities(), elementClickListener);
		
		return builder.create();
	}
	
	public static AlertDialog createPhonesDialog(Context context, OnClickListener listener, String[] numbers, String title){
		
		Builder builder = new Builder(context);
		
		builder.setTitle(title);
		
		BaseAdapter adapter = new PhoneListAdapter(numbers);
		
		builder.setAdapter(adapter, listener);
		
		return builder.create();
	}

}
