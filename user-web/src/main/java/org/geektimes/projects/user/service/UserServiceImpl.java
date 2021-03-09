package org.geektimes.projects.user.service;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.geektimes.projects.user.context.ComponentContext;
import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.repository.UserRepository;
import org.geektimes.projects.user.validator.UserValid;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @description
 * @autor 吴光熙
 * @date 2021/3/2  20:34
 **/
public class UserServiceImpl implements UserService{


    private UserRepository userRepository;

    private EntityManager entityManager;

    private Validator validator;

    @Override
    public boolean register(User user) {
        assertStatus();
        Set<ConstraintViolation<User>> set = validator.validate(user);
        System.out.println("获得验证结果");
        if(set.isEmpty()){
            entityManager.persist(user);
            System.out.println("插入成功");
            return true;
        }
        System.out.println("验证失败");
        for(ConstraintViolation<User> violation : set){
            System.out.println(violation.getMessage());
        }
        return false;
    }

    @Override
    public boolean deregister(User user) {
        assertStatus();
        return userRepository.deleteById(user.getId());
    }

    @Override
    public boolean update(User user) {
        assertStatus();
        return userRepository.update(user);
    }

    @Override
    public User queryUserById(Long id) {
        assertStatus();
        return userRepository.getById(id);
    }

    @Override
    public User queryUserByNameAndPassword(String name, String password) {
        assertStatus();
        return userRepository.getByNameAndPassword(name, password);
    }

    private void assertStatus(){
        if(userRepository == null){
            userRepository = ComponentContext.getInstance().getComponent("bean/DataBaseUserRepository");
        }
        if(entityManager == null){

//            EntityManagerFactory entityManagerFactory =
//                    Persistence.createEntityManagerFactory("emf", getProperties());
            entityManager = ComponentContext.getInstance().getComponent("bean/EntityManager");
        }
        if(validator == null){
            validator = ComponentContext.getInstance().getComponent("bean/Validator");
        }
    }


    private static Map<String, Object> getProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.DerbyDialect");
        properties.put("hibernate.id.new_generator_mappings", false);
        properties.put("hibernate.connection.datasource", getDataSource());
        return properties;
    }

    private static DataSource getDataSource() {
        EmbeddedDataSource dataSource = new EmbeddedDataSource();
        dataSource.setDatabaseName("/db/user-platform");
        dataSource.setCreateDatabase("create");
        return dataSource;
    }

}
