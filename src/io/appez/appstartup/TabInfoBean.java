package io.appez.appstartup;

/**
 * TabInfoBean : Model for holding all the required information fields,
 * corresponding to a single application tab.(Applicable to tab based
 * application only)
 * 
 * */
public class TabInfoBean {
	// Indicates the label of the tab item. This label is the one that appears
	// in the tab bar of the application
	private String tabLabel = null;
	// Indicates the icon of the tab item.
	private String tabIcon = null;
	// Indicates a unique ID assigned to the tab item. This is specified by the
	// application developer for its reference.
	private String tabId = null;
	// Indicates the page URL which is associated with this tab. On selecting
	// this tab, the page specified gets loaded.
	private String tabContentUrl = null;

	// Getter-Setter for 'tabLabel'
	public String getTabLabel() {
		return tabLabel;
	}

	public void setTabLabel(String tabLabel) {
		this.tabLabel = tabLabel;
	}

	// Getter-Setter for 'tabIcon'
	public String getTabIcon() {
		return tabIcon;
	}

	public void setTabIcon(String tabIcon) {
		this.tabIcon = tabIcon;
	}

	// Getter-Setter for 'tabId'
	public String getTabId() {
		return tabId;
	}

	public void setTabId(String tabId) {
		this.tabId = tabId;
	}

	// Getter-Setter for 'tabContentUrl'
	public String getTabContentUrl() {
		return tabContentUrl;
	}

	public void setTabContentUrl(String tabContentUrl) {
		this.tabContentUrl = tabContentUrl;
	}

}
