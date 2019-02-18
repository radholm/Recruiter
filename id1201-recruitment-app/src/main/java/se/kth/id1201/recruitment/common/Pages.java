/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1201.recruitment.common;

/**
 *  Class for storing all pages used in the program
 * @author Perttu Jääskeläinen
 */
public class Pages {
    private static final String EXTENSION =".xhtml";
    public static final String LOGIN_PAGE = "login" + EXTENSION;
    public static final String REGISTER_PAGE = "register" + EXTENSION;
    public static final String HOME_PAGE = "home" + EXTENSION;
    public static final String APPLICATION_PAGE = "application" + EXTENSION;
    public static final String APPLICATION_CREATE = "applicationCreate" + EXTENSION;
    public static final String APPLICATION_DISPLAY = "applicationDisplay" + EXTENSION;
    public static final String APPLICATION_REVIEW = "reviewPage" + EXTENSION;
    public static final String REDIRECT = "?faces-redirect=true";
}
