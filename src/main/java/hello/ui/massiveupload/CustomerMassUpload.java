package hello.ui.massiveupload;

import org.vaadin.firitin.components.DynamicFileDownloader;
import org.vaadin.olli.FileDownloadWrapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;


import elemental.json.Json;
import hello.entities.Customer;
import hello.massiveupload.CustomerExcelService;
import hello.massiveupload.ErrorExcelService;
import hello.massiveupload.FileDownloadWrapperHelper;
import hello.massiveupload.RowError;
import hello.massiveupload.ThrowingException;
import hello.massiveupload.excelrow.EntityExcelRow;
import hello.repositories.CustomerRepository;

@SpringComponent
@UIScope
public class CustomerMassUpload extends VerticalLayout implements KeyNotifier{

	private static final String SAMPLE_FILE_NAME = "customers.xslx";

	private static final String ERRORS_FILE_NAME = "errors_list.xslx";

	private static final String SAMPLE_FILE_PATH = "samples/customers.xlsx";

	private static final String SAMPLE_FILE_ACCESS_ERROR = "There was an error accessing the sample file, please try again later.";

	/**
	 * 
	 */
	private static final long serialVersionUID = 2176424911150128481L;
	
	List<Customer> customers = new LinkedList<Customer>();
	
	private ChangeHandler changeHandler;
	
	private final CustomerRepository repository;
	
	private final Upload upload;
	
	private final MemoryBuffer memoryBuffer;
	private final CustomerExcelService customerExcelImport;
	private final ErrorExcelService errorExcelService;
	private List<Customer> excelCustomers;
	private List<EntityExcelRow> excelRows;
	private Grid<Customer> grid;
	private Grid<RowError> rowErrorsGrid;
	private FileDownloadWrapper sampleFileWrapper;
	private FileDownloadWrapper errorFileWrapper;
	private Button addBtn;
	private Button cancelBtn;
	private Button showErrorBtn;
	private boolean errorsShowing;
	private Integer errorCount;
	private InputStream sampleInputStream;
	private InputStream errorInputStream;

	private VerticalLayout uploadVert;
	private HorizontalLayout uploadBar;
	private HorizontalLayout actions;
	private HorizontalLayout downloadActions;

	VerticalLayout errorsVert;
	
	@Autowired
	public CustomerMassUpload(CustomerRepository repository, CustomerExcelService customerExcelImport, 
		ErrorExcelService errorExcelService) throws IOException{
		
		openSampleInputStream();

		this.repository = repository;
		//this.customerExcelImport = new CustomerExcelService();
		this.customerExcelImport = customerExcelImport;
		this.errorExcelService = errorExcelService;
		this.excelCustomers = new LinkedList<Customer>();
		this.excelRows = new LinkedList<EntityExcelRow>();
		this.memoryBuffer = new MemoryBuffer();
		this.upload = new Upload(memoryBuffer);
		this.grid = new Grid<>(Customer.class);
		this.rowErrorsGrid = new Grid<>(RowError.class);
		
		this.errorFileWrapper = null;
		this.addBtn = new Button("Add customers", VaadinIcon.ARROW_DOWN.create());
		this.cancelBtn = new Button("Cancel", VaadinIcon.TRASH.create());
		this.showErrorBtn = new Button("Show errors", VaadinIcon.EYE.create());
		this.errorsShowing = false;
		this.sampleFileWrapper = getWrapper(sampleInputStream, "Download sample", SAMPLE_FILE_NAME);


		//this.inputStream = sampleFileStream();

		
		grid.setHeight("250px");
		grid.setWidth("200px");
		grid.setColumns("firstName", "lastName", "age");
		grid.getColumnByKey("age").setWidth("150px").setFlexGrow(0);
		grid.setWidth("100%");

		
		//uploadVert = new VerticalLayout();
		uploadVert = new VerticalLayout(upload);
		
		upload.setHeight("120px");
		uploadVert.setAlignItems(Alignment.START);
		uploadVert.setWidth("250px");
		//uploadVert.setSpacing(false);

		DynamicFileDownloader downloadButton = getDownloader("Download sample", SAMPLE_FILE_NAME, sampleInputStream);
        uploadVert.add(downloadButton);
		
		//errorsVert = new VerticalLayout(rowErrorsGrid);
		//errorsVert.setWidth("100%");

		
		uploadBar = new HorizontalLayout(uploadVert, grid);
		uploadBar.setWidth("100%");
		//uploadBar.setAlignItems(Alignment.START);
		
		rowErrorsGrid.setHeight("200px");
		rowErrorsGrid.setWidth("100%");
		rowErrorsGrid.setColumns("rowNumber", "errorMessage");
		rowErrorsGrid.getColumnByKey("rowNumber").setWidth("150px").setFlexGrow(0);
		add();

		downloadActions = new HorizontalLayout();
		downloadActions.setAlignItems(Alignment.BASELINE);

		actions = new HorizontalLayout(cancelBtn, showErrorBtn, addBtn);
		actions.setAlignItems(Alignment.END);
		showErrorBtn.setEnabled(false);

		//actions.add(this.sampleFileWrapper);
		
		addBtn.getElement().getThemeList().add("primary");
		cancelBtn.getElement().getThemeList().add("error");
		
		upload.addSucceededListener(
		    ThrowingException.handlingExceptionWrapper(e -> callReadExcel(), 
		    Exception.class
		));
				
		addBtn.addClickListener(e -> addCustomers());
		cancelBtn.addClickListener(e -> cancel());
		showErrorBtn.addClickListener(e -> showErrors());
		
		add(uploadBar, rowErrorsGrid, actions);
		//add(uploadBar, rowErrorsGrid, uploadButtons, actions);
		rowErrorsGrid.setVisible(errorsShowing);
		this.setAlignItems(Alignment.END);
		
		

		setVisible(false);
		
	}

