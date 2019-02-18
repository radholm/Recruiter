/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1201.recruitment.common;

import java.util.List;
import se.kth.id1201.recruitment.model.Application;
import se.kth.id1201.recruitment.model.components.Availability;
import se.kth.id1201.recruitment.model.components.CompetenceProfile;

/**
 *  Wrapper class for information regarding a users application
 * @author Perttu Jääskeläinen
 */
public class UserApplication {
    private final UserDTO person;
    private final Application application;
    private final List<CompetenceProfile> profiles;
    private final List<Availability> availabilities;
    public UserApplication(UserDTO person, Application a, List<CompetenceProfile> profiles, List<Availability> availabilities) {
        this.person = person;
        this.application = a; 
        this.profiles = profiles; 
        this.availabilities = availabilities;
    }
    public UserDTO getPerson() {
        return this.person;
    }
    public Application getApplication() {
        return this.application;
    }
    public List<CompetenceProfile> getProfiles() {
        return this.profiles;
    }
    public List<Availability> getAvailabilities() {
        return this.availabilities;
    }
}
