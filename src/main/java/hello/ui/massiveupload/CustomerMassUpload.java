package hello.ui.massiveupload;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.vaadin.olli.FileDownloadWrapper;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import elemental.json.Json;
import hello.entities.Customer;
import hello.massiveupload.CustomerExcelService;
import hello.massiveupload.RowError;
import hello.massiveupload.ThrowingException;
import hello.massiveupload.excelrow.EntityExcelRow;
import hello.repositories.CustomerRepository;

@SpringComponent
@UIScope
public class CustomerMassUpload extends VerticalLayout implements KeyNotifier{

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
	private List<Customer> excelCustomers;
	private List<EntityExcelRow> excelRows;
	private Grid<Customer> grid;
	private Grid<RowError> rowErrorsGrid;
	private Button downloadErrorsBtn;
	private FileDownloadWrapper wrapper;
	private Button addBtn;
	private Button cancelBtn;
	private Button showErrorBtn;
	private boolean errorsShowing;
	private Integer errorCount;
	private InputStream inputStream;
	
	@Autowired
	public CustomerMassUpload(CustomerRepository repository, CustomerExcelService customerExcelImport) throws IOException{
		this.repository = repository;
		this.customerExcelImport = new CustomerExcelService();
		this.excelCustomers = new LinkedList<Customer>();
		this.excelRows = new LinkedList<EntityExcelRow>();
		this.memoryBuffer = new MemoryBuffer();
		this.upload = new Upload(memoryBuffer);
		this.grid = new Grid<>(Customer.class);
		this.rowErrorsGrid = new Grid<>(RowError.class);
		this.downloadErrorsBtn = new Button("Download errors");
		this.wrapper = getDownloadWrapper();
		this.addBtn = new Button("Add customers", VaadinIcon.ARROW_DOWN.create());
		this.cancelBtn = new Button("Cancel", VaadinIcon.TRASH.create());
		this.showErrorBtn = new Button("Show errors", VaadinIcon.EYE.create());
		this.errorsShowing = false;
		//this.inputStream = sampleFileStream();
		
		grid.setHeight("250px");
		grid.setWidth("100%");
		grid.setColumns("firstName", "lastName", "age");
		
		VerticalLayout uploadVert = new VerticalLayout(upload, wrapper);
		uploadVert.setAlignItems(Alignment.START);
		uploadVert.setWidth("300px");
		//uploadVert.setSpacing(false);
		
		VerticalLayout errorsVert = new VerticalLayout(rowErrorsGrid, downloadErrorsBtn);
		errorsVert.setWidth("100%");
		
		HorizontalLayout uploadBar = new HorizontalLayout(uploadVert, errorsVert);
		uploadBar.setWidth("100%");
		uploadBar.setAlignItems(Alignment.END);
		
		upload.setHeight("120px");

		
		rowErrorsGrid.setHeight("200px");
		rowErrorsGrid.setWidth("100%");
		rowErrorsGrid.setColumns("rowNumber", "errorMessage");
		rowErrorsGrid.getColumnByKey("rowNumber").setWidth("125px").setFlexGrow(0);
		
		HorizontalLayout actions = new HorizontalLayout(cancelBtn, showErrorBtn, addBtn);
		actions.setAlignItems(Alignment.END);
		showErrorBtn.setEnabled(false);
		
		addBtn.getElement().getThemeList().add("primary");
		cancelBtn.getElement().getThemeList().add("error");
		
		upload.addSucceededListener(
		    ThrowingException.handlingExceptionWrapper(e -> callReadExcel(), 
		    Exception.class
		));
				
		addBtn.addClickListener(e -> addCustomers());
		cancelBtn.addClickListener(e -> cancel());
		showErrorBtn.addClickListener(e -> showErrors());
		
		add(uploadBar, grid, actions);
		rowErrorsGrid.setVisible(errorsShowing);
		downloadErrorsBtn.setVisible(errorsShowing);
		this.setAlignItems(Alignment.END);
		
		setVisible(false);
		
		
	}

	private void addCustomers() {
		this.repository.saveAll(excelCustomers);
		close();
	}
	
	private void cancel() {
		close();
	}

	public void upload() {
		setVisible(true);
	}
	
	private void callReadExcel() throws Exception {
		System.out.println("Calling callReadExcel");
		InputStream inputStream = memoryBuffer.getInputStream();
		excelRows = customerExcelImport.readCustomersExcel(inputStream);
		List<RowError> rowErrors = new LinkedList<RowError>(); 
		@SuppressWarnings("unused")
		Boolean isError = false;
		
		errorCount = 0;
		for(EntityExcelRow customerRow: excelRows) {
			if(isError = customerRow.isError()) {
				rowErrors.add(customerRow.getRowError());
				errorCount++;
			} 
			else excelCustomers.add((Customer) customerRow.getEntity());
		}
		rowErrorsGrid.setItems(rowErrors);
		grid.setItems(excelCustomers);
		showErrorBtn.setEnabled(true);
		showErrorBtn.setText("Show errors (" + errorCount + ")");
	}
	
	private void close() {
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
		downloadErrorsBtn.setVisible(errorsShowing);
		
	}
	
	public void setChangeHandler(ChangeHandler h) {
		changeHandler = h;
	}
	
	public interface ChangeHandler {
		void onChange();
	}
	
	private FileDownloadWrapper getDownloadWrapper(){
		FileDownloadWrapper wrapper = null;
		//IMPLEMENTAR LA INTERFACE FUNCIONAL ????
		
		
		try {
			File initialFile = new ClassPathResource("samples/customers.xlsx").getFile();
			wrapper = getWrapper(initialFile);
		} catch (IOException e) {
			new Notification("There was an error downloading the sample file, please try again later.").open();
			e.printStackTrace();
		}
		return wrapper;
	}
	
	private FileDownloadWrapper getWrapper(File initialFile) throws IOException {
		FileDownloadWrapper buttonWrapper = null;
		if(initialFile != null) {
			Button button = new Button("Download sample");
			buttonWrapper = new FileDownloadWrapper(new StreamResource("customers.xslx", () -> inputStream));
			buttonWrapper.wrapComponent(button);
		}
		return buttonWrapper;
	}
	
	private InputStream sampleFileStream() {
		File initialFile = null;
		InputStream is = null;		
		
		try {
			initialFile = new ClassPathResource("samples/customers.xlsx").getFile();
			is = new FileInputStream(initialFile);
		} catch (IOException e) {
			new Notification("There was an error downloading the sample file, please try again later.").open();
			e.printStackTrace();
		}	
			
		return is;
	}
	
	@Override
	protected void onAttach(AttachEvent attachEvent) {
		inputStream = sampleFileStream();
		System.out.println("Stream opened");
	}
	
	@Override
	protected void onDetach(DetachEvent detachEvent) {
		try {
			inputStream.close();
			System.out.println("Stream closed");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
