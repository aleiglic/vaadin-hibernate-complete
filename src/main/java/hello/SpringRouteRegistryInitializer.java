package hello;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.startup.RouteRegistryInitializer;

@SuppressWarnings("unused")
@Component
public class SpringRouteRegistryInitializer extends RouteRegistryInitializer
        implements ServletContextInitializer {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7248706874341760026L;
	private static final String PACKAGE = "hello.ui";

    @Override
    public void onStartup(ServletContext servletContext)
            throws ServletException {
    	/*
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(
                false);
        provider.addIncludeFilter(new AnnotationTypeFilter(Route.class));
        provider.addIncludeFilter(new AnnotationTypeFilter(RouteAlias.class));
        Set<Class<?>> classSet = provider.findCandidateComponents(PACKAGE)
                .stream().map(BeanDefinition::getBeanClassName)
                .map(className -> {
                    try {
                        return Class.forName(className);
                    } catch (ClassNotFoundException e) {
                        return null;
                    }
                }).filter(Objects::nonNull).collect(Collectors.toSet());
        super.onStartup(classSet, servletContext);
        */
    }

}