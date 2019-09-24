package hello.ui;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import hello.entities.Product;
import hello.repositories.ProductRepository;
import hello.ui.editors.ProductEditor;

@SuppressWarnings("unused")
@Route(value = "Products", layout = MainLayout.class)
@PageTitle("Products")
public class ProductView extends VerticalLayout{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7864326781415582184L;

	private final ProductRepository repo;
	
	private final ProductEditor editor;
	
	final Grid<Product> grid;
	
	final TextField filter;
	
	private final Button addNewBtn;

	public ProductView(ProductRepository repo, ProductEditor editor) {
		this.repo = repo;
		this.editor = editor;
		this.grid = new Grid<>(Product.class);
		this.filter = new TextField();
		this.addNewBtn = new Button("New product", VaadinIcon.PLUS.create());

		// build layout
		HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);
		add(actions, grid, editor);
		
		grid.setHeight("300px");
		grid.setColumns("id", "name", "price");
		grid.getColumnByKey("id").setWidth("50px").setFlexGrow(0);

		filter.setPlaceholder("Filter by product name");

		// Hook logic to components
		// Replace listing with filtered content when user changes filter
		filter.setValueChangeMode(ValueChangeMode.EAGER);
		filter.addValueChangeListener(e -> listProducts(e.getValue()));
		
		// Connect selected Product to editor or hide if none is selected
		grid.asSingleSelect().addValueChangeListener(e -> {
			editor.editProduct(e.getValue());
		});
		
		// Instantiate and edit new Customer the new button is clicked
		addNewBtn.addClickListener(e -> 
			editor.editProduct(new Product("", BigDecimal.valueOf(0L))));
		
		// Listen changes made by the editor, refresh data from backend
		editor.setChangeHandler(() -> {
			editor.setVisible(false);
			listProducts(filter.getValue());
		});
		
		// Initialize listing
		listProducts(null);
	}
	
	void listProducts(String filterText) {
		if(StringUtils.isEmpty(filterText)) 
			grid.setItems(repo.findAll());
		else
			grid.setItems(repo.findByNameStartsWithIgnoreCase(filterText));
	}
}
