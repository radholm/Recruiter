/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1201.recruitment.common;

/**
 * DTO class for an application
 * @author Fredrik  
 */
public interface ApplicationDTO extends EntityObject {

    public ApplicationStatus getApplicationStatus();

    public void setApplicationStatus(ApplicationStatus applicationStatus);
}
