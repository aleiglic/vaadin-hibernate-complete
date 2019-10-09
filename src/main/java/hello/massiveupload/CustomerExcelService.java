package hello.massiveupload;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.spring.annotation.SpringComponent;

import hello.entities.Customer;
import hello.massiveupload.excelrow.CustomerExcelRow;
import hello.massiveupload.excelrow.EntityExcelRow;

@SpringComponent
public class CustomerExcelService{

	private static final int FIRST_SHEET = 0;
	private static final int FIRST_COLUMN = 0;
	private static final int SECOND_COLUMN = 1;
	private static final int THIRD_COLUMN = 2;
	private static final int FIRSTNAME_MAX = 255;
	private static final long MINIMUM_AGE = 13;

	@Autowired
	public CustomerExcelService() {}
	
	public List<EntityExcelRow> readCustomersExcel(InputStream reapExcelDataFileStream) throws Exception {

		List<EntityExcelRow> tempExcelRowList = new LinkedList<EntityExcelRow>();
		XSSFWorkbook workbook = new XSSFWorkbook(reapExcelDataFileStream);
		XSSFSheet worksheet = workbook.getSheetAt(FIRST_SHEET);
		
		Iterator<Row> it = worksheet.iterator();
		
		if(it.hasNext()) it.next(); //Skip header
		
		while(it.hasNext()) {
			Customer tempCustomer = new Customer();
			
			Boolean isError = false;
			Row customerRow = it.next();
			EntityExcelRow entityRow = new CustomerExcelRow(tempCustomer);
			entityRow.setRowError(new RowError(customerRow.getRowNum(), ""));
			entityRow.setError(isError);

			
			String firstName = readFirstName(customerRow.getCell(FIRST_COLUMN), entityRow);
			String lastName = readLastName(customerRow.getCell(SECOND_COLUMN), entityRow);
			LocalDate birthDate = readBirthDate(customerRow.getCell(THIRD_COLUMN), entityRow);
			
			tempCustomer.setFirstName(firstName);
			tempCustomer.setLastName(lastName);
			tempCustomer.setBirthDate(birthDate);
			
			tempExcelRowList.add(entityRow);
		}
		
		workbook.close();
		return tempExcelRowList;
	}

	

	private String readFirstName(Cell firstNameCell, EntityExcelRow entityRow){
		Boolean isError = entityRow.isError();
		String rowMessage = entityRow.getRowError().getErrorMessage();
		
		if(isError = (firstNameCell == null || cellIsEmpty(firstNameCell)))
			rowMessage = rowMessage.concat("Column FirstName must not be empty.\n");
		else if(isError = !firstNameCell.getCellType().equals(CellType.STRING))
			rowMessage = rowMessage.concat("Column FirstName must be a character string\n");
		else if(isError = firstNameCell.getStringCellValue().length() > FIRSTNAME_MAX)
			rowMessage = rowMessage.concat("Column FirstName must not have more than " 
				+ FIRSTNAME_MAX + " characters.\n");
		entityRow.setError(isError);
		entityRow.getRowError().setErrorMessage(rowMessage);
		return isError ? null : firstNameCell.getStringCellValue();
	}
	
	private String readLastName(Cell lastNameCell, EntityExcelRow entityRow) {
		Boolean isError = entityRow.isError();
		String rowMessage = entityRow.getRowError().getErrorMessage();
		
		if(isError = (lastNameCell == null || cellIsEmpty(lastNameCell)))
			rowMessage = rowMessage.concat("Column LastName must not be empty.\n");
		else if(isError = !lastNameCell.getCellType().equals(CellType.STRING))
			rowMessage = rowMessage.concat("Column LastName must be a character string\n");
		else if(isError = lastNameCell.getStringCellValue().length() > FIRSTNAME_MAX)
			rowMessage = rowMessage.concat("Column LastName must not have more than " 
				+ FIRSTNAME_MAX + " characters.\n");
		entityRow.setError(isError);
		entityRow.getRowError().setErrorMessage(rowMessage);
		return isError ? null : lastNameCell.getStringCellValue();
	}
	
	private LocalDate readBirthDate(Cell birthDateCell, EntityExcelRow entityRow) {
		Boolean isError = entityRow.isError();
		String rowMessage = entityRow.getRowError().getErrorMessage();
		LocalDate birthDate = LocalDate.now(ZoneId.systemDefault());
		
		if(isError = (birthDateCell == null || cellIsEmpty(birthDateCell)))
			rowMessage = rowMessage.concat("Column BirthDate must not be empty.\n");
		else try {
			birthDate = birthDateCell.getDateCellValue()
					.toInstant().atZone(ZoneId.systemDefault())
					.toLocalDate();
		} catch (IllegalStateException e) {
			isError = true;
			rowMessage = rowMessage.concat("Column BirthDate must be a valid date.\n");
		}
		if(isError = birthDate.isAfter(LocalDate.now().minus(MINIMUM_AGE, ChronoUnit.YEARS)))
			rowMessage = rowMessage.concat("Customer must be at least "+ MINIMUM_AGE +".\n");
		
		entityRow.setError(isError);
		entityRow.getRowError().setErrorMessage(rowMessage);
		return birthDate;
	}
	
	private boolean cellIsEmpty(Cell cell) {
		CellType type = cell.getCellType();
		return 
			   type.equals(CellType.BLANK) 
			|| type.equals(CellType.STRING) ? cell.getStringCellValue().equals("") : false 	
			|| type.equals(CellType.NUMERIC) ? cell.getNumericCellValue() == 0 : false
			;
	}
}

