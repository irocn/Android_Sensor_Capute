package com.irocn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.util.Log;

public class HttpClient {
	public boolean HttpGetX(String ip, String data)
	{
		//先将参数放入List，再对参数进行URL编码
		List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
		
		params.add(new BasicNameValuePair("user", data));

		//对参数编码
		String param = URLEncodedUtils.format(params, "UTF-8");

		//baseUrl			
		String baseUrl = "http://" + ip + "/sensor/";

		//将URL与参数拼接
		HttpGet getMethod = new HttpGet(baseUrl + "?" + data);
		Log.i("info", "resCode = " + data); 
		
		//String s = "user=carx&Ax=0.199&Ay=0.11&Az=0.11&Gx=0.11&Gy=0.11&Gz=0.11&Mx=0.11&My=0.11&Mz=0.11&Sl=0.11&Sw=0.11&Sh=0.11&Sv=0.11";
		//HttpGet getMethod = new HttpGet(baseUrl + "?" + s);
					
		DefaultHttpClient httpClient = new DefaultHttpClient();

		try {
			HttpResponse response = httpClient.execute(getMethod); //发起GET请求
			/*
			
		    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
		    String json = reader.readLine();
		    //JSONTokener tokener = new JSONTokener(json);
			//JSONArray finalResult = new JSONArray(tokener);

		    if ( json != "OK")
		    {
		    	return false;
		    }
		    */
			
		    //Log.i("error", response.getEntity().getContent().); //获取响应码
		   // Log.i(TAG, "result = " + EntityUtils.toString(response.getEntity(), "utf-8"));//获取服务器响应内容
		} catch (ClientProtocolException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		return true;
	}


	public void HttpPostX(){
		//先将参数放入List，再对参数进行URL编码
		List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();

		//和GET方式一样，先将参数放入List
		params = new LinkedList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("param1", "Post方法"));
		params.add(new BasicNameValuePair("param2", "第二个参数"));
		//baseUrl			
		String baseUrl = "http://ubs.free4lab.com/php/method.php";
		DefaultHttpClient httpClient = new DefaultHttpClient();
		try {
		    HttpPost postMethod = new HttpPost(baseUrl);
		    postMethod.setEntity(new UrlEncodedFormEntity(params, "utf-8")); //将参数填入POST Entity中
						
		    HttpResponse response = httpClient.execute(postMethod); //执行POST方法
		  //  Log.i(TAG, "resCode = " + response.getStatusLine().getStatusCode()); //获取响应码
		  //  Log.i(TAG, "result = " + EntityUtils.toString(response.getEntity(), "utf-8")); //获取响应内容
						
		} catch (UnsupportedEncodingException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (ClientProtocolException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	}
}
