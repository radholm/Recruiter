/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1201.recruitment.view;

import com.itextpdf.text.DocumentException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import se.kth.id1201.recruitment.common.Pages;
import se.kth.id1201.recruitment.common.UserApplication;
import se.kth.id1201.recruitment.controller.Controller;
import se.kth.id1201.recruitment.common.UserDTO;
import se.kth.id1201.recruitment.integration.EntityFoundException;
import se.kth.id1201.recruitment.integration.EntityNullException;
import se.kth.id1201.recruitment.common.ApplicationStatus;
import se.kth.id1201.recruitment.common.PdfCreator;
import se.kth.id1201.recruitment.common.PdfObject;
import se.kth.id1201.recruitment.common.RoleType;
import se.kth.id1201.recruitment.common.TranslatedCompetence;
import se.kth.id1201.recruitment.controller.VerificationFailedException;
import se.kth.id1201.recruitment.model.Application;
import se.kth.id1201.recruitment.model.components.CompetenceProfile;

/**
 * Session scoped class for storing all information that is important to a users
 * session
 *
 * @author Fredrik
 */
@Named("manager")
@SessionScoped
public class ViewManager implements Serializable {

    @EJB
    Controller contr;

    @Inject
    Nav nav;

    // handler and logger objects, used to log exceptions
    private Handler handler = null;
    private Logger logger = null;

    /**
     * The users UserDTO object
     */
    private UserDTO person;
    private Translator translator = new Translator();
    private boolean loggedIn;
    /**
     * ID for default server response location in the .xhtml files
     */
    private final String DEFAULT_RESPONSE_ID = "response";
    /**
     * ID for default success response location in .xhtml files
     */
    private final String SUCCESS_RESPONSE_ID = "success";
    /**
     * for default danger response location in .xhtml files
     */
    private final String DANGER_RESPONSE_ID = "danger";
    private Calendar date;
    private Calendar fromDate;
    private Calendar toDate;
    /**
     * This users application, if such an application exists
     */
    private UserFriendlyApplication app;

    /**
     * List for storing loaded applications from server side
     */
    private List<UserFriendlyApplication> applist;
    /**
     * Map for storing mappings from Key language to a value of List of strings,
     * where all converted competence translations are located
     */
    private Map<String, List<String>> complist;
    /**
     * Object for storing mappings from competence ID values to objects the user
     * has submitted
     */
    private final Map<Long, UserCompetence> compObj = new HashMap<>();
    /**
     * Map for storing translations from translated competences to their
     * original ID's, used to submit values to the database
     */
    private Map<String, Long> compMap = new HashMap<>();

    //The currently selected competence
    private String selectedComp;

    /**
     * Set the selected competence to input s
     *
     * @param s currently selected competence
     */
    public void setSelectedComp(String s) {
        this.selectedComp = s;
    }

    /**
     * Set the current from date for creating application availabilites
     *
     * @param d from date to set
     */
    public void setFromDate(Date d) {
        this.fromDate = new GregorianCalendar();
        this.fromDate.setTime(d);
    }

    /**
     * Set the current to date for creating application availabilities
     *
     * @param d the to date to set
     */
    public void setToDate(Date d) {
        this.toDate = new GregorianCalendar();
        this.toDate.setTime(d);
    }

    /**
     * Gets the users application, if such an application exists
     *
     * @return an Application object, containing the users submitted application
     * details. If no such object exists, return null
     */
    public UserFriendlyApplication getApp() {
        return this.app;
    }

    /**
     * Gets the users currently selected availability from-date
     *
     * @return the currently submitted availability from-date
     */
    public Date getFromDate() {
        Calendar c = this.fromDate;
        if (c != null) {
            return c.getTime();
        }
        return null;
    }

    /**
     * Gets the users currently submitted availability to-date
     *
     * @return the currently submitted availability to-date
     */
    public Date getToDate() {
        Calendar c = this.toDate;
        if (c != null) {
            return c.getTime();
        }
        return null;
    }

    /**
     * Sets and stores the users DOB when registering
     *
     * @param d the users date of birth
     */
    public void setDate(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        this.date = c;
    }

    /**
     * Getter for the register form - should always return null, to keep the
     * field empty with the placeholder
     *
     * @return always null
     */
    public Date getDate() {
        return null;
    }

