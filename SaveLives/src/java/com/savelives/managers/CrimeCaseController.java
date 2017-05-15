/*
 * Created by Taiwen Jin on 2017.04.10  * 
 * Copyright © 2017 Taiwen Jin. All rights reserved. * 
 */
package com.savelives.managers;

import com.savelives.entityclasses.CrimeCase;
import com.savelives.entityclasses.SearchQuery;
import com.savelives.sessionbeans.CrimeCaseFacade;
import com.savelives.sessionbeans.UserFacade;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.PieChartModel;

/**
 *
 * @author TaiwenJin
 */
@SessionScoped
@Named(value = "crimeCaseController")
public class CrimeCaseController implements Serializable {

	@Inject
	private CrimeCaseFacade crimeCaseFacade;

	private MapModel mapModel;
	private CrimeCase selected;
	private final List<String> weapons;
	private List<String> selectedWeapons;
	private final List<String> neighborhoods;
	private List<String> selectedNeighborhoods;
	private Date date1;
	private Date date2;
	private final List<String> crimeCategories;
	private final List<String> crimeCodes;
	private List<String> selectedCategories;
	private List<String> selectedCrimeCodes;
	private final int NUMB_OF_CRIMES = 500;
	private BarChartModel barModel;
	private PieChartModel pieModel;

	@Inject
	private AccountManager accountManager;

	@Inject
	private UserFacade userFacade;

	/**
	 * Default Constructor
	 */
	public CrimeCaseController() throws Exception {
		crimeCaseFacade = new CrimeCaseFacade();
		initializeDates();
		crimeCategories = crimeCaseFacade.getDistinct("description");
		crimeCodes = crimeCaseFacade.getDistinct("crimecode");
		weapons = crimeCaseFacade.getDistinct("weapon");
		neighborhoods = crimeCaseFacade.getDistinct("neighborhood");
	}

	/*========== Getters and Setters ==============*/
	private CrimeCaseFacade getFacade() {
		return crimeCaseFacade;
	}

	public Marker getSelected() {
		return selected;
	}

	public Date getDate1() {
		return date1;
	}

	public void setDate1(Date date1) {
		this.date1 = date1;
	}

	public Date getDate2() {
		return date2;
	}

	public void setDate2(Date date2) {
		this.date2 = date2;
	}

	public List<String> getCrimeCategories() {
		return crimeCategories;
	}

	public List<String> getCrimeCodes() {
		return crimeCodes;
	}

	public void setSelectedCategories(List<String> selectedCategories) {
		this.selectedCategories = selectedCategories;
	}

	public List<String> getSelectedCategories() {

		return selectedCategories;
	}

	public List<String> getSelectedCrimeCodes() {
		return selectedCrimeCodes;
	}

	public void setSelectedCrimeCodes(List<String> selectedCrimeCodes) {
		this.selectedCrimeCodes = selectedCrimeCodes;
	}

	public UserFacade getUserFacade() {
		return userFacade;
	}

	public void setUserFacade(UserFacade userFacade) {
		this.userFacade = userFacade;
	}

	public List<String> getWeapons() {
		return weapons;
	}

	public List<String> getSelectedWeapons() {
		return selectedWeapons;
	}

	public void setSelectedWeapons(List<String> selectedWeapons) {
		this.selectedWeapons = selectedWeapons;
	}

	public void setSelectedNeighborhoods(List<String> selectedNeighborhoods) {
		this.selectedNeighborhoods = selectedNeighborhoods;
	}

	public List<String> getNeighborhoods() {
		return neighborhoods;
	}

	public List<String> getSelectedNeighborhoods() {
		return selectedNeighborhoods;
	}

