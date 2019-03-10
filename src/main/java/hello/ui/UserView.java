package hello.ui;


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

import hello.editors.UserEditor;
import hello.entities.User;
import hello.repositories.UserRepository;

@Route(value = "Users", layout = MainLayout.class)
@PageTitle("Users")
public class UserView extends VerticalLayout{

	/**
	 * 
	 */
	private static final long serialVersionUID = -892231460069680982L;

	private UserRepository repo;
	@SuppressWarnings("unused")
	private final UserEditor editor;
	
	final Grid<User> grid;
	
	final TextField filter;
	
	private final Button addNewBtn;
	
	public UserView(UserRepository repo, UserEditor editor) {
		this.repo = repo;
		this.editor = editor;
		this.grid = new Grid<>(User.class);
		this.filter = new TextField();
		this.addNewBtn = new Button("Create new user", VaadinIcon.PLUS.create());
		
		HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);
		add(actions, grid, editor);
		
		grid.setHeight("300px");
		grid.setColumns("id", "username", "enabled");
		grid.getColumnByKey("id").setWidth("50px").setFlexGrow(0);
		
		filter.setPlaceholder("Filter by username");
		
		filter.setValueChangeMode(ValueChangeMode.EAGER);
		filter.addValueChangeListener(e -> listUsers(e.getValue()));
		
		grid.asSingleSelect().addValueChangeListener(e -> {
			editor.editUser(e.getValue());
		});
		
		addNewBtn.addClickListener(e -> 
			editor.editUser(new User("", ""))
		);
		
		editor.setChangeHandler(() -> {
			editor.setVisible(false);
			listUsers(filter.getValue());
		});
		
		listUsers("");
		
	}

	private void listUsers(String filterText) {
		if(StringUtils.isEmpty(filterText)) 
			grid.setItems(repo.findAll());
		else
			grid.setItems(repo.findByUserNameStartsWithIgnoreCase(filterText));
	}
}