    /**
     * Returns the currently selected competence
     *
     * @return the currently selected competence value
     */
    public String getSelectedComp() {
        return this.selectedComp;
    }

    /**
     * Used to verify if a user is currently logged in
     *
     * @return true if there is a UserDTO object associated with the session
     */
    public boolean isloggedIn() {
        if (person == null) {
            this.person = (UserDTO) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("user");
        }
        return person != null;
    }

    /**
     * Gets a list of all competences in the currently selected language
     *
     * @return a list of all selectable competences
     */
    public List<String> getCompList() {
        if (complist == null) {
            initLang();
        }
        if (complist == null) {
            return null;
        }
        String l = getLang();
        List<String> l1 = complist.get(l);
        if (l1 != null) {
            return l1;
        }
        initLang();
        complist.get(l).removeAll(this.compObj.keySet());
        return complist.get(l);
    }

    /**
     * Initialization method for a logger
     *
     * @param e exception to log
     * @param defaultlang language to log messages in
     * @param reason the reason for the exception being thrown
     */
    private void initLogger(Exception e, String reason) {
        try {
            Date d = new Date();
            File f = new File("Logging" + shortDate(d) + ".txt");
            OutputStream out = new FileOutputStream(f);
            this.handler = new StreamHandler(out, new SimpleFormatter());
            this.logger = Logger.getAnonymousLogger();
            this.logger.addHandler(handler);
            logger.log(new LogRecord(Level.WARNING, reason + "----" + e.getMessage()));
            this.handler.flush();
            sendMessage("Logger: " + f.getAbsolutePath(), DANGER_RESPONSE_ID); // for testing
        } catch (IOException ex) {
            sendMessage("logger_failed", DANGER_RESPONSE_ID);
        }
    }

    /**
     * Logs a message and exception
     *
     * @param e exception to log
     * @param msg message to log
     */
    private void log(Exception e, String msg) {
        if (this.logger == null) {
            initLogger(e, msg);
            return;
        }
        logger.log(new LogRecord(Level.WARNING, msg + "----" + e.getMessage()));
        handler.flush();
    }

    /**
     * Initiates a language, loading translations and necessary data from the
     * database
     */
    public void initLang() {
        try {
            String l = getLang();
            Map<String, Long> map = contr.getCompetenceMap(getLang());
            if (this.compMap == null) {
                this.compMap = map;
            } else {
                this.compMap.putAll(map);
            }
            if (complist == null) {
                complist = new HashMap<>();
            }
            Set<String> keep = new HashSet<>();
            for (String s : map.keySet()) {
                if (!this.compObj.containsKey(map.get(s))) {
                    keep.add(s);
                }
            }
            complist.put(l, new ArrayList<>(keep));
        } catch (EntityNullException e) {
            if (this.handler == null) {
                initLogger(e, "language_failed");
                sendMessage("language_failed", DANGER_RESPONSE_ID);
            } else {
                log(e, "language_failed");
                sendMessage("language_failed", DANGER_RESPONSE_ID);
            }
        } catch (EJBException e) {
            ejbErrorMessage();
            log(e.getCausedByException(), "EJB Exception thrown from controller");
            sendMessage("ejb_exception", DANGER_RESPONSE_ID);
        }
    }

    /**
     * Loads all applications found in the database
     *
     * @return a list of all applications found in the database
     */
    public List<UserFriendlyApplication> getAllApps() {
        try {
            List<UserApplication> listOfApplications = contr.getAllApplications();
            List<UserFriendlyApplication> apps = new ArrayList<>();
            for (UserApplication application : listOfApplications) {
                apps.add(new UserFriendlyApplication(application, getListOfCompetences(application)));
            }
            applist = apps;
            return applist;
        } catch (EJBException e) {
            ejbErrorMessage();
            log(e.getCausedByException(), "EJB Exception thrown from controller");
        }
        return null;
    }

