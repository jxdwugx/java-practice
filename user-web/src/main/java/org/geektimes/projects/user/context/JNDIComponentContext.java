package org.geektimes.projects.user.context;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import java.util.Enumeration;

/**
 * @description
 * @autor 吴光熙
 * @date 2021/3/6  16:09
 **/
public class JNDIComponentContext {

    public static final String NAME = JNDIComponentContext.class.getName();

    private static ServletContext servletContext;

    private Context context;

    public static JNDIComponentContext getInstance(){
        Enumeration<String> enumeration = servletContext.getAttributeNames();
        System.out.println("上下文中元素");
        while (enumeration.hasMoreElements()){
            System.out.println(enumeration.nextElement());
        }
        return (JNDIComponentContext) servletContext.getAttribute(JNDIComponentContext.NAME);
    }

    public void init(ServletContext servletContext){
        JNDIComponentContext.servletContext = servletContext;
        try {
            context = (Context) new InitialContext().lookup("java:comp/env");
        } catch (NamingException e) {
            e.printStackTrace();
        }
        System.out.println("初始化上下文");
        servletContext.setAttribute(JNDIComponentContext.NAME, this);
        Enumeration<String> enumeration = servletContext.getAttributeNames();
        System.out.println("上下文中元素");
        while (enumeration.hasMoreElements()){
            System.out.println(enumeration.nextElement());
        }
    }

    public <C> C getComponent(String name){
        C component = null;
        try {
            component = (C) context.lookup(name);
        } catch (NamingException e) {
            e.printStackTrace();
        }
        return component;
    }

}
