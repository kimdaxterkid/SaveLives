/*
 * Created by Pingxin Shang on 2016.05.21  * 
 * Copyright © 2017 Pingxin Shang. All rights reserved. * 
 */
package com.savelives.validators;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 *
 * @author ping
 */

/*
The @FacesValidator annotation registers this class with the runtime as a validator-class.
The value attribute "emailAddressesValidator" is set to be the validator-id.
A JSF XHTML page uses this id to execute the "validate" method of this class.
For example, the SendEmail.xhtml page uses <f:validator validatorId="emailAddressesValidator" /> 
to execute the "validate" method of this class.
 */
@FacesValidator("emailAddressesValidator")
public class EmailAddressesValidator implements Validator {

    // This method validates each of the comma separated email addresses entered in the CC field with no spaces
    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        /*
            ^                   # start of the regex line
            [_A-Za-z0-9-\\+]+   # can start with underscore, A to Z, a to z, 0 to 9, hyphen, and can contain one or more "+"
            (                   # start of group 1
            \\.[_A-Za-z0-9-]+   # after a dot "." it can start with underscore, A to Z, a to z, 0 to 9, or hyphen
            )*                  # end of group1. "*" designates this group as optional.
            @                   # must contain the "@" symbol
            [A-Za-z0-9-]+       # after the "@" symbol, it can start with A to Z, a to z, 0 to 9, or hyphen
            (                   # start of group 2
            \\.[A-Za-z0-9]+     # after a dot "." it can start with A to Z, a to z, or 0 to 9
            )*                  # end of group 2. "*" designates this group as optional.
            (                   # start of group 3
            \\.[A-Za-z]{2,}     # after a dot "." it can start with A to Z or a to z, with minimum length of 2
            )                   # end of group #3
            $                   # end of the regex line
         */
        // REGular EXpression (regex) to validate an email address
        String regex = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        // Type cast the input parameter "value" to ccEmailAddresses of type String
        String ccEmailAddresses = (String) value;

        if (ccEmailAddresses == null || ccEmailAddresses.isEmpty()) {
            // Do not take any action since it is optional to enter email addresses in the CC field
            return;
        }

        // Obtain a list of the CC email addresses, which must be comma separated with no spaces
        String emailsList[] = ccEmailAddresses.split(",");

        for (String email : emailsList) {

            // Validate each email address in the list
            if (!email.matches(regex)) {
                throw new ValidatorException(new FacesMessage("Please enter comma separated valid email addresses with no spaces in the CC field!"));
            }
        }
    }

}
