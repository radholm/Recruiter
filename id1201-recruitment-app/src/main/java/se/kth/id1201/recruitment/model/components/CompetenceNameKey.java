/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1201.recruitment.model.components;

import java.io.Serializable;
import java.util.Objects;

/**
 *  Composite primary key used to identify competence names
 * @author Perttu Jääskeläinen
 */
public class CompetenceNameKey implements Serializable {

        private Long comp;
        private String lang;

        public CompetenceNameKey(Competence comp, DbLang lang) {
            this.comp = comp.getId();
            this.lang = lang.getLanguage();
        }
        public CompetenceNameKey(Long compID, String langPK) {
            this.comp = compID;
            this.lang = langPK;
        }
        @Override
        public boolean equals(Object ob) {
            CompetenceNameKey o = (CompetenceNameKey) ob;
            return Objects.equals(o.comp, this.comp) && o.lang == this.lang;
        }
        @Override
        public int hashCode() {
            return comp.hashCode() + lang.hashCode();
        }
        public Long getCompId() {
            return this.comp;
        }
        public String getLangId() {
            return this.lang;
        }
    }
