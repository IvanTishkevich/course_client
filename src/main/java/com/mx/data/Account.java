package com.mx.data;

import java.io.Serializable;

public class Account implements Serializable {
    private static final long serialVersionUID = 1L;
    private int userID;
    private String login;
    private String email;
    private String role="user";
    private String password;
    private String name;
    private String surname;
    private String address;
    private String telephone;

    public Account(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public Account(String login, String email, String password, String name, String surname, String address, String telephone) {
        this.login = login;
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.telephone = telephone;
    }

    public Account(String login, String email, String password, String role, String name, String surname, String address, String telephone) {
        this.login = login;
        this.email = email;
        this.password = password;
        this.role = role;
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.telephone = telephone;
    }

    public Account(String login, String email, String name, String surname, String address, String telephone) {
        this.login = login;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.telephone = telephone;
    }

    public void setInfo(String email, String name, String surname, String address, String telephone){
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.telephone = telephone;
    }

    public void setRole(String role){
        this.role = role;
    }

    public int getUserID() {
        return userID;
    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getAddress() {
        return address;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }
}