    /**
     * Changes the status for an application
     *
     * @param statusObj the status to change to
     * @param applicationId the application to change status for
     * @param username the username for the user submitting the change, to
     * achieve non-repudiation. Has to be a recruiter
     * @param password the password ^
     */
    public void setApplicationStatus(String statusObj, Application applicationId, String username, String password) {
        if (username == null || password == null || username.length() == 0 || password.length() == 0) {
            sendMessage("credentials_empty", DANGER_RESPONSE_ID);
            return;
        }
        if (this.person != null
                && this.person.getUsername().equals(username)
                && this.person.getPassword().equals(password)
                && this.person.getRole() == RoleType.RECRUITER) {
            String status = statusObj;
            try {
                contr.setApplicationStatus(status, getLang(), applicationId);
            } catch (EntityNullException e) {
                // send message to user explaining the error
                log(e, "exception thrown when changing status to: " + status + " in language: " + getLang());
                return;
            }
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("previous", Pages.APPLICATION_PAGE);
        } else {
            sendMessage("credentials_unauthorized", DANGER_RESPONSE_ID);
        }
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        sendMessage("application_review_success", SUCCESS_RESPONSE_ID);
        nav.redirect();
    }

    /**
     * Gets a list of user competences submitted so far in the application
     *
     * @return a list of user competences, see UserCompetence object for details
     */
    public List<UserCompetence> getUserCompList() {
        Collection<UserCompetence> c = this.compObj.values();
        List<UserCompetence> l = new ArrayList<>(c);
        return l;
    }

    /**
     * Converts a competence ID to a translated string value
     *
     * @param ID ID value to convert
     * @return a converted string value in the currently chosen language
     */
    public String convertId(Long ID) {
        try {
            String s = contr.getTranslation(ID, getLang());
            return s;
        } catch (EntityNullException e) {
            log(e, "exception thrown when getting translation for " + ID + " in language " + getLang());
            sendMessage("competence_convert_failed", DANGER_RESPONSE_ID);
            return null;
        }
    }

    /**
     * Convert an applications status to a string
     *
     * @param status the status to convert
     * @return a string representation of the status
     */
    public String convert(ApplicationStatus status) {
        try {
            String s = contr.getTranslation(status, getLang());
            return s;
        } catch (EntityNullException e) {
            if (this.handler == null) {
                initLogger(e, "status_convert_failed");
                sendMessage("status_convert_failed", DANGER_RESPONSE_ID);
            }
        }
        return null;
    }

    /**
     * Returns the UserDTO object associated with the session, if such an object
     * exists
     *
     * @return an UserDTO object if such exists, else, returns null
     */
    public UserDTO getPerson() {
        return this.person;
    }

    /**
     * Returns the currently logged in users first name
     *
     * @return the users first name
     */
    public String getName() {
        return person.getFname();
    }

    /**
     * Returns the currently logged in users username
     *
     * @return the users username
     */
    public String getUsername() {
        return person.getUsername();
    }

    /**
     * Returns the currently logged in users first name
     *
     * @return the users first name
     */
    public String getFname() {
        return person.getFname().toUpperCase();
    }

    /**
     * Returns the currently logged in users last name
     *
     * @return the users last name
     */
    public String getLname() {
        return person.getLname().toUpperCase();
    }

    /**
     * Returns the currently logged in users email
     *
     * @return the users email
     */
    public String getEmail() {
        return person.getEmail();
    }

    /**
     * Returns the users date of birth
     *
     * @return the users date of birth
     */
    public String getDob() {
        Format formatter = new SimpleDateFormat("yyyy-MM-dd");
        String formatted = formatter.format(person.getDob());
        return formatted;
    }

    /**
     * Returns the language associated with the session
     *
     * @return the language currently used in the session
     */
    private String getLang() {
        return Lang.getUserLang(FacesContext.getCurrentInstance()).toUpperCase();
    }

    /**
     * Returns the language associated with the session
     *
     * @return the language currently used in the session
     */
    private Locale getLocale() {
        return Lang.getUserLocale(FacesContext.getCurrentInstance());
    }

    /**
     * Gets the application associated with the current user and stores in
     * inside the external session map, if it exists
     */
    private void getApplication() {
        if (this.person == null) {
            return;
        }
        UserApplication appl = contr.getApplication(this.person);
        if (appl == null) {
            return;
        }
        List<UserCompetence> profiles = getListOfCompetences(appl);
        this.app = new UserFriendlyApplication(appl, profiles);
        if (this.app != null) {
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext external = context.getExternalContext();
            external.getSessionMap().put("application", this.app);
        }
    }

    /**
     * Send default error message to the user. Should be called when an EJB
     * exception has occurred
     */
    private void ejbErrorMessage() {
        sendMessage("serverside_error", DANGER_RESPONSE_ID);
    }

