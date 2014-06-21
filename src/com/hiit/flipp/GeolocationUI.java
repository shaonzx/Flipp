package com.hiit.flipp;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class GeolocationUI extends Activity {
	


	private static final int REQUEST_CODE = 0;
	public static String USER_ID;
	public static String USER_AUTH_ID;
	private String PICK_UP_URL;
	private String PICK_UP_RESPONSE_URL;
	
	private String REQUEST_ID, NO_OF_CABS_AVAILABLE;
	
	private String addressFromMap, notes;

	private ProgressDialog progressDialog;
	private AlertDialog alertDialog;

	
	boolean hasDataFromMapview = false;
	private boolean alertDialogResponse;
	private boolean calledFromUseMap = false;
	
	
	ImageButton btnCallTaxi;
	Button btnUseMap;
	EditText edtAddressGeolocation, edtNoteGeolocation;

	double latitude = 0.0, longitude = 0.0;
	//double updatedLatutude = 0.0, updatedLongitude;

	LocationManager aLocationManager;
	MyLocationListener aMyLocationListener;
	
	/***************** Declaration Regarding Menu Section**********************/
	ImageView menuIcon;
	RelativeLayout menu;
	RelativeLayout mainLayout;
	private boolean checkMenuStatus;
	Button profile, billing, help, logout;

	
	private void InitializeComponant() {
		// TODO Auto-generated method stub
		
		PICK_UP_URL = "http://taxiflipp.apiary-mock.com/user/pick_me_up/"+USER_AUTH_ID;
		PICK_UP_RESPONSE_URL = "http://taxiflipp.apiary-mock.com/user/pick_me_up_response/"+USER_AUTH_ID;
		
		/***************** Initialization Regarding Menu Section**********************/
		checkMenuStatus = false;
		menuIcon = (ImageView) findViewById(R.id.imgMenuImage);
		menu = (RelativeLayout) findViewById(R.id.popUpMenuLayout);
		mainLayout = (RelativeLayout) findViewById(R.id.relativeLayoutMain);
		profile = (Button) findViewById(R.id.btnProfile);
		billing = (Button) findViewById(R.id.btnBilling);
		help = (Button) findViewById(R.id.btnHelp);
		logout = (Button) findViewById(R.id.btnLogout);
				
		USER_ID = getIntent().getExtras().getString("putUserId");
		USER_AUTH_ID = getIntent().getExtras().getString("putUserAuthId");
		// System.out.println("User Id: " + USER_ID +"\nAUTH_ID: " +
		// USER_AUTH_ID);
		
		btnUseMap = (Button) findViewById(R.id.btnUseMap);
		btnCallTaxi = (ImageButton) findViewById(R.id.imgCallTaxiButton);
		edtAddressGeolocation = (EditText) findViewById(R.id.edtAddressGeolocation);
		edtNoteGeolocation = (EditText) findViewById(R.id.edtNoteGeolocation);		
	}

	public void ShowSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(GeolocationUI.this);
      
        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");
  
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
  
        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);
  
        // On pressing Settings button
        alertDialog.setNegativeButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                GeolocationUI.this.startActivity(intent);
            }
        });
  
        // on pressing cancel button
        alertDialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
        
        btnCallTaxi.setEnabled(false);
  
        // Showing Alert Message
        alertDialog.show();
    }
	
	public boolean CanGetLocation()
	{
		aLocationManager = (LocationManager) GeolocationUI.this.getSystemService(LOCATION_SERVICE);
		
		boolean isGPSEnabled, isNetworkEnabled;
		
		// getting GPS status
        isGPSEnabled = aLocationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
                
        // getting network status
        isNetworkEnabled = aLocationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        
        if (isGPSEnabled && isNetworkEnabled)
        {
        	return true;
        }else
        {
        	return false;
        }
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.geolocation);		
		
		InitializeComponant();
		
		
		profile.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent startProfile = new Intent(GeolocationUI.this, ProfileUI.class);
				startActivity(startProfile);
				
			}
		});
		
		billing.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent startBilling = new Intent(GeolocationUI.this, BillingUI.class);
				startActivity(startBilling);
			}
		});
		
		help.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent startHelp = new Intent(GeolocationUI.this, HelpUI.class);
				startActivity(startHelp);
				
			}
		});

		logout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				menu.setVisibility(View.GONE);
				
				Thread timer = new Thread(){
					
					public void run()
					{
						
						try{					
							
							sleep(500);
														
						}catch(Exception e){
							e.printStackTrace();
						}finally{
							progressDialog.dismiss();
							Intent login = new Intent(GeolocationUI.this, LoginUI.class);
							startActivity(login);
							
							
						}
					}
					
				};
				timer.start();
				finish();
			}
		});
		
		menuIcon.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(!checkMenuStatus)
				{
					menu.setVisibility(View.VISIBLE);
					mainLayout.setClickable(false);
					checkMenuStatus = true;
				}else{
					menu.setVisibility(View.GONE);
					checkMenuStatus = false;
					mainLayout.setClickable(true);
				}
				
			}
		});
		
		if(CanGetLocation())
		{			
			if (!startService()) {
				Toast.makeText(GeolocationUI.this, "Service can not be Started!",
						Toast.LENGTH_LONG).show();
			} else {
//				Toast.makeText(GeolocationUI.this, "Service Started!",
//						Toast.LENGTH_SHORT).show();
				System.out.println("Service Started!");
			}			
		}else{
			ShowSettingsAlert();
		}


		
		

		btnUseMap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				
	            
				Intent intentToCallMap = new Intent(GeolocationUI.this, Mapview.class);
				intentToCallMap.putExtra("putLatitude", latitude);
				intentToCallMap.putExtra("putLongitude", longitude);
				//startActivity(intentToCallMap);
				startActivityForResult(intentToCallMap, REQUEST_CODE);
			}
		});
		
		btnCallTaxi.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				addressFromMap = edtAddressGeolocation.getText().toString();
				notes = edtNoteGeolocation.getText().toString();				
				JSONObject aJsonObject = BuildJSON();			
								
				ShowAlertDialogForCallTaxi(aJsonObject);
				
				
			}			
		});
	}
	
	

	private void ShowAlertDialogForCallTaxi(final JSONObject myJsonObject) {
		// TODO Auto-generated method stub
		
		AlertDialog.Builder anAlertDialog = new AlertDialog.Builder(GeolocationUI.this);
		anAlertDialog.setTitle("Call The Cab?");
		anAlertDialog.setMessage(addressFromMap);
		
			
		anAlertDialog.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				new TaxiRequest().execute(myJsonObject);
			}
		});
		
		anAlertDialog.setPositiveButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
		
		
		anAlertDialog.show();
	}
	
	private void SorryAlertDialog(String message)
	{
		AlertDialog.Builder anAlertDialog = new AlertDialog.Builder(GeolocationUI.this);
		anAlertDialog.setTitle("Sorry");
		anAlertDialog.setMessage(message);
		
		anAlertDialog.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		
		anAlertDialog.setPositiveButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
		anAlertDialog.show();
		
	}
	
	private void ConfirmedAlertDialog(String message)
	{
		AlertDialog.Builder anAlertDialog = new AlertDialog.Builder(GeolocationUI.this);
		anAlertDialog.setTitle("Confirmed");
		anAlertDialog.setMessage(message);
		
		anAlertDialog.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Intent intentToStartTaxiInfoScreen = new Intent(GeolocationUI.this, TaxiInfoScreenUI.class);
				startActivity(intentToStartTaxiInfoScreen);
			}
		});
		
		anAlertDialog.setPositiveButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
		
		anAlertDialog.show();
		
	}
	
	private JSONObject BuildJSON()
	{
		
		JSONObject aJSONObject = new JSONObject();
		
		try{
			aJSONObject.put("lat", latitude);
			aJSONObject.put("lng", longitude);
			aJSONObject.put("address", addressFromMap);
			aJSONObject.put("note", notes);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		
		return aJSONObject;
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
		    if (data.hasExtra("returnAddress")) {
		    	addressFromMap = data.getExtras().getString("returnAddress");
		    	edtAddressGeolocation.setText(addressFromMap);
		    	System.out.println("Previous Geolocation: " + latitude + " " + longitude);
		    	latitude = data.getExtras().getDouble("returnMyLatitude");
		    	longitude = data.getExtras().getDouble("returnMyLongitude");
		    	System.out.println("New Geolocation: " + latitude + " " + longitude);
		    	
		      }			
		}
		btnCallTaxi.setEnabled(true);
	}

	public boolean startService() {
		try {
			// this.locatorService= new
			// Intent(FastMainActivity.this,LocatorService.class);
			// startService(this.locatorService);

			GetCordinates aGetCordinates = new GetCordinates();
			aGetCordinates.execute();
			return true;
		} catch (Exception error) {
			return false;
		}

	}

	public void getMyLocationAddress() {

		Geocoder geocoder = new Geocoder(GeolocationUI.this, Locale.ENGLISH);

		try {

			// Place your latitude and longitude
			List<Address> addresses = geocoder.getFromLocation(latitude,
					longitude, 1);

			if (addresses != null) {

				Address fetchedAddress = addresses.get(0);
				StringBuilder strAddress = new StringBuilder();

				for (int i = 0; i < fetchedAddress.getMaxAddressLineIndex(); i++) {
					strAddress.append(fetchedAddress.getAddressLine(i)).append(
							"\n");
				}

				// myAddress.setText("I am at: " +strAddress.toString());
				edtAddressGeolocation.setText(strAddress.toString());
				btnCallTaxi.setImageResource(R.drawable.call_taxi_button_enabled);
				Toast.makeText(GeolocationUI.this, "Press Call to call a cab or Use Map to have more accurate location",
						Toast.LENGTH_LONG).show();
				progressDialog.dismiss();
			}

			else
				Toast.makeText(GeolocationUI.this, "No location found..!",
						Toast.LENGTH_SHORT).show();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "Could not get address..!",
					Toast.LENGTH_SHORT).show();
			progressDialog.dismiss();
		}
	}
	
	class GetCordinates extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
			aMyLocationListener = new MyLocationListener();
            aLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            aLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, aMyLocationListener);
            
            
			progressDialog = new ProgressDialog(GeolocationUI.this);
			progressDialog.setMessage("Determining Location...");
			progressDialog.setIndeterminate(false);
			progressDialog.setCanceledOnTouchOutside(true);
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			while (latitude == 0.0) {
				//System.out.println("Still Getting location...");
            }

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			
			
			GeolocationUI.this.getMyLocationAddress();
			
