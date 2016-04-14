package io.appez.listeners;

/**
 * DialogListener : Defines an Interface for capturing the events from the UI
 * dialog. This includes listening to events on decision dialog, single list
 * selection dialog, multiple choice selection dialog, date pickers and other
 * standard UI components
 * 
 * */
public interface DialogListener {

	public static final int DIALOG_MESSAGE_YESNO = 1002;
	public static final int DIALOG_MESSAGE_OK = 1004;
	public static final int DIALOG_LOADING = 1005;
	public static final int DIALOG_SINGLE_CHOICE_LIST = 1006;
	public static final int DIALOG_SINGLE_CHOICE_LIST_RADIO_BTN = 1007;
	public static final int DIALOG_MULTIPLE_CHOICE_LIST_CHECKBOXES = 1008;

	/**
	 * Specifies action to be taken when application exits
	 * 
	 */
	void exitApp();

	/**
	 * Specifies action to be taken on the basis of user selection provided
	 * 
	 * userSelection : User selection
	 */
	void processUsersSelection(String userSelection);
}
