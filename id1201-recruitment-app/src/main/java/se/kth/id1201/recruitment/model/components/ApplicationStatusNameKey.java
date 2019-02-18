/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1201.recruitment.model.components;

import java.io.Serializable;
import java.util.Objects;
import se.kth.id1201.recruitment.common.ApplicationStatus;

/**
 *  Composite primary key used by ApplicationStatusName
 * to convert from enumerators to a String in a specific language
 * @author Perttu Jääskeläinen
 */
public class ApplicationStatusNameKey implements Serializable {

    
    private final ApplicationStatus status;
    private final String language;
    
    public ApplicationStatusNameKey(DbLang language, ApplicationStatus status) {
        this.language = language.getLanguage();
        this.status = status;
    }
    public ApplicationStatusNameKey(String language, String status) {
        this.status = ApplicationStatus.valueOf(status.toUpperCase());
        this.language = language;
    }
    
    public String getLanguage() {
        return this.language;
    }
    
    public ApplicationStatus getApplicationStatus() {
        return this.status;
    }

    @Override
    public boolean equals(Object ob) {
        ApplicationStatusNameKey o = (ApplicationStatusNameKey) ob;
        return o.language.equalsIgnoreCase(this.language) && o.status.equals(this.status);
    }

    @Override
    public int hashCode() {
        return language.hashCode() + status.hashCode();
    }
}