//			progressDialog.dismiss();
//            Toast.makeText(GeolocationUI.this, "LATITUDE :" + latitude + " LONGITUDE :" + longitude, 
//           		Toast.LENGTH_SHORT).show();
		}

	}

	public class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {

			int lat = (int) location.getLatitude(); // * 1E6);
			int log = (int) location.getLongitude(); // * 1E6);
			int acc = (int) (location.getAccuracy());

			String info = location.getProvider();
			try {

				// LocatorService.myLatitude=location.getLatitude();

				// LocatorService.myLongitude=location.getLongitude();

				latitude = location.getLatitude();
				longitude = location.getLongitude();
				//System.out.println("Latitude: " + latitude);

			} catch (Exception e) {
				e.printStackTrace();
				// progDailog.dismiss();
				// Toast.makeText(getApplicationContext(),"Unable to get Location"
				// , Toast.LENGTH_LONG).show();
			}

		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.i("OnProviderDisabled", "OnProviderDisabled");
		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.i("onProviderEnabled", "onProviderEnabled");
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.i("onStatusChanged", "onStatusChanged");

		}

	}

	class TaxiRequest extends AsyncTask<JSONObject, Void, Void> {
		
		String stringResponse;
		JSONObject jsonResponse;
		int responselength;

		@Override
		protected void onPreExecute() {
			// do something before execution
			progressDialog = new ProgressDialog(GeolocationUI.this);
			progressDialog.setMessage("Looking for taxis...");
			progressDialog.setIndeterminate(false);
			progressDialog.setCancelable(false);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.show();
		}
		
		
		@Override
		protected Void doInBackground(JSONObject... jsonObjects) {
			// TODO Auto-generated method stub
			JSONObject aJsonObject = jsonObjects[0];
			System.out.println("New Geolocation: " + latitude + " " + longitude);
			System.out.println("JSON Geolocation: "+ aJsonObject.toString());
			HttpClient httpclient = new DefaultHttpClient();
			HttpParams myParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(myParams, 10000);
			HttpConnectionParams.setSoTimeout(myParams, 10000);
			
			String json = aJsonObject.toString();

			try {

				HttpPost httppost = new HttpPost(PICK_UP_URL);
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
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			progressDialog.dismiss();
			
			try {
				
				
				/************* This Block would be enabled in real API *****************/
				//jsonResponse = new JSONObject(stringResponse);
				
				/************* This is a dummy data for testing purpose *****************/
				
				jsonResponse = new JSONObject();
				jsonResponse.put("request_id", "1");
				jsonResponse.put("noofcabsavailable", "2");
				
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//System.out.println("Lenghth: " + jsonResponse.length() + " Data: " + jsonResponse.toString());
			
			if(jsonResponse.length() == 1)
			{
				Toast.makeText(GeolocationUI.this, "Not authorised. User Does not exist!", Toast.LENGTH_SHORT).show();
			}else{
				
				try {
					REQUEST_ID = jsonResponse.getString("request_id");
					NO_OF_CABS_AVAILABLE = jsonResponse.getString("noofcabsavailable");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				JSONObject json4Response = new JSONObject();
				
				try{
					json4Response.put("request_id", REQUEST_ID);
					
				}
				catch (JSONException e)
				{
					e.printStackTrace();
				}
				
				new TaxiResponse().execute(json4Response);
			}
		}
			
	}

	
	class TaxiResponse extends AsyncTask<JSONObject, Void, Void> {		


		String stringResponse;
		JSONObject jsonResponse;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(GeolocationUI.this);
			progressDialog.setMessage("Retriving Taxi Information...");
			progressDialog.setIndeterminate(false);
			progressDialog.setCancelable(false);
			progressDialog.setCanceledOnTouchOutside(false);
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

				HttpPost httppost = new HttpPost(PICK_UP_RESPONSE_URL);
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
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			progressDialog.dismiss();
			
			try {
				
				
				/************* This Block would be enabled in real API *****************/
				//jsonResponse = new JSONObject(stringResponse);
				
				/************* This is a dummy data for testing purpose *****************/
				
				jsonResponse = new JSONObject();
				jsonResponse.put("cab_id", "1");
				jsonResponse.put("dispatch_status", "2");
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			if(jsonResponse.length() == 1)
			{
				try {
					SorryAlertDialog(jsonResponse.getString("message"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(jsonResponse.length() == 2) {
				try {
					System.out.println("Hello: " + jsonResponse.toString());
					String msg = jsonResponse.toString();
					ConfirmedAlertDialog("Dispatch Status: " + jsonResponse.getString("dispatch_status"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
}