	public BarChartModel getBarModel() {
		if (barModel == null) {
			try {
				createBarModel();
			} catch (Exception ex) {
				Logger.getLogger(CrimeCaseController.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return barModel;
	}

	public PieChartModel getPieModel() {
		if (pieModel == null) {
			createPieModel();
		}
		return pieModel;
	}

	public MapModel getMapModel() {
		if (mapModel == null) {
			
			mapModel = getFacade().filterCrimes(getDate1(), getDate2(), null, null, null, null);
		}
		return mapModel;
	}

	public void setBarModel(BarChartModel model) {
		this.barModel = model;
	}

	public void onMarkerSelect(OverlaySelectEvent event) {
		selected = (CrimeCase) event.getOverlay();
	}

	private void initializeDates() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2016);
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		date1 = cal.getTime();

		cal.set(Calendar.YEAR, 2016);
		cal.set(Calendar.MONTH, 11);
		cal.set(Calendar.DAY_OF_MONTH, 30);
		date2 = cal.getTime();
	}

	private void createPieModel() {
		pieModel = new PieChartModel();
		crimeCategories.forEach((category) -> {
			long frequency = crimeCaseFacade.getCount(date1, date2, selectedCrimeCodes,
					selectedCategories, selectedWeapons, selectedNeighborhoods, "description", category);
			if (frequency > 0) {
				pieModel.set(category, frequency);
			}
		});

		pieModel.setTitle("Crime Types");
		pieModel.setLegendPosition("w");
		pieModel.setShowDataLabels(true);
		pieModel.setShowDatatip(true);
	}

	private void createBarModel() throws Exception {
		BarChartModel model = new BarChartModel();
                
                DateAxis axis = new DateAxis("Date");

                Date dateOne = getDate1();
                Date dateTwo = getDate2();
                
                int maxCrimes = 0;
                
		ChartSeries crimes = new ChartSeries();
		crimes.setLabel("Crimes");

		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");

		if (dateOne == null || dateTwo == null) {
			throw new Exception("Either date1 or date2 were not set");
		}

		Calendar calDate1 = Calendar.getInstance();
		Calendar calDate2 = Calendar.getInstance();

		calDate1.setTime(dateOne);
		calDate2.setTime(dateTwo);

		if (calDate1.get(Calendar.YEAR) == calDate2.get(Calendar.YEAR)) {
                    if (calDate1.get(Calendar.MONTH) == calDate2.get(Calendar.MONTH)) {
                        calDate1.set(Calendar.DAY_OF_MONTH, 1);
                        
                        int numDays = 0;
                        int prevDays = 0;
                        
                        switch (calDate2.get(Calendar.MONTH)) {
				case 0:
					calDate2.set(Calendar.DAY_OF_MONTH, 31);
                                        numDays = 31;
                                        prevDays = 31;
					break;
				case 1:
					calDate2.set(Calendar.DAY_OF_MONTH, 28);
                                        numDays = 28;
                                        prevDays = 31;
					break;
				case 2:
					calDate2.set(Calendar.DAY_OF_MONTH, 31);
                                        numDays = 31;
                                        prevDays = 28;
					break;
				case 3:
					calDate2.set(Calendar.DAY_OF_MONTH, 30);
                                        numDays = 30;
                                        prevDays = 31;
					break;
				case 4:
					calDate2.set(Calendar.DAY_OF_MONTH, 31);
                                        numDays = 31;
                                        prevDays = 30;
					break;
				case 5:
					calDate2.set(Calendar.DAY_OF_MONTH, 30);
                                        numDays = 30;
                                        prevDays = 31;
					break;
				case 6:
					calDate2.set(Calendar.DAY_OF_MONTH, 31);
                                        numDays = 31;
                                        prevDays = 30;
					break;
				case 7:
					calDate2.set(Calendar.DAY_OF_MONTH, 31);
                                        numDays = 31;
                                        prevDays = 31;
					break;
				case 8:
					calDate2.set(Calendar.DAY_OF_MONTH, 30);
                                        numDays = 30;
                                        prevDays = 31;
					break;
				case 9:
					calDate2.set(Calendar.DAY_OF_MONTH, 31);
                                        numDays = 31;
                                        prevDays = 30;
					break;
				case 10:
					calDate2.set(Calendar.DAY_OF_MONTH, 30);
                                        numDays = 30;
                                        prevDays = 31;
					break;
				case 11:
					calDate2.set(Calendar.DAY_OF_MONTH, 31);
                                        numDays = 31;
                                        prevDays = 30;
					break;
				default:
					break;
			}
                        
                        Calendar temp = Calendar.getInstance();
                        temp.setTime(calDate1.getTime());
                        
                        for (int i = 1; i <= numDays; i++) {
                            temp.set(Calendar.DAY_OF_MONTH, i);
                            
                            int numCrimes = getFacade().filterCrimes(
					temp.getTime(), 
					temp.getTime(), 
					selectedCrimeCodes, 
					selectedCategories, 
					selectedWeapons, 
					selectedNeighborhoods).getMarkers().size();
			
                            if (numCrimes > maxCrimes) {
                                    maxCrimes = numCrimes;
                            }
			
                            crimes.set(f.format(temp.getTime()), numCrimes);
                        }
                        
                        //set min and max of date axis one day below and one day above
                        calDate1.add(Calendar.MONTH, -1);
                        calDate1.set(Calendar.DAY_OF_MONTH, prevDays);
                        calDate2.add(Calendar.DAY_OF_MONTH, 1);
                        
                        model.setBarWidth(10);
                        
                        axis.setTickFormat("%b %#d, %Y");
                        axis.setTickCount(numDays + 2);
                        
                    } else { //not the same month but same year, display 12 months of data starting with the first month
                        
                        //sets the second date to be 12 months from the first date
                        int diffMonths = 12 - (calDate2.get(Calendar.MONTH) - calDate1.get(Calendar.MONTH));
                        if (diffMonths > 0) {
                                calDate2.add(Calendar.MONTH, diffMonths);

                                dateTwo = calDate2.getTime();
                        }
                        
                        //beginning of x-axis
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(calDate1.getTime());
                        cal.set(Calendar.DAY_OF_MONTH, 15);
                        cal.add(Calendar.MONTH, -1);

                        for (int i = 1; i <= 12; i++) {

                                cal.add(Calendar.MONTH, 1);

                                Calendar cal4 = Calendar.getInstance(); //first day of month
                                cal4.setTime(cal.getTime());
                                cal4.set(Calendar.DAY_OF_MONTH, 1);

                                Calendar cal3 = Calendar.getInstance(); //last day of month
                                cal3.setTime(cal.getTime());

                                switch (cal3.get(Calendar.MONTH)) {
                                        case 0:
                                                cal3.set(Calendar.DAY_OF_MONTH, 31);
                                                break;
                                        case 1:
                                                cal3.set(Calendar.DAY_OF_MONTH, 28);
                                                break;
                                        case 2:
                                                cal3.set(Calendar.DAY_OF_MONTH, 31);
                                                break;
                                        case 3:
                                                cal3.set(Calendar.DAY_OF_MONTH, 30);
                                                break;
                                        case 4:
                                                cal3.set(Calendar.DAY_OF_MONTH, 31);
                                                break;
                                        case 5:
                                                cal3.set(Calendar.DAY_OF_MONTH, 30);
                                                break;
                                        case 6:
                                                cal3.set(Calendar.DAY_OF_MONTH, 31);
                                                break;
                                        case 7:
                                                cal3.set(Calendar.DAY_OF_MONTH, 31);
                                                break;
                                        case 8:
                                                cal3.set(Calendar.DAY_OF_MONTH, 30);
                                                break;
                                        case 9:
                                                cal3.set(Calendar.DAY_OF_MONTH, 31);
                                                break;
                                        case 10:
                                                cal3.set(Calendar.DAY_OF_MONTH, 30);
                                                break;
                                        case 11:
                                                cal3.set(Calendar.DAY_OF_MONTH, 31);
                                                break;
                                        default:
                                                break;
                                }

                                //return number of crimes within this month
                                int numCrimes = getFacade().filterCrimes(
                                                cal4.getTime(), 
                                                cal3.getTime(), 
                                                selectedCrimeCodes, 
                                                selectedCategories, 
                                                selectedWeapons, 
                                                selectedNeighborhoods).getMarkers().size();

                                if (numCrimes > maxCrimes) {
                                        maxCrimes = numCrimes;
                                }

                                crimes.set(f.format(cal.getTime()), numCrimes);
                        }

                        calDate1.add(Calendar.MONTH, -1);
                        //calDate2.add(Calendar.MONTH, 1);
                        
                        model.setBarWidth(45);
                        
                        axis.setTickFormat("%b, %Y");
                        axis.setTickCount(14);
                    }
		} else { //else if years are different
                    //beginning of x-axis
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(calDate1.getTime());
                    cal.set(Calendar.DAY_OF_MONTH, 15);
                    cal.add(Calendar.MONTH, -1);

                    for (int i = 1; i <= 12; i++) {

                            cal.add(Calendar.MONTH, 1);

                            Calendar cal4 = Calendar.getInstance(); //first day of month
                            cal4.setTime(cal.getTime());
                            cal4.set(Calendar.DAY_OF_MONTH, 1);

                            Calendar cal3 = Calendar.getInstance(); //last day of month
                            cal3.setTime(cal.getTime());

                            switch (cal3.get(Calendar.MONTH)) {
                                    case 0:
                                            cal3.set(Calendar.DAY_OF_MONTH, 31);
                                            break;
                                    case 1:
                                            cal3.set(Calendar.DAY_OF_MONTH, 28);
                                            break;
                                    case 2:
                                            cal3.set(Calendar.DAY_OF_MONTH, 31);
                                            break;
                                    case 3:
                                            cal3.set(Calendar.DAY_OF_MONTH, 30);
                                            break;
                                    case 4:
                                            cal3.set(Calendar.DAY_OF_MONTH, 31);
                                            break;
                                    case 5:
                                            cal3.set(Calendar.DAY_OF_MONTH, 30);
                                            break;
                                    case 6:
                                            cal3.set(Calendar.DAY_OF_MONTH, 31);
                                            break;
                                    case 7:
                                            cal3.set(Calendar.DAY_OF_MONTH, 31);
                                            break;
                                    case 8:
                                            cal3.set(Calendar.DAY_OF_MONTH, 30);
                                            break;
                                    case 9:
                                            cal3.set(Calendar.DAY_OF_MONTH, 31);
                                            break;
                                    case 10:
                                            cal3.set(Calendar.DAY_OF_MONTH, 30);
                                            break;
                                    case 11:
                                            cal3.set(Calendar.DAY_OF_MONTH, 31);
                                            break;
                                    default:
                                            break;
                            }

                            //return number of crimes within this month
                            int numCrimes = getFacade().filterCrimes(
                                            cal4.getTime(), 
                                            cal3.getTime(), 
                                            selectedCrimeCodes, 
                                            selectedCategories, 
                                            selectedWeapons, 
                                            selectedNeighborhoods).getMarkers().size();

                            if (numCrimes > maxCrimes) {
                                    maxCrimes = numCrimes;
                            }

                            crimes.set(f.format(cal.getTime()), numCrimes);
                    }
                    
                    calDate1.add(Calendar.MONTH, -1);
                    calDate2.add(Calendar.MONTH, 1);

                    model.setBarWidth(45);
                    
                    axis.setTickFormat("%b, %Y");
                    axis.setTickCount(14);
                }
                
		model.addSeries(crimes);

		barModel = model;

		barModel.getAxis(AxisType.Y).setLabel("Crime Count");
		barModel.getAxis(AxisType.Y).setTickFormat("%d");
		barModel.getAxis(AxisType.Y).setMin(0);
		barModel.getAxis(AxisType.Y).setMax(maxCrimes * 1.15);
                
		axis.setTickAngle(-50);
		axis.setMin(f.format(calDate1.getTime()));
		axis.setMax(f.format(calDate2.getTime()));
		barModel.getAxes().put(AxisType.X, axis);

		barModel.setTitle("Crimes in Baltimore");
		barModel.setLegendPosition("ne");
		barModel.setAnimate(true);
	}

	public void submit() throws Exception {
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);
		mapModel = null;
		mapModel = getFacade().filterCrimes(date1, date2, selectedCrimeCodes, selectedCategories, selectedWeapons, selectedNeighborhoods);

		createBarModel();
		createPieModel();
		// Record this search
		if (accountManager.isLoggedIn()) {
			SearchQuery sq = new SearchQuery(0, "untitled", new Date(), date1, date2,
					(ArrayList<String>) selectedCategories, (ArrayList<String>) selectedCrimeCodes,
					(ArrayList<String>) selectedWeapons, (ArrayList<String>) selectedNeighborhoods);
			accountManager.getSelected().addHistorySearch(sq);
			getUserFacade().edit(accountManager.getSelected());
			//User u = getUserFacade().findById(accountManager.getSelected().getId());
		}
	}

	public void submitWithoutAddHistory() throws Exception {
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);
		mapModel = null;
		mapModel = getFacade().filterCrimes(date1, date2, selectedCrimeCodes, selectedCategories, selectedWeapons, selectedNeighborhoods);

		createBarModel();
		createPieModel();
	}
}
