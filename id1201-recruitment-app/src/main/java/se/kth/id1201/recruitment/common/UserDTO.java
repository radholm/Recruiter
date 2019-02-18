/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1201.recruitment.common;

import java.util.Date;

/**
 *  Interface implemented by all users
 * @author Fredrik
 */
public interface UserDTO extends EntityObject {

    public String getFname();

    public String getLname();

    public String getEmail();

    public Date getDob();

    public String getUsername();

    public String getPassword();

    public RoleType getRole();
    
    public <T extends UserDTO> boolean equals(T p);

}
