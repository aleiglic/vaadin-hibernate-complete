package hello.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/*
@Route(value = "login")
@HtmlImport("webapp/frontend/styles/shared-styles.html")
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
@PageTitle("Login")
*/
@SuppressWarnings("unused")
public class LoginView extends VerticalLayout{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8543643489825477406L;
	
	TextField user;
    PasswordField password;
    Button loginButton;
	
	public LoginView() {
		user = new TextField("User");
		password = new PasswordField("Pass");
		loginButton = new Button("Login");
		add(user, password, loginButton);
	}

}
