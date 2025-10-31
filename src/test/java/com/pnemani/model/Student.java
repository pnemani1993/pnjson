package com.pnemani.model;

public class Student {
    
    private String nameOfStudent;

    private Integer rollNo;

    private String address;

    public String getNameOfStudent() {
        return this.nameOfStudent;
    }

    public void setNameOfStudent(String nameOfStudent){
        this.nameOfStudent = nameOfStudent;
    }

    public Integer getRollNo() {
        return rollNo;
    }

    public void setRollNo(Integer rollNo) {
        this.rollNo = rollNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }    
}
