package io.appez.utility;

import io.appez.constants.CommMessageConstants;
import io.appez.constants.SmartConstants;
import io.appez.listeners.DialogListener;
import io.appez.modal.SessionData;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

/**
 * {@link UIUtility} : Responsible for creating the dialogs for the UI service
 * 
 * */
public final class UIUtility {

	private Context mAppContext;
	private Dialog mAlertDialog;
	private DialogListener mDialogListener;
	private DatePickerDialog datePickerDialog = null;
	private int selectedIndex = -1;
	private ArrayList<Integer> selectedIndices = null;

	private String dialogTitle = null;
	private String dialogText = null;

	private boolean isDateSet = false;

	private String positiveBtnText = null;
	private String negativeBtnText = null;
	private String btnText = null;

	public UIUtility(Context context, DialogListener dialogListener) {
		mAppContext = context;
		this.mDialogListener = dialogListener;
	}

	/**
	 * Sets current application context
	 * 
	 * context : Current application Context
	 * */
	public void setContext(Context context) {
		mAppContext = context;
	}

	/**
	 * Creates dialog corresponding to the ID and the message specified.
	 * Currently supported dialog types include Decision dialog, Message dialog,
	 * Loading dialog, Single choice List, Single choice radio list, Multiple
	 * selection dialog
	 * 
	 * @param id
	 *            : Specifies the ID required to create dialog of the required
	 *            type
	 * @param mStrStatusMessage
	 *            : Message to be shown on dialog
	 * 
	 * */
	public void createDialog(int id, String mStrStatusMessage) {
		AlertDialog.Builder builder = null;
		builder = new AlertDialog.Builder(mAppContext);
		switch (id) {
		case DialogListener.DIALOG_MESSAGE_YESNO:
			setMessageDialogTitleText(mStrStatusMessage);
			String positiveBtn = null;
			if ((this.getPositiveBtnText() != null) && (this.getPositiveBtnText().length() > 0)) {
				positiveBtn = this.getPositiveBtnText();
			} else {
				positiveBtn = AppUtility.getStringForId(AppUtility.getResourseIdByName(mAppContext.getPackageName(), SmartConstants.RESOURCE_CLASS_NAME_STRING, "im_yes"));
			}

			String negativeBtn = null;
			if ((this.getNegativeBtnText() != null) && (this.getNegativeBtnText().length() > 0)) {
				negativeBtn = this.getNegativeBtnText();
			} else {
				negativeBtn = AppUtility.getStringForId(AppUtility.getResourseIdByName(mAppContext.getPackageName(), SmartConstants.RESOURCE_CLASS_NAME_STRING, "im_no"));
			}

			builder.setMessage(dialogText).setTitle(dialogTitle).setCancelable(false).setPositiveButton(positiveBtn, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int id) {
					mDialogListener.processUsersSelection(SmartConstants.USER_SELECTION_YES);
					mAlertDialog.dismiss();
				}
			}).setNegativeButton(negativeBtn, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					mDialogListener.processUsersSelection(SmartConstants.USER_SELECTION_NO);
					mAlertDialog.cancel();
				}
			});

			break;

		case DialogListener.DIALOG_MESSAGE_OK:
			setMessageDialogTitleText(mStrStatusMessage);
			try {
				builder.setMessage(dialogText);
				builder.setTitle(dialogTitle);
				String okBtn = null;
				if ((this.getBtnText() != null) && (this.getBtnText().length() > 0)) {
					okBtn = this.getBtnText();
				} else {
					okBtn = AppUtility.getStringForId(AppUtility.getResourseIdByName(mAppContext.getPackageName(), SmartConstants.RESOURCE_CLASS_NAME_STRING, "im_ok"));
				}

				builder.setPositiveButton(okBtn, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						mDialogListener.processUsersSelection(SmartConstants.USER_SELECTION_OK);
						mAlertDialog.dismiss();
					}
				});
			} catch (Exception e) {
				// TODO handle this exception
			}

			break;
		case DialogListener.DIALOG_LOADING:
			try {
				// builder = new AlertDialog.Builder(mAppContext);
				// String message = mStrStatusMessage;
				LayoutInflater li = LayoutInflater.from(mAppContext);
				View view = li.inflate(AppUtility.getResourseIdByName(mAppContext.getPackageName(), "layout", "im_progress_layout"), null);
				builder.setView(view);
				TextView progressMessage = (TextView) view.findViewById(AppUtility.getResourseIdByName(mAppContext.getPackageName(), "id", "txt_message"));
				progressMessage.setText(mStrStatusMessage);
				mAlertDialog = builder.create();
				SessionData.getInstance().setProgressDialogShown(true);
			} catch (Exception e) {
				// TODO handle this exception
			}

			break;

		case DialogListener.DIALOG_SINGLE_CHOICE_LIST:
			createSingleChoiceListDialog(builder, mStrStatusMessage);
			break;

		case DialogListener.DIALOG_SINGLE_CHOICE_LIST_RADIO_BTN:
			createSingleChoiceListRadioBtnDialog(builder, mStrStatusMessage);
			break;

		case DialogListener.DIALOG_MULTIPLE_CHOICE_LIST_CHECKBOXES:
			selectedIndices = new ArrayList<Integer>();
			createMultiChoiceCheckboxDialog(builder, mStrStatusMessage);
			break;
		}
		mAlertDialog = builder.create();
		SessionData.getInstance().setProgressDialog(mAlertDialog);
		mAlertDialog.setCancelable(false);
		mAlertDialog.show();
	}

	/**
	 * Change the default title of the dialog with the one provided by the user
	 * 
	 * @param message
	 *            : Custom dialog title
	 * */
	private void setMessageDialogTitleText(String message) {
		// Initially default values are set for dialog text and titles
		dialogTitle = AppUtility.getStringForId(AppUtility.getResourseIdByName(mAppContext.getPackageName(), SmartConstants.RESOURCE_CLASS_NAME_STRING, "im_title_information"));
		dialogText = message;

		if (message.contains(SmartConstants.MESSAGE_DIALOG_TITLE_TEXT_SEPARATOR)) {
			String[] dialogAttributes = message.split(SmartConstants.MESSAGE_DIALOG_TITLE_TEXT_SEPARATOR);
			if (dialogAttributes.length == 2) {
				dialogTitle = dialogAttributes[0];
				dialogText = dialogAttributes[1];
			}
		}
	}

	/**
	 * Prepare the single choice dialog based on the list elements provided by
	 * the user
	 * 
	 * @param dialogBuilder
	 * @param mStrStatusMessage
	 *            : Contains the list of elements to be shown on the dialog
	 * 
	 * @return AlertDialog.Builder
	 * 
	 * */
	private AlertDialog.Builder createSingleChoiceListDialog(AlertDialog.Builder dialogBuilder, String mStrStatusMessage) {
		AlertDialog.Builder builder = dialogBuilder;
		String[] listItems = processSelectionListItemInfo(mStrStatusMessage);
		builder.setTitle(AppUtility.getStringForId(AppUtility.getResourseIdByName(mAppContext.getPackageName(), SmartConstants.RESOURCE_CLASS_NAME_STRING, "im_title_select_item")))
				.setItems(listItems, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int selectedItemIndex) {
						mDialogListener.processUsersSelection("" + selectedItemIndex);
					}
				})
				.setNegativeButton(AppUtility.getStringForId(AppUtility.getResourseIdByName(mAppContext.getPackageName(), SmartConstants.RESOURCE_CLASS_NAME_STRING, "im_cancel")),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								mDialogListener.processUsersSelection("-1");
								mAlertDialog.dismiss();
							}
						});

		return builder;
	}

	/**
	 * Prepare the single choice dialog based on the list elements provided by
	 * the user. Selection mode in Radio
	 * 
	 * @param dialogBuilder
	 * @param mStrStatusMessage
	 *            : Contains the list of elements to be shown on the dialog
	 * 
	 * @return AlertDialog.Builder
	 * 
	 * */
	private AlertDialog.Builder createSingleChoiceListRadioBtnDialog(AlertDialog.Builder dialogBuilder, String mStrStatusMessage) {
		AlertDialog.Builder builder = dialogBuilder;
		String[] listItems = processSelectionListItemInfo(mStrStatusMessage);
		builder.setTitle(AppUtility.getStringForId(AppUtility.getResourseIdByName(mAppContext.getPackageName(), SmartConstants.RESOURCE_CLASS_NAME_STRING, "im_title_select_item")))
				.setSingleChoiceItems(listItems, 0, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int selectedItemIndex) {
						setSelectedIndex(selectedItemIndex);
					}
				})
				.setPositiveButton(AppUtility.getStringForId(AppUtility.getResourseIdByName(mAppContext.getPackageName(), SmartConstants.RESOURCE_CLASS_NAME_STRING, "im_ok")),
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int id) {
								mDialogListener.processUsersSelection("" + UIUtility.this.selectedIndex);
								mAlertDialog.dismiss();
							}
						})
				.setNegativeButton(AppUtility.getStringForId(AppUtility.getResourseIdByName(mAppContext.getPackageName(), SmartConstants.RESOURCE_CLASS_NAME_STRING, "im_cancel")),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								mDialogListener.processUsersSelection("-1");
								mAlertDialog.dismiss();
							}
						});
		return builder;
	}

	/**
	 * Prepare the multiple choice dialog based on the list elements provided by
	 * the user. Selection mode in multi selection
	 * 
	 * @param dialogBuilder
	 * @param mStrStatusMessage
	 *            : Contains the list of elements to be shown on the dialog
	 * 
	 * @return AlertDialog.Builder
	 * 
	 * */
	private AlertDialog.Builder createMultiChoiceCheckboxDialog(AlertDialog.Builder dialogBuilder, String mStrStatusMessage) {
		AlertDialog.Builder builder = dialogBuilder;
		String[] listItems = processSelectionListItemInfo(mStrStatusMessage);
		builder.setTitle(AppUtility.getStringForId(AppUtility.getResourseIdByName(mAppContext.getPackageName(), SmartConstants.RESOURCE_CLASS_NAME_STRING, "im_title_select_items")))
				.setMultiChoiceItems(listItems, null, new DialogInterface.OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int selectedIndex, boolean isChecked) {
						if (isChecked) {
							// If the user checked the item, add it to the
							// selected items
							addToSelectedIndices(selectedIndex);
						} else if (selectedIndices.contains(selectedIndex)) {
							// Else, if the item is already in the array, remove
							// it
							removeFromSelectedIndices(selectedIndex);
						}
					}
				})
				.setPositiveButton(AppUtility.getStringForId(AppUtility.getResourseIdByName(mAppContext.getPackageName(), SmartConstants.RESOURCE_CLASS_NAME_STRING, "im_ok")),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								mDialogListener.processUsersSelection(prepareMultiSelectionListString());
								mAlertDialog.dismiss();
							}
						})
				.setNegativeButton(AppUtility.getStringForId(AppUtility.getResourseIdByName(mAppContext.getPackageName(), SmartConstants.RESOURCE_CLASS_NAME_STRING, "im_cancel")),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								mDialogListener.processUsersSelection("-1");
								mAlertDialog.dismiss();
							}
						});
		return builder;
	}

	private void setSelectedIndex(int index) {
		this.selectedIndex = index;
	}

	/**
	 * Used in case of multiple selection dialog to add the selected indices in
	 * the list
	 * 
	 * @param selected
	 * 
	 * */
	private void addToSelectedIndices(int selected) {
		if (selectedIndices != null) {
			selectedIndices.add(Integer.valueOf(selected));
		} else {
			selectedIndices = new ArrayList<Integer>();
			selectedIndices.add(Integer.valueOf(selected));
		}
	}

	/**
	 * Used in case of multiple selection dialog to remove the selected indices
	 * in the list
	 * 
	 * @param selected
	 * 
	 * */
	private void removeFromSelectedIndices(int selected) {
		if ((selectedIndices != null) && (selectedIndices.size() > 0) && (selectedIndices.contains(selected))) {
			selectedIndices.remove(Integer.valueOf(selected));
		}
	}

	/**
	 * Prepares JSON string that contains the list of elements to be shown on
	 * dialogs
	 * 
	 * @return String
	 * 
	 * */
	private String prepareMultiSelectionListString() {
		String selectedIndicesString = null;
		JSONArray selectedIndicesArray = new JSONArray();
		try {
			if ((selectedIndices != null) && (selectedIndices.size() > 0)) {
				selectedIndicesString = "";
				int totalSelectedIndices = selectedIndices.size();
				for (int index = 0; index < totalSelectedIndices; index++) {
					JSONObject selectedIndexObj = new JSONObject();
					selectedIndexObj.put(CommMessageConstants.MMI_RESPONSE_PROP_USER_SELECTED_INDEX, selectedIndices.get(index));
					selectedIndicesArray.put(selectedIndexObj);
				}
				selectedIndicesString = selectedIndicesArray.toString();
			} else {
				selectedIndicesString = "";
			}
		} catch (JSONException je) {
			selectedIndicesString = "";
		}

		return selectedIndicesString;
	}

	/**
	 * Creating the date picker dialog based on the date provided by the user
	 * 
	 * @param dateOnPicker
	 *            : {@link String} date specified by the user
	 * 
	 * */
	public void createDatePicker(String dateOnPicker) {
		int year, month, day;
		final Calendar cal = Calendar.getInstance();
		year = cal.get(Calendar.YEAR);
		month = cal.get(Calendar.MONTH);
		day = cal.get(Calendar.DAY_OF_MONTH);
		datePickerDialog = new DatePickerDialog(mAppContext, dateSetListener, year, month, day);
		datePickerDialog.setOnDismissListener(mOnDismissListener);
		datePickerDialog.show();
	}

	private DialogInterface.OnDismissListener mOnDismissListener = new DialogInterface.OnDismissListener() {
		public void onDismiss(DialogInterface dialog) {
			handleDatePickerCancel();
		}
	};

	private void handleDatePickerCancel() {
		try {
			String selectedDate = "";
			JSONObject dateResponse = new JSONObject();
			dateResponse.put(SmartConstants.RESPONSE_JSON_PROP_DATA, selectedDate);
			mDialogListener.processUsersSelection(dateResponse.toString());
		} catch (JSONException je) {
			// TODO handle this exception
		}
	}

	/** Listener for the Date picker. */
	private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			if (!isDateSet) {
				isDateSet = true;
				datePickerDialog.setOnDismissListener(null);
				// Date format in "MM-DD-YYYY"
				String selectedDate = (monthOfYear + 1) + "-" + dayOfMonth + "-" + year;
				mDialogListener.processUsersSelection(selectedDate);
			}
		}
	};

	private String[] processSelectionListItemInfo(String listInfo) {
		String[] listElements = null;
		try {
			JSONArray listInfoObj = new JSONArray(listInfo);
			if (listInfoObj != null && listInfoObj.length() > 0) {
				int allListElements = listInfoObj.length();
				listElements = new String[allListElements];
				for (int listElementIndex = 0; listElementIndex < allListElements; listElementIndex++) {
					JSONObject currentListElementObj = (JSONObject) listInfoObj.get(listElementIndex);
					listElements[listElementIndex] = currentListElementObj.getString(CommMessageConstants.MMI_REQUEST_PROP_ITEM);
				}
			}
			/*-// TODO to parse the JSON string received from JavaScript and
			// initialise the list item array
			if ((listInfo != null) && (listInfo.length() > 0)) {
				listElements = listInfo.split(SmartConstants.PICKER_LIST_ELEMENTS_SEPARATOR);
			}*/
		} catch (JSONException je) {
			listElements = null;
		}

		return listElements;
	}

	/**
	 * Dismisses dialog
	 * 
	 */
	public void dissmissDialog() {
		Dialog alertDialog = SessionData.getInstance().getProgressDialog();
		boolean isDialogShown = SessionData.getInstance().isProgressDialogShown();
		if (alertDialog != null && isDialogShown) {
			alertDialog.dismiss();
			SessionData.getInstance().setProgressDialog(null);
			SessionData.getInstance().setProgressDialogShown(false);
		}
	}

	/**
	 * Updates the text on the dialog with the text provided
	 * 
	 * @param message
	 *            : Message which will replace existing message on the dialog
	 * */
	public void updateDialogText(String message) {
		if (mAlertDialog == null) {
			mAlertDialog = SessionData.getInstance().getProgressDialog();
		}
		TextView progressMessage = (TextView) mAlertDialog.findViewById(AppUtility.getResourseIdByName(mAppContext.getPackageName(), "id", "txt_message"));
		progressMessage.setText(message);
		SessionData.getInstance().setProgressDialogShown(true);
	}

	public boolean isDialogShown() {
		// return mAlertDialog.isShowing();
		return SessionData.getInstance().isProgressDialogShown();
	}

	public String getPositiveBtnText() {
		return positiveBtnText;
	}

	public void setPositiveBtnText(String positiveBtnText) {
		this.positiveBtnText = positiveBtnText;
	}

	public String getNegativeBtnText() {
		return negativeBtnText;
	}

	public void setNegativeBtnText(String negativeBtnText) {
		this.negativeBtnText = negativeBtnText;
	}

	public String getBtnText() {
		return btnText;
	}

	public void setBtnText(String btnText) {
		this.btnText = btnText;
	}
}
