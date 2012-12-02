package com.syndarin.icpdroid;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PhoneListAdapter extends BaseAdapter{

	private String[] phones;
	
	public PhoneListAdapter(String[] phones) {
		this.phones = phones;
	}

	@Override
	public int getCount() {
		return phones.length;
	}

	@Override
	public String getItem(int position) {
		return phones[position];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View result = convertView==null ? inflateNewView(parent.getContext()) : convertView;
		
		TextView phone = (TextView) result.findViewById(R.id.phone_number);
		String number = wrapPhoneNumber(phones[position]);
		phone.setText(number);
		Typeface tf = Typeface.createFromAsset(parent.getContext().getAssets(), "upcil.ttf");
		phone.setTypeface(tf);
		
		String gsmCode = phones[position].substring(0, 3);
		GSMProvider provider = GSMProvider.getProviderByCode(gsmCode);
		
		ImageView logo = (ImageView) result.findViewById(R.id.gsm_provider_logo);
		logo.setImageResource(provider.getLogo());
		
		return result;
	}
	
	private View inflateNewView(Context context){
		LayoutInflater inflater = LayoutInflater.from(context);
		return inflater.inflate(R.layout.phones_dialog, null);
	}
	
	private String wrapPhoneNumber(String number){
		return String.format("(%s) %s-%s", number.substring(0, 3), number.substring(3, 6), number.substring(6));
	}

	

}
