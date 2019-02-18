/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1201.recruitment.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import se.kth.id1201.recruitment.common.ApplicationStatus;
import se.kth.id1201.recruitment.common.UserApplication;
import se.kth.id1201.recruitment.model.Person;
import se.kth.id1201.recruitment.common.UserDTO;
import se.kth.id1201.recruitment.integration.ApplicationDAO;
import se.kth.id1201.recruitment.integration.EntityFoundException;
import se.kth.id1201.recruitment.integration.EntityNullException;
import se.kth.id1201.recruitment.model.Application;
import se.kth.id1201.recruitment.model.components.ApplicationStatusName;
import se.kth.id1201.recruitment.model.components.ApplicationStatusNameKey;
import se.kth.id1201.recruitment.model.components.DbLang;
import se.kth.id1201.recruitment.model.components.Availability;
import se.kth.id1201.recruitment.model.components.Competence;
import se.kth.id1201.recruitment.model.components.CompetenceName;
import se.kth.id1201.recruitment.model.components.CompetenceNameKey;
import se.kth.id1201.recruitment.model.components.CompetenceProfile;
import se.kth.id1201.recruitment.view.UserCompetence;

/**
 * Class for processing user actions which include transactions
 *
 * @author Perttu Jääskeläinen
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class Controller {

    @EJB
    ApplicationDAO appDAO;

    /**
     * Performs a login for a user
     *
     * @param <T> The type of class implementing the UserDTO interface
     * @param username username to login with
     * @param password password to login with
     * @return a UserDTO implementing class
     * @throws EJBException if a server side error occurs
     */
    public <T extends UserDTO> T loginUser(String username, String password) throws EJBException {
        Person person = appDAO.getEntity(Person.class, username);
        if (person == null) {
            return null;
        }
        boolean verified = Person.verifyUser(person, password);
        if (!verified) {
            person = null; // if password is incorrect, return null
        }
        return (T) person;
    }

    /**
     * Registers a new user
     *
     * @param fname users first name
     * @param lname users last name
     * @param email users email
     * @param DOB users date of birth
     * @param username username for the user
     * @param password password for the user
     * @return true if registration succeeded, else false
     * @throws EJBException if an exception occurs on the server side (database
     * error)
     */
    public boolean registerUser(String fname, String lname, String email, Date DOB, String username, String password) throws EJBException {
        Person p = new Person(fname, lname, email, DOB, username, password);
        boolean registered = appDAO.persistEntity(p);
        return registered;
    }

    /**
     * Returns a mapping between strings and database id's for competences
     *
     * @param language language to map translations to
     * @return a map containing keys as translations for each competence, where
     * the value is the ID for the converted competence
     * @throws EntityNullException if translations are unexpectedly not found in
     * the database (database error)
     * @throws EJBException if a server side transactional error occurs
     */
    public Map<String, Long> getCompetenceMap(String language) throws EntityNullException, EJBException {
        List<Competence> comps = getCompetences();
        DbLang lang = appDAO.getEntity(DbLang.class, language);
        Map<String, Long> map = new HashMap<>();
        for (Competence c : comps) {
            CompetenceName name = appDAO.getEntity(CompetenceName.class, new CompetenceNameKey(c, lang));
            map.put(name.getName(), c.getId());
        }
        return map;
    }

    /**
     * Returns a list of applications found on the server
     *
     * @return A list of applications converted to a more user-friendly object.
     * If none exist, return null
     * @throws EJBException if a server side transactional error occurs
     */
    public List<UserApplication> getAllApplications() throws EJBException {
        List<Application> list2 = appDAO.findAllApplications();
        if (list2 == null) {
            return null;
        }
        List<UserApplication> list = new ArrayList<>();
        for (Application a : list2) {
            List<Availability> availList = appDAO.findAllAvailabilities(a.getPerson());
            List<CompetenceProfile> profiles = appDAO.findAllProfiles(a.getPerson());
            list.add(new UserApplication(a.getPerson(), a, profiles, availList));
        }
        return list;
    }

    /**
     * Returns a list of all competence translations found in the database
     *
     * @param language the language to translate all competences to
     * @return a list containing all translations
     * @throws EntityNullException If the database does not contain the
     * necessary translations
     * @throws EJBException if a server side transactional error occurs
     */
    public List<String> getCompetenceTranslations(String language) throws EntityNullException, EJBException {
        return getTranslations(getCompetences(), language);
    }

    /**
     * Gets all competences from the database
     *
     * @return a list of competences retreived from the database
     * @throws EntityNullException If there are no competences found in the
     * database (database error)
     * @throws EJBException if a server side transactional error occurs
     */
    public List<Competence> getCompetences() throws EntityNullException, EJBException {
        List<Competence> list1 = appDAO.findAllCompetences();
        return list1;
    }

    /**
     * Gets a direct translation from a CompetenceName ID to a specified
     * language
     *
     * @param ID the ID to translate
     * @param language the language to translate to
     * @return String translation of the ID
     * @throws EntityNullException If the translation does not exist - should
     * not happen (database error)
     * @throws EJBException if a server side transactional error occurs
     */
    public String getTranslation(Long ID, String language) throws EntityNullException, EJBException {
        DbLang lang = appDAO.getEntity(DbLang.class, language);
        CompetenceName name = appDAO.getEntity(CompetenceName.class, new CompetenceNameKey(ID, lang.getLanguage()));
        return name.getName();
    }

    /**
     * Gets a direct translation from a competence to a specified language
     *
     * @param c the competence to translate
     * @param language the language to translate to
     * @return a String translation of the competence
     * @throws EntityNullException If the translation does not exist - should
     * not happen (database error)
     * @throws EJBException if a server side transactional error occurs
     */
    public String getTranslation(Competence c, String language) throws EntityNullException {
        return getTranslation(c.getId(), language);
    }

    /**
     * Returns a translation for a provided list of competences to a specified
     * language
     *
     * @param comps list of competences to translate
     * @param language language to translate to
     * @return a list of strings containing all translations
     * @throws EntityNullException If the translations do not exist - should not
     * happen (database error)
     * @throws EJBException if a server side transactional error occurs
     */
    public List<String> getTranslations(List<Competence> comps, String language) throws EntityNullException, EJBException {
        DbLang lang = appDAO.getEntity(DbLang.class, language);
        List<String> list = appDAO.findAllTranslations(comps, lang);
        return list;
    }

    /**
     * Gets the application for a specific user
     *
     * @param <T> The class implementing the UserDTO interface
     * @param person the user in question
     * @return The users application if it exists, else - null
     * @throws EJBException if a server side transactional error occurs
     */
    public <T extends UserDTO> UserApplication getApplication(T person) throws EJBException {
        Application app = appDAO.findApplication(person);
        if (app == null) {
            return null;
        }
        List<CompetenceProfile> profiles = this.getProfiles(person);
        List<Availability> availabilities = this.getAvailabilities(person);
        return new UserApplication(person, app, profiles, availabilities);

    }

    /**
     * Gets a list of availabilities for a specific user
     *
     * @param <T> The class implementing the UserDTO interface
     * @param person the user in question
     * @return A list of the users availabilities, if they do not exist, return
     * null
     */
    public <T extends UserDTO> List<Availability> getAvailabilities(T person) throws EJBException {
        List<Availability> list = appDAO.findAllAvailabilities(person);
        return list;
    }

    /**
     * Gets a list of competence profiles for a specific user
     *
     * @param <T> The class implementing the UserDTO interface
     * @param person the user in question
     * @return A list of competence profiles, if they do not exist, return null
     * @throws EJBException if a server side transactional error occurs
     */
    public <T extends UserDTO> List<CompetenceProfile> getProfiles(T person) throws EJBException {
        List<CompetenceProfile> list = appDAO.findAllProfiles(person);
        return list;
    }

    /**
     * Create an application for a provided user
     *
     * @param person the user to create an application for
     * @param ucomps the users competence profiles
     * @param fromDate a date the user is available from
     * @param toDate a date the user is available to
     * @param verifyPassword the users password to ensure non-repudiation
     * @return true if persistence succeeded, else false
     * @throws EntityFoundException if the user already has an application
     * registered
     * @throws EntityNullException if loading competences from the database
     * fails
     * @throws VerificationFailedException if the users credentials are invalid
     * @throws EJBException if a server side transactional error occurs
     */
    public boolean createApplication(UserDTO person, Map<Long, UserCompetence> ucomps, Date fromDate, Date toDate, String verifyPassword)
            throws EntityFoundException, EntityNullException, VerificationFailedException, EJBException {

        if (!person.getPassword().equals(verifyPassword)) {
            throw new VerificationFailedException();
        }
        List<Competence> comp = getCompetences();

        List<CompetenceProfile> profiles = new ArrayList<>();

        for (Competence c : comp) {
            UserCompetence uc = ucomps.get(c.getId());
            if (uc == null) {
                continue;
            }
            CompetenceProfile p = new CompetenceProfile((Person) person, c, uc.getExperience());
            profiles.add(p);
        }

        for (CompetenceProfile p : profiles) {
            appDAO.persistEntity(p);
        }

        Availability avail = new Availability((Person) person, fromDate, toDate);
        appDAO.persistEntity(avail);
        Application a = new Application((Person) person);
        boolean submitted = appDAO.persistApplication(a);
        return submitted;
    }

    /**
     * Changes the application status for a specified application
     *
     * @param status        The status to change to
     * @param lang          The language the status string is from
     * @param applicationId The ID of the application
     * @throws EntityNullException  if a translation for the status cannot be loaded from the database
     * @throws EJBException         if a server side transactional error occurs
     * @return                      true if the change was successful
     */
    public boolean setApplicationStatus(String status, String lang, Application applicationId) throws EntityNullException, EJBException {
        DbLang language = appDAO.getEntity(DbLang.class, lang);
        ApplicationStatusName name = appDAO.findStatusWithTranslation(status, language);
        return setApplicationStatus(name.getStatus(), applicationId);
    }

    /**
     * Changes the application status for a specified application
     *
     * @param status The status to change to
     * @param applicationId The ID of the application
     * @return             true if the change was successful
     * @throws EJBException if a server side transactional error occurs
     */
    public boolean setApplicationStatus(ApplicationStatus status, Application applicationId) throws EJBException {
        switch (status) {
            case ACCEPTED:
                return appDAO.setStatus(ApplicationStatus.ACCEPTED, applicationId);
            case REJECTED:
                return appDAO.setStatus(ApplicationStatus.REJECTED, applicationId);
            case PENDING:
            default:
                return appDAO.setStatus(ApplicationStatus.PENDING, applicationId);
        }
    }

    /**
     * Gets a translation for an application status in the language specified
     *
     * @param status the status to translate
     * @param lang the language to translate to
     * @return the string translation of the status to language 'lang'
     * @throws EntityNullException if the translation is unexpectedly not found
     * @throws EJBException if a server side transactional error occurs
     */
    public String getTranslation(ApplicationStatus status, String lang) throws EntityNullException, EJBException {
        DbLang language = appDAO.getEntity(DbLang.class, lang.toUpperCase());
        ApplicationStatusName name = appDAO.getEntity(ApplicationStatusName.class, new ApplicationStatusNameKey(language, status));
        return name.getStatusName();
    }
}
