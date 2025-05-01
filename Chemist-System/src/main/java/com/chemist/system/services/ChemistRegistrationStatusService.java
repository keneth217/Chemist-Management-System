package com.chemist.system.services;

import com.chemist.system.models.MasterUser;
import com.chemist.system.models.User;

public class ChemistRegistrationStatusService {
    public void updateStatus(String tenantId, String s) {

    }

    public static class MasterUserService {

        public MasterUser findByPhoneNo(String username) {

            return null;
        }
    }

    public static class UserService {
        public User saveUser(User admin) {

            return admin;
        }

        public User  findByPhoneNo(String username) {
            return null;
        }
    }
}
