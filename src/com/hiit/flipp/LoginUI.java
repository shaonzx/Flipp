package com.hiit.flipp;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import android.R.string;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class LoginUI extends Activity implements OnClickListener {

	private EditText userEditText, passEditText;
	private ImageView loginButton, registerButton;

	private ProgressDialog pDialog;

	// testing from a real server:
	private static final String LOGIN_URL = "http://pharmasolve.apiary-mock.com/user/login";

	
	
	

	
	String usernameString, passworddString;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		userEditText = (EditText) findViewById(R.id.edtUserName);
		passEditText = (EditText) findViewById(R.id.edtPassword);

		loginButton = (ImageView) findViewById(R.id.imgLogin);
		registerButton = (ImageView) findViewById(R.id.imgRegister);

		loginButton.setOnClickListener(this);
		registerButton.setOnClickListener(this);
		
		

	}
	
 
    public void NoConnectionDialog() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginUI.this);

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


	public JSONObject BuildJson() {

		usernameString = userEditText.getText().toString();
		passworddString = passEditText.getText().toString();

		JSONObject loginJasonData = new JSONObject();
		try {
			loginJasonData.put("password", passworddString);
			loginJasonData.put("username", usernameString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return loginJasonData;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.imgLogin:

			JSONObject logJson = BuildJson();
			
			ConnectionDetector aConnectionDetector = new ConnectionDetector(getApplicationContext());
			boolean isConnected = aConnectionDetector.isConnectingToInternet();
			
			if(isConnected)
			{
				new AttemptLogin().execute(logJson);
			}else{
				NoConnectionDialog();
			}
						

			break;

		case R.id.imgRegister:
			
			ConnectionDetector mConnectionDetector = new ConnectionDetector(getApplicationContext());
			boolean isInternetConnected = mConnectionDetector.isConnectingToInternet();
			
			if(isInternetConnected)
			{
				Intent startRegister = new Intent(this, RegisterUI.class);
				startActivity(startRegister);
			}else{
				NoConnectionDialog();
			}
			
			
			break;

		default:
			break;

		}

	}

	class AttemptLogin extends AsyncTask<JSONObject, Void, Void> {
		
		
		String stringResponse;
		JSONObject jsonResponse;
		int responselength;
		
		public String user_id;
		public String user_auth_id;
		public String error_message;
		
		
		

		@Override
		protected void onPreExecute() {
			// do something before execution
			pDialog = new ProgressDialog(LoginUI.this);
			pDialog.setMessage("Logging in...");
			pDialog.setIndeterminate(false);			
			pDialog.setCanceledOnTouchOutside(true);
			pDialog.show();
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

				HttpPost httppost = new HttpPost(LOGIN_URL);
				StringEntity se = new StringEntity(aJsonObject.toString());
				se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
						"application/json"));
				httppost.setEntity(se);

				HttpResponse response = httpclient.execute(httppost);				
				stringResponse = EntityUtils.toString(response.getEntity());
//				System.out.println("Json Data From doIn func: "
//						+ aJsonObject.toString() + "\nResponse: " + stringResponse);

			} catch (ClientProtocolException e) {
				e.printStackTrace();

			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void unused) {			
			pDialog.dismiss();
			
			try {
				jsonResponse = new JSONObject(stringResponse);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			
			int len = jsonResponse.length();
			System.out.println("Json Data: " + jsonResponse.toString());
			
			
			
					/***********************************/
			
			if(len == 2)
			{
				
				
				
				try {
					user_id = jsonResponse.getString("id");
					user_auth_id = jsonResponse.getString("auth_id");
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Intent intentToStartGeolocation = new Intent(LoginUI.this, GeolocationUI.class);
				intentToStartGeolocation.putExtra("putUserId", user_id);
				intentToStartGeolocation.putExtra("putUserAuthId", user_auth_id);
				//System.out.println("ID: " + user_id + "\nAUTH_ID: " + user_auth_id); // copied
				startActivity(intentToStartGeolocation);
				
			}
			else{
				try {
					error_message = jsonResponse.getString("message");
					System.out.println("ERROR MESSAGE: " + error_message);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
					/***********************************/
			
		}
	}

	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}
	
}