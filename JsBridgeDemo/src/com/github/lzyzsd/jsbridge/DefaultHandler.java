package com.github.lzyzsd.jsbridge;

import android.util.Log;

public class DefaultHandler implements BridgeHandler{

	String TAG = "DefaultHandler";
	
	@Override
	public void handler(String data, CallBackFunction function) {
	    
		Log.i(TAG, data);
		if(function != null){
			function.onCallBack("DefaultHandler response data");
		}
	}

}
