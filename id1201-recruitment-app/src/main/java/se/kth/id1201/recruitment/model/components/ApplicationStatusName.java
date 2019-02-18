/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1201.recruitment.model.components;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import se.kth.id1201.recruitment.common.ApplicationStatus;
import se.kth.id1201.recruitment.common.EntityObject;

/**
 * Translation for an enumerator application statuts
 *
 * @author Perttu Jääskeläinen
 */
@Entity
@IdClass(ApplicationStatusNameKey.class)
public class ApplicationStatusName implements Serializable, EntityObject {

    @ManyToOne
    @Id
    private final DbLang language;

    @Id
    @Enumerated(EnumType.STRING)
    private final ApplicationStatus status;

    private final String statusname;

    public ApplicationStatusName() {
        this.language = null;
        this.status = null;
        this.statusname = null;
    }

    public ApplicationStatusName(DbLang language, ApplicationStatus status, String desc) {
        this.language = language;
        this.status = status;
        this.statusname = desc;
    }

    @Override
    public Object getPrimaryKey() {
        return new ApplicationStatusNameKey(language, status);
    }

    public String getStatusName() {
        return this.statusname;
    }

    public DbLang getLanguage() {
        return this.language;
    }

    public ApplicationStatus getStatus() {
        return this.status;
    }

}
