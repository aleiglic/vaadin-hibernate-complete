package hello.editors;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.*;

import hello.editors.CustomerEditor.ChangeHandler;
import hello.entities.Customer;
import hello.entities.Product;
import hello.entities.Purchase;
import hello.entities.PurchaseDetail;
import hello.repositories.CustomerRepository;
import hello.repositories.ProductRepository;
import hello.repositories.PurchaseRepository;

@SpringComponent
@UIScope
public class PurchaseEditor  extends VerticalLayout implements KeyNotifier{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final PurchaseRepository purchaseRepo;
	
	private final CustomerRepository customerRepo;
	
	private final ProductRepository productRepo;
	
	private Purchase purchase;
	
	ComboBox<Customer> comboCustomer;
	
	ComboBox<Product> comboProduct;
	
	Text textDetailQuantity;
	
	Grid<PurchaseDetail> detailsGrid;
	
	Button addDetail = new Button("Add detail");
	
	Button save = new Button("Save", VaadinIcon.CHECK.create());
	Button cancel = new Button("Cancel");
	Button delete = new Button("Delete", VaadinIcon.TRASH.create());
	HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);
	
	Binder<Purchase> binder = new Binder<>(Purchase.class);
	private ChangeHandler changeHandler;
	
	@Autowired
	public PurchaseEditor(PurchaseRepository purchaseRepo, 
			CustomerRepository customerRepo, ProductRepository productRepo) {
		
		this.purchaseRepo = purchaseRepo;
		this.customerRepo = customerRepo;
		this.productRepo = productRepo;
		
		this.comboCustomer = new ComboBox<Customer>("Customer");
		
		this.comboProduct = new ComboBox<Product>("Product");
		
		this.textDetailQuantity = new Text("Quantity");
		
		this.detailsGrid = new Grid<PurchaseDetail>();
		detailsGrid.setHeight("100px");
		detailsGrid.setColumns("id", "product", "quantity");
		detailsGrid.getColumnByKey("id").setWidth("50px").setFlexGrow(0);
		
		setCustomers();
		setProducts();
		
		add(comboCustomer, comboProduct, detailsGrid, actions);
		
		binder.bindInstanceFields(this);
		
		setSpacing(true);
		
		save.getElement().getThemeList().add("primary");
		delete.getElement().getThemeList().add("error");
		
		addKeyPressListener(Key.ENTER, e -> save());
		
		save.addClickListener(e -> save());
		delete.addClickListener(e -> delete());
		cancel.addClickListener(e -> editPurchase(purchase));
		setVisible(false);
	}

	

	void delete() {
		purchaseRepo.delete(purchase);
		changeHandler.onChange();
	}

	void save() {
		purchaseRepo.save(purchase);
		changeHandler.onChange();
	}

	public interface ChangeHandler {
		void onChange();
	}
	
	public final void editPurchase(Purchase p) {
		if(p == null) {
			setVisible(false); 
			return;
		}
		final boolean persisted = p.getId()	!= null;
		purchase = (persisted ? 
				purchaseRepo.findById(p.getId()).get() : p);
		cancel.setVisible(persisted);
		binder.setBean(p);
		setVisible(true);
		comboCustomer.focus();
	}
	
	public void setChangeHandler(ChangeHandler h) {
		// ChangeHandler is notified when either save or delete
		// is clicked
		changeHandler = h;
	}
	
	private void setProducts() {
		this.comboProduct.setItems(this.productRepo.findAll());
	}

	private void setCustomers() {
		comboCustomer.setItems(this.customerRepo.findAll());
	}

}
