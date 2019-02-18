/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1201.recruitment.model.components;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import se.kth.id1201.recruitment.common.EntityObject;
import se.kth.id1201.recruitment.model.Person;

/**
 *  Entity for storing a users profile for a competence
 * @author Fredrik
 */
@Entity
@Table(name = "competence_profile")
@IdClass(CompetenceProfileKey.class)
public class CompetenceProfile implements Serializable, EntityObject {

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "username_ref", nullable = false)
    private final Person personID;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "competence_id_ref", nullable = false)
    private final Competence competence_id;

    @Column(name = "years_of_experience", nullable = false)
    private final Double yearsOfExperience;

    public CompetenceProfile() {
        this.personID = null;
        this.competence_id = null;
        this.yearsOfExperience = null;
    }
    /**
     * Creates a new competence profile for a user, given a competence ID and years of experience
     * @param personID  the person to create the profile for
     * @param competenceID  which competence to create the profile
     * @param yearsOfExperience     the users experience in this competence
     */
    public CompetenceProfile(Person personID, Competence competenceID, Double yearsOfExperience) {
        this.personID = personID;
        this.competence_id = competenceID;
        this.yearsOfExperience = yearsOfExperience;
    }
    public Person getPerson() {
        return this.personID;
    }
    @Override
    public Object getPrimaryKey() {
        return new CompetenceProfileKey(this.competence_id.getId(), this.personID.getUsername());
    }

    public Competence getCompetence() {
        return this.competence_id;
    }

    public Double getExperience() {
        return this.yearsOfExperience;
    }
}