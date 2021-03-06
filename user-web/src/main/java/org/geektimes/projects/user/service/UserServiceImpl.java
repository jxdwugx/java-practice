package org.geektimes.projects.user.service;

import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.repository.DatabaseUserRepository;
import org.geektimes.projects.user.repository.UserRepository;

/**
 * @description
 * @autor 吴光熙
 * @date 2021/3/2  20:34
 **/
public class UserServiceImpl implements UserService{


    private UserRepository userRepository;

    @Override
    public boolean register(User user) {
        assertStatus();
        return userRepository.save(user);
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
            userRepository = new DatabaseUserRepository();
        }
    }

}
