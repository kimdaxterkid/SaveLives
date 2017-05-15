/*
 * Created by Pingxin Shang on 2017.04.26  * 
 * Copyright © 2017 Pingxin Shang. All rights reserved. * 
 */
package com.savelives.managers;

import com.mycompany.jsfclasses.util.JsfUtil;
import com.savelives.entityclasses.SearchQuery;
import com.savelives.entityclasses.User;
import com.savelives.sessionbeans.UserFacade;
import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author ping
 */
@Named(value = "historySearchController")
@SessionScoped
public class HistorySearchController implements Serializable {

    private List<SearchQuery> items = null;
    private SearchQuery selected;

    @Inject
    private UserFacade userFacade;

    @Inject
    private CrimeCaseController crimeCaseController;

    @Inject
    private PreferredSearchController preferredSearchController;

    @Inject
    private EditorView editorView;

    public HistorySearchController() {
    }

    public List<SearchQuery> getItems() {
        Map<String, Object> map = FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap();
        String userPrimaryKey = (String) map.get("user_id");
        User u = getUserFacade().findById(userPrimaryKey);
        this.items = u.getHistorySearch();

        return items;
    }

    public void setItems(List<SearchQuery> items) {
        this.items = items;
    }

    public SearchQuery getSelected() {
        return selected;
    }

    public void setSelected(SearchQuery selected) {
        this.selected = selected;

    }

    public UserFacade getUserFacade() {
        return userFacade;
    }

    public void setUserFacade(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    public void searchAgain() throws Exception {

        crimeCaseController.setDate1(selected.getFrom());
        crimeCaseController.setDate2(selected.getTo());
        crimeCaseController.setSelectedCrimeCodes(selected.getCrimeCodes());
        crimeCaseController.setSelectedCategories(selected.getCategories());
        crimeCaseController.setSelectedWeapons(selected.getWeapons());
        crimeCaseController.setSelectedNeighborhoods(selected.getNeighborhoods());
        crimeCaseController.submitWithoutAddHistory();
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("CrimeMap.xhtml");
        } catch (IOException ex) {
            Logger.getLogger(HistorySearchController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addToPreferred() {

        Map<String, Object> map = FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap();
        String userPrimaryKey = (String) map.get("user_id");
        User u = getUserFacade().findById(userPrimaryKey);

        for (int i = 0; i < u.getPreferredSearch().size(); i++) {
            if (u.getPreferredSearch().get(i).getIndex() == selected.getIndex()) {
                JsfUtil.addErrorMessage("Selected Search is already in preferred search!");
                return;
            }
        }
        u.addPreferredSearch(selected);
        System.out.println(u.toDocument());
        userFacade.edit(u);
        JsfUtil.addSuccessMessage("Successfully saved selected search to preferred search");
    }

    public void prepareEmailBody() {

        try {
            String headerUrl = "<img src=\"https://image.ibb.co/j93imQ/email_header.png\" style=\"width:1000px;\">";
            String footerUrl = "<img src=\"https://image.ibb.co/bEeZt5/email_footer.png\" style=\"width:1000px;\">";

            Map<String, Object> map = FacesContext.getCurrentInstance()
                    .getExternalContext().getSessionMap();
            String userPrimaryKey = (String) map.get("user_id");
            User u = getUserFacade().findById(userPrimaryKey);

            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            String baseUrl = request.getRequestURL().substring(0, request.getRequestURL().toString().length() - request.getServletPath().length());
            String wsUrl = baseUrl + "/webresources/search/" + u.getUsername() + "/" + selected.getIndex();
            String indexUrl = baseUrl + "/index.xhtml";

            // Compose the email content in HTML format
            String emailBodyText = "<div align=\"left\">" + headerUrl + "<br /><br /><h3>   Shared Search Query on SaveLives</h3> <br />  "
                    + u.getFirstName() + " sends you a search query on <a href=\"" + indexUrl + "\">SaveLives</a>. If you want to see the search result, please click <a href=\""
                    + wsUrl + "\">here</a> to view the search details and search the crime cases. <br /><br />   If you did not know SaveLives and the person send the search to you, please discard this email. "
                    + "Sorry for the inconvenience.<br /><br />  If you have any questions, please reply this email with questions. <br /><br /> Thank you.<br /><br />" + footerUrl + " <p>&nbsp;</p></div>";

            // Set the HTML content to be the body of the email message
            editorView.setText(emailBodyText);

            // Redirect to show the SendMail.xhtml page
            FacesContext.getCurrentInstance().getExternalContext().redirect("SendEmail.xhtml");
        } catch (UnknownHostException ex) {
            Logger.getLogger(HistorySearchController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HistorySearchController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void prepareEmailBodyForLastSearch() {

        try {
            String headerUrl = "<img src=\"https://image.ibb.co/j93imQ/email_header.png\" style=\"width:1000px;\">";
            String footerUrl = "<img src=\"https://image.ibb.co/bEeZt5/email_footer.png\" style=\"width:1000px;\">";

            Map<String, Object> map = FacesContext.getCurrentInstance()
                    .getExternalContext().getSessionMap();
            String userPrimaryKey = (String) map.get("user_id");
            User u = getUserFacade().findById(userPrimaryKey);

            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            String baseUrl = request.getRequestURL().substring(0, request.getRequestURL().toString().length() - request.getServletPath().length());
            String wsUrl = baseUrl + "/webresources/search/" + u.getUsername() + "/" + u.getHistorySearch().get(0).getIndex();
            String indexUrl = baseUrl + "/index.xhtml";

            // Compose the email content in HTML format
            String emailBodyText = "<div align=\"left\">" + headerUrl + "<br /><br /><h3>   Shared Search Query on SaveLives</h3> <br />  "
                    + u.getFirstName() + " sends you a search query on <a href=\"" + indexUrl + "\">SaveLives</a>. If you want to see the search result, please click <a href=\""
                    + wsUrl + "\">here</a> to view the search details and search the crime cases. <br /><br />   If you did not know SaveLives and the person send the search to you, please discard this email. "
                    + "Sorry for the inconvenience.<br /><br />  If you have any questions, please reply this email with questions. <br /><br /> Thank you.<br /><br />" + footerUrl + " <p>&nbsp;</p></div>";

            // Set the HTML content to be the body of the email message
            editorView.setText(emailBodyText);

            // Redirect to show the SendMail.xhtml page
            FacesContext.getCurrentInstance().getExternalContext().redirect("SendEmail.xhtml");
        } catch (UnknownHostException ex) {
            Logger.getLogger(HistorySearchController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HistorySearchController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
