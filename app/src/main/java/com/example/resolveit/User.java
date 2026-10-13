package com.example.resolveit;

public class User {
    private int userId;
    private String name;
    private String email;
    private String phone;
    private String department;
    private String password;
    private String role; // "USER" or "ADMIN"

    public User(int userId, String name, String email, String phone, String department, String password, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.department = department;
        this.password = password;
        this.role = role;
    }

    public User(int userId, String name, String email, String phone, String password, String role) {
        this(userId, name, email, phone, "Computer Science", password, role);
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getDepartment() {
        return department != null ? department : "N/A";
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role) || "admin@resolveit.com".equalsIgnoreCase(email);
    }
}
