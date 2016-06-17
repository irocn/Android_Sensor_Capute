package com.irocn;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MainActivity extends Activity implements SensorEventListener,LocationListener{
	private SensorManager sensorManager;
	private float[] linear_accelerations;
	private float[] sensor_sample;

	private LocationManager locationManager;
	private String provider;
	private int TIME = 6000;
	Handler handler = new Handler();

	private TextView AltitudeValue = null;
	private TextView BearingValue = null;
	private TextView SpeedValue = null;
	private TextView LongitudeValue = null;
	private TextView LatitudeValue = null;

	private TextView Ax = null;
	private TextView Ay = null;
	private TextView Az = null;

	private TextView Gx = null;
	private TextView Gy = null;
	private TextView Gz = null;

	private TextView Mx = null;
	private TextView My = null;
	private TextView Mz = null;

	private TextView TimeX = null;
	private TextView IpTxt = null;

	private Switch Startme = null;
	private EditText Username = null;
	private String USERNAME = null;
	private String ip = null;
	private Boolean isStart = false;

	private HttpRunnable hr = new HttpRunnable();
	private Handler mHandler = new Handler();
	private Runnable runX;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		// Initialize variable
		AltitudeValue = (TextView) findViewById(R.id.AltitudeValue);
		// BearingValue = (TextView) findViewById(R.id.BearingValue);
		SpeedValue = (TextView) findViewById(R.id.SpeedValue);
		LongitudeValue = (TextView) findViewById(R.id.LongitudeValue);
		LatitudeValue = (TextView) findViewById(R.id.LatitudeValue);

		Startme = (Switch) findViewById(R.id.StartMe);

		Ax = (TextView) findViewById(R.id.AxValue);
		Ay = (TextView) findViewById(R.id.AyValue);
		Az = (TextView) findViewById(R.id.AzValue);

		Gx = (TextView) findViewById(R.id.GxValue);
		Gy = (TextView) findViewById(R.id.GyValue);
		Gz = (TextView) findViewById(R.id.GzValue);

		Mx = (TextView) findViewById(R.id.MxValue);
		My = (TextView) findViewById(R.id.MyValue);
		Mz = (TextView) findViewById(R.id.MzValue);

		TimeX = (TextView) findViewById(R.id.TimeXValue);
		Username = (EditText) findViewById(R.id.Username);
		IpTxt = (EditText) findViewById(R.id.ipAddr);

		// Get the sensor manager
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		// Get the location manager
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				1000, 1, this);
		// 得到LocationManager对象
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);// 设置为最大精度
		criteria.setAltitudeRequired(true);// 要求海拔信息
		criteria.setBearingRequired(true);// 要求方位信息
		criteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);// 要求方位信息 的精确度
		criteria.setCostAllowed(false);// 是否允许付费
		criteria.setPowerRequirement(Criteria.POWER_LOW);// 对电量的要求
		criteria.setSpeedAccuracy(criteria.ACCURACY_HIGH);// 对速度的精确度
		criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);// 对水平的精确度
		criteria.setSpeedRequired(true);// 要求速度信息
		criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);// 对垂直精度

		provider = locationManager.getBestProvider(criteria, true); // 找到最好的能用的Provider。
		if (provider == null) {
			System.out.print("provider == null");
		}

		final Location location = locationManager
				.getLastKnownLocation(provider);
		if (location != null) {
			onLocationChanged(location);
		}

		Startme.setChecked(false);

		Startme.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if (isChecked) {
					USERNAME = Username.getText().toString();
					ip = IpTxt.getText().toString();
					if ( ip.length() == 0 ){
						isStart = false;
						Toast.makeText(getApplicationContext(), "请输入ip地址！", Toast.LENGTH_SHORT).show();
						Startme.setChecked(false);
						return;
					}
					if (USERNAME.length() == 0){
						isStart = false;
						Toast.makeText(getApplicationContext(), "请输出用户名！", Toast.LENGTH_SHORT).show();
						Startme.setChecked(false);
						return;
					}else{
						handler.postDelayed(runX, TIME); //每隔1s执行 
						isStart = true;						
					}			
				} else {
					isStart = false;
				}

			}

		});
		
		// Handle message between threads
		Looper looper = Looper.myLooper(); // Get current thread looper
		mHandler = new HandlerX(looper);
		mHandler.removeMessages(0);
		String msgStr = "message from main thread";
		Message m = mHandler.obtainMessage(1,1,1,msgStr);
		mHandler.sendMessage(m);	
		
		runX = new Runnable(){
			@Override
			public void run() {
				try {			
					if(!isStart){return;}
					handler.postDelayed(this, TIME); //每隔1s执行 
					// Send sensor's data to server
					String s = String.format("user=" + USERNAME
							+ "&Ax=%s&Ay=%s&Az=%s&Gx=%s&Gy=%s&Gz=%s&"
							+ "Mx=%s&My=%s&Mz=%s&Sl=%s&Sw=%s&Sh=%s&Sv=%s&TimeX=%s",
							Ax.getText(),
							Ay.getText(),
							Az.getText(), // acceleration
							Gx.getText(),
							Gy.getText(),
							Gz.getText(), // gravities
							Mx.getText(),
							My.getText(),
							Mz.getText(), // M
							LongitudeValue.getText(), LatitudeValue.getText(),
							AltitudeValue.getText(), SpeedValue.getText(),
							TimeX.getText() // GPS
							);
					hr.setData(ip, s);
					new Thread(hr).start();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("exception...");
				}}};
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			getAccelerometer(event);
		}
		if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
			getGyroscope(event);
		}
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			getMagnetic(event);
		}
		/*
		if (event.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR) {
			getGameRotation(event);
		}*/
	}

	private void getGyroscope(SensorEvent event) {
		sensor_sample = new float[] { 0, 0, 0 };
		// Get sensor sample input
		sensor_sample[0] = event.values[0];
		sensor_sample[1] = event.values[1];
		sensor_sample[2] = event.values[2];

		String gx = String.valueOf(sensor_sample[0]);
		String gy = String.valueOf(sensor_sample[1]);
		String gz = String.valueOf(sensor_sample[2]);

		Gx.setText(gx);
		Gy.setText(gy);
		Gz.setText(gz);
	}

	private void getMagnetic(SensorEvent event) {
		sensor_sample = new float[] { 0, 0, 0 };
		// Get sensor sample input
		sensor_sample[0] = event.values[0];
		sensor_sample[1] = event.values[1];
		sensor_sample[2] = event.values[2];

		String mx = String.valueOf(sensor_sample[0]);
		String my = String.valueOf(sensor_sample[1]);
		String mz = String.valueOf(sensor_sample[2]);

		Mx.setText(mx);
		My.setText(my);
		Mz.setText(mz);
	}

	private void getAccelerometer(SensorEvent event) {
		// In this example, alpha is calculated as t / (t + dT),
		// where t is the low-pass filter's time-constant and
		// dT is the event delivery rate.

		// final float alpha = (float) 0.8;
		// gravities = new float[] {0,0,0};
		linear_accelerations = new float[] { 0, 0, 0 };

		// gravities[0] = alpha * gravities[0] + (1 - alpha) * event.values[0];
		// gravities[1] = alpha * gravities[1] + (1 - alpha) * event.values[1];
		// gravities[2] = alpha * gravities[2] + (1 - alpha) * event.values[2];

		// Remove the gravity contribution with the high-pass filter.
		linear_accelerations[0] = event.values[0]; // - gravities[0];
		linear_accelerations[1] = event.values[1]; // - gravities[1];
		linear_accelerations[2] = event.values[2]; // - gravities[2];

		String ax = String.valueOf(linear_accelerations[0]);
		String ay = String.valueOf(linear_accelerations[1]);
		String az = String.valueOf(linear_accelerations[2]);
		Ax.setText(ax);
		Ay.setText(ay);
		Az.setText(az);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// register this class as a listener for the orientation and
		// accelerometer sensors
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);

		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
				SensorManager.SENSOR_DELAY_NORMAL);

		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
				SensorManager.SENSOR_DELAY_NORMAL);

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				1000, 1, this);
	}

	@Override
	protected void onPause() {
		// unregister listener
		super.onPause();
		sensorManager.unregisterListener(this);

		locationManager.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		AltitudeValue.setText(String.format("%.8f", location.getAltitude()));
		// BearingValue.setText(String.format("%.8f",location.getBearing()));
		SpeedValue.setText(String.format("%.8f",
				location.getSpeed() * 60 * 60 / 1000));
		LongitudeValue.setText(String.format("%.8f", location.getLongitude()));
		LatitudeValue.setText(String.format("%.8f", location.getLatitude()));

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		String timeS = sdf.format(location.getTime());
		TimeX.setText(String.format("%s", timeS));
	}

	@Override
	public void onProviderDisabled(String arg0) {
		Toast.makeText(this, "Disabled provider " + provider,
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderEnabled(String arg0) {

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}
}
