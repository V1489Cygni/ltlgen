package ltlgen.multiobjective;

import ec.EvolutionState;
import ec.Individual;
import ec.Statistics;
import ec.gp.GPIndividual;
import ec.multiobjective.MultiObjectiveFitness;
import ec.util.Parameter;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class LTLStatistics extends Statistics {
    private int logFile;
    private int bestNumber;

    @Override
    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);
        File log = state.parameters.getFile(base.push("logfile"), null);
        bestNumber = state.parameters.getInt(base.push("best-number"), null);
        try {
            logFile = state.output.addLog(log, true);
        } catch (IOException e) {
            state.output.fatal("Error while setting up log file: " + e.getMessage());
        }
    }

    @Override
    public void postEvaluationStatistics(final EvolutionState state) {
        super.postEvaluationStatistics(state);
        state.output.println("Generation " + state.generation, logFile);
        Individual[] individuals = state.population.subpops[0].individuals;
        Individual best = individuals[0];
        for (int i = 1; i < individuals.length; i++) {
            if (individuals[i].fitness.betterThan(best.fitness)) {
                best = individuals[i];
            }
        }
        state.output.println("Best fitness: " + best.fitness.fitnessToStringForHumans(), logFile);
        state.output.print("Best individual: ", logFile);
        state.output.println("G(" + ((GPIndividual) best).trees[0].child.toStringForHumans() + ")", logFile);
    }

    @Override
    public void finalStatistics(final EvolutionState state, int result) {
        state.output.print("----------------\nResult: ", logFile);
        state.output.println(result == state.R_SUCCESS ? "Ideal individual found." : "Ideal individual not found.", logFile);
        state.output.println("Showing best individuals: ", logFile);
        List<Individual> individuals = new ArrayList<>();
        for (Individual individual : state.population.subpops[0].individuals) {
            individuals.add(individual);
        }
        Collections.sort(individuals, new IndividualsComparator());
        Set<String> shown = new HashSet<>();
        int count = 0;
        for (int i = 0; i < individuals.size(); i++) {
            if (count >= bestNumber) {
                break;
            }
            String ind = "G(" + ((GPIndividual) individuals.get(i)).trees[0].child.toStringForHumans() + ")";
            if (!shown.contains(ind)) {
                state.output.println(ind, logFile);
                shown.add(ind);
                count++;
            }
        }
        state.output.println("----------------\nFront: ", logFile);
        ArrayList<Individual> front = new ArrayList<>();
        MultiObjectiveFitness.partitionIntoParetoFront(state.population.subpops[0].individuals, front, null);
        for (Individual individual : front) {
            state.output.println("G(" + ((GPIndividual) individual).trees[0].child.toStringForHumans() + ")", logFile);
        }
    }

    private static class IndividualsComparator implements Comparator<Individual> {

        @Override
        public int compare(Individual o1, Individual o2) {
            if (o1.fitness.betterThan(o2.fitness)) {
                return -1;
            } else if (o2.fitness.betterThan(o1.fitness)) {
                return 1;
            }
            return 0;
        }
    }
}
