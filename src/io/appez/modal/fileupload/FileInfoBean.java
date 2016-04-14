package io.appez.modal.fileupload;

/**
 * FileInfoBean : Model bean that indicates the property of file. This is used
 * in scenarios involving the image upload.
 * */
public class FileInfoBean {
	// Indicates the name of the file
	private String fileName = null;
	// Indicates the absolute path of the file
	private String filePath = null;
	// Indicates the Base64 content of the file
	private String fileData = null;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileData() {
		return fileData;
	}

	public void setFileData(String fileData) {
		this.fileData = fileData;
	}
}
