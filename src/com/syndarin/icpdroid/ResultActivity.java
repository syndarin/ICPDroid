package com.syndarin.icpdroid;

import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;

public class ResultActivity extends ListActivity {

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		List<HwbEvent> events = (List<HwbEvent>)getIntent().getSerializableExtra("data");
		ResultListAdapter adapter = new ResultListAdapter(events);
		setListAdapter(adapter);
		
	}
	
	

}
