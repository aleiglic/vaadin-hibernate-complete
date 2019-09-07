package hello.massiveupload;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.spring.annotation.SpringComponent;

import hello.entities.Customer;

@SpringComponent
public class CustomerExcelImport{

	private static final int FIRST_SHEET = 0;
	private static final int FIRST_COLUMN = 0;
	private static final int SECOND_COLUMN = 1;
	private static final int THIRD_COLUMN = 2;
	private static final int FIRSTNAME_MAX = 255;

	@Autowired
	public CustomerExcelImport() {}
	
	public List<Customer> readCustomersExcel(InputStream reapExcelDataFileStream) throws Exception {

		List<Customer> tempCustomersList = new ArrayList<Customer>();
		XSSFWorkbook workbook = new XSSFWorkbook(reapExcelDataFileStream);
		XSSFSheet worksheet = workbook.getSheetAt(FIRST_SHEET);
		
		Iterator<Row> it = worksheet.iterator();
		
		if(it.hasNext()) it.next(); //Skip header
		
		while(it.hasNext()) {
			Customer tempCustomer = new Customer();
			Row customerRow = it.next();
			
			String firstName = readFirstName(customerRow);
			String lastName = customerRow.getCell(SECOND_COLUMN).getStringCellValue();
			LocalDate birthDate = customerRow.getCell(THIRD_COLUMN).getDateCellValue()
					.toInstant().atZone(ZoneId.systemDefault())
					.toLocalDate();
			
			tempCustomer.setFirstName(firstName);
			tempCustomer.setLastName(lastName);
			tempCustomer.setBirthDate(birthDate);

			tempCustomersList.add(tempCustomer);
		}
		
		workbook.close();
		return tempCustomersList;
	}

	private String readFirstName(Row customerRow) throws Exception {
		Cell firstNameCell = customerRow.getCell(FIRST_COLUMN);
		if(cellIsEmpty(firstNameCell))
			throw new Exception("Column FirstName must not be empty.");
		if(!firstNameCell.getCellType().equals(CellType.STRING))
			throw new Exception("Column FirstName must be a character string");
		if(firstNameCell.getStringCellValue().length() > FIRSTNAME_MAX)
			throw new Exception("Column FirstName must not have more than " 
				+ FIRSTNAME_MAX + " characters.");
		return customerRow.getCell(FIRST_COLUMN).getStringCellValue();
	}

	private boolean cellIsEmpty(Cell cell) {
		return cell.getCellType().equals(CellType.BLANK) 
			|| cell.getStringCellValue().isEmpty();
	}
}

