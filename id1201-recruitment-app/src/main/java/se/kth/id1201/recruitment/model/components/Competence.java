/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1201.recruitment.model.components;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import se.kth.id1201.recruitment.common.EntityObject;

/**
 *  Entity for storing competences in the database
 * @author Fredrik
 */
@Entity
@NamedQuery(name="findAllCompetences",
        query="SELECT c FROM Competence c")
@Table(name = "competence")
public class Competence implements Serializable, EntityObject {

    @Id
    @GeneratedValue
    @Column(name = "competence_id")
    private Long competenceId;
    
    @Column(unique = true)
    private final String description; // describing the competence in default language EN
    
    public Competence() {
        this.description = null;
    }
    public Competence(String desc) {
        this.description = desc;
    }
    @Override
    public Object getPrimaryKey() {
        return this.competenceId;
    }
    public Long getId() {
        return this.competenceId;
    }
    public String getDescription() {
        return this.description;
    }
}
