package hello.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.ui.LoadMode;

import hello.entities.Customer;
import hello.massiveupload.CustomerExcelService;
import hello.repositories.CustomerRepository;
import hello.ui.editors.CustomerEditor;
import hello.ui.massiveupload.CustomerMassUpload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

@SuppressWarnings("unused")
@Route(value = "", layout = MainLayout.class)
@PageTitle("Customers")
public class MainView extends VerticalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5959967959646653988L;

	private static final int MIN_SEARCH_LENGTH = 3;

	private final CustomerRepository repo;

	private final CustomerEditor editor;
	
	private final CustomerMassUpload customerMassUpload;

	public final Grid<Customer> grid;

	final TextField filter;

	private final Button addNewBtn;
	
	private final Button excelUpload;
			
	public MainView(CustomerRepository repo, CustomerEditor editor, 
			CustomerMassUpload customerMassUpload, CustomerExcelService customerExcelImport, ServletContext context) {
		this.repo = repo;
		this.editor = editor;
		this.customerMassUpload = customerMassUpload;
		this.grid = new Grid<>(Customer.class);
		this.filter = new TextField();
		this.addNewBtn = new Button("New customer", VaadinIcon.PLUS.create());
		this.excelUpload = new Button("Excel upload", VaadinIcon.UPLOAD.create());
	
		// build layout
		HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn, excelUpload);
		actions.setAlignItems(Alignment.CENTER);
		add(actions, customerMassUpload, grid, editor);

		grid.setHeight("300px");
		grid.setColumns("id", "firstName", "lastName", "age");
		grid.getColumnByKey("id").setWidth("150px").setFlexGrow(0);

		filter.setPlaceholder("Filter by last name");

		// Hook logic to components

		// Replace listing with filtered content when user changes filter
		filter.setValueChangeMode(ValueChangeMode.EAGER);
		filter.addValueChangeListener(
			e -> {
				if(e.getValue().length() >= MIN_SEARCH_LENGTH)
					listCustomers(e.getValue());
				else
					listCustomers("");
			}
		);

		// Connect selected Customer to editor or hide if none is selected
		grid.asSingleSelect().addValueChangeListener(e -> {
			editor.editCustomer(e.getValue());
		});

		// Instantiate and edit new Customer the new button is clicked
		addNewBtn.addClickListener(e -> {
			editor.editCustomer(new Customer("", ""));
			Notification.show("New customer " + this.getUI().get().getPage(), 3, Position.MIDDLE);
		});
		
		// Listen changes made by the editor, refresh data from backend
		editor.setChangeHandler(() -> {
			editor.setVisible(false);
			listCustomers(filter.getValue());
		});
		
		excelUpload.addClickListener(e -> {
			customerMassUpload.upload();
		});
		
		customerMassUpload.setChangeHandler(() -> {
			customerMassUpload.setVisible(false);
			listCustomers(filter.getValue());
		});
		
		// Initialize listing
		listCustomers(null);
		this.addClassName("main-view");
				
	}

	// tag::listCustomers[]
	public void listCustomers(String filterText) {
		if (StringUtils.isEmpty(filterText)) {
			grid.setItems(repo.findTop100());
		}
		else {
			grid.setItems(repo.findByLastNameStartsWithIgnoreCase(filterText));
		}
	}
	
	// end::listCustomers[]
}


