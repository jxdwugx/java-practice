package org.geektimes.projects.user.repository;

import org.geektimes.function.ThrowableFunction;
import org.geektimes.projects.user.context.ComponentContext;
import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.sql.DBConnectionManager;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.commons.lang.ClassUtils.wrapperToPrimitive;

public class DatabaseUserRepository implements UserRepository {

    private static Logger logger = Logger.getLogger(DatabaseUserRepository.class.getName());


    private static String mapColumnLabel(String fieldName) {
        return fieldName;
    }

    /**
     * 数据类型与 ResultSet 方法名映射
     */
    static Map<Class, String> resultSetMethodMappings = new HashMap<>();

    static Map<Class, String> preparedStatementMethodMappings = new HashMap<>();

    static {
        resultSetMethodMappings.put(Long.class, "getLong");
        resultSetMethodMappings.put(String.class, "getString");

        preparedStatementMethodMappings.put(Long.class, "setLong"); // long
        preparedStatementMethodMappings.put(String.class, "setString"); //
    }

    /**
     * 通用处理方式
     */
    private static Consumer<Throwable> COMMON_EXCEPTION_HANDLER = e -> {
        logger.log(Level.SEVERE, e.getMessage());
        System.out.println("异常信息"+e);
    };

    public static final String INSERT_USER_DML_SQL =
            "INSERT INTO users(name,password,email,phoneNumber) VALUES " +
                    "(?,?,?,?)";

    public static final String QUERY_ALL_USERS_DML_SQL = "SELECT id,name,password,email,phoneNumber FROM users";

    @Resource(name = "bean/DBConnectionManager")
    private DBConnectionManager dbConnectionManager;

    public DatabaseUserRepository() {
        System.out.println("ComponentContext" + ComponentContext.getInstance());
        this.dbConnectionManager = ComponentContext.getInstance().getComponent("bean/DBConnectionManager");
    }

    @PostConstruct
    public void postConstruct(){
        System.out.println("PostConstruct");
    }

    private Connection getConnection() {
        return dbConnectionManager.getConnection();
    }

    @Override
    public boolean save(User user) {
        return execute(INSERT_USER_DML_SQL,
                COMMON_EXCEPTION_HANDLER, user.getName(), user.getPassword(), user.getEmail(), user.getPhoneNumber());
    }

    @Override
    public boolean deleteById(Long userId) {
        return false;
    }

    @Override
    public boolean update(User user) {
        return false;
    }

    @Override
    public User getById(Long userId) {
        return executeQuery("SELECT id,name,password,email,phoneNumber FROM users WHERE id=?",
                resultSet -> {
                    if(resultSet.next()){
                        return pack2User(resultSet);
                    }
                    return null;
                }, COMMON_EXCEPTION_HANDLER, userId);
    }

    @Override
    public User getByNameAndPassword(String userName, String password) {
        return executeQuery("SELECT id,name,password,email,phoneNumber FROM users WHERE name=? and password=?",
                resultSet -> {
                    if(resultSet.next()){
                        return pack2User(resultSet);
                    }
                    return null;
                }, COMMON_EXCEPTION_HANDLER, userName, password);
    }

    @Override
    public Collection<User> getAll() {
        return executeQuery("SELECT id,name,password,email,phoneNumber FROM users", resultSet -> {
            // BeanInfo -> IntrospectionException
//            BeanInfo userBeanInfo = Introspector.getBeanInfo(User.class, Object.class);
            List<User> users = new ArrayList<>();
            while (resultSet.next()) { // 如果存在并且游标滚动 // SQLException
                users.add(pack2User(resultSet));
            }
            return users;
        }, e -> {
            // 异常处理
        });
    }

    /**
     * @param sql
     * @param function
     * @param <T>
     * @return
     */
    protected <T> T executeQuery(String sql, ThrowableFunction<ResultSet, T> function,
                                 Consumer<Throwable> exceptionHandler, Object... args) {
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                Class argType = arg.getClass();

                Class wrapperType = wrapperToPrimitive(argType);

                if (wrapperType == null) {
                    wrapperType = argType;
                }

                // Boolean -> boolean
                String methodName = preparedStatementMethodMappings.get(argType);
                Method method = PreparedStatement.class.getMethod(methodName, int.class, wrapperType);
                method.invoke(preparedStatement, i + 1, arg);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            // 返回一个 POJO List -> ResultSet -> POJO List
            // ResultSet -> T
            return function.apply(resultSet);
        } catch (Throwable e) {
            exceptionHandler.accept(e);
        }
        return null;
    }


    protected boolean execute(String sql, Consumer<Throwable> exceptionHandler, Object... args) {
        Connection connection = getConnection();

        try {
            System.out.println("connection" + connection);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            System.out.println("preparedStatement");
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                Class argType = arg.getClass();
                System.out.println("argType" + argType.getSimpleName());

                Class wrapperType = wrapperToPrimitive(argType);

                if (wrapperType == null) {
                    wrapperType = argType;
                }

                // Boolean -> boolean
                String methodName = preparedStatementMethodMappings.get(argType);
                System.out.println("methodName" + methodName);
                Method method = PreparedStatement.class.getMethod(methodName, int.class ,wrapperType);
                method.invoke(preparedStatement, i + 1, arg);
            }
            preparedStatement.execute();
            // ResultSet -> T
            return true;
        } catch (Throwable e) {
            System.out.println();
            exceptionHandler.accept(e);
        }
        return false;
    }

    private User pack2User(ResultSet resultSet) throws Throwable{
        User user = new User();
        BeanInfo userBeanInfo = Introspector.getBeanInfo(User.class, Object.class);
        for (PropertyDescriptor propertyDescriptor : userBeanInfo.getPropertyDescriptors()) {
            String fieldName = propertyDescriptor.getName();
            Class fieldType = propertyDescriptor.getPropertyType();
            String methodName = resultSetMethodMappings.get(fieldType);
            // 可能存在映射关系（不过此处是相等的）
            String columnLabel = mapColumnLabel(fieldName);
            Method resultSetMethod = ResultSet.class.getMethod(methodName, String.class);
            // 通过放射调用 getXXX(String) 方法
            Object resultValue = resultSetMethod.invoke(resultSet, columnLabel);
            // 获取 User 类 Setter方法
            // PropertyDescriptor ReadMethod 等于 Getter 方法
            // PropertyDescriptor WriteMethod 等于 Setter 方法
            Method setterMethodFromUser = propertyDescriptor.getWriteMethod();
            // 以 id 为例，  user.setId(resultSet.getLong("id"));
            setterMethodFromUser.invoke(user, resultValue);
        }
        return user;
    }
}
