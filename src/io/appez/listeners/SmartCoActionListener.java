package io.appez.listeners;

/**
 * SmartCoActionListener : Defines an interface for notifying the result for
 * completion of Co-action. Essentially used by services such as Map service
 * which lie in the co-event category
 * 
 * */
public interface SmartCoActionListener {
	/**
	 * Indicates the successful completion of the co-action along with the data
	 * corresponding to the completion
	 * 
	 * @param actionCompletionData
	 *            : Data accompanying the completion of the co-event
	 * */
	public void onSuccessCoAction(String actionCompletionData);

	/**
	 * Indicates the erroneous completion of the co-event action
	 * 
	 * @param exceptionType
	 *            : Unique code corresponding to the problem in the co-event
	 * 
	 * @param exceptionMessage
	 *            : Message describing the nature of problem with the co-event
	 *            execution
	 * */
	public void onErrorCoAction(int exceptionType, String exceptionMessage);
}
