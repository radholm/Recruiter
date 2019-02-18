/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1201.recruitment.view;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import se.kth.id1201.recruitment.common.Pages;
import se.kth.id1201.recruitment.common.RoleType;
import se.kth.id1201.recruitment.common.UserDTO;

/**
 * Class for handling all navigation authorization cases when using the project website
 * @author Fredrik
 */
@ManagedBean(name = "navView", eager = true)
@RequestScoped
public class Nav implements Serializable {

    private final String LOGIN_PAGE = Pages.LOGIN_PAGE;
    private final String REGISTER_PAGE = Pages.REGISTER_PAGE;
    private final String HOME_PAGE = Pages.HOME_PAGE;
    private final String APPLICATION_PAGE = Pages.APPLICATION_PAGE;
    private final String APPLICATION_CREATE = Pages.APPLICATION_CREATE;
    private final String REDIRECT = Pages.REDIRECT;
    private final String APPLICATION_DISPLAY = Pages.APPLICATION_DISPLAY;
    private final String APPLICATION_REVIEW = Pages.APPLICATION_REVIEW;
    private static final long serialVersionUID = 1L;

    public String getLoginPage() {
        return LOGIN_PAGE;
    }

    public String getRegisterPage() {
        return REGISTER_PAGE;
    }

    public String getHomePage() {
        return HOME_PAGE;
    }

    public String getApplicationPage() {
        return APPLICATION_PAGE;
    }

    public String getApplicationCreatePage() {
        return APPLICATION_CREATE;
    }

    public String getApplicationDisplayPage() {
        return this.APPLICATION_DISPLAY;
    }

    public String getApplicationReview() {
        return this.APPLICATION_REVIEW;
    }

    public String showPage() {
        return (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("pageId") + REDIRECT;
    }

    public void redirect() {
        String previous = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("previous");
        if (previous == null) {
            redirect(HOME_PAGE);
        } else {
            redirect(previous);
        }
        
    }
    public void redirect(String page) {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(page);
            validate(page);
        } catch (IOException e) {
            // do nothing
        }
    }

    /**
     * Validate a provided page with the current associated instance
     * @param page  the page to be validated
     * @throws IOException  If an input/output error occurs
     */
    public void validate(String page) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext external = context.getExternalContext();
        UserDTO p = (UserDTO) context.getExternalContext().getSessionMap().get("user");
        switch (page) {
            case REGISTER_PAGE:
            case LOGIN_PAGE:
                if (p == null) {
                    break;
                } else {
                    external.redirect(HOME_PAGE);
                }
                break;
            case APPLICATION_PAGE:
                if (p != null && p.getRole() == RoleType.RECRUITER) {
                    external.getSessionMap().put("previous", APPLICATION_PAGE);
                    break;
                } else {
                    if (p == null) {
                        context.addMessage("response", new FacesMessage(((HttpServletRequest) external.getRequest()).getRequestURI()));
                        context.getExternalContext().getSessionMap().put("page", APPLICATION_PAGE);
                        context.getExternalContext().dispatch(LOGIN_PAGE);
                        break;
                    }
                    external.redirect(APPLICATION_CREATE);
                }
                break;
            case APPLICATION_CREATE:
                if (p == null) {
                    external.redirect(LOGIN_PAGE);
                    break;
                }
                if (p.getRole() != RoleType.APPLICANT) {
                    external.redirect(HOME_PAGE); // TODO - redirect Recruiters to listings of applications
                    break;
                }
                Object o = external.getSessionMap().get("application");
                if (o != null) {
                    external.redirect(APPLICATION_DISPLAY);
                    break;
                }
                // if the below code is run, it enters an infinite redirect loop.
                // instead, just break and it will proceed.
                //external.dispatch(APPLICATION_CREATE);
                break;
            case APPLICATION_DISPLAY:
                if (p == null) {
                    external.redirect(LOGIN_PAGE);
                    break;
                }
                if (p.getRole() != RoleType.APPLICANT) {
                    external.redirect(HOME_PAGE);
                    break;
                }
                Object o1 = external.getSessionMap().get("application");
                if (o1 == null) {
                    external.redirect(APPLICATION_CREATE);
                    break;
                }
                break;
            case APPLICATION_REVIEW:
                Map<String, Object> map = external.getSessionMap();
                String previous = (String) map.get("previous");
                UserDTO user = (UserDTO) map.get("user");
                // if the previous page was not the applications page, the user is not a recruiter 
                // or logged in, redirect to application page
                if (previous == null || user == null || !previous.equals(APPLICATION_PAGE) || user.getRole() != RoleType.RECRUITER) {
                    external.redirect(HOME_PAGE);
                }
                if (map.get("review") == null) {
                    external.redirect(HOME_PAGE);
                }
                break;
            default:
                external.redirect(HOME_PAGE + REDIRECT);
                break;
        }
    }
}
