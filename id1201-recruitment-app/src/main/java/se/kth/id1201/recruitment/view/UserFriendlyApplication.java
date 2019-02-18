/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1201.recruitment.view;

import java.util.Arrays;
import java.util.List;
import se.kth.id1201.recruitment.common.UserApplication;
import se.kth.id1201.recruitment.common.UserDTO;
import se.kth.id1201.recruitment.model.Application;
import se.kth.id1201.recruitment.model.components.Availability;

/**
 *  More easily readable application, used by .xhtml files 
 * to display information about an application
 * @author Perttu Jääskeläinen
 */
public class UserFriendlyApplication {
    private final UserDTO person;
    private final Application application;
    private final List<UserCompetence> profiles;
    private final List<Availability> availabilities;
    
    public UserFriendlyApplication(UserDTO person, Application app, List<UserCompetence> profiles, List<Availability> avail) {
        this.application = app;
        this.person = person;
        this.profiles = profiles;
        this.availabilities = avail;
    }
    public UserFriendlyApplication(UserApplication app, List<UserCompetence> profiles) {
        this.person = app.getPerson();
        this.application = app.getApplication();
        this.availabilities = app.getAvailabilities();
        this.profiles = profiles;
    }
    public UserDTO getPerson() {
        return this.person;
    }
    public Application getApplication() {
        return this.application;
    }
    public List<UserCompetence> getProfiles() {
        return this.profiles;
    }
    public List<Availability> getAvailabilities() {
        return this.availabilities;
    }
    public String getProfilesAsString() {
        return Arrays.toString(this.profiles.toArray());
    }
}
