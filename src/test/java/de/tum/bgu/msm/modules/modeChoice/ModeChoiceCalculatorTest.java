package de.tum.bgu.msm.modules.modeChoice;

import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.resources.Implementation;
import de.tum.bgu.msm.resources.Resources;
import de.tum.bgu.msm.util.MitoUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ModeChoiceCalculatorTest {

    private ModeChoiceJSCalculator calculator;

    private final double[] reference = new double[]{0.584061072,0.273145755,0.08932696,0.023002169,0.009248449,0.017134754,0.00408084};

    @Before
    public void setup() {
        ResourceBundle bundle = MitoUtil.createResourceBundle("./testInput/test.properties");
        Resources.initializeResources(bundle, Implementation.MUNICH);

        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("ModeChoice"));
        calculator = new ModeChoiceJSCalculator(reader);
    }

    @Test
    public void test() {
        Zone origin = new Zone(1, 100, null);
        origin.setDistanceToNearestTransitStop(0.5f);
        //origin.setAreaTypeHBWModeChoice(AreaTypeForModeChoice.HBW_mediumSizedCity);
        MitoHousehold hh = new MitoHousehold(1, 30000, 1, null);
        MitoPerson pp = new MitoPerson(1, Occupation.STUDENT, 1, 20, Gender.FEMALE, true);
        hh.addPerson(pp);
        MitoTrip trip = new MitoTrip(1, Purpose.HBE);
        trip.setTripOrigin(origin);

        Map<String, Double> travelTimeByMode = new HashMap<>();
        travelTimeByMode.put("autoD", 15.);
        travelTimeByMode.put("autoP", 15.);
        travelTimeByMode.put("bus", 30.);
        travelTimeByMode.put("tramMetro", 25.);
        travelTimeByMode.put("train", 40.);
        for(int i= 0; i< 1000000; i ++) {
            double[] result = calculator.calculateHBSProbabilities(hh, pp, trip, travelTimeByMode, 5.);
        }
//        for(int i = 0; i < result.length; i++) {
//            Assert.assertEquals("Result " + i + " is totally wrong.",reference[i], result[i], 0.000001);
//        }

    }

}
