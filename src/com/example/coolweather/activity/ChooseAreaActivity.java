package com.example.coolweather.activity;

import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;

import com.example.coolweather.R;
import com.example.coolweather.db.CoolWeatherDb;
import com.example.coolweather.model.City;
import com.example.coolweather.model.County;
import com.example.coolweather.model.Province;
import com.example.coolweather.util.HrttpCallbackListener;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utitlity;

import android.R.anim;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {

	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;

	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDb coolWeatherDb;
	private List<String> dataList = new ArrayList<String>();

	/*
	 * 省列表
	 */
	private List<Province> provincelList;

	/*
	 * 市列表
	 */
	private List<City> citylList;

	/*
	 * 县列表
	 */
	private List<County> countylList;

	/*
	 * 选中的省份
	 */

	private Province selectedProvince;

	/*
	 * 选中的城市
	 */
	private City selectedcCity;

	/*
	 * 选中的级别
	 */
	private int currntLevel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);

		coolWeatherDb = CoolWeatherDb.getInstnece(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if (currntLevel == LEVEL_PROVINCE) {
					selectedProvince = provincelList.get(arg2);
					queryCities();
				} else if (currntLevel == LEVEL_CITY) {
					selectedcCity = citylList.get(arg2);
					queryCounties();
				}

			}
		});

		queryProvince();
	}

	/*
	 * 查询全国所有的省，优化从数据库里查询，如果没有再从服务器上查询
	 */

	private void queryProvince(){
		provincelList = coolWeatherDb.loadProvince();
		if (provincelList.size()>0) {
			dataList.clear();
			
			for (Province province : provincelList) {
				dataList.add(province.getProvinceName());
			}			
			adapter.notifyDataSetChanged();			
			listView.setSelection(0);
			titleText.setText("中国");
			currntLevel = LEVEL_PROVINCE;
			
		}else {
			queryFromServer(null,"province");
		}
		
	}
	
	/*
	 * 查询全国所有的市，优化从数据库里查询，如果没有再从服务器上查询
	 */

	private void queryCities(){
		citylList = coolWeatherDb.loadCities(selectedProvince.getId());
		if (citylList.size()>0) {
			dataList.clear();
			
			for (City city : citylList) {
				dataList.add(city.getCityName());
			}			
			adapter.notifyDataSetChanged();			
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currntLevel = LEVEL_CITY;
			
		}else {
			queryFromServer(selectedProvince.getProvinceCode(),"city");
		}
		
	}
	
	/*
	 * 查询全国所有的县，优化从数据库里查询，如果没有再从服务器上查询
	 */

	private void queryCounties(){
		countylList = coolWeatherDb.loadCounties(selectedcCity.getId());
		if (countylList.size()>0) {
			dataList.clear();
			
			for (County county : countylList) {
				dataList.add(county.getCountyName());
			}			
			adapter.notifyDataSetChanged();			
			listView.setSelection(0);
			titleText.setText(selectedcCity.getCityName());
			currntLevel = LEVEL_COUNTY;
			
		}else {
			queryFromServer(selectedcCity.getCityCode(),"county");
		}
		
	}
	
	/*
	 * 根据传入的代号和类型从服务器上查询省市县的数据
	 */
	private void queryFromServer(final String code, final String type){
		String address;
		
		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
		}else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HrttpCallbackListener(){

			@Override
			public void onFinish(String responce) {
				// TODO Auto-generated method stub
				boolean result = false;
				if ("province".equals(type)) {
					result = Utitlity.handleProvincesResponse(coolWeatherDb, responce);
				}else if ("city".equals(type)) {
					result = Utitlity.handleCitiesResponse(coolWeatherDb, responce, selectedProvince.getId());
				}else if ("county".equals(type)) {
					result = Utitlity.handleCountyResponse(coolWeatherDb, responce, selectedcCity.getId());
				}
				
				if (result) {
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
						closeProgressDialog();
						if ("province".equals(type)) {
							queryProvince();
						}else if ("city".equals(type)) {
							queryCities();
						}else if ("county".equals(type)) {
							queryCounties();
						}
						}
					});
				}
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
					}
				});
			}
			
		});
	} 
	
	/*
	 * 显示进度对话框
	 */
	private void showProgressDialog(){
		if (progressDialog != null) {
			progressDialog= new ProgressDialog(this);
			progressDialog.setMessage("正在加载中……");
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.show();
		}
	}
	
	/*
	 * 关闭进度对话框
	 */
	private void closeProgressDialog(){
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
	
	/*
	 * 按下Backr后根据当前的级别来判断返回市列表，还是省列表，还是直接退出
	 */
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	//	super.onBackPressed();
		if (currntLevel == LEVEL_COUNTY) {
			queryCities();
		}else if (currntLevel == LEVEL_CITY) {
			queryProvince();
		}else {
			finish();
		}
		
	}

}
