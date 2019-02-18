/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1201.recruitment.model.components;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import se.kth.id1201.recruitment.common.EntityObject;
import se.kth.id1201.recruitment.common.RoleType;

/**
 * Entity for storing role types and mapping them with ID values
 * @author Perttu
 */
@Entity
@Table(name = "role", uniqueConstraints = @UniqueConstraint(columnNames = {"role_name"}))
public class Role implements Serializable, EntityObject {

    @Id
    @Column(name = "role_id")
    private final Long roleId;
    @Column(name = "role_name", nullable = false)
    @Enumerated(EnumType.STRING)
    private final RoleType userRole;

    public Role() {
        this.roleId = null;
        this.userRole = RoleType.APPLICANT;
    }
    /**
     * Gets the default ID value for a role
     * @param type  the roletype to get number for
     * @return      the ID number for the role
     */
    private static Long roleIdSwitch(RoleType type) {
        switch (type) {
            case APPLICANT:
                return (long) 1;
            case RECRUITER:
                return (long) 2;
            default:
                return (long) -1;
        }
    }

    /**
     * Creates a new role
     * @param userRole type of role to create
     */
    public Role(RoleType userRole) {
        this.userRole = userRole;
        this.roleId = roleIdSwitch(userRole);
    }

    @Override
    public Object getPrimaryKey() {
        return this.roleId;
    }

    public static Long getRoleID(RoleType type) {
        return roleIdSwitch(type);
    }

    public RoleType getRole() {
        return this.userRole;
    }
}
