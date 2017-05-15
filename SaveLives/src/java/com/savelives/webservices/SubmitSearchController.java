/*
 * Created by Pingxin Shang on 2017.05.02  * 
 * Copyright © 2017 Pingxin Shang. All rights reserved. * 
 */
package com.savelives.webservices;

import com.savelives.entityclasses.SearchQuery;
import com.savelives.managers.CrimeCaseController;
import com.savelives.managers.HistorySearchController;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author ping
 *
 *
 */
@Named(value = "submitSearchController")
@SessionScoped
public class SubmitSearchController implements Serializable {

    @Inject
    private CrimeCaseController crimeCaseController;

    @Inject
    private ConfirmSearchController confirmSearchController;

    public SubmitSearchController() {

    }

    public void search() {
        SearchQuery selected = confirmSearchController.getSelected();
        crimeCaseController.setDate1(selected.getFrom());
        crimeCaseController.setDate2(selected.getTo());
        crimeCaseController.setSelectedCrimeCodes(selected.getCrimeCodes());
        crimeCaseController.setSelectedCategories(selected.getCategories());
        crimeCaseController.setSelectedWeapons(selected.getWeapons());
        crimeCaseController.setSelectedNeighborhoods(selected.getNeighborhoods());
        try {
            crimeCaseController.submit();
            FacesContext.getCurrentInstance().getExternalContext().redirect("CrimeMap.xhtml");
        } catch (Exception ex) {
            Logger.getLogger(HistorySearchController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
