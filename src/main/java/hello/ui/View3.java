package hello.ui;



import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.logging.log4j2.Log4J2LoggingSystem;

import com.mysql.jdbc.log.Log;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import hello.editors.PurchaseDetailEditor;
import hello.entities.Customer;
import hello.entities.Product;
import hello.entities.Purchase;
import hello.entities.PurchaseDetail;
import hello.repositories.CustomerRepository;
import hello.repositories.ProductRepository;
import hello.repositories.PurchaseDetailRepository;
import hello.repositories.PurchaseRepository;

@Route(value = "make_purchase", layout = MainLayout.class)
@PageTitle("View3")
public class View3 extends VerticalLayout{

	/**
	 * 
	 */
	private static final long serialVersionUID = -61220769330799335L;

	/**
	 * 
	 */
	
	private final CustomerRepository customerRepo;
	
	private final ProductRepository productRepo;
	
	private final PurchaseRepository purchaseRepo;
	
	private ComboBox<Customer> comboCustomer;
	
	private PurchaseDetailEditor detailEditor;
	
	private Button addDetail;
		
	private Grid<PurchaseDetail> grid;
	
	private List<PurchaseDetail> detailsInGrid;
	
	private Purchase purchase;
		
	private Button buttonSave;
	
	private Binder<Purchase> binder;
		
	public View3(CustomerRepository customerRepo, 
			ProductRepository productRepo, 
			PurchaseRepository purchaseRepo, 
			PurchaseDetailRepository detailRepo) {
		
		this.customerRepo = customerRepo;
		this.productRepo = productRepo;
		this.purchaseRepo = purchaseRepo;
		this.purchase = new Purchase();
		this.binder = new Binder<>(Purchase.class);
		this.detailsInGrid = new ArrayList<PurchaseDetail>();
		this.detailEditor = new PurchaseDetailEditor(
				productRepo, detailRepo, detailsInGrid);
		
		this.comboCustomer = new ComboBox<Customer>("Customer");
		this.addDetail = new Button("Add", VaadinIcon.ADD_DOCK.create());
		this.grid = new Grid<>(PurchaseDetail.class);
		this.buttonSave = new Button("Save", VaadinIcon.SAFE_LOCK.create());
						
		comboCustomer.setRequired(true);
		
		setComboCustomerItems(); 
		
		grid.setHeight("300px");
		grid.setColumns("product", "quantity");
		//grid.getColumnByKey("id").setWidth("50px").setFlexGrow(0);		
		
		addDetail.addClickListener(e -> {
			detailEditor.editDetail(new PurchaseDetail());
		});

		
		comboCustomer.addValueChangeListener(e -> {
			
			}
		);
		
		grid.asSingleSelect().addValueChangeListener(e -> {
			detailEditor.editDetail(e.getValue());
		});
		
		detailEditor.setChangeHandler(() -> {
			detailEditor.setVisible(false);
			listDetails();
		});
		
		this.buttonSave.addClickListener(e -> {
			if(saveConditionsAreTrue()) 
				savePurchase();
		});
		
		HorizontalLayout customerActions = new HorizontalLayout(comboCustomer, addDetail);
		customerActions.setAlignItems(Alignment.BASELINE);
		add(customerActions, detailEditor, grid, buttonSave);
		
		this.setAlignItems(Alignment.START);
	}

	

	@SuppressWarnings("deprecation")
	private void savePurchase() {
		if(purchase != null && !comboCustomer.isEmpty() && !detailsInGrid.isEmpty()) {
			try {
				Customer c = comboCustomer.getValue();
				purchase.getDetails().addAll(detailsInGrid);
				c.addPurchase(purchase);
				this.customerRepo.save(c);
				
				Notification.show("Congratulations! ", 3000, Position.MIDDLE)
					.add(new Text("Your purchase has been saved"));
				
				clearFields();	
			}
			catch(Exception e){
				Notification.show("There was an exception, consult the system administrator: /n" + e.getStackTrace(), 10000, Position.MIDDLE);
			}
		}
		else {
			Notification.show("There is still some missing fields in the form, please fill them", 3000, Position.MIDDLE);
		}
	}

	private void clearFields() {
		this.comboCustomer.clear();
		this.detailsInGrid.clear();
		this.grid.setItems(detailsInGrid);
	}



	private boolean saveConditionsAreTrue() {
		return 
			!comboCustomer.isInvalid() && 
			detailsInGrid.size() >= 1
		;
	}

	private void setComboCustomerItems() {
		comboCustomer.setItems(customerRepo.findAll());
	}
	
	public void listDetails() {
		grid.setItems(detailsInGrid);
	}
}
