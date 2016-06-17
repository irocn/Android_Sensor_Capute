package com.irocn;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class HandlerX extends Handler{ 

	public HandlerX(Looper looper){
		super(looper);
	}

	public void handleMessage(Message msg){
		System.out.print(msg.toString());
	}

}
