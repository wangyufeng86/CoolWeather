package com.example.coolweather.util;

public interface HrttpCallbackListener {
	void onFinish(String responce);

	void onError(Exception e);
}
