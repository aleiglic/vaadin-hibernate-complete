package hello.massiveupload.excelrow;

import hello.entities.BusinessEntity;
import hello.entities.Customer;
import hello.massiveupload.RowError;

public class CustomerExcelRow extends EntityExcelRow{

	public CustomerExcelRow(BusinessEntity entity, RowError rowError, Boolean isError) {
		super(entity, rowError, isError);
		this.customer = (Customer) entity;
	}
	
	public CustomerExcelRow(BusinessEntity entity) {
		super(entity);
		this.customer = (Customer) entity;
	}

	private Customer customer;
	
	@Override
	public BusinessEntity getEntity() {
		return this.customer;
	}

	@Override
	public void setEntity(BusinessEntity entity) {
		this.customer = (Customer) entity;
	}
	
	
}
