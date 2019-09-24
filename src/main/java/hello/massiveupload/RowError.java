package hello.massiveupload;

public class RowError {

	private Integer rowNumber;
	private String errorMessage;
	
	public RowError() {
		rowNumber = 0;
		errorMessage = "";
	}
	
	public RowError(Integer rowNumber, String errorMessage) {
		super();
		this.rowNumber = rowNumber;
		this.errorMessage = errorMessage;
	}

	public Integer getRowNumber() {
		return rowNumber;
	}
	public void setRowNumber(Integer rowNumber) {
		this.rowNumber = rowNumber;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	
}
