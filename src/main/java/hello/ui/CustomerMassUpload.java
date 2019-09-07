package hello.ui;

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
import hello.massiveupload.ThrowingConsumer;
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
	
	private Grid<Customer> grid;
	
	private Button addBtn;
	
	private Button cancelBtn;
	
	@Autowired
	public CustomerMassUpload(CustomerRepository repository, CustomerExcelImport customerExcelImport) throws IOException{
		this.repository = repository;
		this.customerExcelImport = new CustomerExcelImport();
		this.excelCustomers = new LinkedList<Customer>();
		this.memoryBuffer = new MemoryBuffer();
		this.upload = new Upload(memoryBuffer);
		this.grid = new Grid<>(Customer.class);
		this.addBtn = new Button("Add customers", VaadinIcon.ARROW_DOWN.create());
		this.cancelBtn = new Button("Cancel", VaadinIcon.TRASH.create());
		
		HorizontalLayout uploadBar = new HorizontalLayout(upload, grid);
		uploadBar.setWidthFull();
		uploadBar.setAlignItems(Alignment.START);
		
		grid.setHeight("200px");
		grid.setWidthFull();
		grid.setColumns("firstName", "lastName", "age");
		
		HorizontalLayout actions = new HorizontalLayout(cancelBtn, addBtn);
		actions.setAlignItems(Alignment.END);
		
		addBtn.getElement().getThemeList().add("primary");
		cancelBtn.getElement().getThemeList().add("error");
		
		upload.addSucceededListener(
		    ThrowingConsumer.handlingConsumerWrapper(e -> callReadExcel(), 
		    Exception.class
		));
				
		addBtn.addClickListener(e -> addCustomers());
		cancelBtn.addClickListener(e -> cancel());
		
		add(uploadBar, actions);
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
		excelCustomers = customerExcelImport.readCustomersExcel(inputStream);
		grid.setItems(excelCustomers);
	}
	
	private void close() {
		ThrowingConsumer.handlingConsumerWrapper(
			e -> memoryBuffer.getInputStream().close(), 
			IOException.class
		);
		upload.getElement().setPropertyJson("files", Json.createArray());
		excelCustomers.clear();
		grid.setItems(new LinkedList<Customer>());
		changeHandler.onChange();
	}
	
	public void setChangeHandler(ChangeHandler h) {
		changeHandler = h;
	}
	
	public interface ChangeHandler {
		void onChange();
	}
}
