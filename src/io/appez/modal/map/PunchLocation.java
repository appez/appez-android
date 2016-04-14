package io.appez.modal.map;

/**
 * PunchLocation : Model bean containing attributes that define the user
 * location on a map, location description and its coloured marker. This model
 * defines the above mentioned properties for a single location on the map. For
 * marking multiple locations on the map, an array of these fields needs to be
 * sent from the web layer
 * 
 * */
public class PunchLocation {
	// Latitude of the location
	private String latitude = null;
	// Longitude of the location
	private String longitude = null;
	// Title of the location to be marked. This gets shown in the info-bubble
	// marker of the location.
	private String locationTitle = null;
	// Description of the location to be marked. This gets shown in the
	// info-bubble marker of the location.
	private String locationDescription = null;
	// Defines the colour of the marker pin of this location on the map.
	// Currently 4 coloured pins are supported(Red, Green, Blue, Yellow)
	private String locationMarker = null;

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLocationTitle() {
		return locationTitle;
	}

	public void setLocationTitle(String locationTitle) {
		this.locationTitle = locationTitle;
	}

	public String getLocationDescription() {
		return locationDescription;
	}

	public void setLocationDescription(String locationDescription) {
		this.locationDescription = locationDescription;
	}

	public String getMarkerPinColor() {
		return locationMarker;
	}

	public void setLocationMarker(String locationMarker) {
		this.locationMarker = locationMarker;
	}
}
