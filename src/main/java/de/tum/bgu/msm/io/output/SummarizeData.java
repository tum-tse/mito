package de.tum.bgu.msm.io.output;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;
import com.google.common.math.Stats;
import de.tum.bgu.msm.MitoModel;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.resources.Properties;
import de.tum.bgu.msm.resources.Resources;
import de.tum.bgu.msm.util.MitoUtil;
import de.tum.bgu.msm.util.charts.Histogram;
import de.tum.bgu.msm.util.charts.PieChart;
import de.tum.bgu.msm.util.charts.ScatterPlot;

import java.io.PrintWriter;
import java.util.*;

/**
 * Methods to summarize model results
 * Author: Ana Moreno, Munich
 * Created on 11/07/2017.
 */
public class SummarizeData {
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(SummarizeData.class);

    private static final String outputSubDirectory = "scenOutput/" + MitoModel.getScenarioName() + "/";


    public static void writeOutSyntheticPopulationWithTrips(DataSet dataSet) {
        LOGGER.info("  Writing household file");
        String filehh = Resources.INSTANCE.getString(Properties.BASE_DIRECTORY) + "/" + outputSubDirectory + dataSet.getYear() + "/" + Resources.INSTANCE.getString(Properties.HOUSEHOLDS) + "_t.csv";
        PrintWriter pwh = MitoUtil.openFileForSequentialWriting(filehh, false);
        pwh.println("id,zone,homeX,homeY,hhSize,autos,trips,workTrips");
        for (MitoHousehold hh : dataSet.getHouseholds().values()) {
            pwh.print(hh.getId());
            pwh.print(",");
            pwh.print(hh.getHomeZone());
            pwh.print(",");
            pwh.print(hh.getHomeLocation().getCoordinate().x);
            pwh.print(",");
            pwh.print(hh.getHomeLocation().getCoordinate().y);
            pwh.print(",");
            pwh.print(hh.getHhSize());
            pwh.print(",");
            pwh.print(hh.getAutos());
            pwh.print(",");
            int totalNumber = 0;
            for(Purpose purpose: Purpose.values()) {
                totalNumber += hh.getTripsForPurpose(purpose).size();
            }
            pwh.print(totalNumber);
            pwh.print(",");
            pwh.println(hh.getTripsForPurpose(Purpose.HBW).size());
        }
        pwh.close();

        LOGGER.info("  Writing person file");
        String filepp = Resources.INSTANCE.getString(Properties.BASE_DIRECTORY) + "/" + outputSubDirectory + dataSet.getYear() + "/" + Resources.INSTANCE.getString(Properties.PERSONS) + "_t.csv";
        PrintWriter pwp = MitoUtil.openFileForSequentialWriting(filepp, false);
        pwp.println("id,hhID,hhSize,hhTrips,avTrips");
        for(MitoHousehold hh: dataSet.getHouseholds().values()) {
            for (MitoPerson pp : hh.getPersons().values()) {
                    pwp.print(pp.getId());
                    pwp.print(",");
                    pwp.print(hh.getId());
                    pwp.print(",");
                    pwp.print(hh.getHhSize());
                    pwp.print(",");
                    long hhTrips = Arrays.stream(Purpose.values()).mapToLong(purpose -> hh.getTripsForPurpose(purpose).size()).sum();
                    pwp.print(hhTrips);
                    pwp.print(",");
                    pwp.println(hhTrips / hh.getHhSize());
                }
            }
        pwp.close();
    }

