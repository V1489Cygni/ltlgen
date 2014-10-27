package ltlgen.multiobjective;

import ec.EvolutionState;
import ec.Individual;
import ec.Statistics;
import ec.gp.GPIndividual;
import ec.multiobjective.MultiObjectiveFitness;
import ec.multiobjective.spea2.SPEA2MultiObjectiveFitness;
import ec.util.Parameter;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class LTLStatistics extends Statistics {
    private int logFile, bestFile;
    private int bestNumber;

    @Override
    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);
        File log = state.parameters.getFile(base.push("logfile"), null);
        File best = state.parameters.getFile(base.push("best-file"), null);
        bestNumber = state.parameters.getInt(base.push("best-number"), null);
        try {
            logFile = state.output.addLog(log, true);
            bestFile = state.output.addLog(best, true);
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
        state.output.println(result == EvolutionState.R_SUCCESS ? "Ideal individual found." : "Ideal individual not found.", logFile);
        state.output.println("Showing best individuals:\n", logFile);
        List<Individual> individuals = new ArrayList<>();
        Collections.addAll(individuals, state.population.subpops[0].individuals);
        Collections.sort(individuals, new IndividualsComparator());
        Set<String> shown = new HashSet<>();
        int count = 0;
        for (Individual individual1 : individuals) {
            if (count >= bestNumber) {
                break;
            }
            String ind = "G(" + ((GPIndividual) individual1).trees[0].child.toStringForHumans() + ")";
            if (!shown.contains(ind)) {
                state.output.println(ind + "\n" + individual1.fitness.fitnessToStringForHumans() + "\n", logFile);
                shown.add(ind);
                count++;
            }
        }
        state.output.println("----------------\nFront: ", logFile);
        ArrayList<Individual> front = new ArrayList<>();
        MultiObjectiveFitness.partitionIntoParetoFront(state.population.subpops[0].individuals, front, null);
        shown.clear();
        for (Individual individual : front) {
            String ind = "G(" + ((GPIndividual) individual).trees[0].child.toStringForHumans() + ")";
            if (!shown.contains(ind)) {
                state.output.println(ind + "\n" + individual.fitness.fitnessToStringForHumans() + "\n", logFile);
                shown.add(ind);
            }
        }
        Set<String> bsh = new HashSet<>();
        for (Individual individual : individuals) {
            SPEA2MultiObjectiveFitness fitness = (SPEA2MultiObjectiveFitness) individual.fitness;
            double[] values = fitness.getObjectives();
            if (values[0] == 1 && values[1] >= 0.5 && values[3] >= 0.5 && values[4] >= 0.5 && values[5] >= (1.0 / 20)) {
                String ind = "G(" + ((GPIndividual) individual).trees[0].child.toStringForHumans() + ")";
                if (!bsh.contains(ind)) {
                    state.output.println(ind + "\n" + individual.fitness.fitnessToStringForHumans() + "\n", bestFile);
                    bsh.add(ind);
                }
            }
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
