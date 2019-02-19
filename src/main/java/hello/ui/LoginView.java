package hello.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PageConfigurator;

/*
@Route(value = "login")
@HtmlImport("webapp/frontend/styles/shared-styles.html")
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
@PageTitle("Login")
*/
@SuppressWarnings("unused")
@Route(value = "login")
@HtmlImport("styles/shared-styles.html")
public class LoginView extends Div implements KeyNotifier{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8543643489825477406L;
	
	@Autowired
	DaoAuthenticationProvider daoAuthenticationProvider;
	
	TextField user;
    PasswordField password;
    Button loginButton;
    Authentication auth;
    Authentication authenticated;

    VerticalLayout form;

	
	public LoginView() {
		H2 title = new H2("Purchases");
        title.addClassName("main-layout__title");
        
        form = new VerticalLayout();
        form.setAlignItems(Alignment.CENTER);
        
		user = new TextField("User");
		password = new PasswordField("Pass");
		loginButton = new Button("LOGIN");
		
		
		form.add(user, password, loginButton);
		
		//user.addClassName("main-layout__nav-item");
		//password.addClassName("main-layout__nav-item");
		//loginButton.addClassName("main-layout__nav-item");
		
		loginButton.addClickListener(e -> {
			login();
		});
		
		addKeyPressListener(Key.ENTER, e -> login());
		
		add(form);
		
	}
	
	private void login() {
		if(!user.getValue().isEmpty() && !password.getValue().isEmpty()) {
			auth = new UsernamePasswordAuthenticationToken(user.getValue(),password.getValue());
			authenticated = daoAuthenticationProvider.authenticate(auth);
			SecurityContextHolder.getContext().setAuthentication(authenticated);
	
			//redirect to main application
			this.getUI().get().navigate("");
		}
	}

}
