package com.syndarin.icpdroid;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionChecker {
	
	public static boolean isOnline(Context context){
		ConnectivityManager cManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nInfo=cManager.getActiveNetworkInfo();
		return nInfo!=null && nInfo.isConnectedOrConnecting();
	}

}
