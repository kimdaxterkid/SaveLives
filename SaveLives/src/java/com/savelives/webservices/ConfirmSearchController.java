/*
 * Created by Pingxin Shang on 2017.05.02  * 
 * Copyright © 2017 Pingxin Shang. All rights reserved. * 
 */
package com.savelives.webservices;

import com.savelives.entityclasses.SearchQuery;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author ping
 */
@Named(value = "comfirmSearchController")
@SessionScoped
public class ConfirmSearchController implements Serializable {

    private SearchQuery selected;

    public ConfirmSearchController() {
    }

    public SearchQuery getSelected() {
        return selected;
    }

    public void setSelected(SearchQuery selected) {
        this.selected = selected;
    }

}
