package org.acme.travels;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Person {

    @NotNull
    @Size(min = 2, max = 30)
    private String name;

    @NotNull
    @Min(value = 2, message = "Age must be higher than 5")
    private int age;

    @NotNull
    @Email
    private String email;

    private boolean adult;

    public Person() {

    }

    public Person(String name, int age) {
        super();
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Person [name=" + name + ", age=" + age + ", adult=" + adult + "]";
    }

}
