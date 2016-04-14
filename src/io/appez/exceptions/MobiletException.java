package io.appez.exceptions;

/**
 * MobiletException : Generic exception class for handling exceptions occurring
 * due to the framework.
 * 
 * */
public final class MobiletException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private int exceptionType = ExceptionTypes.UNKNOWN_EXCEPTION;
	private String errorMsg = null;

	public MobiletException() {
		super();
	}

	public MobiletException(int exceptionType) {
		super();
		this.exceptionType = exceptionType;
	}

	public MobiletException(String detailMessage) {
		super(detailMessage);
		this.errorMsg = detailMessage;
	}

	public MobiletException(Throwable throwable) {
		super(throwable);
	}

	public MobiletException(Throwable throwable, int exceptionType) {
		super(throwable);
		this.exceptionType = exceptionType;
	}

	public MobiletException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
		this.errorMsg = detailMessage;
	}

	/**
	 * Sets the type of exception for which MobiletException needs to be
	 * generated
	 * 
	 * @param exceptionType
	 *            : Type of exception
	 */
	public void setExceptionType(int exceptionType) {
		this.exceptionType = exceptionType;
	}

	/**
	 * Returns the type of exception for which MobiletException needs to be
	 * generated
	 * 
	 * @return int : Type of exception
	 */
	public int getExceptionType() {
		return this.exceptionType;
	}

	/**
	 * Sets the error message corresponding to the MobiletException generated
	 * 
	 * @param errorMsg
	 *            : Message specifying the MobiletException
	 */
	public void setErrorMessage(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	/**
	 * Returns the error message corresponding to the MobiletException generated
	 * 
	 * @return String : Message specifying the MobiletException
	 */
	public String getErrorMessage() {
		return this.errorMsg;
	}
}
