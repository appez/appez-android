package io.appez.appstartup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * ActionBarStylingInfoBean : Model for holding the parsed information regarding
 * the styling and theme of the Android topbar. Also, holds information
 * regarding the tab bar for Action bar tabs (for tab based applications where
 * Action bar tabs are used)
 * 
 * */
public class ActionBarStylingInfoBean {
	/**
	 * Properties for the topbar of the application
	 * */
	// Indicates the type of background to be set for action bar. Currently
	// supported background types- GRADIENT, IMAGE, COLOUR
	private String topbarBgType = null;
	// Indicates the colour of the background to be set. Applicable if the
	// background type is COLOUR. Accepts hex value of the colour.
	private String topbarBgColor = null;
	// Indicates the image of the background to be set. Applicable if the
	// background type is IMAGE.
	private String topbarBgImage = null;
	// Indicates the gradient of the background to be set. Applicable if the
	// background type is GRADIENT. Defined as an array that contains the set of
	// gradient colours provided by the user.
	private String[] topbarBgGradient = null;
	// Indicates the type of gradient to be applied if the background type is
	// GRADIENT. Based on user specification, the gradient can be horizontal or
	// vertical type.
	private String topbarBgGradientType = null;
	// Indicates the colour of text on the action bar
	private String topbarTextColor = null;

	/**
	 * Properties for the tab bar of the application(applicable for tab based
	 * application only)
	 * */
	// Indicates the type of background to be set for action bar tabs. Currently
	// supported background types- GRADIENT, IMAGE, COLOUR
	private String tabbarBgType = null;
	// Indicates the colour of the background to be set on action bar tabs.
	// Applicable if the
	// background type is COLOUR. Accepts hex value of the colour.
	private String tabbarBgColor = null;
	// Indicates the image of the background to be set. Applicable if the
	// background type is IMAGE.
	private String tabbarBgImage = null;
	// Indicates the gradient of the background to be set. Applicable if the
	// background type is GRADIENT. Defined as an array that contains the set of
	// gradient colours provided by the user.
	private String tabbarBgGradient = null;
	// Indicates the colour of text on the action bar tabs
	private String tabbarTextColor = null;

	public String getTopbarBgType() {
		return topbarBgType;
	}

	public void setTopbarBgType(String topbarBgType) {
		this.topbarBgType = topbarBgType;
	}

	public String getTopbarBgColor() {
		return topbarBgColor;
	}

	public void setTopbarBgColor(String topbarBgColor) {
		this.topbarBgColor = topbarBgColor;
	}

	public String getTopbarBgImage() {
		return topbarBgImage;
	}

	public void setTopbarBgImage(String topbarBgImage) {
		this.topbarBgImage = topbarBgImage;
	}

	public String[] getTopbarBgGradient() {
		return topbarBgGradient;
	}

	public void setTopbarBgGradient(String topbarBgGradient) {
		String[] gradientColors = null;
		try {
			JSONArray infoProperties = new JSONArray(topbarBgGradient);
			gradientColors = new String[infoProperties.length()];
			for (int i = 0; i < infoProperties.length(); i++) {
				JSONObject gradientStop = (JSONObject) infoProperties.get(i);
				gradientColors[i] = (String) gradientStop.getString("color");
			}
			// this.topbarBgGradient = topbarBgGradient;
			this.topbarBgGradient = gradientColors;
		} catch (JSONException je) {
			// TODO handle this exception
		}
	}

	public String getTopbarBgGradientType() {
		return topbarBgGradientType;
	}

	public void setTopbarBgGradientType(String topbarBgGradientType) {
		this.topbarBgGradientType = topbarBgGradientType;
	}

	public String getTopbarTextColor() {
		return this.topbarTextColor;
	}

	public void setTopbarTextColor(String topbarTextColor) {
		this.topbarTextColor = topbarTextColor;
	}

	public String getTabbarBgType() {
		return tabbarBgType;
	}

	public void setTabbarBgType(String tabbarBgType) {
		this.tabbarBgType = tabbarBgType;
	}

	public String getTabbarBgColor() {
		return tabbarBgColor;
	}

	public void setTabbarBgColor(String tabbarBgColor) {
		this.tabbarBgColor = tabbarBgColor;
	}

	public String getTabbarBgImage() {
		return tabbarBgImage;
	}

	public void setTabbarBgImage(String tabbarBgImage) {
		this.tabbarBgImage = tabbarBgImage;
	}

	public String getTabbarBgGradient() {
		return tabbarBgGradient;
	}

	public void setTabbarBgGradient(String tabbarBgGradient) {
		this.tabbarBgGradient = tabbarBgGradient;
	}

	public String getTabbarTextColor() {
		return tabbarTextColor;
	}

	public void setTabbarTextColor(String tabbarTextColor) {
		this.tabbarTextColor = tabbarTextColor;
	}
}
