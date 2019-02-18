/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1201.recruitment.model.components;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import se.kth.id1201.recruitment.common.EntityObject;

/**
 *  Class for storing all available languages in the database
 * @author Perttu Jääskeläinen
 */
@Entity
@NamedQuery(name = "findAllLanguages",
        query="SELECT lang.lang_name FROM DbLang lang")
public class DbLang implements Serializable, EntityObject {
    
    @Id
    private final String lang_name;
    
    public DbLang() {
        this.lang_name = null;
    }
    @Override
    public Object getPrimaryKey() {
        return this.lang_name;
    }
    public DbLang(String langname) {
        this.lang_name = langname;
    }
    public String getLanguage() {
        return this.lang_name;
    }
}
