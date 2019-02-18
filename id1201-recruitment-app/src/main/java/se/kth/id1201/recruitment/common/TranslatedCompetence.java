/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1201.recruitment.common;

/**
 *  A Translated competence, used by PDFObject to more easily
 * iterate through and print out a users competences
 * @author Perttu Jääskeläinen
 */
public class TranslatedCompetence {
    private final String competence;
    private final Double experience;
    
    public TranslatedCompetence(String comp, Double ex) {
        this.competence = comp;
        this.experience = ex;
    }
    
    public String getCompetence() {
        return this.competence;
    }
    
    public Double getExperience() {
        return this.experience;
    }
}
