package com.chemist.system.tenant.service;

import com.chemist.system.tenant.model.User;
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