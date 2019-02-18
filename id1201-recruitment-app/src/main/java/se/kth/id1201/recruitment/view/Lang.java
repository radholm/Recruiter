/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1201.recruitment.view;

import java.io.Serializable;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;

import javax.inject.Named;

/**
 * Class for storing a users chosen language during a session
 *
 * @author Fredrik
 */
@Named("language")
@SessionScoped
public class Lang implements Serializable {

    private Locale locale;

    public static String getUserLang(FacesContext context) {
        return context.getViewRoot().getLocale().getLanguage();
    }

    public static Locale getUserLocale(FacesContext context) {
        return context.getViewRoot().getLocale();
    }

    @PostConstruct
    public void init() {
        locale = FacesContext.getCurrentInstance().getApplication().getDefaultLocale();
    }

    public Locale getLocale() {
        return locale;
    }

    public String getLanguage() {
        return locale.getLanguage();
    }

    public void updateLang() {
        FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
    }

    public void setLanguage(String language) {
        locale = new Locale(language);
        FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
    }
}
