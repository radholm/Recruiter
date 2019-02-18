/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1201.recruitment.model.components;

import java.util.Objects;
import se.kth.id1201.recruitment.model.Person;

/**
 *  Composite key for a competence profile, combining 
 * a persons username string with a competence's id-value
 * @author Perttu Jääskeläinen
 */
public class CompetenceProfileKey {

    private String personID;
    private Long competence_id;

    public CompetenceProfileKey(Person p, Competence c) {
        this.competence_id = c.getId();
        this.personID = p.getUsername();
    }

    public CompetenceProfileKey(Long compID, String person) {
        this.competence_id = compID;
        this.personID = person;
    }
    public Long getCompetenceId() {
        return this.competence_id;
    }
    public String getPersonId() {
        return this.personID;
    }

    @Override
    public boolean equals(Object ob) {
        CompetenceProfileKey o = (CompetenceProfileKey) ob;
        return Objects.equals(o.competence_id, this.competence_id) && o.personID == this.personID;
    }

    @Override
    public int hashCode() {
        return this.competence_id.hashCode() + this.personID.hashCode();
    }
}
