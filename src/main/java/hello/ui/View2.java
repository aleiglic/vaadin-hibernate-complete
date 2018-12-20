package hello.ui;



import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import hello.entities.Customer;
import hello.repositories.CustomerRepository;

@Route(value = "View2", layout = MainLayout.class)
@PageTitle("View2")
public class View2 extends VerticalLayout{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5253868433399497752L;
	
	private final CustomerRepository repo;
	
	private ComboBox<Customer> comboCustomer;
	
	private Button addCustomer;
	
	private Text text;
	
	private Grid<Customer> grid;
	
	private List<Customer> customersInGrid;
	
	public View2(CustomerRepository repo) {
		
		this.repo = repo;
		
		this.comboCustomer = new ComboBox<Customer>("Customers");
		this.addCustomer = new Button("Add", VaadinIcon.ADD_DOCK.create());
		HorizontalLayout actions = 
				new HorizontalLayout(comboCustomer, addCustomer);
		actions.setAlignItems(Alignment.BASELINE);
		this.grid = new Grid<>(Customer.class);
		this.customersInGrid = new ArrayList<Customer>();
		
		setComboItems();  
		
		grid.setHeight("300px");
		grid.setColumns("id", "firstName", "lastName", "age");
		grid.getColumnByKey("id").setWidth("50px").setFlexGrow(0);

		addCustomer.addClickListener(e -> {
			this.addGrid(comboCustomer.getValue());
		});

		
		comboCustomer.addValueChangeListener(e -> {
			if(e != null  && e.getValue() != null) {
			text.setText(String.format(
				"The selected Customer is %s, %d years old", 
				e.getValue().getFirstName(), 
				e.getValue().getAge().intValue()));
			}
		});
		
		text = new Text("View2");
		add(actions, text, grid);
		
		this.setAlignItems(Alignment.CENTER);
	}

	private void setComboItems() {
		comboCustomer.setItems(repo.findAll());
	}
	
	private void addGrid(Customer c) {
		if(c == null) return;
		
		if(!customersInGrid.contains(c))
			customersInGrid.add(c);
		grid.setItems(customersInGrid);
	}

}
