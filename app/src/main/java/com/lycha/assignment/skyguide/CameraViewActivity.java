package com.lycha.assignment.skyguide;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CameraViewActivity extends Activity implements
		SurfaceHolder.Callback, OnLocationChangedListener, OnAzimuthChangedListener{

	private Camera mCamera;
	private SurfaceHolder mSurfaceHolder;
	private boolean isCameraviewOn = false;
	private List<AugmentedPOI> mPoi = new ArrayList<>();

	private double mAzimuthReal = 0;
	private double mAzimuthTeoretical = 0;
	private static double AZIMUTH_ACCURACY;
	private double mMyLatitude = 0;
	private double mMyLongitude = 0;

	private MyCurrentAzimuth myCurrentAzimuth;
	private MyCurrentLocation myCurrentLocation;

	TextView descriptionTextView;
	ImageView pointerIcon;
	TextView textViewHere;

	ImageButton buttonSetting;

	private SharedPreferences preference;
	private SharedPreferences.Editor editor;
	private AugmentedPOI insertPoi = new AugmentedPOI();

	private Spinner spinner;
	private String cat;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera_view);

		ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.INTERNET,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		buttonSetting = (ImageButton) findViewById(R.id.imageButtonSetting);

		preference = PreferenceManager.getDefaultSharedPreferences(this);
		editor = preference.edit();
		preference.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
				if(s.equals("Accuracy")){
					AZIMUTH_ACCURACY = sharedPreferences.getInt("Accuracy",5);
				}
			}
		});

		AZIMUTH_ACCURACY = preference.getInt("Accuracy",5);
		setupListeners();
		setupLayout();

		DatabaseHelper db = new DatabaseHelper(this);

		List<AugmentedPOI> values = db.getAllRecords();
		for(AugmentedPOI poi:values){
			addAugmentedRealityPoint(poi);
		}

		final List<String> categories = db.getCategories();
		spinner = (Spinner) findViewById(R.id.spinner);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,categories);
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(arrayAdapter);
		spinner.setSelection(0);
		cat = categories.get(0);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				cat = spinner.getSelectedItem().toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
				cat = categories.get(0);
			}
		});
	}

	public void buttonSetting(View view){
		Intent intent = new Intent(this,SettingActivity.class);
		startActivity(intent);
	}

	public List<Double> calculateDistance(double targetLat, double targetLog){
		List<Double> distanceList = new ArrayList<Double>();

		Location myLocation = new Location("My Location");
		myLocation.setLatitude(mMyLatitude);
		myLocation.setLongitude(mMyLongitude);

		Location targetLocation = new Location("Target Location");

		for(AugmentedPOI poi:mPoi){
			double passTargetLat = targetLat;
			double passTargetLog = targetLog;
			LatLng latLng = new LatLng(passTargetLat,passTargetLog);
			targetLocation.setLatitude(latLng.latitude);
			targetLocation.setLongitude(latLng.longitude);

			double distance = myLocation.distanceTo(targetLocation);
			distanceList.add(distance);
		}
		return distanceList;
	}

	private void addAugmentedRealityPoint(AugmentedPOI poi) {
		mPoi.add(new AugmentedPOI(poi.getPoiName(),poi.getPoiDescription(),poi.getPoiLatitude(),poi.getPoiLongitude(),poi.getmCategories()));
	}

	public double calculateTeoreticalAzimuth(AugmentedPOI mPoi) {
		double dX = mPoi.getPoiLatitude() - mMyLatitude;
		double dY = mPoi.getPoiLongitude() - mMyLongitude;

		double phiAngle;
		double tanPhi;
		double azimuth = 0;

		tanPhi = Math.abs(dY / dX);
		phiAngle = Math.atan(tanPhi);
		phiAngle = Math.toDegrees(phiAngle);

		if (dX > 0 && dY > 0) { // I quater
			return azimuth = phiAngle;
		} else if (dX < 0 && dY > 0) { // II
			return azimuth = 180 - phiAngle;
		} else if (dX < 0 && dY < 0) { // III
			return azimuth = 180 + phiAngle;
		} else if (dX > 0 && dY < 0) { // IV
			return azimuth = 360 - phiAngle;
		}

		return phiAngle;
	}

	private List<Double> calculateAzimuthAccuracy(double azimuth) {
		double minAngle = azimuth - AZIMUTH_ACCURACY;
		double maxAngle = azimuth + AZIMUTH_ACCURACY;
		List<Double> minMax = new ArrayList<Double>();

		if (minAngle < 0)
			minAngle += 360;

		if (maxAngle >= 360)
			maxAngle -= 360;

		minMax.clear();
		minMax.add(minAngle);
		minMax.add(maxAngle);

		return minMax;
	}

	private boolean isBetween(double minAngle, double maxAngle, double azimuth) {
		if (minAngle > maxAngle) {
			if (isBetween(0, maxAngle, azimuth) && isBetween(minAngle, 360, azimuth))
				return true;
		} else {
			if (azimuth > minAngle && azimuth < maxAngle)
				return true;
		}
		return false;
	}

	private void updateDescription() {
		String str = "Nearby "+cat+" Location\n--------------------------\n";
		int a = 0;
		int count = 0;
		for(AugmentedPOI poi : mPoi) {
			double distance = calculateDistance(poi.getPoiLatitude(),poi.getPoiLongitude()).get(a);
			a++;
			//if(distance < 1000.00 ) {
				if(cat.equals("All")||cat.equals(poi.getmCategories())) {
					mAzimuthTeoretical = calculateTeoreticalAzimuth(poi);
					str = str.concat(poi.getPoiName() + "\n");
					count++;
				}
			//}
		}
		if(count == 0){
			str = str.concat("No Nearby Location\n------------------------------"+ "\nCoordinate ( " + mMyLatitude + " , " + mMyLongitude + " )");
			descriptionTextView.setText(str);
		}
		else{
			str = str.concat("------------------------------"+ "\nCoordinate ( " + mMyLatitude + " , " + mMyLongitude + " )");
			descriptionTextView.setText(str);
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		mMyLatitude = location.getLatitude();
		mMyLongitude = location.getLongitude();
		//Toast.makeText(this,"latitude: "+location.getLatitude()+" longitude: "+location.getLongitude(), Toast.LENGTH_SHORT).show();
		for(AugmentedPOI poi : mPoi) {
			mAzimuthTeoretical = calculateTeoreticalAzimuth(poi);
			updateDescription();
		}
	}

	@Override
	public void onAzimuthChanged(float azimuthChangedFrom, float azimuthChangedTo) {
		int a = 0;
		pointerIcon = (ImageView) findViewById(R.id.icon);
		textViewHere = (TextView) findViewById(R.id.textViewHere);
		pointerIcon.setVisibility(View.INVISIBLE);
		textViewHere.setVisibility(View.INVISIBLE);

		for(AugmentedPOI poi : mPoi) {
			mAzimuthReal = azimuthChangedTo;
			mAzimuthTeoretical = calculateTeoreticalAzimuth(poi);

			double minAngle = calculateAzimuthAccuracy(mAzimuthTeoretical).get(0);
			double maxAngle = calculateAzimuthAccuracy(mAzimuthTeoretical).get(1);

			if (isBetween(minAngle, maxAngle, mAzimuthReal)) {
				double distance = calculateDistance(poi.getPoiLatitude(),poi.getPoiLongitude()).get(a);
				a++;
				//if(distance < 1000.00){
					if(cat.equals("All")||cat.equals(poi.getmCategories())) {
						textViewHere.setText("Distance to " + poi.getPoiName() + " is " + String.format("%.2f", distance) + "m");
						pointerIcon.setVisibility(View.VISIBLE);
						textViewHere.setVisibility(View.VISIBLE);
					}
				//}
			}
			updateDescription();
		}
	}

	@Override
	protected void onStop() {
		if(myCurrentAzimuth!=null)
			myCurrentAzimuth.stop();
		if(myCurrentLocation!=null)
			myCurrentLocation.stop();
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(myCurrentAzimuth!=null)
			myCurrentAzimuth.start();
		if(myCurrentLocation!=null)
			myCurrentLocation.start();
	}

	private void setupListeners() {
		myCurrentLocation = new MyCurrentLocation(this);
		myCurrentLocation.buildGoogleApiClient(this);
		myCurrentLocation.start();

		myCurrentAzimuth = new MyCurrentAzimuth(this, this);
		myCurrentAzimuth.start();
	}

	private void setupLayout() {
		descriptionTextView = (TextView) findViewById(R.id.cameraTextView);

		getWindow().setFormat(PixelFormat.UNKNOWN);
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.cameraview);
		mSurfaceHolder = surfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
							   int height) {
		if (isCameraviewOn) {
			mCamera.stopPreview();
			isCameraviewOn = false;
		}

		if (mCamera != null) {
			try {
				mCamera.setPreviewDisplay(mSurfaceHolder);
				mCamera.startPreview();
				isCameraviewOn = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mCamera = Camera.open();
		mCamera.setDisplayOrientation(90);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
		isCameraviewOn = false;
	}
}
