package hello.massiveupload;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.vaadin.flow.spring.annotation.SpringComponent;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;

@SpringComponent
public class ErrorExcelService {

	@Autowired
	public ErrorExcelService() {
	}

	public InputStream exportErrorsExcel(List<RowError> errorsList) throws IOException {
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("errors");

		Font headerFont = createHeaderFont(workbook);
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        // Create a Row
		Row headerRow = sheet.createRow(0);
		
		Cell rowNumberCell = headerRow.createCell(0);
        rowNumberCell.setCellValue("Row number");
		//rowNumberCell.setCellStyle(headerCellStyle);
		Cell cell = headerRow.createCell(1);
        cell.setCellValue("Error message");
		//cell.setCellStyle(headerCellStyle);
		
		int rowNum = 1;
		for(RowError rowError : errorsList){
			Row row = sheet.createRow(rowNum++);

			row.createCell(0).setCellValue(rowError.getRowNumber());
			row.createCell(1).setCellValue(rowError.getErrorMessage());
			row.setHeight((short)-1);
		}

		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);

		InputStream is = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			workbook.write(bos);
			byte[] barray = bos.toByteArray();
			is = new ByteArrayInputStream(barray);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return is;
	}

	private Font createHeaderFont(XSSFWorkbook workbook) {
		// Create a Font for styling header cells
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.RED.getIndex());
		return headerFont;
	}


	
}
