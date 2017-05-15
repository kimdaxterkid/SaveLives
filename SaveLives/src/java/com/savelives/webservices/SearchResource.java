/*
 * Created by Pingxin Shang on 2017.05.02  * 
 * Copyright © 2017 Pingxin Shang. All rights reserved. * 
 */
package com.savelives.webservices;

import com.savelives.entityclasses.SearchQuery;
import com.savelives.entityclasses.User;
import com.savelives.sessionbeans.UserFacade;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author ping
 */
@Stateless
@Path("search")
public class SearchResource {

    @Context
    private UriInfo context;

    @Inject
    private UserFacade userFacade;

    @Inject
    private ConfirmSearchController confirmSearchController;

    /**
     * Creates a new instance of searchResource
     */
    public SearchResource() {
    }

    /**
     * Retrieves representation of an instance of
     * com.savelives.webservices.searchResource
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getHtml() {
        return "<html><body>hahaha</body></html>";
    }

    /**
     * Retrieves representation of an instance of
     * com.savelives.managers.SearchResource
     *
     * @return an instance of java.io.InputStream
     * @param username user id
     * @param index user search index
     *
     * /webresource/search?username=XXX&search_index=XXX
     */
    @GET
    @Path("{username}/{index}")
    @Produces(MediaType.TEXT_HTML)
    public Response getHtml(@PathParam("username") String username, @PathParam("index") int index) {

        User u = userFacade.findByUsername(username);
        if (u == null) {
            return null;
        }
        SearchQuery sq = u.getHistorySearch().get(u.getHistorySearch().size() - index - 1);
        confirmSearchController.setSelected(sq);
        try {
            java.net.URI location = new java.net.URI("../ConfirmSearch.xhtml");
            return Response.temporaryRedirect(location).build();
        } catch (URISyntaxException ex) {
            Logger.getLogger(SearchResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * PUT method for updating or creating an instance of searchResource
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.TEXT_HTML)
    public void putHtml(String content) {
    }
}
