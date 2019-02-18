/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1201.recruitment.model.components;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import se.kth.id1201.recruitment.common.EntityObject;
import se.kth.id1201.recruitment.model.Person;

/**
 * Entity class for storing a persons availability date
 * @author Fredrik
 */
@Entity
@Table(name = "availablility")
public class Availability implements Serializable, EntityObject {

    @Id
    @GeneratedValue
    @Column(name = "availability_id")
    private final Long availabilityId;
    
    @ManyToOne
    private final Person person;

    @Column(name = "from_date", nullable = false)
    @Temporal(javax.persistence.TemporalType.DATE)
    private final Date fromDate;

    @Column(name = "to_date", nullable = false)
    @Temporal(javax.persistence.TemporalType.DATE)
    private final Date toDate;

    public Availability() {
        this.availabilityId = null;
        this.person = null;
        this.fromDate = null;
        this.toDate = null;
    }
    /**
     * Create a new avaiability eneity for a person
     * @param person    person to create availability for
     * @param fromDate  date from which the person is available
     * @param toDate    date to which the person is available
     */
    public Availability(Person person, Date fromDate, Date toDate) {
        this.availabilityId = null;
        this.person = person;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }
    @Override
    public Object getPrimaryKey() {
        return this.availabilityId;
    }
    public Date getFromDate() {
        return this.fromDate;
    }
    public Person getPerson() {
        return this.person;
    }

    public Date getToDate() {
        return this.toDate;
    }
}