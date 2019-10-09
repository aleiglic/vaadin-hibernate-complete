package hello.massiveupload;

import java.io.OutputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

public class ErrorExcelService {

	@Autowired
	public ErrorExcelService() {}
	
	public OutputStream exportErrorsExcel(List<RowError> errorsList) {
		
		
		return null;
	}
	
}
