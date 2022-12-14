package com.mx.data;

import java.io.Serializable;

public class Providers implements Serializable {
    private static final long serialVersionUID = 2L;
    private int providerID;
    private String name;
    private String address;
    private String telephone;
    private int sumSupply;

    public Providers(int providerID, String name, String address, String telephone) {
        this.providerID = providerID;
        this.name = name;
        this.address = address;
        this.telephone = telephone;
    }

    public Providers(String name, String address, String telephone) {
        this.name = name;
        this.address = address;
        this.telephone = telephone;
    }

    public Providers(int id, String name) {
        this.providerID = id;
        this.name = name;
    }

    public int getSumSupply() {
        return sumSupply;
    }

    public void setSumSupply(int sumSupply) {
        this.sumSupply = sumSupply;
    }

    public int getProviderID() {
        return providerID;
    }

    public void setProviderID(int providerID) {
        this.providerID = providerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String toString(){
        return this.name;
    }
}
