package com.syndarin.icpdroid;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ResultListAdapter extends BaseAdapter {

	private List<HwbEvent> events;
	
	public ResultListAdapter(List<HwbEvent> events) {
		this.events = events;
	}

	@Override
	public int getCount() {
		return events.size();
	}

	@Override
	public Object getItem(int position) {
		return events.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Context context = parent.getContext();
		
		LayoutInflater inflater=LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.list_element, null);
		
		HwbEvent event=events.get(position);
		
		TextView dateView=(TextView)view.findViewById(R.id.text_date);
		dateView.setText(CalendarHelper.calendarToString(event.getDate(), context.getString(R.string.date_pattern)));
		
		TextView cityView=(TextView)view.findViewById(R.id.text_city);
		cityView.setText(event.getOffice());
		
		TextView cpView = (TextView)view.findViewById(R.id.text_state);
		cpView.setText(event.getState());
		
//		String[] commentDetail = event.getComment().split(" "); 
		
		TextView commentTimeView = (TextView)view.findViewById(R.id.text_comment);
		commentTimeView.setText(event.getComment());
		
//		if(commentDetail.length>1){
//			TextView commentSurnameView = (TextView) view.findViewById(R.id.text_comment_detail);
//			commentSurnameView.setText(commentDetail[1]);
//		}
		
		return view;
	}

}
