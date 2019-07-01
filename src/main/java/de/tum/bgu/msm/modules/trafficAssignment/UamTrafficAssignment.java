package de.tum.bgu.msm.modules.trafficAssignment;

import de.tum.bgu.msm.data.DataSet;
import de.tum.bgu.msm.data.Mode;
import de.tum.bgu.msm.modules.externalFlows.LongDistanceTraffic;
import de.tum.bgu.msm.resources.Properties;
import de.tum.bgu.msm.resources.Resources;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.scenario.MutableScenario;
import org.matsim.core.scenario.ScenarioUtils;

import java.util.HashSet;
import java.util.Set;

public class UamTrafficAssignment extends TrafficAssignment {

    
    public UamTrafficAssignment(DataSet dataSet, String scenarioName) {
        super(dataSet, scenarioName);
    }
    
    protected void configMatsim() {
        matsimConfig = ConfigUtils.createConfig();
        matsimConfig = ConfigureMatsim.configureMatsim(matsimConfig);

        String runId = "mito_assignment";
        matsimConfig.controler().setRunId(runId);
        matsimConfig.controler().setOutputDirectory(Resources.INSTANCE.getString(Properties.BASE_DIRECTORY) + "/" + outputSubDirectory + "/trafficAssignment");
        matsimConfig.network().setInputFile(Resources.INSTANCE.getString(Properties.MATSIM_NETWORK_FILE));

        matsimConfig.qsim().setNumberOfThreads(16);
        matsimConfig.global().setNumberOfThreads(16);
        matsimConfig.parallelEventHandling().setNumberOfThreads(16);
        matsimConfig.qsim().setUsingThreadpool(false);

        matsimConfig.controler().setLastIteration(Resources.INSTANCE.getInt(Properties.MATSIM_ITERATIONS));
        matsimConfig.controler().setWritePlansInterval(matsimConfig.controler().getLastIteration());
        matsimConfig.controler().setWriteEventsInterval(matsimConfig.controler().getLastIteration());

        matsimConfig.qsim().setStuckTime(10);
        matsimConfig.qsim().setFlowCapFactor(SILO_SAMPLING_RATE * Double.parseDouble(Resources.INSTANCE.getString(Properties.TRIP_SCALING_FACTOR)));
        matsimConfig.qsim().setStorageCapFactor(SILO_SAMPLING_RATE * Double.parseDouble(Resources.INSTANCE.getString(Properties.TRIP_SCALING_FACTOR)));


        String[] networkModes = Resources.INSTANCE.getArray(Properties.MATSIM_NETWORK_MODES, new String[]{"autoDriver"});
        Set<String> networkModesSet = new HashSet<>();

        for (String mode : networkModes){
            String matsimMode = Mode.getMatsimMode(Mode.valueOf(mode));
            if (!networkModesSet.contains(matsimMode)){
                networkModesSet.add(matsimMode);
            }
        }

        matsimConfig.plansCalcRoute().setNetworkModes(networkModesSet);



    }

    protected void createPopulation() {
        MatsimPopulationGenerator matsimPopulationGenerator = new MatsimPopulationGenerator();
        Population population = matsimPopulationGenerator.generateMatsimPopulation(dataSet, matsimConfig);
        if (Resources.INSTANCE.getBoolean(Properties.ADD_EXTERNAL_FLOWS, false)) {
            LongDistanceTraffic longDistanceTraffic = new LongDistanceTraffic(dataSet);
            population = longDistanceTraffic.addLongDistancePlans(Double.parseDouble(Resources.INSTANCE.getString(Properties.TRIP_SCALING_FACTOR)), population);
        }
        matsimScenario = (MutableScenario) ScenarioUtils.loadScenario(matsimConfig);
        matsimScenario.setPopulation(population);
    }

    protected void runMatsim() {
        final Controler controler = new Controler(matsimScenario);
        controler.run();

        CarSkimUpdater skimUpdater = new CarSkimUpdater(controler, matsimScenario.getNetwork(), dataSet);
        skimUpdater.run();
        dataSet.setMatsimControler(controler);
       }
}
