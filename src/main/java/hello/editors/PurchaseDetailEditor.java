package hello.editors;

import java.util.Collection;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.spring.annotation.*;

import hello.entities.Product;
import hello.entities.Purchase;
import hello.entities.PurchaseDetail;
import hello.repositories.ProductRepository;
import hello.repositories.PurchaseDetailRepository;

@SuppressWarnings("unused")
@SpringComponent
@UIScope
public class PurchaseDetailEditor extends VerticalLayout implements KeyNotifier{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6710259021069300285L;

	Collection<PurchaseDetail> detailsCollection;
	
	private final ProductRepository productRepo;
	
	private final PurchaseDetailRepository detailRepo;
	
	private PurchaseDetail purchaseDetail;
	
	private ComboBox<Product> product;
	private TextField quantity;
	
	Button save = new Button("Save", VaadinIcon.CHECK.create());
	Button cancel = new Button("Cancel");
	Button delete = new Button("Delete", VaadinIcon.TRASH.create());
	HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);
	
	Binder<PurchaseDetail> binder = new Binder<>(PurchaseDetail.class);
	private ChangeHandler changeHandler;
	
	public PurchaseDetailEditor(ProductRepository productRepo,
			PurchaseDetailRepository detailRepo,
			Collection<PurchaseDetail> detailsCollection) {
		
		this.productRepo = productRepo;
		this.detailRepo = detailRepo;
		this.detailsCollection = detailsCollection;
		
		product = new ComboBox<Product>("Product");
		quantity = new TextField("Quantity");
		
		setProductCombo();
		
		add(product, quantity, actions);
		
		binder.forField(quantity)
		.withConverter(
		        new StringToIntegerConverter("Must enter a integer"))
		.withNullRepresentation(0)
		.withValidator(quantity -> quantity != null, "Must be higher than 0")
		.withValidator(quantity -> quantity > 0, "Must be a positive number")
		.asRequired("Must enter quantity")
		.bind(PurchaseDetail::getQuantity, PurchaseDetail::setQuantity);
		
		binder.forField(product)
		.asRequired("Must enter product")
		.bind(PurchaseDetail::getProduct, PurchaseDetail::setProduct);
		
		//binder.forField(quantity) ... etc ...
		
		//binder.bindInstanceFields(this);
		
		setSpacing(true);
		
		save.getElement().getThemeList().add("primary");
		delete.getElement().getThemeList().add("error");

		addKeyPressListener(Key.ENTER, e -> save());

		// wire action buttons to save, delete and reset
		save.addClickListener(e -> save());
		delete.addClickListener(e -> delete());
		cancel.addClickListener(e -> editDetail(purchaseDetail));
		setVisible(false);
	}

	void delete() {
		detailsCollection.remove(purchaseDetail);
		changeHandler.onChange();
	}

	void save() {
		if(fieldsAreValid() && !detailsCollection.contains(purchaseDetail)) {
			detailsCollection.add(purchaseDetail);
		}
		changeHandler.onChange();

	}

	public interface ChangeHandler {
		void onChange();
	}
	
	/*
	public final void editDetail(PurchaseDetail pd) {
		if (pd == null) {
			setVisible(false);
			return;
		}
		final boolean persisted = pd.getId() != null;
		if(persisted)
			purchaseDetail = detailRepo.findById(pd.getId()).get();
		else
			purchaseDetail = pd;		
		cancel.setVisible(persisted);

		// Bind customer properties to similarly named fields
		// Could also use annotation or "manual binding" or programmatically
		// moving values from fields to entities before saving
		binder.setBean(purchaseDetail);

		setVisible(true);

		// Focus first name initially
		product.focus();
	}*/
	
	
	public final void editDetail(PurchaseDetail pd) {
		if (pd == null) {
			setVisible(false);
			return;
		}
		final boolean isOnCollection = detailsCollection.contains(pd);
		
		purchaseDetail = pd;
		
		cancel.setVisible(isOnCollection);

		// Bind customer properties to similarly named fields
		// Could also use annotation or "manual binding" or programmatically
		// moving values from fields to entities before saving
		binder.setBean(purchaseDetail);

		setVisible(true);

		// Focus first name initially
		product.focus();
	}
	
	public void setChangeHandler(ChangeHandler h) {
		// ChangeHandler is notified when either save or delete
		// is clicked
		changeHandler = h;
	}
	
	private void setProductCombo() {
		this.product.setItems(productRepo.findAll());
	}
	
	private boolean fieldsAreValid() {
		return !product.isInvalid() && !quantity.isInvalid(); 
	}

}
