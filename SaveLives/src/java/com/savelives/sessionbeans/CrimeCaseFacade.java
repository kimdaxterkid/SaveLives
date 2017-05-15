/*
 * Created by Cheick Berthe on 2017.04.14  * 
 * Copyright © 2017 Cheick Berthe. All rights reserved. * 
 */
package com.savelives.sessionbeans;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mycompany.jsfclasses.util.JsfUtil;
import com.savelives.entityclasses.CrimeCase;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.MapModel;
import org.joda.time.DateTime;
import org.joda.time.Interval;

@SessionScoped
/**
 *
 * @author Berthe
 */
public class CrimeCaseFacade implements Serializable  {

	private static final Logger LOGGER = Logger.getLogger(CrimeCaseFacade.class.getName());
	private static final String COLLECTION_NAME = "CrimeCases";
	private static final Client CLIENT = new Client(COLLECTION_NAME);

	/**
	 * Get a number of crimes from the database
	 *
	 * @param NumbOfCrimes number of crimes to get
	 * @return list of crimes as map model
	 */
	public MapModel getCrimesModel(int NumbOfCrimes) {

		MapModel crimes = new DefaultMapModel();
		MongoCollection<Document> collection = CLIENT.getCollection();

		if (collection.count() == 0) {
			try {
				this.populateCollection(collection);
			} catch (IOException ex) {
				Logger.getLogger(CrimeCaseFacade.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		collection.find().limit(NumbOfCrimes).forEach(new Consumer<Document>() {
			@Override
			public void accept(Document doc) {
				if (doc.containsKey("coorX") && doc.containsKey("coorY")) {
					try {
						crimes.addOverlay(new CrimeCase(doc));
					} catch (ParseException ex) {
						LOGGER.log(Level.SEVERE, null, ex);
					}
				}
			}
		});

		return crimes;
	}

	public long getCount(Date from, Date to, List<String> crimeCodes, List<String> categories,
			List<String> weapons, List<String> neighborhoods, String attribute, String value) {
	  
		MongoCollection<Document> collection = CLIENT.getCollection();

		BasicDBList andQueryList = new BasicDBList();
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");

		String fromDate = formater.format(from) + "T00:00:00.000";
		String toDate = formater.format(to) + "T23:59:59.999";

		BasicDBObject dateQuery1 = new BasicDBObject("crimedate", new BasicDBObject("$gte", fromDate));
		BasicDBObject dateQuery2 = new BasicDBObject("crimedate", new BasicDBObject("$lte", toDate));

		andQueryList.add(dateQuery1);
		andQueryList.add(dateQuery2);

		// CRIME CODES
		if (crimeCodes != null && !crimeCodes.isEmpty()) {

			BasicDBList dblist = new BasicDBList();
			dblist.addAll(crimeCodes);
			BasicDBObject codeQuery = new BasicDBObject("crimecode", new BasicDBObject("$in", dblist));
			andQueryList.add(codeQuery);
		}

		//CRIME CATEGORIES
		if (categories != null && !categories.isEmpty()) {

			BasicDBList dblist = new BasicDBList();
			dblist.addAll(categories);
			BasicDBObject descriptionQuery = new BasicDBObject("description", new BasicDBObject("$in", dblist));
			andQueryList.add(descriptionQuery);
		}

		//CRIME WEAPONS
		if (weapons != null && !weapons.isEmpty()) {
			BasicDBList dblist = new BasicDBList();
			dblist.addAll(weapons);
			BasicDBObject weaponQuery = new BasicDBObject("weapon", new BasicDBObject("$in", dblist));
			andQueryList.add(weaponQuery);
		}

		//CRIME NEIGHBORHOOD
		if (neighborhoods != null && !neighborhoods.isEmpty()) {
			BasicDBList dblist = new BasicDBList();
			dblist.addAll(neighborhoods);
			BasicDBObject neighborhoodQuery = new BasicDBObject("neighborhood", new BasicDBObject("$in", dblist));
			andQueryList.add(neighborhoodQuery);
		}
		andQueryList.add(new BasicDBObject(attribute, value));
		
		BasicDBObject queryObject = new BasicDBObject("$and", andQueryList);

		return collection.count((Bson)queryObject);

	}

	public MapModel filterCrimes(Date from, Date to, List<String> crimeCodes, List<String> categories, List<String> weapons, List<String> neighborhoods) {
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);
		MapModel crimes = new DefaultMapModel();

		if (!validateDates(from, to)) {
			return crimes;
		}

		MongoCollection<Document> collection = CLIENT.getCollection();

		BasicDBList andQueryList = new BasicDBList();
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");

		String fromDate = formater.format(from) + "T00:00:00.000";
		String toDate = formater.format(to) + "T23:59:59.999";

		BasicDBObject dateQuery1 = new BasicDBObject("crimedate", new BasicDBObject("$gte", fromDate));
		BasicDBObject dateQuery2 = new BasicDBObject("crimedate", new BasicDBObject("$lte", toDate));

		andQueryList.add(dateQuery1);
		andQueryList.add(dateQuery2);

		// CRIME CODES
		if (crimeCodes != null && !crimeCodes.isEmpty()) {

			BasicDBList dblist = new BasicDBList();
			dblist.addAll(crimeCodes);
			BasicDBObject codeQuery = new BasicDBObject("crimecode", new BasicDBObject("$in", dblist));
			andQueryList.add(codeQuery);
		}

		//CRIME CATEGORIES
		if (categories != null && !categories.isEmpty()) {

			BasicDBList dblist = new BasicDBList();
			dblist.addAll(categories);
			BasicDBObject descriptionQuery = new BasicDBObject("description", new BasicDBObject("$in", dblist));
			andQueryList.add(descriptionQuery);
		}

		//CRIME WEAPONS
		if (weapons != null && !weapons.isEmpty()) {
			BasicDBList dblist = new BasicDBList();
			dblist.addAll(weapons);
			BasicDBObject weaponQuery = new BasicDBObject("weapon", new BasicDBObject("$in", dblist));
			andQueryList.add(weaponQuery);
		}

		//CRIME NEIGHBORHOOD
		if (neighborhoods != null && !neighborhoods.isEmpty()) {
			BasicDBList dblist = new BasicDBList();
			dblist.addAll(neighborhoods);
			BasicDBObject neighborhoodQuery = new BasicDBObject("neighborhood", new BasicDBObject("$in", dblist));
			andQueryList.add(neighborhoodQuery);
		}

		BasicDBObject queryObject = new BasicDBObject("$and", andQueryList);

		FindIterable<Document> cursor = collection.find(queryObject);

		cursor.forEach(new Consumer<Document>() {
			@Override
			public void accept(Document doc) {
				if (doc.containsKey("coorX") && doc.containsKey("coorY")) {
					try {
						crimes.addOverlay(new CrimeCase(doc));
					} catch (ParseException ex) {
						JsfUtil.addErrorMessage(ex, "Error - Transfer MongoDB data model to MapModel");
					}
				}
			}
		});

		return crimes;
	}

	/**
	 * Helper function to check validity of dates Throws exceptions with error
	 * message if validation fails
	 *
	 * @param from start date
	 * @param to end date
	 */
	private boolean validateDates(Date from, Date to) {
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);
		if (from == null || to == null) {
			JsfUtil.addErrorMessage("A date range is required");
			return false;
		}

		//Validate parameter consistency
		if (from.after(to)) {
			JsfUtil.addErrorMessage("'from' Date must be earlier than 'to' Date.");
			return false;
		}
		DateTime fromTime = new DateTime(from);
		DateTime toTime = new DateTime(to);
		if ((new Interval(fromTime, toTime)).toDuration().getStandardDays() >= 366) {
			JsfUtil.addErrorMessage("Range cannot be longer than a year.");
			return false;
		}
		return true;
	}

	/**
	 * Get a list of distinct values for a given field name
	 *
	 * @param fieldName field name
	 * @return list of values
	 */
	public List<String> getDistinct(String fieldName) {
		List<String> result = new ArrayList<>();
		return CLIENT.getCollection().distinct(fieldName, String.class).into(result);
	}

	/**
	 * Call this to populate local database if it doesn't contain any crime data
	 *
	 * @param collection
	 * @throws IOException
	 */
	private void populateCollection(MongoCollection<Document> collection) throws IOException {
		JsonReader reader = null;
		String CrimeCaseURL = "https://data.baltimorecity.gov/resource/4ih5-d5d5.json?$$app_token=yjfXOuQMUxKqegMpx42YoV9RJ&$limit=5000000";

		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(
					new URL(CrimeCaseURL).openStream(), Charset.forName("UTF-8")));

			reader = new JsonReader(r);

			JsonParser parser = new JsonParser();

			reader.beginArray(); // the initial '['
			while (reader.hasNext()) {
				JsonObject tempCrimeCaseJSONObject = parser.parse(reader).getAsJsonObject();

				JsonElement code = tempCrimeCaseJSONObject.get("crimecode");
				JsonElement date = tempCrimeCaseJSONObject.get("crimedate");
				JsonElement time = tempCrimeCaseJSONObject.get("crimetime");
				JsonElement description = tempCrimeCaseJSONObject.get("description");

				JsonElement district = tempCrimeCaseJSONObject.get("district");

				JsonElement location = tempCrimeCaseJSONObject.get("location");
				JsonElement weapon = tempCrimeCaseJSONObject.get("weapon");
				JsonElement post = tempCrimeCaseJSONObject.get("post");
				JsonObject location_1 = tempCrimeCaseJSONObject.getAsJsonObject("location_1");
				JsonArray coor = (location_1 == null) ? null : location_1.getAsJsonArray("coordinates");
				Double coorX = (coor == null) ? null : coor.get(0).getAsDouble();
				Double coorY = (coor == null) ? null : coor.get(1).getAsDouble();
				JsonElement neighborhood = tempCrimeCaseJSONObject.get("neighborhood");

				Document doc = new Document();
				if (date != null) {
					doc.append("crimedate", date.getAsString());
				}
				if (time != null) {
					doc.append("crimetime", time.getAsString());
				}
				if (code != null) {
					doc.append("crimecode", code.getAsString());
				}
				if (location != null) {
					doc.append("location", location.getAsString());
				}
				if (description != null) {
					doc.append("description", description.getAsString());
				}
				if (weapon != null) {
					doc.append("weapon", weapon.getAsString());
				}
				if (post != null) {
					doc.append("post", post.getAsString());
				}
				if (district != null) {
					doc.append("district", district.getAsString());
				}
				if ((coorX != null) && (coorY != null)) {
					doc.append("coorX", coorX).append("coorY", coorY);
				}
				if (neighborhood != null) {
					doc.append("neighborhood", neighborhood.getAsString());
				}
				collection.insertOne(doc);
			}
		} catch (JsonIOException | JsonSyntaxException | IOException ex) {
			LOGGER.log(Level.SEVERE, null, ex);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

}
