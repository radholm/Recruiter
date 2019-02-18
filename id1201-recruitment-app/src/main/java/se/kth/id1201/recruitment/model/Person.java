/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1201.recruitment.model;

import se.kth.id1201.recruitment.model.components.Role;
import se.kth.id1201.recruitment.common.UserDTO;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import se.kth.id1201.recruitment.common.RoleType;

/**
 *  Entity for each person in the organization
 * @author Fredrik
 */
@Entity
public class Person implements UserDTO, Serializable {

    private final String fName;
    private final String lName;
    private final String email;
    @Id
    private final String username;
    private final String password;
    @Temporal(javax.persistence.TemporalType.DATE)
    private final Date dob;
    @ManyToOne
    private final Role userRole;

    public Person() {
        this.fName = null;
        this.lName = null;
        this.email = null;
        this.dob = null;
        this.username = null;
        this.password = null;
        this.userRole = null;
    }
    /**
     * Verifies a person
     * @param p person to verify
     * @param password  password to compare with person
     * @return  true if verification succeeded
     */
    public static boolean verifyUser(Person p, String password) {
        return p.getPassword().equals(password);
    }
    @Override
    public <T extends UserDTO> boolean equals(T p) {
        return this.username.equals(p.getUsername()) && this.password.equals(p.getPassword());
    }

    public Person(String fName, String lName,
            String email, Date dob, String username, String password) {
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.dob = dob;
        this.username = username;
        this.password = password;
        this.userRole = new Role(RoleType.APPLICANT);
    }
    
    @Override
    public Object getPrimaryKey() {
        return this.username;
    }

    @Override
    public String getFname() {
        return this.fName;
    }

    @Override
    public String getLname() {
        return this.lName;
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public Date getDob() {
        return this.dob;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public RoleType getRole() {
        return this.userRole.getRole();
    }
}