    /**
     * Sends the user a specified error message
     *
     * @param msg the message to send to the user
     */
    private void ejbErrorMessage(String msg) {
        sendMessage(msg, DANGER_RESPONSE_ID);
    }

    /**
     * Called when the user wants to login
     *
     * @param A the username which the user wants to login with
     * @param B the password for the username
     */
    public void login(Object A, Object B) {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext external = context.getExternalContext();
        String a = (String) A;
        String b = (String) B;
        if (a == null || b == null) {
            sendMessage("credentials_required", DANGER_RESPONSE_ID);
            return;
        }
        try {
            person = contr.loginUser(a, b);
        } catch (EJBException e) {
            ejbErrorMessage();
            log(e, null);
            return;
        }

        if (person != null) {
            external.getSessionMap().put("user", person);
        } else {
            sendMessage("login_failed", DANGER_RESPONSE_ID);
            return;
        }
        getApplication();
        String s = (String) external.getSessionMap().get("page");
        try {
            if (s != null) {
                external.redirect(s);
                external.getSessionMap().remove("page");
            } else {
                external.getFlash().setKeepMessages(true);
                sendMessage("login_success" + a, SUCCESS_RESPONSE_ID);
                external.redirect(Pages.HOME_PAGE);
            }
        } catch (IOException e) {
            log(e, "redirect_error");
            sendMessage("redirect_error", DANGER_RESPONSE_ID);
        } catch (EJBException e) {
            ejbErrorMessage();
            log(e.getCausedByException(), "ejb_exception");
        }
    }

    /**
     * Registers a user
     *
     * @param username username to register
     * @param fname first name of user
     * @param lname surname of user
     * @param email email of user
     * @param pass password for user
     */
    public void register(String username, String fname, String lname, String email, String pass) {
        try {
            boolean registered = contr.registerUser(fname, lname, email, this.date.getTime(), username, pass);
            if (!registered) {
                sendMessage("username_taken", DANGER_RESPONSE_ID);
            } else {
                sendMessage("register_success" + username, SUCCESS_RESPONSE_ID);
                FacesContext context = FacesContext.getCurrentInstance();
                ExternalContext external = context.getExternalContext();
                external.getFlash().setKeepMessages(true);
                external.redirect(Pages.LOGIN_PAGE);
            }
        } catch (EJBException e) {
            ejbErrorMessage();
            log(e.getCausedByException(), "EJB Exception thrown from controller during register");
        } catch (IOException e) {
            log(e, "redirect_error");
            sendMessage("redirect_error", DANGER_RESPONSE_ID);
        }
    }

    /**
     * Sends a message to the user
     *
     * @param msg message to send
     * @param msgType type of message, should be one server response types
     */
    public void sendMessage(String msg, String msgType) {
        String tMsg = translator.getLangPrint(getLocale(), msg);
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage fm = new FacesMessage(tMsg);
        context.addMessage(msgType, fm);
    }

    /**
     * Called to invalidate the users faces session when logging out
     */
    public void logout() {
        try {
            ExternalContext external = FacesContext.getCurrentInstance().getExternalContext();
            external.redirect(Pages.HOME_PAGE);
            external.invalidateSession();
        } catch (IOException e) {
            log(e, "redirect_error");
            sendMessage("redirect_error", DANGER_RESPONSE_ID);
        }
    }

    /**
     * Reviews an application, redirecting the user to the correct site
     *
     * @param app application to review
     */
    public void review(UserFriendlyApplication app) {
        try {
            ExternalContext external = FacesContext.getCurrentInstance().getExternalContext();
            external.getSessionMap().put("previous", Pages.APPLICATION_PAGE);
            external.getSessionMap().put("review", app);
            this.app = app;
            external.redirect(Pages.APPLICATION_REVIEW);
        } catch (IOException e) {
            log(e, "redirect_error");
            sendMessage("redirect_error", DANGER_RESPONSE_ID);
        }
    }

