package hello.massiveupload.excelrow;

import hello.entities.BusinessEntity;
import hello.massiveupload.RowError;

public abstract class EntityExcelRow {
	protected RowError rowError;
	protected Boolean error;
	
	public EntityExcelRow(BusinessEntity entity, RowError rowError, Boolean isError) {
		this.error = isError;
		this.rowError = rowError;
	}
	
	public EntityExcelRow(BusinessEntity entity) {
		this.error = false;
		this.rowError = new RowError();
	}
		
	public RowError getRowError() {
		return rowError;
	}
	public void setRowError(RowError rowError) {
		this.rowError = rowError;
	}
	public Boolean isError() {
		return error;
	}
	public void setError(Boolean error) {
		this.error = error;
	}
	public abstract BusinessEntity getEntity();
	
	public abstract void setEntity(BusinessEntity entity);
	
	
}
