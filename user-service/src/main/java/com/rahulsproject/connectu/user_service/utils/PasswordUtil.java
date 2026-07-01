package com.rahulsproject.connectu.user_service.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    public static String hashPassword(String plainText){
        return BCrypt.hashpw(plainText, BCrypt.gensalt());
    }

    public static Boolean checkPassword(String plainText, String hashedPassword){
        return BCrypt.checkpw(plainText, hashedPassword);
    }
}
