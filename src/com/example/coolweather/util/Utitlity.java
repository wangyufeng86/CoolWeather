package com.example.coolweather.util;

import android.R.string;
import android.text.TextUtils;

import com.example.coolweather.db.CoolWeatherDb;
import com.example.coolweather.model.City;
import com.example.coolweather.model.County;
import com.example.coolweather.model.Province;

public class Utitlity {
/*
 * ����ͽ������������ص�ʡ������ 
 */
	public synchronized static boolean handleProvincesResponse(CoolWeatherDb coolWeatherDb,
			String response){
		if (!TextUtils.isEmpty(response)) {
			String[] allProvinceStrings = response.split(",");
			if (allProvinceStrings != null && allProvinceStrings.length>0) {
				for (String p : allProvinceStrings) {
					String[] array = p.split("\\|");
					
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					coolWeatherDb.saveProvince(province);
				}
				return true;
			}
		}
		
		return false;
	}
	
	/*
	 * ����ͽ������������ص��м����� 
	 */
		public synchronized static boolean handleCitiesResponse(CoolWeatherDb coolWeatherDb,
				String response,int provinceId){
			if (!TextUtils.isEmpty(response)) {
				String[] allCitiesStrings = response.split(",");
				if (allCitiesStrings != null && allCitiesStrings.length>0) {
					for (String c : allCitiesStrings) {
						String[] array = c.split("\\|");
						
						City city = new City();
						city.setCityCode(array[0]);
						city.setCityName(array[1]);
						city.setProvinceId(provinceId);
						coolWeatherDb.savecity(city);
					}
					return true;
				}
			}
			
			return false;
		}
		
		/*
		 * ����ͽ������������ص��ؼ����� 
		 */
			public synchronized static boolean handleCountyResponse(CoolWeatherDb coolWeatherDb,
					String response,int cityId){
				if (!TextUtils.isEmpty(response)) {
					String[] allCountiesStrings = response.split(",");
					if (allCountiesStrings != null && allCountiesStrings.length>0) {
						for (String c : allCountiesStrings) {
							String[] array = c.split("\\|");
							
							County county = new County();
							county.setCountyCode(array[0]);
							county.setCountyName(array[1]);
							county.setCityId(cityId);
							coolWeatherDb.saveCounty(county);
						}
						return true;
					}
				}
				
				return false;
			}
}
