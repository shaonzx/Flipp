package com.hiit.flipp;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class Mapview extends Activity {
	
	private GoogleMap aGoogleMap;
	
	ImageView btnPickMe;	
	MarkerOptions marker;
	
	private String newAddress;
	private ProgressDialog progressDialog;
	private double myLatitude = 0;
	private double myLongitude = 0;
	
	

	
	private void GetMyCoordinates()
	{
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocationListener ll = new myLocationListener();
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
	}
	
	private class myLocationListener implements LocationListener{

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			
			if(location != null)
			{
				myLatitude = location.getLatitude();
				myLongitude = location.getLongitude();
			}
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub0
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapview);
		
		btnPickMe = (ImageView) findViewById(R.id.imgPickMe);
		myLatitude = getIntent().getExtras().getDouble("putLatitude");
		myLongitude = getIntent().getExtras().getDouble("putLongitude");
		
		
		
		
		try{
			InitializeMap();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		//myLatitude = 23.7000;
		//myLongitude = 90.3750;
		
		if(myLatitude == 0 && myLongitude == 0)
		{
			GetMyCoordinates();
		}
		
		while(myLatitude == 0)
		{
			//wait...
		}
		
		PointMyLocation();
		
		btnPickMe.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				//double newlati, newlongi;
				
				myLatitude = aGoogleMap.getCameraPosition().target.latitude;
				myLongitude = aGoogleMap.getCameraPosition().target.longitude;
				
//				myLatitude = 23.7000;
//				myLongitude = 90.3750;
				
				new RetriveAddress().execute();				
				
			}
		});
	}
	
	
	private void PointMyLocation()
	{
		//marker = new MarkerOptions().position(new LatLng(latitude, longiteude));
		//marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
		//aGoogleMap.addMarker(marker);
		
		CameraPosition aCameraPosition = new CameraPosition.Builder().target(
				new LatLng(myLatitude, myLongitude)).zoom(15).build();
		
		aGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(aCameraPosition));
	}

	private void InitializeMap() {
		// TODO Auto-generated method stub
		
		if(aGoogleMap == null)		
			aGoogleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		
		
		if(aGoogleMap == null)		
			Toast.makeText(Mapview.this, "Unable to fetch Map!", Toast.LENGTH_SHORT).show();		
	}

	@Override
	public void finish() {
	  // Prepare data intent 
	  Intent callGeo = new Intent(Mapview.this, GeolocationUI.class);
	  callGeo.putExtra("returnAddress", newAddress);
	  callGeo.putExtra("returnMyLatitude", myLatitude);
	  callGeo.putExtra("returnMyLongitude", myLongitude);
	  setResult(RESULT_OK, callGeo);
	  super.finish();
	} 
	
	
	
	private StringBuilder getMyLocationAddress() {

		Geocoder geocoder = new Geocoder(Mapview.this, Locale.ENGLISH);

		try {

			// Place your latitude and longitude
			List<Address> addresses = geocoder.getFromLocation(myLatitude,
					myLongitude, 1);

			if (addresses != null) {

				Address fetchedAddress = addresses.get(0);
				StringBuilder strAddress = new StringBuilder();

				for (int i = 0; i < fetchedAddress.getMaxAddressLineIndex(); i++) {
					strAddress.append(fetchedAddress.getAddressLine(i)).append(
							"\n");
				}

				return strAddress;
				
			}

			else
				Toast.makeText(Mapview.this, "No location found..!",
						Toast.LENGTH_SHORT).show();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "Could not get address..!",
					Toast.LENGTH_SHORT).show();
			
		}
		return null;
	}
	
	
	class RetriveAddress extends AsyncTask<Void, Void, Void> {
		
		StringBuilder getAddress;
		
		@Override
		protected void onPreExecute() {
			// do something before execution
			progressDialog = new ProgressDialog(Mapview.this);
			progressDialog.setMessage("Retriving Address...");
			progressDialog.setIndeterminate(false);
			progressDialog.setCancelable(false);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			getAddress = getMyLocationAddress();
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(getAddress == null)
			{
				progressDialog.dismiss();
			}else{
				progressDialog.dismiss();
				newAddress = getAddress.toString();
				finish();				
			}
		}
		
		
		
	}
}
