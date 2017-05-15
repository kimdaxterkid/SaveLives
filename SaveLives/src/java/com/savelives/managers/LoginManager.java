/*
 * Created by Cheick Berthe on 2017.03.31  * 
 * Copyright © 2017 Cheick Berthe. All rights reserved. * 
 */
package com.savelives.managers;

import com.mycompany.jsfclasses.util.JsfUtil;
import com.savelives.entityclasses.User;
import static com.savelives.managers.AccountManager.hashPassword;
import com.savelives.sessionbeans.UserFacade;
import java.io.Serializable;
import java.util.Arrays;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

@Named(value = "loginManager")
@SessionScoped
/**
 *
 * @author Berthe
 */
public class LoginManager implements Serializable {

    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */
    private String username;
    private String password;
    private String errorMessage;

    @Inject
    private UserFacade userFacade;

    // Constructor method instantiating an instance of LoginManager
    public LoginManager() {
    }

    /*
    =========================
    Getter and Setter Methods
    =========================
     */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public UserFacade getUserFacade() {
        return userFacade;
    }

    public void setUserFacade(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    /*
    ================
    Instance Methods
    ================
     */
    public String createUser() {

        // Redirect to show the CreateAccount page
        return "CreateAccount.xhtml?faces-redirect=true";
    }

    public String resetPassword() {

        // Redirect to show the EnterUsername page
        return "EnterUsername.xhtml?faces-redirect=true";
    }

    /*
    Sign in the user if the entered username and password are valid
    @return "" if an error occurs; otherwise, redirect to show the Profile page
     */
    public String loginUser() {
        // Obtain the object reference of the User object from the entered username
        User user = getUserFacade().findByUsername(getUsername());

        if (user == null) {
            errorMessage = "Entered username " + getUsername() + " does not exist!";
            JsfUtil.addErrorMessage(errorMessage);

            return "";
        } else {
            if (!isCorrectPassword(user, getPassword())) {
                errorMessage = "Invalid Username or Password!";
                JsfUtil.addErrorMessage(errorMessage);
                return "";
            }

            errorMessage = "";

            // Initialize the session map with user properties of interest
            initializeSessionMap(user);

            // Redirect to show the Profile page
            return "Profile.xhtml?faces-redirect=true";
        }
    }

    /**
     * Initialize the session map with the user properties of interest, namely,
     * first_name, last_name, username, and user_id. user_id = primary key of
     * the user entity in the database
     *
     * @param user
     */
    public void initializeSessionMap(User user) {
        FacesContext.getCurrentInstance().getExternalContext().
                getSessionMap().put("first_name", user.getFirstName());
        FacesContext.getCurrentInstance().getExternalContext().
                getSessionMap().put("last_name", user.getLastName());
        FacesContext.getCurrentInstance().getExternalContext().
                getSessionMap().put("username", username);
        FacesContext.getCurrentInstance().getExternalContext().
                getSessionMap().put("user_id", user.getId());
    }

    private boolean isCorrectPassword(User user, String entered_password) {
        byte[] entered_passwordKey = hashPassword(entered_password.toCharArray(), user.getSalt(), user.getIterations(), 256);
        return Arrays.equals(entered_passwordKey, user.getPasswordKey());
    }
}
