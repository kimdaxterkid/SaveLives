/*
 * Created by Pingxin Shang on 2017.03.13  * 
 * Copyright © 2017 Pingxin Shang. All rights reserved. * 
 */
package com.savelives.entityclasses;

import java.util.ArrayList;
import javax.xml.bind.DatatypeConverter;
import org.bson.Document;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONObject;

/*
 * @author ping
 */
public class User {

    private String id;
    private String username;
    private byte[] passwordKey;
    private byte[] salt;
    private int iterations;
    private String firstName;
    private String middleName;
    private String lastName;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String zipCode;
    private int securityQuestion;
    private String securityAnswer;
    private String email;
    private ArrayList<SearchQuery> historySearch;
    private ArrayList<SearchQuery> preferredSearch;

    public User() {
    }

    public User(String id) {
        this.id = id;
    }

    /**
     * Custom Constructor. Builds user object based on given parameter
     *
     * @param id user id
     * @param username username
     * @param passwordKey user passwordKey
     * @param firstName First Name
     * @param lastName Last Name
     * @param address1 Address Line 1
     * @param city City
     * @param state State
     * @param zipCode Zip Code
     * @param securityQuestion Security Question Number
     * @param securityAnswer Security Answer
     * @param email user email
     * @param salt Salt
     * @param iterations iteration number
     * @param historySearch history search
     * @param preferredSearch preferred search
     */
    public User(String id, String username,
            byte[] passwordKey, String firstName,
            String lastName, String address1,
            String city, String state,
            String zipCode, int securityQuestion,
            String securityAnswer, String email,
            byte[] salt, int iterations,
            ArrayList<SearchQuery> historySearch,
            ArrayList<SearchQuery> preferredSearch) {
        this.id = id;
        this.username = username;
        this.passwordKey = passwordKey;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address1 = address1;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
        this.email = email;
        this.salt = salt;
        this.iterations = iterations;
        this.historySearch = historySearch;
        this.preferredSearch = preferredSearch;
    }

    /**
     * Custom constructor. Converts given document to user object
     *
     * @param doc document
     */
    public User(Document doc) {
        this.id = doc.getObjectId("_id").toString();
        this.address1 = doc.getString("address1");
        this.address2 = doc.getString("address2");
        this.city = doc.getString("city");
        this.email = doc.getString("email");
        this.firstName = doc.getString("firstName");
        this.middleName = doc.getString("middleName");
        this.lastName = doc.getString("lastName");
        // Convert String in JSON to byte[]
        this.passwordKey = DatatypeConverter.parseBase64Binary(doc.getString("passwordKey"));
        this.securityAnswer = doc.getString("securityAnswer");
        this.securityQuestion = doc.getInteger("securityQuestion");
        this.state = doc.getString("state");
        this.username = doc.getString("username");
        this.zipCode = doc.getString("zipCode");
        this.iterations = doc.getInteger("iterations");
        this.salt = DatatypeConverter.parseBase64Binary(doc.getString("salt"));

        this.historySearch = new ArrayList<>();
        JSONObject obj = new JSONObject(doc);
        JSONArray arr = obj.getJSONArray("historySearch");
        for (int i = 0; i < arr.length(); i++) {
            this.historySearch.add(new SearchQuery(Document.parse(arr.get(i).toString())));
        }

        this.preferredSearch = new ArrayList<>();
        obj = new JSONObject(doc);
        arr = obj.getJSONArray("preferredSearch");
        for (int i = 0; i < arr.length(); i++) {
            this.preferredSearch.add(new SearchQuery(Document.parse(arr.get(i).toString())));
        }
    }

    /* Getters and Setters */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public byte[] getPasswordKey() {
        return passwordKey;
    }

    public void setPasswordKey(byte[] passwordKey) {
        this.passwordKey = passwordKey;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public int getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(int securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setZipcode(String zipcode) {
        zipCode = zipcode;
    }

    public String getZipcode() {
        return zipCode;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public ArrayList<SearchQuery> getHistorySearch() {
        return historySearch;
    }

    public void setHistorySearch(ArrayList<SearchQuery> historySearch) {
        this.historySearch = historySearch;
    }

    public ArrayList<SearchQuery> getPreferredSearch() {
        return preferredSearch;
    }

    public void setPreferredSearch(ArrayList<SearchQuery> preferredSearch) {
        this.preferredSearch = preferredSearch;
    }

    public void addHistorySearch(SearchQuery sq) {
        ArrayList<SearchQuery> temp = new ArrayList<>();
        sq.setIndex(historySearch.size());
        temp.add(sq);
        for (int i = 0; i < historySearch.size(); i++) {
            temp.add(historySearch.get(i));
        }
        this.historySearch = temp;

    }

    public void addPreferredSearch(SearchQuery sq) {
        ArrayList<SearchQuery> temp = new ArrayList<>();
        temp.add(sq);
        for (int i = 0; i < preferredSearch.size(); i++) {
            temp.add(preferredSearch.get(i));
        }
        this.preferredSearch = temp;

    }

    public Document toDocument() {
        ArrayList<Document> docs = new ArrayList<>();
        for (int i = 0; i < this.historySearch.size(); i++) {
            docs.add(historySearch.get(i).toDocument());
        }
        ArrayList<Document> docs2 = new ArrayList<>();
        for (int i = 0; i < this.preferredSearch.size(); i++) {
            docs2.add(preferredSearch.get(i).toDocument());
        }
        
        return new Document()
                .append("username", getUsername())
                .append("passwordKey", DatatypeConverter.printBase64Binary(getPasswordKey()))
                .append("firstName", getFirstName())
                .append("middleName", getMiddleName())
                .append("lastName", getLastName())
                .append("address1", getAddress1())
                .append("address2", getAddress2())
                .append("city", getCity())
                .append("state", getState())
                .append("zipCode", getZipCode())
                .append("securityQuestion", getSecurityQuestion())
                .append("securityAnswer", getSecurityAnswer())
                .append("email", getEmail())
                .append("iterations", getIterations())
                .append("salt", DatatypeConverter.printBase64Binary(getSalt()))
                .append("historySearch", docs)
                .append("preferredSearch", docs2);
        
    }
}
