package ltlgen.multiobjective;

import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
import ec.multiobjective.spea2.SPEA2MultiObjectiveFitness;
import ec.util.Parameter;
import ru.ifmo.ctddev.genetic.transducer.algorithm.FST;
import ru.ifmo.ctddev.tools.Verifier;
import ru.ifmo.random.RandomProvider;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LTLProblem extends GPProblem {
    public static int EVENT_NUMBER;
    public static int ACTION_NUMBER;
    private static FST automaton, scenarios;
    private static FST[] testAutomatons, mutants, scMutants;
    private static Map<String, EvaluationResult> result = new HashMap<>();

    @Override
    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);
        EVENT_NUMBER = state.parameters.getInt(base.push("automaton").push("event-number"), null);
        ACTION_NUMBER = state.parameters.getInt(base.push("automaton").push("action-number"), null);
        int stateNumber = state.parameters.getInt(base.push("automaton").push("state-number"), null);
        automaton = Verifier.loadFST(state.parameters.getString(base.push("automaton").push("file"), null), stateNumber);
        scenarios = Verifier.loadFST(state.parameters.getString(base.push("automaton").push("scenarios"), null), stateNumber);
        testAutomatons = new FST[state.parameters.getInt(base.push("test-automatons").push("number"), null)];
        mutants = new FST[state.parameters.getInt(base.push("test-automatons").push("mutated"), null)];
        scMutants = new FST[state.parameters.getInt(base.push("automaton").push("scenarios").push("mutated"), null)];
        RandomProvider.initialize(1, System.currentTimeMillis());
        RandomProvider.register();
        //System.out.println("Mutants:");
        for (int i = 0; i < mutants.length; i++) {
            mutants[i] = automaton.mutate(false);
            //System.out.println(mutants[i]);
        }
        //System.out.println("ScMutants:");
        for (int i = 0; i < scMutants.length; i++) {
            scMutants[i] = scenarios.mutate(false);
            //System.out.println(scMutants[i]);
        }
        for (int i = 0; i < testAutomatons.length; i++) {
            //String name = "ats/tmp" + i + ".gv";
            try {
                Process p = Runtime.getRuntime().exec("./generate-efsm -n " + stateNumber + " -e " + EVENT_NUMBER + " -a " + ACTION_NUMBER + " -m 0 -s 2 -v 0");
                Scanner sc = new Scanner(p.getInputStream());
                BufferedWriter out = new BufferedWriter(new FileWriter(new File("tmp.gv")));
                while (sc.hasNext()) {
                    out.write(sc.nextLine() + "\n");
                }
                out.close();
            } catch (IOException e) {
                state.output.fatal("Error while generating test automatons: " + e.getMessage());
            }
            testAutomatons[i] = Verifier.loadFST("tmp.gv", stateNumber);
        }
        if (!(input instanceof LTLData)) {
            state.output.fatal("GPData class must subclass from " + LTLData.class, base.push(P_DATA));
        }
        /*double[] rating = getRating("G(!( wasEvent(ep.E) and X(wasEvent(ep.B)) ) or X(X(wasEvent(ep.A) or wasEvent(ep.C))))", 12);
        System.out.println("Target: ");
        for (double r : rating) {
            System.out.println(r);
        }*/
    }

    private double[] getRating(String formula, int size) {
        if (result.containsKey(formula)) {
            EvaluationResult er = result.get(formula);
            return new double[]{er.correctness, er.rRating, er.mRating, er.sRating, er.smRating, er.correctness >= 0.75 ? 1 / (1.0 + size) : 0};
        }
        double[] fitness = new double[6];
        fitness[0] = Verifier.getVerifiedTransitionsRatio(automaton, formula);
        if (fitness[0] >= 0.75) {
            fitness[3] = 1 - Verifier.getVerifiedTransitionsRatio(scenarios, formula);
            for (FST fst : testAutomatons) {
                fitness[1] += Math.pow(Verifier.getVerifiedTransitionsRatio(fst, formula), 2);
            }
            fitness[1] = 1 - fitness[1] / testAutomatons.length;
            for (FST fst : mutants) {
                fitness[2] += Math.pow(Verifier.getVerifiedTransitionsRatio(fst, formula), 2);
            }
            fitness[2] = 1 - fitness[2] / mutants.length;
            for (FST fst : scMutants) {
                fitness[4] += Math.pow(Verifier.getVerifiedTransitionsRatio(fst, formula), 2);
            }
            fitness[4] = 1 - fitness[4] / scMutants.length;
        }
        result.put(formula, new EvaluationResult(fitness[0], fitness[1], fitness[2], fitness[3], fitness[4]));
        if (fitness[0] >= 0.75) {
            fitness[5] = 1 / (1.0 + size);
        }
        return fitness;
    }

    @Override
    public void evaluate(EvolutionState state, Individual ind, int subpopulation, int threadnum) {
        if (!ind.evaluated) {
            LTLData input = (LTLData) this.input;
            ((GPIndividual) ind).trees[0].child.eval(state, threadnum, input, stack, ((GPIndividual) ind), this);
            SPEA2MultiObjectiveFitness f = (SPEA2MultiObjectiveFitness) ind.fitness;
            String formula = "G(" + input.result + ")";
            double[] rating = getRating(formula, input.size);
            f.setObjectives(state, rating);
            ind.evaluated = true;
        }
    }

    private static class EvaluationResult {
        private double correctness, rRating, mRating, sRating, smRating;

        public EvaluationResult(double correctness, double rRating, double mRating, double sRating, double smRating) {
            this.correctness = correctness;
            this.rRating = rRating;
            this.mRating = mRating;
            this.sRating = sRating;
            this.smRating = smRating;
        }
    }
}