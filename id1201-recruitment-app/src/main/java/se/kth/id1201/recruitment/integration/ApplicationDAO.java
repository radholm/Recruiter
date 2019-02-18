/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1201.recruitment.integration;

import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import se.kth.id1201.recruitment.common.ApplicationStatus;
import se.kth.id1201.recruitment.common.EntityObject;
import se.kth.id1201.recruitment.common.UserDTO;
import se.kth.id1201.recruitment.model.Application;
import se.kth.id1201.recruitment.model.components.DbLang;
import se.kth.id1201.recruitment.model.Person;
import se.kth.id1201.recruitment.model.components.ApplicationStatusName;
import se.kth.id1201.recruitment.model.components.Availability;
import se.kth.id1201.recruitment.model.components.Competence;
import se.kth.id1201.recruitment.model.components.CompetenceProfile;

/**
 * Class for persisting and fetching entities from the database
 *
 * @author Fredrik
 */
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Stateless
public class ApplicationDAO {

    @PersistenceContext
    private EntityManager em;

    /**
     * Used by test classes to perform operations directly
     * on the entity manager
     * @return  the entity manager instance
     */
    public EntityManager getEM() {
        return this.em;
    }

    /**
     * Persist a generic entity type in the database
     *
     * @param <T> The type of entity to persist
     * @param object the entity to persist
     * @return true if persistence succeeded, false if object already exists
     */
    public <T extends EntityObject> boolean persistEntity(T object) {
        Object primaryKey = object.getPrimaryKey();
        if (primaryKey != null && em.find(object.getClass(), primaryKey) != null) {
            return false;
        }
        em.persist(object);
        return true;
    }

    /**
     * Generic method for finding an entity from the database
     *
     * @param <T>           type of entity to find
     * @param cls           entity class to find of type T
     * @param primaryKey        primary key for the entity class
     * @return the T entity found with primaryKey if it exists, else null
     */
    public <T> T getEntity(Class<T> cls, Object primaryKey) {
        T obj = em.find(cls, primaryKey);
        return obj;
    }
    /**
     * Due to a flaw, applications cannot be uniquely identified
     * by the persistEntity(app) method. This method should be called instead
     * @param app   the application to persist
     * @return      true if succeeded, false if already exists
     */
    public boolean persistApplication(Application app) {
        if (findApplication(app.getPerson()) == null) {
            return persistEntity(app);
        }
        return false;
    }

    /**
     * Returns the application associated with this user
     * @param <T>   Type of person extending the UserDTO interface
     * @param p     The person in question
     * @return      The users application if it exists. Else, null.
     */
    public <T extends UserDTO> Application findApplication(T p) {
        Query q = em.createQuery("SELECT a FROM Application a WHERE a.person = :person");
        q.setParameter("person", (Person) p);
        List<Application> list = q.getResultList();
        if (list == null || list.isEmpty()) {
            return null;
        }
        return (Application) q.getResultList().get(0);
    }

   /**
    * Returns applications found in the database, up to a max of 100
    * @return   a list of applications
    */
    public List<Application> findAllApplications() {
        Query q = em.createNamedQuery("findAllApplications");
        List<Application> list = q.getResultList();
        if (list == null) {
            return null;
        }
        int max = list.size() >= 100 ? 100 : list.size();
        return list.subList(0, max);
    }
    /**
     * Loads all competence profiles found in the database
     * @return  a list of competence profiles. If none exist, return null
     */
    public List<CompetenceProfile> findAllCompetenceProfiles() {
        Query q = em.createQuery("SELECT c FROM CompetenceProfile c");
        List<CompetenceProfile> list = q.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list;
    }

   /**
    * Returns a list of all competences found in the database
    * @return   a list of competences
    * @throws EntityNullException   If no competences are found - database
    *                               should always contain competences
    */
    public List<Competence> findAllCompetences() throws EntityNullException {
        Query q = em.createNamedQuery("findAllCompetences");
        List<Competence> list = q.getResultList();
        if (list == null) {
            throw new EntityNullException("No competence profiles found in database");
        }
        return list;
    }

   /**
    * Returns a list of all translations for competences in a specified language
    * @param list       the list of competences to translate
    * @param language   the language to translate to
    * @return           a list of translations acquired from the database
    * @throws EntityNullException   if no translations exist - database error
    */
    public List<String> findAllTranslations(List<Competence> list, DbLang language) throws EntityNullException {
        Query q = em.createQuery("SELECT cn.name FROM CompetenceName AS cn WHERE cn.comp IN :inclList AND cn.lang = :language");
        q.setParameter("inclList", list);
        q.setParameter("language", language);
        List<String> list1 = q.getResultList();
        if (list1 == null || list1.isEmpty()) {
            throw new EntityNullException("No translations found - database error");
        }
        return list1;
    }

    /**
     * Finds all languages that are supported in the database
     *
     * @return  a list of strings, where the strings represent supported
     *          languages
     * @throws EntityNullException  if no languages are found - database error
     */
    public List<String> findAllLanguages() throws EntityNullException {
        Query q = em.createNamedQuery("findAllLanguages");
        List<String> list = q.getResultList();
        if (list == null) {
            throw new EntityNullException("No languages found in database");
        }
        return q.getResultList();
    }

    /**
     * Returns a list of competence profiles for a specified user
     *
     * @param <T>   the class implementing the UserDTO interface
     * @param user  the user in question
     * @return  a list of competence profiles for the user
     */
    public <T extends UserDTO> List<CompetenceProfile> findAllProfiles(T user) {
        Query q = em.createQuery("SELECT p FROM CompetenceProfile p WHERE p.personID = :person");
        q.setParameter("person", user);
        return q.getResultList();
    }

    /**
     * Returns a list of availabilities for a specified user
     *
     * @param <T> the user entity class type
     * @param user the user object
     * @return all availabilities for the specified user
     */
    public <T extends UserDTO> List<Availability> findAllAvailabilities(T user) {
        Query q = em.createQuery("SELECT p FROM Availability p WHERE p.person = :person");
        q.setParameter("person", user);
        return q.getResultList();
    }
    
    
    public ApplicationStatusName findStatusWithTranslation(String translation, DbLang language) throws EntityNullException {
        Query q = em.createQuery("SELECT asn FROM ApplicationStatusName asn WHERE asn.statusname = :statusname AND asn.language = :language");
        q.setParameter("statusname", translation);
        q.setParameter("language", language);
        ApplicationStatusName name = (ApplicationStatusName) q.getSingleResult();
        if (name == null) {
            throw new EntityNullException("Translation for language: " + language.getLanguage() + " for status: " + translation + " not found");
        }
        return name;
    }

    /**
     * Changes the status of an application
     *
     * @param applicationStatus is the enum for the status
     * @param applicationId is the id of the application in progress
     * @return true if any applications were changed
     */
    public boolean setStatus(ApplicationStatus applicationStatus, Application applicationId) {
        Query q = em.createQuery("UPDATE Application a SET a.applicationStatus = :applicationStatus WHERE a.applicationId = :applicationId");
        q.setParameter("applicationStatus", applicationStatus);
        q.setParameter("applicationId", applicationId.getPrimaryKey());
        int amount = q.executeUpdate();
        return amount > 0;
    }
}
