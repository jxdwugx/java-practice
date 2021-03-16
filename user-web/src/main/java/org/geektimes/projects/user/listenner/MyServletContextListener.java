package org.geektimes.projects.user.listenner;

import org.geektimes.projects.user.context.ComponentContext;
import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.mbean.UserManager;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.lang.management.ManagementFactory;

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
        ComponentContext context = new ComponentContext();
        context.init(servletContext);
        registerMBean();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }

    private void registerMBean(){
        // 获取平台 MBean Server
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            // 为 UserMXBean 定义 ObjectName
            ObjectName objectName = new ObjectName("org.geektimes.projects.user.mbean:type=User");
            // 创建 UserMBean 实例
            User user = new User();
            mBeanServer.registerMBean(createUserMBean(user), objectName);
        }catch (Exception e){
            servletContext.log("注册MBean失败", e);
        }

    }

    private static Object createUserMBean(User user) {
        return new UserManager(user);
    }
}
