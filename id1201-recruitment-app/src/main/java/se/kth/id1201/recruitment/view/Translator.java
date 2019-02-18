/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1201.recruitment.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javax.faces.context.FacesContext;

/**
 *
 * @author Fredrik
 */
public class Translator implements Serializable {

    private static final long serialVersionUID = 11L;

    private ResourceBundle bundle;

    private List<Locale> localeList;
    private final Map<String, ResourceBundle> bundleMap = new HashMap<>();
    private Locale localeToAdd;

    /**
     * Generates a HashMap containing all bundles
     */
    public void createResourceBundles() {
        localeList = new ArrayList<>();
        localeList.add(FacesContext.getCurrentInstance().getApplication().getDefaultLocale());
        Iterator itr = FacesContext.getCurrentInstance().getApplication().getSupportedLocales();
        while (itr.hasNext()) {
            localeList.add((Locale) itr.next());
        }

        for (int i = 0; i < localeList.size(); i++) {
            localeToAdd = localeList.get(i);
            String defaultAppend = "se.kth.id1201.recruitment.lang.template_";
            String localeAppend = localeToAdd.toString();
            defaultAppend += localeAppend;
            bundle = ResourceBundle.getBundle(defaultAppend, localeToAdd);
            bundleMap.put(localeAppend, bundle);
        }
    }

    /**
     * Initializes ResourceBundles and calls a method to recieve translation
     *
     * @param locale the current locale
     * @param translate the string to translate
     * @return translated string
     */
    public String getLangPrint(Locale locale, String translate) {
        String translation;
        if (localeList == null) {
            createResourceBundles();
            if (translate.contains("login_success")) {
                String username = translate.replace(translate, "");
                translation = getTranslator(locale, "login_success") + username;
            } else if (translate.contains("register_success")) {
                String username = translate.replace(translate, "");
                translation = getTranslator(locale, "login_success") + username;
            } else {
                translation = getTranslator(locale, translate);
            }
        }
        if (localeList == null) {
            createResourceBundles();
            translation = getTranslator(locale, translate);
        } else {
            translation = getTranslator(locale, translate);
        }
        return translation;
    }

    /**
     * Finds the right bundle to find translated word
     *
     * @param loc the current locale
     * @param trans the string to translate (bundle word getter)
     * @return translated string
     */
    public String getTranslator(Locale loc, String trans) {
        String strLocale = loc.toString();
        ResourceBundle bnd = bundleMap.get(strLocale);
        String str = bnd.getString(trans);
        return str;
    }
}
