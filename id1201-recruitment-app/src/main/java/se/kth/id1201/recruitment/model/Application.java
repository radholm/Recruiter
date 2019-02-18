/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1201.recruitment.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import se.kth.id1201.recruitment.common.ApplicationDTO;
import se.kth.id1201.recruitment.common.ApplicationStatus;

/**
 * Entity for storing user made applications in the database
 *
 * @author Fredrik
 */
@Entity
@NamedQuery(name = "findAllApplications",
        query = "SELECT a FROM Application a")
@Table(name = "application")
public class Application implements Serializable, ApplicationDTO {

    @Id
    @GeneratedValue
    @Column(name = "application_id")
    private final Long applicationId;

    @OneToOne
    @JoinColumn(name = "username", unique = true)
    private final Person person;

    @Column(name = "application_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ApplicationStatus applicationStatus;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    private final Date creationDate;

    public Application() {
        this.applicationId = null;
        this.applicationStatus = null;
        this.person = null;
        this.creationDate = null;
    }
    public Application(Person person) {
        this.applicationId = null;
        this.person = person;
        this.applicationStatus = ApplicationStatus.PENDING;
        this.creationDate = new Date();
    }

    @Override
    public Object getPrimaryKey() {
        return this.applicationId;
    }
    
    public Date getCreationDate() {
        return this.creationDate;
    }
    
    /**
     * Returns the person this application is for
     * @return person associated with the application
     */
    public Person getPerson() {
        return this.person;
    }
    /**
     * Get the status for this application
     *
     * @return the application status
     */
    @Override
    public ApplicationStatus getApplicationStatus() {
        return this.applicationStatus;
    }

    /**
     * Changes the application status for this application
     *
     * @param applicationStatus the new application status
     */
    @Override
    public void setApplicationStatus(ApplicationStatus applicationStatus) {
        this.applicationStatus = applicationStatus;
    }
}
