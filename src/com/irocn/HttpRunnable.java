package com.irocn;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class HttpRunnable implements Runnable {

	private String ip;
	private String data;
	private HttpClient httpClient = new HttpClient();
	private Handler mHandler = new Handler();
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(!httpClient.HttpGetX(ip,data)){
			Looper looper = Looper.myLooper(); // Get current thread looper
			mHandler = new HandlerX(looper);
			mHandler.removeMessages(0);
			String msgStr = "message from main thread";
			Message m = mHandler.obtainMessage(1,1,1,msgStr);
			mHandler.sendMessage(m);			
		}			
	}
	
	public void setData(String ip, String data){
		this.data = data;
		this.ip = ip;
	}

}
