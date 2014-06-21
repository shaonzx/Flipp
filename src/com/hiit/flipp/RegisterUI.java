package com.hiit.flipp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterUI extends Activity implements OnItemSelectedListener {
	
	Spinner spinnerCountry, spinnerCity;
	EditText edtUserName, edtPassword, edtConfirmPassword, edtEmail, edtAddress, edtFullName;
	TextView txtUserName, txtPassword, txtConfirmPassword, txtEmail, txtAddress, txtFullName;
	String username = "", password = "", confirmPassword = "", email = "", address = "", fullName = "";
	Button saveButton;	
	String errorMessage;
	
	private ProgressDialog progressDialog;
	
	
	private static final String COUNTRIES_URL = "http://api.fliptaxi.com/countries";
	private static final String CITIES_URL = "http://api.fliptaxi.com/cities";
	private static final String REGISTRATION_URL = "http://taxiflipp.apiary-mock.com/user/create";
	
	private List<Countries> countryList;
	private List<Cities> cityList;
	
	
	

	@Override
	public void onItemSelected(AdapterView<?> parent, View arg1, int pos,
			long arg3) {
		// TODO Auto-generated method stub

		parent.getItemAtPosition(pos);
		
		
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub		
		
	}
	
	public void InitializeFormControls()
	{
		spinnerCity = (Spinner) findViewById(R.id.spnrCity);
		spinnerCountry = (Spinner) findViewById(R.id.spnrCountry);		
		spinnerCountry.setOnItemSelectedListener(this);	
		
		edtFullName = (EditText) findViewById(R.id.edtFullName);
		edtUserName = (EditText) findViewById(R.id.edtUsernameRegister);
		edtPassword = (EditText) findViewById(R.id.edtPasswordRegister);
		edtConfirmPassword = (EditText) findViewById(R.id.edtConfirmPassword);
		edtEmail = (EditText) findViewById(R.id.edtEmailRegister);
		edtAddress = (EditText) findViewById(R.id.edtAddressRegister);
		
		txtFullName = (TextView) findViewById(R.id.txtFullName);
		txtUserName = (TextView) findViewById(R.id.txtUsername);
		txtPassword = (TextView) findViewById(R.id.txtPassword);
		txtConfirmPassword = (TextView) findViewById(R.id.txtConfirmPassword);
		txtEmail = (TextView) findViewById(R.id.txtEmail);
		txtAddress = (TextView) findViewById(R.id.txtMyAddress);
		
		saveButton = (Button) findViewById(R.id.btnSave);	
		
		countryList = new ArrayList<Countries>();
		cityList = new ArrayList<Cities>();
	}
	
	public void GetValuesFromControls()
	{
		fullName = edtFullName.getText().toString();
		username = edtUserName.getText().toString();
		password = edtPassword.getText().toString();
		confirmPassword = edtConfirmPassword.getText().toString();
		email = edtEmail.getText().toString();
		address = edtAddress.getText().toString();
	}
	
	public final static boolean IsValidEmail(CharSequence target) {
	    if (target == null) {
	        return false;
	    } else {
	        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
	    }
	}

	public boolean ValidateRegistrationForm()
	{
		boolean isInputsValid = true;
		
		if(username.length() <= 3)
		{
			Toast.makeText(RegisterUI.this, "Username too short", Toast.LENGTH_SHORT).show();
			txtUserName.setTextColor(Color.RED);
			isInputsValid = false;
		}else{
			txtUserName.setTextColor(Color.WHITE);
		}
		
		if(username.length() == 0)
		{
			Toast.makeText(RegisterUI.this, "Username can not be blank", Toast.LENGTH_SHORT).show();
			txtUserName.setTextColor(Color.RED);
			isInputsValid = false;
		}else{
			txtUserName.setTextColor(Color.WHITE);
		}
		
		if(fullName.length() == 0)
		{
			Toast.makeText(RegisterUI.this, "Full name can not be blank", Toast.LENGTH_SHORT).show();
			txtFullName.setTextColor(Color.RED);
			isInputsValid = false;
		}else{
			txtFullName.setTextColor(Color.WHITE);
		}
		
		
		if(password.length() < 6)
		{
			Toast.makeText(RegisterUI.this, "Password too short", Toast.LENGTH_SHORT).show();
			txtPassword.setTextColor(Color.RED);
			isInputsValid = false;
		}else{
			txtPassword.setTextColor(Color.WHITE);
		}
		
		if(password.length() == 0)
		{
			Toast.makeText(RegisterUI.this, "Password can not be blank", Toast.LENGTH_SHORT).show();
			txtPassword.setTextColor(Color.RED);
			isInputsValid = false;
		}		
		
		if(!password.equals(confirmPassword))
		{
			Toast.makeText(RegisterUI.this, "Password confirmation doesn’t match password", Toast.LENGTH_SHORT).show();
			txtConfirmPassword.setTextColor(Color.RED);
			isInputsValid = false;
		}else{
			txtConfirmPassword.setTextColor(Color.WHITE);
		}
		
		if(address.length() == 0)
		{
			Toast.makeText(RegisterUI.this, "Address can not be blank", Toast.LENGTH_SHORT).show();
			txtAddress.setTextColor(Color.RED);
			isInputsValid = false;
		}else{
			txtAddress.setTextColor(Color.WHITE);
		}
		
		if(!IsValidEmail(email))
		{
			Toast.makeText(RegisterUI.this, "Email is not in format", Toast.LENGTH_SHORT).show();
			txtEmail.setTextColor(Color.RED);
			isInputsValid = false;		
		}else{
			txtEmail.setTextColor(Color.WHITE);
		}
		
		return isInputsValid;		
	}
	
	public JSONObject BuildJson()
	{
		JSONObject jsonData = new JSONObject();
		
		try {
			jsonData.put("name", fullName);
			jsonData.put("username", username);
			jsonData.put("password", password);
			jsonData.put("confirm_password", confirmPassword);
			jsonData.put("email", email);
			jsonData.put("address", address);
			jsonData.put("country_id", "something");
			jsonData.put("city_id", "something");
			jsonData.put("mobile", "something");
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return jsonData;
	}
	
    public void NoConnectionDialog() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegisterUI.this);

		// Setting Dialog Title
		alertDialog.setTitle("No Internet Connection");

		// Setting Dialog Message
		alertDialog.setMessage("Please connect to Internet and try again!");

		// Setting alert dialog icon
		alertDialog.setIcon(R.drawable.fail);

		// Setting OK Button
		alertDialog.setPositiveButton("OK",null);
		// Showing Alert Message
		alertDialog.show();
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_account);
		
		InitializeFormControls();
		
		new GetCountries().execute();
		
		
		
		saveButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub				
				
				
				GetValuesFromControls();
				boolean isInputsValid = true;
				isInputsValid = ValidateRegistrationForm();
				
				if(isInputsValid)
				{
					// Operations if all inputs are valid
					
					JSONObject registrationDataJson = BuildJson();
					
					
					
					ConnectionDetector aConnectionDetector = new ConnectionDetector(getApplicationContext());
					boolean isInternetConnected = aConnectionDetector.isConnectingToInternet();
					
					if(isInternetConnected)
					{
						new AttemptRegistration().execute(registrationDataJson);
					}else{
						NoConnectionDialog();
					}
				}
				
			}
		});
	}
	
	class GetCountries extends AsyncTask<Void, Void, Void>
	{
		
		String stringResponse;
		JSONObject jsonResponse;
		int responselength;
		JSONArray aJsonArray;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
			progressDialog = new ProgressDialog(RegisterUI.this);
			progressDialog.setMessage("Fetching Required Information...");
			progressDialog.setIndeterminate(false);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			HttpClient httpClient = new DefaultHttpClient();
			
			/*************** Fetch Country Data *****************/
			
			try{
		         HttpGet httpGet = new HttpGet(COUNTRIES_URL);
		         HttpResponse httpResponse = httpClient.execute(httpGet);
		         HttpEntity httpEntity = httpResponse.getEntity();

		         if(httpEntity != null){

		             InputStream inputStream = httpEntity.getContent();

		             //Lecture du retour au format JSON
		             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		             StringBuilder stringBuilder = new StringBuilder();

		             String ligneLue = bufferedReader.readLine();
		             while(ligneLue != null){
		                 stringBuilder.append(ligneLue + " \n");
		                 ligneLue = bufferedReader.readLine();
		             }
		             bufferedReader.close();
		             //Log.i("JSON", stringBuilder.toString());  
		             aJsonArray = new JSONArray(stringBuilder.toString());
 

		         }
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
	
			
			return null;
		}
		

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			progressDialog.dismiss(); 
			responselength = aJsonArray.length();
			for(int I=0; I<responselength; I++)
			{
				Countries aCountries = null;
				try {
					
					JSONObject mJSONObject = aJsonArray.getJSONObject(I);
					aCountries = new Countries(mJSONObject.getString("country"), mJSONObject.getString("id"));
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				countryList.add(aCountries);
				
			}
			
			
					
			/*************** Populate country spinner using countryList *****************/
			
			List<String>countryNameList = new ArrayList<String>();
			
			for (Countries myCountries : countryList) {
				countryNameList.add(myCountries.CountryName());
			}
			
			ArrayAdapter<String> myArrayAdapter = new ArrayAdapter<String>(RegisterUI.this, android.R.layout.simple_spinner_item, countryNameList);
			myArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
			spinnerCountry.setAdapter(myArrayAdapter);
			
			/*************** Populate City spinner using Top Country on the list *****************/
			
			Countries topCountry = countryList.get(0);
			//System.out.println("Top Country: " + testCountry.CountryName() +" " + testCountry.CountryId());
			
			new GetCities().execute(topCountry.CountryId());
			
		}
		
	}
	
	class GetCities extends AsyncTask<String, Void, Void>
	{
		
		String stringResponse;
		JSONObject jsonResponse;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
			progressDialog = new ProgressDialog(RegisterUI.this);
			progressDialog.setMessage("Getting Cities...");
			progressDialog.setIndeterminate(false);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			String countryId = params[0];
			HttpClient httpclient = new DefaultHttpClient();
			HttpParams myParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(myParams, 10000);
			HttpConnectionParams.setSoTimeout(myParams, 10000);
			
			
			JSONObject aJsonObject = BuildCitiesJSONObject(countryId);
			//String json = aJsonObject.toString();
			System.out.println("Cities Request: " + aJsonObject.toString());

			try {

				HttpPost httppost = new HttpPost(CITIES_URL);
				StringEntity se = new StringEntity(aJsonObject.toString());
				se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
						"application/json"));
				httppost.setEntity(se);

				HttpResponse response = httpclient.execute(httppost);				
				stringResponse = EntityUtils.toString(response.getEntity());
				System.out.println("Cities Response: " + stringResponse);

			} catch (ClientProtocolException e) {
				e.printStackTrace();

			} catch (IOException e) {
				e.printStackTrace();
			}

					
			
			return null;
		}		
		

		private JSONObject BuildCitiesJSONObject(String countryId) {
			// TODO Auto-generated method stub
			
			JSONObject myJsonObject = new JSONObject();
			
			try {
				myJsonObject.put("country_id", countryId);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return myJsonObject;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			progressDialog.dismiss();
			
//			try {
//				jsonResponse = new JSONObject(stringResponse);
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			System.out.println("Cities Response: " + jsonResponse.toString());
		}
		
		
	}
	
	class AttemptRegistration extends AsyncTask<JSONObject, Void, Void> {
		
		String stringResponse;
		JSONObject jsonResponse;
		int responselength;
		
		
		@Override
		protected void onPreExecute() {
			// do something before execution
			progressDialog = new ProgressDialog(RegisterUI.this);
			progressDialog.setMessage("Signing you up...");
			progressDialog.setIndeterminate(false);
			progressDialog.setCanceledOnTouchOutside(true);
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(JSONObject... jsonObjects) {
			// TODO Auto-generated method stub
			
			JSONObject aJsonObject = jsonObjects[0];
			HttpClient httpclient = new DefaultHttpClient();
			HttpParams myParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(myParams, 10000);
			HttpConnectionParams.setSoTimeout(myParams, 10000);

			String json = aJsonObject.toString();
			
			try {

				HttpPost httppost = new HttpPost(REGISTRATION_URL);
				StringEntity se = new StringEntity(aJsonObject.toString());
				se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
						"application/json"));
				httppost.setEntity(se);

				HttpResponse response = httpclient.execute(httppost);				
				stringResponse = EntityUtils.toString(response.getEntity());

			} catch (ClientProtocolException e) {
				e.printStackTrace();

			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			progressDialog.dismiss(); 
			
			try {
				jsonResponse = new JSONObject(stringResponse);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			
			int len = jsonResponse.length();
			System.out.println("The length of json Data: " + len + "Received Data: " + jsonResponse.toString());
		}
		
		
		
	}
	
}
