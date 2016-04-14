package io.appez.appstartup;

/**
 * MenuInfoBean : Model for holding all the required information fields,
 * corresponding to a single application menu
 * 
 * */
public class MenuInfoBean {
	// Indicates the label of the menu item. This label is the one that appears
	// in
	// the application. For Android 2.3 it appears in the menu bar. For Android
	// 4.x it could be either on the Action bar or on the overflow menu
	private String menuLabel = null;
	// Indicates the icon of the menu item. This label is the one that appears
	// in
	// the application. For Android 2.3 it appears in the menu bar. For Android
	// 4.x it appears in the Action bar
	private String menuIcon = null;
	// Indicates a unique ID assigned to the menu item. This is specified by the
	// application developer for its reference. When any menu item is selected,
	// this menu ID is passed to the web layer.
	private String menuId = null;

	// Getter-Setter for 'menuLabel'
	public String getMenuLabel() {
		return menuLabel;
	}

	public void setMenuLabel(String menuLabel) {
		this.menuLabel = menuLabel;
	}

	// Getter-Setter for 'menuIcon'
	public String getMenuIcon() {
		return menuIcon;
	}

	public void setMenuIcon(String menuIcon) {
		this.menuIcon = menuIcon;
	}

	// Getter-Setter for 'menuId'
	public String getMenuId() {
		return menuId;
	}

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}
}
