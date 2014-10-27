package ltlgen;

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
    private int logFile, resultFile;
    private int bestNumber;

    @Override
    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);
        File log = state.parameters.getFile(base.push("log"), null);
        File result = state.parameters.getFile(base.push("result"), null);
        bestNumber = state.parameters.getInt(base.push("best-number"), null);
        try {
            logFile = state.output.addLog(log, true);
            resultFile = state.output.addLog(result, true);
        } catch (IOException e) {
            state.output.fatal("Error while setting up stats file: " + e.getMessage());
        }
    }

    private String individualForHumans(GPIndividual individual) {
        return "G(" + individual.trees[0].child.toStringForHumans() + ")\n" + individual.fitness.fitnessToStringForHumans();
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
        state.output.println("Best individual: " + individualForHumans((GPIndividual) best), logFile);
        state.output.println("--------------------", logFile);
    }

    @Override
    public void finalStatistics(final EvolutionState state, int result) {
        state.output.println("Ideal individual " + (result == EvolutionState.R_SUCCESS ? "" : "not ") + "found.\n", resultFile);
        state.output.println("Front:", resultFile);
        ArrayList<Individual> front = new ArrayList<>();
        Set<String> shown = new HashSet<>();
        MultiObjectiveFitness.partitionIntoParetoFront(state.population.subpops[0].individuals, front, null);
        for (Individual individual : front) {
            String s = individualForHumans((GPIndividual) individual);
            if (!shown.contains(s)) {
                state.output.println(s, resultFile);
                state.output.println("--------------------", resultFile);
                shown.add(s);
            }
        }
        state.output.println("\nBest individuals:", resultFile);
        List<Individual> individuals = new ArrayList<>();
        Collections.addAll(individuals, state.population.subpops[0].individuals);
        Collections.sort(individuals, new IndividualsComparator());
        shown.clear();
        int count = 0;
        for (Individual individual : individuals) {
            if (count >= bestNumber) {
                break;
            }
            String s = individualForHumans((GPIndividual) individual);
            if (!shown.contains(s)) {
                state.output.println(s, resultFile);
                state.output.println("--------------------", resultFile);
                shown.add(s);
                count++;
            }
        }
    }

    private static class IndividualsComparator implements Comparator<Individual> {

        @Override
        public int compare(Individual i1, Individual i2) {
            if (i1.fitness.betterThan(i2.fitness)) {
                return -1;
            } else if (i2.fitness.betterThan(i1.fitness)) {
                return 1;
            }
            return 0;
        }
    }
}
