package it.polimi.tiw.project4.utils;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;

public class TemplateEngineHandler {
    public static TemplateEngine getEngine(ServletContext servletContext) {
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/WEB-INF/");
        templateResolver.setSuffix(".html");
        templateResolver.setCharacterEncoding("UTF-8");
        org.thymeleaf.TemplateEngine engine = new org.thymeleaf.TemplateEngine();
        engine.setTemplateResolver(templateResolver);
        return engine;
    }
}
