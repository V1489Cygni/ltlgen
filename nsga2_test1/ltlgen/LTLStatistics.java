package ec.app.ltlgen;

import ec.EvolutionState;
import ec.Individual;
import ec.Statistics;
import ec.gp.GPIndividual;
import ec.util.Parameter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
        for (int i = 0; i < Math.min(individuals.size(), bestNumber); i++) {
            state.output.println("G(" + ((GPIndividual) individuals.get(i)).trees[0].child.toStringForHumans() + ")", logFile);
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
