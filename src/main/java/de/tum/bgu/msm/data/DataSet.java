package de.tum.bgu.msm.data;

import com.pb.common.datafile.TableDataSet;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nico on 14.07.2017.
 */
public class DataSet {

    private TableDataSet travelSurveyHouseholdTable;
    private TableDataSet travelSurveyTripsTable;

    private TableDataSet tripAttractionRates;

    private TravelTimes autoTravelTimes;
    private TravelTimes transitTravelTimes;

    private final Map<Integer, Zone> zones= new HashMap<>();
    private final Map<Integer, MitoHousehold> households = new HashMap<>();
    private final Map<Integer, MitoPerson> persons = new HashMap<>();
    private final Map<Integer, MitoTrip> trips = new HashMap<>();

    public TableDataSet getTravelSurveyHouseholdTable() {
        return travelSurveyHouseholdTable;
    }

    public void setTravelSurveyHouseholdTable(TableDataSet travelSurveyHouseholdTable) {
        this.travelSurveyHouseholdTable = travelSurveyHouseholdTable;
    }

    public TableDataSet getTravelSurveyTripsTable() {
        return travelSurveyTripsTable;
    }

    public void setTravelSurveyTripsTable(TableDataSet travelSurveyTripsTable) {
        this.travelSurveyTripsTable = travelSurveyTripsTable;
    }

    public TableDataSet getTripAttractionRates() {
        return tripAttractionRates;
    }

    public void setTripAttractionRates(TableDataSet tripAttractionRates) {
        this.tripAttractionRates = tripAttractionRates;
    }

    public TravelTimes getAutoTravelTimes() {
        return this.autoTravelTimes;
    }

    public void setAutoTravelTimes(TravelTimes travelTimes) {
        this.autoTravelTimes = travelTimes;
    }

    public TravelTimes getTransitTravelTimes() {
        return this.transitTravelTimes;
    }

    public void setTransitTravelTimes(TravelTimes travelTimes) {
        this.transitTravelTimes = travelTimes;
    }

    public Map<Integer, Zone> getZones() {
        return zones;
    }

    public Map<Integer, MitoHousehold> getHouseholds() {
        return households;
    }

    public Map<Integer, MitoPerson> getPersons() {
        return persons;
    }

    public Map<Integer, MitoTrip> getTrips() {
        return trips;
    }
}