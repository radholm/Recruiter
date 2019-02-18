/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1201.recruitment.model.components;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import se.kth.id1201.recruitment.common.EntityObject;

/**
 * Entity for storing translations between competences and languages
 * @author Perttu Jääskeläinen
 */
@Entity
@IdClass(CompetenceNameKey.class)
public class CompetenceName implements Serializable, EntityObject {

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    private Competence comp;
    @Id
    @ManyToOne
    private DbLang lang;

    private final String name;

    public CompetenceName() {
        name = null;
    }

    /**
     * Create a new translation between an competence and a language
     *
     * @param comp competence to create translation for
     * @param lang language to translate to
     * @param name the name of the translation
     */
    public CompetenceName(Competence comp, DbLang lang, String name) {

        this.comp = comp;
        this.lang = lang;
        this.name = name;
    }

    @Override
    public Object getPrimaryKey() {
        return new CompetenceNameKey(this.comp.getId(), this.lang.getLanguage());
    }

    public String getName() {
        return this.name;
    }
    public Competence getCompetence() {
        return this.comp;
    }
    public DbLang getLanguage() {
        return this.lang;
    }

}
