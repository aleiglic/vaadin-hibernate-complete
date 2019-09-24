package hello.ui.massiveupload;

import java.io.IOException;
import java.io.InputStream;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import elemental.json.Json;
import hello.entities.Customer;
import hello.massiveupload.CustomerExcelImport;
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
		
	private final CustomerExcelImport customerExcelImport;
	
	private List<Customer> excelCustomers;
	
	private List<EntityExcelRow> excelRows;
	
	private Grid<Customer> grid;
	
	private Grid<RowError> rowErrorsGrid;
		
	private Button addBtn;
	
	private Button cancelBtn;
	
	private Button showErrorBtn;
	
	private boolean errorsShowing;
	
	private Integer errorCount;
	
	@Autowired
	public CustomerMassUpload(CustomerRepository repository, CustomerExcelImport customerExcelImport) throws IOException{
		this.repository = repository;
		this.customerExcelImport = new CustomerExcelImport();
		this.excelCustomers = new LinkedList<Customer>();
		this.excelRows = new LinkedList<EntityExcelRow>();
		this.memoryBuffer = new MemoryBuffer();
		this.upload = new Upload(memoryBuffer);
		this.grid = new Grid<>(Customer.class);
		this.rowErrorsGrid = new Grid<>(RowError.class);
		this.addBtn = new Button("Add customers", VaadinIcon.ARROW_DOWN.create());
		this.cancelBtn = new Button("Cancel", VaadinIcon.TRASH.create());
		this.showErrorBtn = new Button("Show errors", VaadinIcon.EYE.create());
		this.errorsShowing = false;
		
		
		HorizontalLayout uploadBar = new HorizontalLayout(upload, grid);
		uploadBar.setWidthFull();
		uploadBar.setAlignItems(Alignment.START);
		
		upload.setHeight("168px");
		
		grid.setHeight("200px");
		grid.setWidthFull();
		grid.setColumns("firstName", "lastName", "age");
		
		rowErrorsGrid.setHeight("200px");
		rowErrorsGrid.setWidthFull();
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
		
		add(uploadBar, rowErrorsGrid, actions);
		rowErrorsGrid.setVisible(errorsShowing);
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
		changeHandler.onChange();
		showErrorBtn.setEnabled(false);
		showErrorBtn.setText("Show errors");
	}
	
	private void showErrors() {
		if(errorsShowing) errorsShowing = false;
		else errorsShowing = true;
		rowErrorsGrid.setVisible(errorsShowing);
	}
	
	public void setChangeHandler(ChangeHandler h) {
		changeHandler = h;
	}
	
	public interface ChangeHandler {
		void onChange();
	}
}
