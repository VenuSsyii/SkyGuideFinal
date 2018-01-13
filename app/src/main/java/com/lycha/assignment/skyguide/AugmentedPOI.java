package com.lycha.assignment.skyguide;

public class AugmentedPOI {
	private String mName;
	private String mDescription;
	private double mLatitude;
	private double mLongitude;
	private String mCategories;

	public AugmentedPOI() {
		this("","",0,0,"");
	}

	public AugmentedPOI(String mName, String mDescription, double mLatitude, double mLongitude, String mCategories) {
		this.mName = mName;
		this.mDescription = mDescription;
		this.mLatitude = mLatitude;
		this.mLongitude = mLongitude;
		this.mCategories = mCategories;
	}

	public String getPoiName() {
		return mName;
	}

	public void setPoiName(String poiName) {
		this.mName = poiName;
	}

	public String getPoiDescription() {
		return mDescription;
	}

	public void setPoiDescription(String poiDescription) {
		this.mDescription = poiDescription;
	}

	public double getPoiLatitude() {
		return mLatitude;
	}

	public void setPoiLatitude(double poiLatitude) {
		this.mLatitude = poiLatitude;
	}

	public double getPoiLongitude() {
		return mLongitude;
	}

	public void setPoiLongitude(double poiLongitude) {
		this.mLongitude = poiLongitude;
	}

	public String getmCategories() {
		return mCategories;
	}

	public void setmCategories(String mCategories) {
		this.mCategories = mCategories;
	}

	@Override
	public String toString() {
		return "AugmentedPOI{" +
				", mName='" + mName + '\'' +
				", mDescription='" + mDescription + '\'' +
				", mLatitude=" + mLatitude +
				", mLongitude=" + mLongitude +
				", mCategories='" + mCategories + '\'' +
				'}';
	}
}
