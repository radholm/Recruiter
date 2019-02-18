package org.arquillian.example;

import java.io.IOException;
import java.sql.SQLException;
import se.kth.id1201.recruitment.model.components.DbLang;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import se.kth.id1201.recruitment.common.ApplicationStatus;
import se.kth.id1201.recruitment.common.RoleType;
import se.kth.id1201.recruitment.common.UserApplication;
import se.kth.id1201.recruitment.common.UserDTO;
import se.kth.id1201.recruitment.controller.Controller;
import se.kth.id1201.recruitment.controller.VerificationFailedException;
import se.kth.id1201.recruitment.integration.ApplicationDAO;
import se.kth.id1201.recruitment.integration.EntityFoundException;
import se.kth.id1201.recruitment.integration.EntityNullException;
import se.kth.id1201.recruitment.model.*;
import se.kth.id1201.recruitment.model.components.*;
import se.kth.id1201.recruitment.view.UserCompetence;
import se.kth.id1201.recruitment.view.ViewManager;

@RunWith(Arquillian.class)
public class RecruitmentPersistenceTest {

    @Deployment
    public static Archive<?> createDeployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackage(se.kth.id1201.recruitment.model.components.Availability.class.getPackage())
                .addPackage(se.kth.id1201.recruitment.model.Application.class.getPackage())
                .addClass(Controller.class)
                .addPackage(se.kth.id1201.recruitment.integration.ApplicationDAO.class.getPackage())
                .addPackage(se.kth.id1201.recruitment.view.ViewManager.class.getPackage())
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        System.out.println(war.toString(true));
        return war;
    }

    private static final String[] SUPPORTED_LANGUAGES = {
        "SE",
        "EN"
    };

    private static final Long[] SUPPORTED_COMPETENCES = {
        new Long(1),
        new Long(2)
    };

    private static final RoleType[] SUPPORTED_ROLES = RoleType.values();

    private static final String TEST_USERNAME = "test";
    private static final String TEST_PASSWORD = "test";
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";

    @Inject
    ApplicationDAO appDAO;

    @PersistenceContext
    EntityManager em;

    @Inject
    Controller contr;

    @Inject
    UserTransaction utx;

    @Before
    public void startTransaction() throws Exception {
        utx.begin();
    }

    @After
    public void commitTransaction() throws Exception {
        utx.commit();
    }

    private void printInfo(String info) {
        System.out.println("\n" + info + "\n");
    }

    // ********************
    // INTEGRATION LAYER TESTS
    // ********************
    
    private final int dbIndex = 1;
    
    @Test
    @InSequence(dbIndex)
    public void clearData() {
        printInfo("Clearing data..");
        Person p = appDAO.getEntity(Person.class, TEST_USERNAME);
        List<Availability> list = appDAO.findAllAvailabilities(p);
        EntityManager em = appDAO.getEM();
        for (Availability a : list) {
            em.remove(a);
        }
        Application app = appDAO.findApplication(p);
        em.remove(app);
        List<CompetenceProfile> profiles = appDAO.findAllProfiles(p);
        for (CompetenceProfile prof : profiles) {
            em.remove(prof);
        }
        appDAO.getEM().remove(p);
    }

    @Test
    @InSequence(dbIndex + 1)
    public void registerTest() throws EntityNullException, EntityFoundException {
        printInfo("Checking if register of user works");
        Date d = new Date();
        Person test = new Person("test", "tester", "test@example.com", d, TEST_USERNAME, TEST_PASSWORD);
        appDAO.persistEntity(test);
        Person p1 = appDAO.getEntity(Person.class, test.getUsername());
        if (p1 == null) {
            Assert.fail("Persisting of test person failed");
        }
    }

    @Test
    @InSequence(dbIndex+2)
    public void registerTestFail() throws EntityFoundException {
        printInfo("Checking if failure of registration works");
        Date d = new Date();
        Person test = new Person("test", "tester", "test@example.com", d, TEST_USERNAME, TEST_PASSWORD);
        boolean succeeded = appDAO.persistEntity(test);
        Assert.assertTrue(!succeeded);
    }

    @Test
    @InSequence(dbIndex+3)
    public void loginTest() throws EntityNullException {
        printInfo("Checking if login works");
        Person p = appDAO.getEntity(Person.class, TEST_USERNAME);
        if (p == null) {
            Assert.fail("Test person not found");
        }
        boolean verify = Person.verifyUser(p, TEST_PASSWORD);
        if (!verify) {
            Assert.fail("Login failed for test user - password incorrect");
        }
    }

    @Test
    @InSequence(dbIndex+4)
    public void logingFailTest() throws EntityNullException {
        printInfo("Checking if login failure works");
        Person p = appDAO.getEntity(Person.class, TEST_USERNAME);
        if (p == null) {
            Assert.fail();
        }
        boolean verified = Person.verifyUser(p, TEST_PASSWORD + ".");
        if (verified) {
            Assert.fail("Verification succeeded with incorrect password");
        }
    }

    @Test
    @InSequence(dbIndex+5)
    public void createApplication() throws EntityNullException {
        printInfo("Creating application");
        Person p = appDAO.getEntity(Person.class, TEST_USERNAME);
        if (p == null) {
            Assert.fail(); // error message already displayed in loginTest
        }
        Availability avail = new Availability(p, new Date(), new Date());
        // choose a random competence to persist
        Competence comp = appDAO.getEntity(Competence.class, SUPPORTED_COMPETENCES[(int) (Math.random() * SUPPORTED_COMPETENCES.length)]);
        CompetenceProfile profile = new CompetenceProfile(p, comp, Math.random() * 10);
        Application app = new Application(p);

        boolean failure = false;
        String failures = "";

        boolean availPersisted = appDAO.persistEntity(avail);
        if (!availPersisted) {
            failure = true;
            failures += "Availability failed - ";
        }
        boolean profilePersisted = appDAO.persistEntity(profile);
        if (!profilePersisted) {
            failure = true;
            failures += "Profile failed -";
        }
        boolean appPersisted = appDAO.persistEntity(app);
        if (!appPersisted) {
            failure = true;
            failures += "Application failed -";
        }
        if (failure) {
            // trigger a transaction rollback on failure
            throw new EJBException(failures);
        }

    }

    @Test
    @InSequence(dbIndex+6)
    public void shouldReturnApplication() {
        printInfo("Getting application");
        Person p = appDAO.getEntity(Person.class, TEST_USERNAME);
        Application app = appDAO.findApplication((UserDTO) p);
        if (app == null) {
            Assert.fail("Couldnt find application for test user");
        }
    }

    @Test
    @InSequence(dbIndex+7)
    public void shouldFailApplication() {
        printInfo("Should fail to create application");
        Person p = appDAO.getEntity(Person.class, TEST_USERNAME);
        Application app = new Application(p);
        boolean persisted = appDAO.persistApplication(app);
        if (persisted) {
            Assert.fail("Persistence of second application succeeded");
        }
    }

    @Test
    @InSequence(dbIndex+8)
    public void adminAccountExists() throws EntityNullException {
        Person p = appDAO.getEntity(Person.class, ADMIN_USERNAME);
        if (p == null) {
            Assert.fail("Admin account does not exist");
        }
        boolean verified = Person.verifyUser(p, ADMIN_PASSWORD);
        if (!verified) {
            Assert.fail("Admin password is incorrect");
        }
    }

    @Test
    @InSequence(dbIndex+9)
    public void acceptApplication() throws EntityNullException {
        Person p = appDAO.getEntity(Person.class, ADMIN_USERNAME);
        Person u = appDAO.getEntity(Person.class, TEST_USERNAME);
        if (p == null || u == null) {
            Assert.fail();
        }
        Application app = appDAO.findApplication((UserDTO) u);
        if (app == null) {
            Assert.fail("Application not found for test user");
        }
        boolean succesful = appDAO.setStatus(ApplicationStatus.ACCEPTED, app);
        if (!succesful) {
            Assert.fail("Application accepting failed - 0 rows updated");
        }
    }
    
    @Test
    @InSequence(dbIndex+10)
    public void readAcceptedApplication() {
        Person u = appDAO.getEntity(Person.class, TEST_USERNAME);
        if (u == null) {
            Assert.fail();
        }
        Application app = appDAO.findApplication(u);
        if (app == null) {
            Assert.fail("Application not found for test user");
        }
        ApplicationStatus status = app.getApplicationStatus();
        if (!status.equals(ApplicationStatus.ACCEPTED)) {
            Assert.fail("Status is not accepted, found: " + status.toString());
        }
    }

    @Test
    @InSequence(dbIndex+12)
    public void rejectApplication() throws EntityNullException {
        Person p = appDAO.getEntity(Person.class, ADMIN_USERNAME);
        Person u = appDAO.getEntity(Person.class, TEST_USERNAME);
        if (p == null || u == null) {
            Assert.fail(); // error will already have been displayed in earlier tests
        }
        Application app = appDAO.findApplication((UserDTO) u);
        if (app == null) {
            Assert.fail("Application not found for test user");
        }
        boolean succesful = appDAO.setStatus(ApplicationStatus.REJECTED, app);
        if (!succesful) {
            Assert.fail("Application rejection failed - 0 rows updated");
        }
    }
    @Test
    @InSequence(dbIndex+13)
    public void readRejectedApplication() {
        Person u = appDAO.getEntity(Person.class, TEST_USERNAME);
        if (u == null) {
            Assert.fail();
        }
        Application app = appDAO.findApplication(u);
        if (app == null) {
            Assert.fail("Application not found for test user");
        }
        ApplicationStatus status = app.getApplicationStatus();
        if (!status.equals(ApplicationStatus.REJECTED)) {
            Assert.fail("Status is not rejected, found: " + status.toString());
        }
    }
    
    private final int dbLastIndex = dbIndex + 13;

    // ********************
    // CONTROLLER LAYER TESTS
    // ********************
    
    private final int controllerIndex = dbLastIndex + 1;
    
    @Test
    @InSequence(controllerIndex)
    public void controllerClearData() throws EntityNullException {
        clearData();
    }

    @Test
    @InSequence(controllerIndex + 1)
    public void controllerRegisterTest() throws EntityFoundException {
        Person p = new Person("test", "test", "test@example.com", new Date(), TEST_USERNAME, TEST_PASSWORD);
        appDAO.persistEntity(p);
    }

    @Test
    @InSequence(controllerIndex + 2)
    public void controllerLoginTest() throws EntityNullException {
        printInfo("Testing controller login..");
        UserDTO dto = contr.loginUser(TEST_USERNAME, TEST_PASSWORD);
        if (dto == null) {
            throw new EntityNullException("Controller login for test user returned NULL");
        }
        Assert.assertEquals(dto.getUsername(), TEST_USERNAME);
        Assert.assertEquals(dto.getPassword(), TEST_PASSWORD);
    }

    @Test
    @InSequence(controllerIndex + 3)
    public void controllerRegisterFailTest() {
        printInfo("Testing controller register failure");
        boolean registered = contr.registerUser("admin", "admin", "admin@example.com", new Date(), ADMIN_USERNAME, ADMIN_PASSWORD);
        boolean registered1 = contr.registerUser("test", "test", "test@example.com", new Date(), TEST_USERNAME, TEST_PASSWORD);
        if (registered || registered1) {
            Assert.fail("Register succeeded for admin/test even though it should have failed");
            throw new EntityExistsException(); // throw a exception to trigger rollback
        }
    }

    @Test
    @InSequence(controllerIndex + 4)
    public void controllerCreateApplication() throws EntityNullException, EntityFoundException, VerificationFailedException {
        printInfo("Testing controller create application");
        Person p = appDAO.getEntity(Person.class, TEST_USERNAME);
        Map<String, Long> compMap = contr.getCompetenceMap(SUPPORTED_LANGUAGES[0]);
        Map<Long, UserCompetence> comps = new HashMap<>();
        for (String id : compMap.keySet()) {
            comps.put(compMap.get(id), new UserCompetence(compMap.get(id), Math.random() * 10));
        }
        boolean created = contr.createApplication(p, comps, new Date(), new Date(), TEST_PASSWORD);
        if (!created) {
            Assert.fail("creation of application for test user failed");
        }
    }

    @Test
    @InSequence(controllerIndex + 5)
    public void controllerReadApplication() throws EntityNullException {
        printInfo("Testing controller read application");
        Person p = contr.loginUser(TEST_USERNAME, TEST_PASSWORD);
        UserApplication app = contr.getApplication(p);
        if (app == null || p == null || !app.getPerson().equals(p)) {
            throw new EntityNullException("Retreiving application through controller for test user failed");
        }
    }

    @Test
    @InSequence(controllerIndex + 6)
    public void controllerReadAvailabilities() throws EntityNullException {
        printInfo("Testing controller read availabilities");
        Person p = appDAO.getEntity(Person.class, TEST_USERNAME);
        List<Availability> list = contr.getAvailabilities(p);
        if (list == null || list.isEmpty()) {
            throw new EntityNullException("Could not find any availabilities for test user");
        }
    }

    @Test
    @InSequence(controllerIndex + 7)
    public void controllerReadProfiles() throws EntityNullException {
        printInfo("Testing controller read profiles");
        Person p = appDAO.getEntity(Person.class, TEST_USERNAME);
        List<CompetenceProfile> profiles = contr.getProfiles(p);
        if (profiles == null || profiles.isEmpty()) {
            throw new EntityNullException("Could not find any competence profiles for test user");
        }
    }

    @Test
    @InSequence(controllerIndex + 8)
    public void controllerAcceptApplication() throws EntityNullException {
        printInfo("Testing controller set application status ACCEPTED");
        Person p = appDAO.getEntity(Person.class, TEST_USERNAME);
        UserApplication app = contr.getApplication(p);
        boolean successful = contr.setApplicationStatus(ApplicationStatus.ACCEPTED, app.getApplication());
        if (!successful) {
            Assert.fail("Something went wrong when changing status through the controller, for application (primary key): " + app.getApplication().getPrimaryKey());
        }
    }

    @Test
    @InSequence(controllerIndex + 9)
    public void controllerReadApplicationStatusAccepted() throws EntityNullException {
        printInfo("Testing controller read application status - expecting ACCEPTED");
        Person p = contr.loginUser(TEST_USERNAME, TEST_PASSWORD);
        UserApplication app = contr.getApplication(p);
        Assert.assertEquals("Controller read application status should be ACCEPTED, found: " + app.getApplication().toString(), ApplicationStatus.ACCEPTED, app.getApplication().getApplicationStatus());
    }

    @Test
    @InSequence(controllerIndex + 10)
    public void controllerRejectApplication() throws EntityNullException {
        printInfo("Testing controller set application status REJECTED");
        Person p = appDAO.getEntity(Person.class, TEST_USERNAME);
        UserApplication app = contr.getApplication(p);
        boolean successful = contr.setApplicationStatus(ApplicationStatus.REJECTED, app.getApplication());
        if (!successful) {
            Assert.fail("Something went wrong when changing status through the controller, for application (primary key): " + app.getApplication().getPrimaryKey());
        }
    }

    @Test
    @InSequence(controllerIndex + 11)
    public void controllerReadApplicationStatusRejected() {
        printInfo("Testing controller read application status - expecting REJECTED");
        Person p = contr.loginUser(TEST_USERNAME, TEST_PASSWORD);
        UserApplication app = contr.getApplication(p);
        Assert.assertEquals("Controller read application status should be REJECTED, found: " + app.getApplication().toString(), ApplicationStatus.REJECTED, app.getApplication().getApplicationStatus());
    }
    private final int controllerLastIndex = controllerIndex + 11;

    // ********************
    // GENERAL TESTS (NON-ORDER DEPENDANT)
    // ********************
    @Test
    public void competenceProfileIntegrity() throws EntityNullException {
        List<CompetenceProfile> profiles = appDAO.findAllCompetenceProfiles();
        for (CompetenceProfile prof : profiles) {
            if (prof.getExperience() < 0) {
                Assert.fail("Negative experience amount in profile: " + prof.getPrimaryKey());
            }
            Person p = prof.getPerson();
            Person p1 = appDAO.getEntity(Person.class, p.getUsername());
            if (!p.equals(p1)) {
                Assert.fail("User registered as owner for an application does not exist/has multiple rows in database");
            }
        }
    }
    @Test
    public void languagesExist() throws EntityNullException {
        printInfo("Checking supported languages");
        for (String lang : SUPPORTED_LANGUAGES) {
            DbLang l = appDAO.getEntity(DbLang.class, lang);
            if (l == null) {
                Assert.fail("Language '" + lang + "' does not exist");
            }
        }
    }

    @Test
    public void competencesExist() throws EntityNullException {
        printInfo("Checking supported competences");
        for (Long compID : SUPPORTED_COMPETENCES) {
            Competence c = appDAO.getEntity(Competence.class, compID);
            if (c == null) {
                Assert.fail("Competence id '" + compID + "' does not exist");
            }
        }
    }

    @Test
    public void allTranslationsExist() throws EntityNullException {
        printInfo("Checking if translations for all competences exist");
        List<String> languages = appDAO.findAllLanguages();
        List<Competence> competences = appDAO.findAllCompetences();

        for (String lang : languages) {
            for (Competence comp : competences) {
                Long compID = comp.getId();
                CompetenceName name = appDAO.getEntity(CompetenceName.class, new CompetenceNameKey(compID, lang));
                if (name == null) {
                    Assert.fail("Translation for competence id '" + compID + "' in language '" + lang + "' does not exist");
                }
            }
        }
    }

    @Test
    public void rolesExist() throws EntityNullException {
        printInfo("Checking if roles exist");
        for (RoleType type : SUPPORTED_ROLES) {
            Role role = appDAO.getEntity(Role.class, Role.getRoleID(type));
            if (role == null) {
                Assert.fail("Role for type " + type.toString() + " does not exist");
            }
        }
    }
}
