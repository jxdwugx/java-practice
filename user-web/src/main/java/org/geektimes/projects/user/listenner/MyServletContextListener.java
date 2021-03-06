package org.geektimes.projects.user.listenner;

import org.geektimes.projects.user.context.JNDIComponentContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @description
 * @autor 吴光熙
 * @date 2021/3/6  14:59
 **/
public class MyServletContextListener implements ServletContextListener {

    private static ServletContext servletContext;


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        servletContext = servletContextEvent.getServletContext();
        JNDIComponentContext context = new JNDIComponentContext();
        context.init(servletContext);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }


}