    /**
     * Called when the user wants to navigate to the PDF page
     */
    public void toPdf() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext external = context.getExternalContext();
            external.redirect("showPdf.xhtml");
        } catch (IOException e) {

        }
    }

    /**
     * Method called when loading an application to be viewed in PDF mode The
     * application loaded is the one that is automatically loaded in this
     * instance, done when clicking on 'Review' button in application.xhtml page
     */
    public void getPdf() {
        if (this.person == null || this.person.getRole() != RoleType.RECRUITER) {
            return;
        }
        try {
            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            response.reset();
            if (this.compMap == null || this.compMap.isEmpty()) {
                initLang();
            }
            String lang = getLang();
            List<TranslatedCompetence> translated = new ArrayList<>();
            for (UserCompetence uc : this.app.getProfiles()) {
                String translation = contr.getTranslation(uc.getCompetence(), lang);
                translated.add(new TranslatedCompetence(translation, uc.getExperience()));
            }
            String status = contr.getTranslation(this.app.getApplication().getApplicationStatus(), lang);

            PdfObject translatedApp = new PdfObject(this.app.getPerson(), translated, this.app.getAvailabilities(), status);
            PdfCreator.print(response, translatedApp, this.lang.getResourceBundle());
            FacesContext.getCurrentInstance().responseComplete();

        } catch (IOException | DocumentException e) {
            log(e, "error when creating pdf for application: " + this.app);
            try {
                FacesContext context = FacesContext.getCurrentInstance();
                ExternalContext external = context.getExternalContext();
                external.getFlash().setKeepMessages(true);
                sendMessage("error when creating pdf for application", DANGER_RESPONSE_ID);
                external.redirect(Pages.APPLICATION_REVIEW);
            } catch (IOException ex) {
                sendMessage("error when redirecting", DANGER_RESPONSE_ID);
            }
        } catch (EntityNullException ex) {
            log(ex, "error when loading competences or application status");
            // send error message
        }
    }

    /**
     * Adds the selected competence to the list of competences
     *
     * @param experience years of experience in the selected competence
     */
    public void addCompetence(double experience) {
        String selectedComp = this.selectedComp;
        if (selectedComp == null || selectedComp.length() == 0) {
            // if trying to add the last element of the list
            return;
        }
        Long compID = this.compMap.get(selectedComp);
        this.compObj.put(compID, new UserCompetence(compID, experience));
        this.complist.get(getLang()).remove(selectedComp);
    }

    /**
     * Removes a competence from the list of competences
     *
     * @param competence competence to remove
     */
    public void removeCompetence(String competence) {
        Long compID = this.compMap.get(competence);
        this.compObj.remove(compID);
        List<String> list = this.complist.get(getLang());
        if (list.contains(competence)) {
            return;
        }
        list.add(competence);
    }

    /**
     * Submits a users submitted application with details, provided that the
     * user has entered the correct password
     *
     * @param pass the users password, used for verification
     */
    public void submitApplication(String pass) {
        try {
            boolean verified = this.person.getPassword().equals(pass);
            if (!verified) {
                sendMessage("password_incorrect", DANGER_RESPONSE_ID);
                return;
            }
            boolean correctDates = this.fromDate.before(toDate);
            if (!correctDates) {
                sendMessage("dates_incorrect", DANGER_RESPONSE_ID);
                return;
            }
            Map<Long, UserCompetence> comps = this.compObj;
            try {
                contr.createApplication(person, comps, this.fromDate.getTime(), this.toDate.getTime(), pass);
                getApplication();
            } catch (EntityFoundException | EntityNullException e) {
                getApplication();
                if (this.app == null) {
                    sendMessage("application_create_failed", DANGER_RESPONSE_ID);
                    log(e, "application_create_failed");
                }
            } catch (VerificationFailedException e) {
                sendMessage("verification_failed", DANGER_RESPONSE_ID);
            } catch (EJBException e) {
                ejbErrorMessage();
                log(e.getCausedByException(), "EJB Exception thrown from controller");
                sendMessage("ejb_exception", DANGER_RESPONSE_ID);
            }
            nav.validate(Pages.APPLICATION_PAGE);
        } catch (IOException e) {
            log(e, "redirect_error");
            sendMessage("redirect_error", DANGER_RESPONSE_ID);
        }
    }

    /**
     * Loads all competence profiles from the database and converts them to
     * UserCompetence's, used in .xhtml files
     *
     * @param app the application to get competence profiles for
     * @return a list of UserCompetences
     */
    public List<UserCompetence> getListOfCompetences(UserApplication app) {
        List<CompetenceProfile> competences = app.getProfiles();
        List<UserCompetence> userCompetences = new ArrayList<>();
        for (CompetenceProfile c : competences) {;
            userCompetences.add(new UserCompetence(c.getCompetence().getId(), c.getExperience()));
        }
        return userCompetences;
    }
}
