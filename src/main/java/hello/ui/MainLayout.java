/*
 * Copyright 2000-2017 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package hello.ui;


import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PageConfigurator;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;


/**
 * The main layout contains the header with the navigation buttons, and the
 * child views below that.
 */
//@HtmlImport("webapp/frontend/styles/shared-styles.html")
@Theme(value = Lumo.class, variant = Lumo.DARK)
@HtmlImport("styles/shared-styles.html")
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
public class MainLayout extends Div
        implements RouterLayout, PageConfigurator {

    /**
	 * 
	 */
	private static final long serialVersionUID = -5072013618262207684L;

	public MainLayout() {
		
		
        H2 title = new H2("Purchases");
        title.addClassName("main-layout__title");
        
        RouterLink mainView = new RouterLink(null, MainView.class);
        mainView.add(new Icon(VaadinIcon.LIST), new Text("Customers"));
        mainView.addClassName("main-layout__nav-item");
        // Only show as active for the exact URL, but not for sub paths
        mainView.setHighlightCondition(HighlightConditions.sameLocation());

        RouterLink productView = new RouterLink(null, ProductView.class);
        productView.add(new Icon(VaadinIcon.ARCHIVES), new Text("Products"));
        productView.addClassName("main-layout__nav-item");

        RouterLink purchaseView = new RouterLink(null, PurchaseView.class);
        purchaseView.add(new Icon(VaadinIcon.MONEY_DEPOSIT), new Text("Purchases"));
        purchaseView.addClassName("main-layout__nav-item");
        
        RouterLink view2 = new RouterLink(null, View2.class);
        view2.add(new Icon(VaadinIcon.ARROW_UP), new Text("View2"));
        view2.addClassName("main-layout__nav-item");
        
        RouterLink view3 = new RouterLink("", View3.class);
        view3.add(new Icon(VaadinIcon.ARROW_UP), new Text("Make purchase"));
        view3.addClassName("main-layout__nav-item");
        
        RouterLink userView = new RouterLink("", UserView.class);
        userView.add(new Icon(VaadinIcon.USER), new Text("Users"));
        userView.addClassName("main-layout__nav-item");
        
        
        HorizontalLayout right = new HorizontalLayout();
        
        Button logout = new Button("Logout", VaadinIcon.SIGN_OUT.create());
        /*logout.addClickListener(e -> {
        	SecurityContextHolder.clearContext();
        	this.getUI().get().getSession().close();
        	this.getUI().get().navigate("login");  // navigate to login page
        });*/
        
        Anchor anchorExit = new Anchor();
        anchorExit.setHref("logout");
        anchorExit.setTarget("logout");
        anchorExit.add(logout);
        
        right.add(anchorExit);
        right.setAlignItems(Alignment.END);
        
        Div navigation = new Div(mainView, productView, purchaseView, view2, view3, userView, right);

        navigation.addClassName("main-layout__nav");

        Div header = new Div(title, navigation);
        header.addClassName("main-layout__header");
        add(header);

        addClassName("main-layout");
        
	}

    @Override
    public void configurePage(InitialPageSettings settings) {
        settings.addMetaTag("apple-mobile-web-app-capable", "yes");
        settings.addMetaTag("apple-mobile-web-app-status-bar-style", "black");
    }
}