	private void addCustomers() {
		this.repository.saveAll(excelCustomers);
		closeMe();
	}
	
	private void cancel() {
		closeMe();
	}

	public void upload() {
		setVisible(true);
	}
	
	private void callReadExcel() throws Exception {
		Boolean isError = false;

		System.out.println("Calling callReadExcel");
		InputStream inputStream = memoryBuffer.getInputStream();
		excelRows = customerExcelImport.readCustomersExcel(inputStream);
		List<RowError> rowErrors = new LinkedList<RowError>(); 
		
		errorCount = 0;
		for(EntityExcelRow customerRow: excelRows) {
			if(customerRow.isError()) {
				rowErrors.add(customerRow.getRowError());
				errorCount++;
				isError = true;
			} 
			else 
				excelCustomers.add((Customer) customerRow.getEntity());
		}
		
		grid.setItems(excelCustomers);
		//grid.setItems(new LinkedList<Customer>());
		if(isError){
			rowErrorsGrid.setItems(rowErrors);
			showErrors();
			openErrorInputStream(rowErrors);
			addErrorDownloadWrapper();
			addErrorDownloader();
			showErrorBtn.setEnabled(isError);
			showErrorBtn.setText("Show errors (" + errorCount + ")");
			//showErrorBtn.getElement().getThemeList().add("primary");
			System.out.println("Showing errors");
		}
		
	}
	
	private void addErrorDownloader() {
		DynamicFileDownloader downloader = getDownloader("Download errors", ERRORS_FILE_NAME, errorInputStream);
		uploadVert.add(downloader);
	}

	private void addErrorDownloadWrapper() {
		FileDownloadWrapper wrapper = null;
		try {
			wrapper = FileDownloadWrapperHelper.getWrapper(errorInputStream, "Download errors", ERRORS_FILE_NAME);
		} catch (IOException e) {
			new Notification("There was an error with the error list file, please try again later.").open();
			e.printStackTrace();
		}
		//this.downloadActions.add(wrapper);
	}

	private void closeMe() {
		ThrowingException.handlingExceptionWrapper(
			e -> memoryBuffer.getInputStream().close(), 
			IOException.class
		);
		upload.getElement().setPropertyJson("files", Json.createArray());
		excelCustomers.clear();
		grid.setItems(new LinkedList<Customer>());
		rowErrorsGrid.setItems(new LinkedList<RowError>());
		changeHandler.onChange();
		showErrorBtn.setEnabled(false);
		showErrorBtn.setText("Show errors");
	}
	
	private void showErrors() {
		if(errorsShowing) {
			errorsShowing = false;
			showErrorBtn.getElement().getThemeList().add("primary");
		}
		else {
			errorsShowing = true;
			showErrorBtn.getElement().getThemeList().remove("primary");
		}
		rowErrorsGrid.setVisible(errorsShowing);
		//errorFileWrapper.setVisible(errorsShowing);
		//errorsVert.setVisible(errorsShowing);
	}
	
	public void setChangeHandler(ChangeHandler h) {
		changeHandler = h;
	}
	
	public interface ChangeHandler {
		void onChange();
	}
	
	private FileDownloadWrapper getWrapper(InputStream is, String label, String path){
		FileDownloadWrapper wrapper = null;
		try {
			wrapper = FileDownloadWrapperHelper.getWrapper(is, "Download sample", SAMPLE_FILE_NAME);
		} catch (IOException e) {
			new Notification("There was an error downloading the sample file, please try again later.").open();
			e.printStackTrace();
		}
		return wrapper;
	}
	
	private void openSampleInputStream() {
		if (sampleInputStream == null)
			sampleInputStream = sampleFileStream();
	}

	private InputStream sampleFileStream() {
		File initialFile = null;
		InputStream is = null;		
		try {
			initialFile = new ClassPathResource(SAMPLE_FILE_PATH).getFile();
			is = new FileInputStream(initialFile);
		} catch (IOException e) {
			new Notification(SAMPLE_FILE_ACCESS_ERROR).open();
			e.printStackTrace();
		}	
		return is;
	}

	private void openErrorInputStream(List<RowError> errorsList){
		if(errorInputStream == null)
			try {
				errorInputStream = errorExcelService.exportErrorsExcel(errorsList);
			} catch (IOException e) {
				new Notification(e.getMessage()).open();
				e.printStackTrace();
			}
	}

	private void closeErrorInputStream(){
		if(errorInputStream != null){
			try {
				errorInputStream.close();
			} catch (IOException e) {
				new Notification(e.getMessage()).open();
				e.printStackTrace();
			}
		}
	}

	private DynamicFileDownloader getDownloader(String caption, String fileName, InputStream is){
		DynamicFileDownloader downloader = new DynamicFileDownloader(caption, fileName,
        outputStream -> {
            try {
                outputStream.write(IOUtils.toByteArray(is));
            } catch (IOException ex) {
				new Notification("There was an error downloading the file, please try again later.").open();
				System.out.println(ex.getStackTrace());
			}
		});
		return downloader;
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		openSampleInputStream();
		System.out.println("Stream opened");
	}
	
	@Override
	protected void onDetach(DetachEvent detachEvent) {
		try {
			if(sampleInputStream != null){
				sampleInputStream.close();
				System.out.println("Stream closed");
			}
		} catch (IOException e) {
			new Notification("Error closing sample file stream, check logs.").open();
			e.printStackTrace();
		}
	}
}
