package com.chemist.system.services;

import com.chemist.system.models.User;
import org.springframework.stereotype.Service;

@Service
public  class UserService {
    public User saveUser(User admin) {

        return admin;
    }

    public User  findByPhoneNo(String username) {
        return null;
    }
}