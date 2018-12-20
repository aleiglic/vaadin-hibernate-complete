package hello.editors;

import java.math.BigDecimal;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import hello.entities.Product;
import hello.repositories.ProductRepository;


@SpringComponent
@UIScope
public class ProductEditor extends VerticalLayout implements KeyNotifier{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3942789618911829498L;

	private final ProductRepository repository;

	private Product product;
	
	TextField name = new TextField("Product name");
	TextField price = new TextField("Price");
		
	/* Action buttons */
	// TODO why more code?
	Button save = new Button("Save", VaadinIcon.CHECK.create());
	Button cancel = new Button("Cancel");
	Button delete = new Button("Delete", VaadinIcon.TRASH.create());
	HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);
	
	Binder<Product> binder = new Binder<>(Product.class);
	
	private ChangeHandler changeHandler;
	
	public ProductEditor(ProductRepository repository) {
		this.repository = repository;
		
		add(name, price, actions);
		binder.forField(price).bind(
				product -> product.getPrice().toString(), 
				(product, price) -> product.setPrice(BigDecimal.valueOf(Double.parseDouble(price))));
		
		// bind using naming convention
		binder.bindInstanceFields(this);
		
		// Configure and style components
		setSpacing(true);
		
		save.getElement().getThemeList().add("primary");
		delete.getElement().getThemeList().add("error");

		addKeyPressListener(Key.ENTER, e -> save());

		// wire action buttons to save, delete and reset
		save.addClickListener(e -> save());
		delete.addClickListener(e -> delete());
		cancel.addClickListener(e -> editProduct(product));
		setVisible(false);
	}
	
	void delete() {
		repository.delete(product);
		changeHandler.onChange();
	}
	
	void save() {
		repository.save(product);
		changeHandler.onChange();
	}
	
	public interface ChangeHandler {
		void onChange();
	}
	
	public final void editProduct(Product p) {
		if(p == null) {
			setVisible(false);
			return;
		}
		final boolean persisted = p.getId() != null;
		if(persisted)
			product = repository.findById(p.getId()).get();
		else
			product = p;
		cancel.setVisible(persisted);
		
		// Bind customer properties to similarly named fields
		// Could also use annotation or "manual binding" or programmatically
		// moving values from fields to entities before saving
		binder.setBean(product);
		
		setVisible(true);
		
		name.focus();
	}
	
	public void setChangeHandler(ChangeHandler h) {
		// ChangeHandler is notified when either save or delete
		// is clicked
		changeHandler = h;
	}
}
