package com.babel.assessments.util;

import com.babel.assessments.model.User;
import java.util.regex.Pattern;

public class UserValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern WHATSAPP_PATTERN = Pattern.compile("^[0-9]{7,15}$");

    public static void validate(User user) {
        if (user == null) throw new IllegalArgumentException("User is null");
        if (user.getName() == null || user.getName().isBlank()) throw new IllegalArgumentException("Name is required");
        if (user.getWhatsapp() == null || !WHATSAPP_PATTERN.matcher(user.getWhatsapp()).matches()) throw new IllegalArgumentException("Invalid Whatsapp");
        if (user.getEmail() == null || !EMAIL_PATTERN.matcher(user.getEmail()).matches()) throw new IllegalArgumentException("Invalid email");
    }

    public static void assignRole(User user) {
        if (user.getRole() == null || user.getRole().isBlank()) user.setRole("client");
        else if (!user.getRole().equalsIgnoreCase("admin")) user.setRole("client");
    }
}
