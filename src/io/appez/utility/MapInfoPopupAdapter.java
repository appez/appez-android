package io.appez.utility;

import io.appez.constants.SmartConstants;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

/**
 * {@link MapInfoPopupAdapter} : Enables the design of the map info window and
 * responsible for various visual properties on it such as title, description
 * and the color of the marker pin
 * 
 * */
public class MapInfoPopupAdapter implements InfoWindowAdapter {

	LayoutInflater inflater = null;
	Context context = null;

	MapInfoPopupAdapter(LayoutInflater inflater, Context ctx) {
		this.inflater = inflater;
		this.context = ctx;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		return (null);
	}

	/**
	 * Defines the behaviour of the InfoWindow on being clicked on it
	 * 
	 * @param marker
	 * */
	@Override
	public View getInfoContents(Marker marker) {
		Log.d(SmartConstants.APP_NAME, "MapInfoPopupAdapter->getInfoContents");
		View popup = inflater.inflate(AppUtility.getResourseIdByName(context.getPackageName(), "layout", "im_infowindow_layout"), null);

		TextView tv = (TextView) popup.findViewById(AppUtility.getResourseIdByName(context.getPackageName(), "id", "im_infowindow_title"));

		tv.setText(marker.getTitle());
		tv = (TextView) popup.findViewById(AppUtility.getResourseIdByName(context.getPackageName(), "id", "im_infowindow_snippet"));
		tv.setText(marker.getSnippet());

		return (popup);
	}

}
