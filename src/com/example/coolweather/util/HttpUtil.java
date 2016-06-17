package com.example.coolweather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
	public static void sendHttpRequest(final String address,
			final HrttpCallbackListener listener){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpURLConnection connection = null;
				
				try {
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setReadTimeout(800);
					connection.setConnectTimeout(800);
					
					InputStream inputStream = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
					
					StringBuilder responseBuilder = new StringBuilder();
					String lineString;
					
					while ((lineString = reader.readLine()) != null) {
						responseBuilder.append(lineString);
					}
					
					if (listener != null) {
						listener.onFinish(responseBuilder.toString());
						
					}
					
				} catch (Exception e) {
					// TODO: handle exception
					if (listener!=null) {
						listener.onError(e);
					}
				}finally{
					if (connection != null) {
						connection.disconnect();
					}
				}
			}
		}).start();
		
	}
	
}
