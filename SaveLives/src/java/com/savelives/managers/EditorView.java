/*
 * Created by Pingxin Shang on 2017.01.01
 * Copyright © 2017 Pingxin Shang. All rights reserved.
 */
package com.savelives.managers;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/*
========================================
PrimeFaces HTML Text Editor UI Component
========================================
Adapted from http://www.primefaces.org/showcase/ui/input/editor.xhtml 

The @Named class annotation designates the bean object created in this class 
as a Contexts and Dependency Injection (CDI) named bean. The value attribute 
defines the logical name of the bean that can be used in JSF facelets pages. 
 */
@Named(value = "editorView")

/* 
The @SessionScoped annotation indicates that this CDI-managed bean will be
maintained (i.e., its property values will be kept) across multiple HTTP requests 
as long as the user's established HTTP session is active. 
 */
@SessionScoped

public class EditorView implements Serializable {

    private String text;

    // Constructor method to provide initial content
    public EditorView() {
        text = "Please enter your HTML email message here.";
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
