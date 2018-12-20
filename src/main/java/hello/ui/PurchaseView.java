package hello.ui;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.util.StringUtils;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import hello.entities.Customer;
import hello.entities.Purchase;
import hello.entities.PurchaseDetail;
import hello.repositories.CustomerRepository;
import hello.repositories.PurchaseRepository;

@Route(value = "Purchases", layout = MainLayout.class)
@PageTitle("Purchases")
public class PurchaseView extends VerticalLayout{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5296463586699374020L;

	private final CustomerRepository customerRepo;
		
	public final Grid<Customer> customerGrid;
	
	public final Grid<Purchase> purchaseGrid;
	
	public final Grid<PurchaseDetail> detailsGrid;
		
	final TextField filter;
	
	public PurchaseView(CustomerRepository customerRepo, 
			PurchaseRepository purchaseRepo) {
		this.customerRepo = customerRepo;
		this.customerGrid = new Grid<>(Customer.class);
		this.purchaseGrid = new Grid<>(Purchase.class);
		this.detailsGrid = new Grid<>(PurchaseDetail.class);
		this.filter = new TextField();
		
		HorizontalLayout actions = new HorizontalLayout(filter);
		add(actions, customerGrid, purchaseGrid, detailsGrid);
		
		customerGrid.setHeight("300px");
		customerGrid.setColumns("id", "firstName", "lastName", "age");
		customerGrid.getColumnByKey("id").setWidth("50px").setFlexGrow(0);
		
		purchaseGrid.setHeight("150px");
		purchaseGrid.setColumns("id", "date");
		purchaseGrid.getColumnByKey("id").setWidth("50px").setFlexGrow(0);
		
		detailsGrid.setHeight("150px");
		detailsGrid.setColumns("product", "quantity");
		
		
		filter.setPlaceholder("Filter by last name");
		filter.setValueChangeMode(ValueChangeMode.EAGER);
		filter.addValueChangeListener(e -> listCustomers(e.getValue()));
		
		customerGrid.asSingleSelect().addValueChangeListener(e -> {
			if(e != null && e.getValue() != null) {
				listPurchases(e.getValue().getId());
				listDetails(null);
			}
		});
		
		purchaseGrid.asSingleSelect().addValueChangeListener(e -> {
			if(e != null && e.getValue() != null) {
				listDetails(e.getValue());
			}
		});
		
		listCustomers(null);
	}
	
	public void listCustomers(String filterText) {
		if (StringUtils.isEmpty(filterText))
			customerGrid.setItems(customerRepo.findAll());
		else
			customerGrid.setItems(customerRepo.findByLastNameStartsWithIgnoreCase(filterText));
	}
	
	public void listPurchases(Long customerId) {
		if(customerId != null) {
			Optional<Customer> c = customerRepo.findById(customerId);
			if(c.isPresent())
				purchaseGrid.setItems(c.get().getAllPurchases());
		}
		else
			purchaseGrid.setItems(new ArrayList<Purchase>());
	}
	
	public void listDetails(Purchase p) {
		if(p != null) {
			detailsGrid.setItems(p.getDetails());
		}
		else
			detailsGrid.setItems(new ArrayList<PurchaseDetail>());
	}
}