    public static void writeOutTrips(DataSet dataSet) {
        LOGGER.info("  Writing trips file");
        String file = Resources.INSTANCE.getString(Properties.BASE_DIRECTORY) + "/" + outputSubDirectory + dataSet.getYear() + "/microData/trips.csv";
        PrintWriter pwh = MitoUtil.openFileForSequentialWriting(file, false);
        pwh.println("id,origin,originX,originY,destination,destinationX,destinationY,purpose,person,distance,time_auto,time_bus,time_train,time_tram_metro,mode,departure_time,departure_time_return");
        for (MitoTrip trip : dataSet.getTrips().values()) {
            pwh.print(trip.getId());
            pwh.print(",");
            MitoZone origin = trip.getTripOrigin();
            String originId = "null";
            if(origin != null) {
                originId = String.valueOf(origin.getId());
            }
            pwh.print(originId);
            pwh.print(",");

            if(trip.getTripOriginCoord() != null){
                pwh.print(trip.getTripOriginCoord().getX());
                pwh.print(",");
                pwh.print(trip.getTripOriginCoord().getY());
                pwh.print(",");
            }else{
                pwh.print("null");
                pwh.print(",");
                pwh.print("null");
                pwh.print(",");
            }

            MitoZone destination = trip.getTripDestination();
            String destinationId = "null";
            if(destination != null) {
                destinationId = String.valueOf(destination.getId());
            }
            pwh.print(destinationId);
            pwh.print(",");
            if(trip.getTripDestinationCoord() != null){
                pwh.print(trip.getTripDestinationCoord().getX());
                pwh.print(",");
                pwh.print(trip.getTripDestinationCoord().getY());
                pwh.print(",");
            }else{
                pwh.print("null");
                pwh.print(",");
                pwh.print("null");
                pwh.print(",");
            }

            pwh.print(trip.getTripPurpose());
            pwh.print(",");
            pwh.print(trip.getPerson().getId());
            pwh.print(",");
            if(origin != null && destination != null) {
                double distance = dataSet.getTravelDistancesAuto().getTravelDistance(origin.getId(), destination.getId());
                pwh.print(distance);
                pwh.print(",");
                double timeAuto = dataSet.getTravelTimes().getTravelTime(origin.getId(), destination.getId(), dataSet.getPeakHour(), "car");
                pwh.print(timeAuto);
                pwh.print(",");
                double timeBus = dataSet.getTravelTimes().getTravelTime(origin.getId(), destination.getId(), dataSet.getPeakHour(), "bus");
                pwh.print(timeBus);
                pwh.print(",");
                double timeTrain = dataSet.getTravelTimes().getTravelTime(origin.getId(), destination.getId(), dataSet.getPeakHour(), "train");
                pwh.print(timeTrain);
                pwh.print(",");
                double timeTramMetro = dataSet.getTravelTimes().getTravelTime(origin.getId(), destination.getId(), dataSet.getPeakHour(), "tramMetro");
                pwh.print(timeTramMetro);
            } else {
                pwh.print("NA,NA,NA,NA,NA");
            }
            pwh.print(",");
            pwh.print(trip.getTripMode());
            pwh.print(",");
            pwh.print(trip.getDepartureInMinutes());
            int departureOfReturnTrip = trip.getDepartureInMinutesReturnTrip();
            if (departureOfReturnTrip != -1){
                pwh.print(",");
                pwh.println(departureOfReturnTrip);
            } else {
                pwh.print(",");
                pwh.println("NA");
            }

        }
        pwh.close();
    }


