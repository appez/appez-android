package io.appez.constants;

//[4][3][2][1][0]
//[4]th Place- Will signify Event type i.e. Web=1, CO=2 or App=3
//[3]rd and [2]nd place - Will signify service type like UI Service,HTTP Service, Database service etc.
//[1]st and [0]th place - Will signify service Action/operation type like show loader, hide loader, HTTP Action types etc.

public interface CoEvents {
	public static final int CONTEXT_CHANGE = 20601;
	public static final int CONTEXT_WEBVIEW_SHOW = 20602;
	public static final int CONTEXT_WEBVIEW_HIDE = 20603;
	
	//Maps service constants
	public static final int CO_SHOW_MAP_ONLY = 20701;
	public static final int CO_SHOW_MAP_N_DIR = 20702;
	public static final int CO_SHOW_MAP_N_ANIMATION = 20703;
	
	public static final int CONTEXT_NOTIFICATION_CREATELIST = 203;
	public static final int ANIMATION_FLIP_PLAY = 204;
	public static final int ANIMATION_FLIP_RESUME = 205;
	public static final int ANIMATION_CURL_PLAY = 206;
	public static final int ANIMATION_CURL_RESUME = 207;
	public static final int CONTEXT_POPOVER_SHOW = 208;
	public static final int CONTEXT_POPOVER_HIDE = 209;
	public static final int CONTEXT_NOTIFICATION_MASTER = 210;
	public static final int CONTEXT_NOTIFICATION_DETAIL = 211;

}
