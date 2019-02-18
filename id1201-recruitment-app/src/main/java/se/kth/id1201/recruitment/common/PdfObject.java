/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1201.recruitment.common;

import java.util.List;
import se.kth.id1201.recruitment.model.components.Availability;

/**
 *  Object that is readable by the PdfCreator,
 * making the creation of PDF files easier
 * @author Perttu Jääskeläinen
 */
public class PdfObject {
    UserDTO person;
    List<TranslatedCompetence> competences;
    List<Availability> availabilities;
    String appStatus;
    
    public <T extends UserDTO> PdfObject(T person, List<TranslatedCompetence> comps, List<Availability> avails, String status) {
        this.person = person;
        this.competences = comps;
        this.availabilities = avails;
        this.appStatus = status;
    }
    
    public UserDTO getPerson() {
        return this.person;
    }
    
    public List<TranslatedCompetence> getCompetences() {
        return this.competences;
    }
    public List<Availability> getAvailabilities() {
        return this.availabilities;
    }
    public String getStatus() {
        return this.appStatus;
    }   
}
