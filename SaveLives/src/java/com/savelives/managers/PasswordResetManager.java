/*
 * Created by Cheick Berthe on 2017.02.24  * 
 * Copyright © 2017 Cheick Berthe. All rights reserved. * 
 */
package com.savelives.managers;

import com.mycompany.jsfclasses.util.JsfUtil;
import com.savelives.entityclasses.User;
import com.savelives.sessionbeans.UserFacade;
import static com.savelives.managers.AccountManager.hashPassword;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.inject.Inject;
import javax.inject.Named;

@Named(value = "passwordResetManager")
@SessionScoped
/**
 *
 * @author Berthe
 */
public class PasswordResetManager implements Serializable {

    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */
    private String username;
    private String message = "";
    private String answer;
    private String password;

    @Inject
    private AccountManager accountManager;

    @Inject
    private UserFacade userFacade;

    /*
    =========================
    Getter and Setter Methods
    =========================
     */
    public String getUsername() {
        if (username == null) {
            username = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username");
        }
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserFacade getUserFacade() {
        return userFacade;
    }

    /*
    ================
    Instance Methods
    ================
     */
    // Process the submitted username
    public String usernameSubmit() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        // Obtain the object reference of the User object with username
        User user = getUserFacade().findByUsername(username);

        if (user == null) {
            message = "Entered username does not exist!";
            JsfUtil.addErrorMessage(message);
            // Redirect to show the EnterUsername page
            return "EnterUsername?faces-redirect=true";
        } else {
            // Entered username exists
            message = "";
            // Redirect to show the SecurityQuestion page
            return "SecurityQuestion?faces-redirect=true";
        }
    }

    // Process the submitted answer to the security question
    public String securityAnswerSubmit() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        // Obtain the object reference of the User object with username
        User user = getUserFacade().findByUsername(username);

        String actualSecurityAnswer = user.getSecurityAnswer();
        String enteredSecurityAnswer = getAnswer();

        if (actualSecurityAnswer.equals(enteredSecurityAnswer)) {
            // Answer to the security question is correct
            message = "";

            // Redirect to show the ResetPassword page
            return "ResetPassword?faces-redirect=true";
        } else {
            // Answer to the security question is wrong
            message = "Security question answer is incorrect!";
            JsfUtil.addErrorMessage(message);
            // Redirect to show the SecurityQuestion page
            return "SecurityQuestion?faces-redirect=true";
        }
    }

    // Return the security question selected by the User object with username
    public String getSecurityQuestion() {

        // Obtain the object reference of the User object with username
        User user = getUserFacade().findByUsername(this.getUsername());

        // Obtain the number of the security question selected by the user
        int questionNumber = user.getSecurityQuestion();

        // Return the security question corresponding to the question number
        return Constants.QUESTIONS[questionNumber];
    }

    // Validate if the entered password matches the entered confirm password
    public void validateInformation(ComponentSystemEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        /*
        FacesContext contains all of the per-request state information related to the processing of
        a single JavaServer Faces request, and the rendering of the corresponding response.
        It is passed to, and potentially modified by, each phase of the request processing lifecycle.
         */
        FacesContext fc = FacesContext.getCurrentInstance();

        /*
        UIComponent is the base class for all user interface components in JavaServer Faces. 
        The set of UIComponent instances associated with a particular request and response are organized into
        a component tree under a UIViewRoot that represents the entire content of the request or response.
         */
        // Obtain the UIComponent instances associated with the event
        UIComponent components = event.getComponent();

        /*
        UIInput is a kind of UIComponent for the user to enter a value in.
         */
        // Obtain the object reference of the UIInput field with id="password" on the UI
        UIInput uiInputPassword = (UIInput) components.findComponent("password");

        // Obtain the password entered in the UIInput field with id="password" on the UI
        String entered_password = uiInputPassword.getLocalValue()
                == null ? "" : uiInputPassword.getLocalValue().toString();

        // Obtain the object reference of the UIInput field with id="confirmPassword" on the UI
        UIInput uiInputConfirmPassword = (UIInput) components.findComponent("confirmPassword");

        // Obtain the confirm password entered in the UIInput field with id="confirmPassword" on the UI
        String entered_confirm_password = uiInputConfirmPassword.getLocalValue()
                == null ? "" : uiInputConfirmPassword.getLocalValue().toString();

        if (entered_password.isEmpty() || entered_confirm_password.isEmpty()) {
            // Do not take any action. 
            // The required="true" in the XHTML file will catch this and produce an error message.
            return;
        }

        if (!entered_password.equals(entered_confirm_password)) {
            message = "Password and Confirm Password must match!";
            JsfUtil.addErrorMessage(message);
        } else {
            message = "";
        }
    }

    public String resetPassword() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        if (message == null || message.isEmpty()) {

            // Obtain the object reference of the User object with username
            User user = getUserFacade().findByUsername(username);

            try {
                byte[] passwordKey = hashPassword(password.toCharArray(), user.getSalt(), user.getIterations(), 256);
                user.setPasswordKey(passwordKey);
                getUserFacade().edit(user);
                accountManager.logout();

                // Initialize the instance variables
                username = message = answer = password = "";

            } catch (Exception e) {
                message = "Something went wrong while resetting your password, please try again!";
                JsfUtil.addErrorMessage(message);
                // Redirect to show the ResetPassword page
                return "ResetPassword?faces-redirect=true";
            }
            JsfUtil.addSuccessMessage("Reset Password Success.");
            // Redirect to show the index (Home) page
            return "index?faces-redirect=true";

        } else {
            // Redirect to show the ResetPassword page
            return "ResetPassword?faces-redirect=true";
        }
    }

}