    private static void writeCharts(DataSet dataSet, Purpose purpose) {
        List<Double> travelTimes = new ArrayList<>();
        List<Double> travelDistances = new ArrayList<>();
        Map<Integer, List<Double>> distancesByZone = new HashMap<>();
        Multiset<MitoZone> tripsByZone = HashMultiset.create();
        SortedMultiset<Mode> modes = TreeMultiset.create();
        for (Mode mode: Mode.values()){
            Double share = dataSet.getModeShareForPurpose(purpose, mode);
            if (share != null) {
                modes.add(mode, (int) (dataSet.getModeShareForPurpose(purpose, mode) * 100));
            }
        }
        for (MitoTrip trip : dataSet.getTrips().values()) {
            if (trip.getTripPurpose() == purpose && trip.getTripOrigin() != null && trip.getTripDestination() != null) {
                travelTimes.add(dataSet.getTravelTimes().getTravelTime(trip.getTripOrigin().getId(), trip.getTripDestination().getId(), dataSet.getPeakHour(), "car"));
                double travelDistance = dataSet.getTravelDistancesAuto().getTravelDistance(trip.getTripOrigin().getId(), trip.getTripDestination().getId());
                travelDistances.add(travelDistance);
                tripsByZone.add(trip.getTripOrigin());
                if(distancesByZone.containsKey(trip.getTripOrigin().getId())){
                    distancesByZone.get(trip.getTripOrigin().getId()).add(travelDistance);
                } else {
                    List<Double> values = new ArrayList<>();
                    values.add(travelDistance);
                    distancesByZone.put(trip.getTripOrigin().getId(), values);
                }
            }
        }

        double[] travelTimesArray = new double[travelTimes.size()];
        int i = 0;
        for (Double value : travelTimes) {
            travelTimesArray[i] = value;
            i++;
        }

        double[] travelDistancesArray = new double[travelTimes.size()];
        i= 0;
        for(Double value: travelDistances) {
            travelDistancesArray[i] = value;
            i++;
        }
        Histogram.createFrequencyHistogram(Resources.INSTANCE.getString(Properties.BASE_DIRECTORY) + "/" + outputSubDirectory + dataSet.getYear() + "/timeDistribution/tripTimeDistribution"+ purpose, travelTimesArray, "Travel Time Distribution " + purpose, "Time", "Frequency", 80, 0, 80);
        Histogram.createFrequencyHistogram(Resources.INSTANCE.getString(Properties.BASE_DIRECTORY) + "/" + outputSubDirectory + dataSet.getYear() + "/distanceDistribution/tripDistanceDistribution"+ purpose, travelDistancesArray, "Travel Distances Distribution " + purpose, "Distance", "Frequency", 400, 0, 100);

        PieChart.createPieChart(Resources.INSTANCE.getString(Properties.BASE_DIRECTORY) + "/" + outputSubDirectory + dataSet.getYear() + "/modeChoice/" + purpose, modes, "Mode Choice " + purpose);

        Map<Double, Double> averageDistancesByZone = new HashMap<>();
        for(Map.Entry<Integer, List<Double>> entry: distancesByZone.entrySet()) {
            averageDistancesByZone.put(Double.valueOf(entry.getKey()), Stats.meanOf(entry.getValue()));
        }
        PrintWriter pw1 = MitoUtil.openFileForSequentialWriting(Resources.INSTANCE.getString(Properties.BASE_DIRECTORY) + "/" + outputSubDirectory + dataSet.getYear() + "/distanceDistribution/tripsByZone"+purpose+".csv", false);
        pw1.println("id,number_trips");
        for(MitoZone zone: dataSet.getZones().values()) {
            pw1.println(zone.getId()+","+tripsByZone.count(zone));
        }
        pw1.close();
        PrintWriter pw = MitoUtil.openFileForSequentialWriting(Resources.INSTANCE.getString(Properties.BASE_DIRECTORY) + "/" + outputSubDirectory + dataSet.getYear() +  "/distanceDistribution/averageZoneDistanceTable"+purpose+".csv", false);
        pw.println("id,avTripDistance");
        for(Map.Entry<Double, Double> entry: averageDistancesByZone.entrySet()) {
            pw.println(entry.getKey().intValue()+","+entry.getValue());
        }
        pw.close();
        ScatterPlot.createScatterPlot(Resources.INSTANCE.getString(Properties.BASE_DIRECTORY) + "/" + outputSubDirectory + dataSet.getYear() +  "/distanceDistribution/averageZoneDistancePlot"+purpose, averageDistancesByZone, "Average Trip Distances by MitoZone", "MitoZone Id", "Average Trip Distance");

    }

    public static void writeCharts(DataSet dataSet) {
        for(Purpose purpose: Purpose.values()) {
            writeCharts(dataSet, purpose);
        }
    }
}
