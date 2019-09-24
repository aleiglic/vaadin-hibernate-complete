package hello.ui.editors;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import hello.entities.User;
import hello.repositories.UserRepository;

@SpringComponent
@UIScope
public class UserEditor extends VerticalLayout implements KeyNotifier{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1352376553862992304L;
	
	private final UserRepository userRepo;
	
	private User user;
	
	TextField userName;
	PasswordField password;
	PasswordField password2;

	Button save = new Button("Save", VaadinIcon.CHECK.create());
	Button cancel = new Button("Cancel");
	Button delete = new Button("Delete", VaadinIcon.TRASH.create());
	HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);
	
	Binder<User> userBinder = new Binder<>(User.class);
	Binder<String> passwordBinder = new Binder<>(String.class);
	private ChangeHandler changeHandler;
	
	public UserEditor(UserRepository userRepo) {
		this.userRepo = userRepo;
		
		; 
		
		String pass2 = "";
		
		userName = new TextField("Username");
		password = new PasswordField("Password");
		password2 = new PasswordField("Repeat password", pass2);
		
		add(userName, password, password2, actions);

		userBinder.forField(userName)
			.asRequired("The value is required")
			.bind(User::getUsername, User::setUsername);
		
		userBinder.forField(password)
			.asRequired("The value is required")
			
			.bind(User::getPassword, User::setPassword);
			
		passwordBinder.forField(password2)
			.asRequired()
			.withValidator(
				(pass, context) -> 
				pass.equals(this.password.getValue())
				? ValidationResult.ok()
				: ValidationResult.error("The password must be the same") 
		).bind(s -> password2.getValue(), (s, v) -> password2.setValue(v))
		
		;
		
		/*passwordBinder.forField(password2)
			.asRequired("The value is required")
			.withValidator((pass, context) -> 
				pass.equals(this.password.getValue())
				? ValidationResult.error("The password must be the same") 
				: ValidationResult.ok()
			)
		;*/
		
		setSpacing(true);	
		save.getElement().getThemeList().add("primary");
		delete.getElement().getThemeList().add("error");

		addKeyPressListener(Key.ENTER, e -> save());

		// wire action buttons to save, delete and reset
		save.addClickListener(e -> save());
		delete.addClickListener(e -> delete());
		cancel.addClickListener(e -> editUser(user));
		setVisible(false);
			
	}
	
	public boolean fieldsAreValid() {
		return !userName.isInvalid() && !password.isInvalid() && !password2.isInvalid();
	}
	
	void delete() {
		
			userRepo.delete(user);
		changeHandler.onChange();
	}
	
	void save() {
		if(fieldsAreValid()) {
			userRepo.save(user);
			changeHandler.onChange();
		}
	}
	
	public interface ChangeHandler {
		void onChange();
	}
	
	public final void editUser(User u) {
		if(u == null) {
			setVisible(false);
			return;
		}
		final boolean persisted = u.getId() != null;
		user = (persisted ?  userRepo.findById(u.getId()).get() : u);
		cancel.setVisible(persisted);
		
		// Bind customer properties to similarly named fields
		// Could also use annotation or "manual binding" or programmatically
		// moving values from fields to entities before saving
		userBinder.setBean(user);
		
		setVisible(true);
		
		userName.focus();
	}

	public void setChangeHandler(ChangeHandler h) {
		// ChangeHandler is notified when either save or delete
		// is clicked
		changeHandler = h;
	}
}
