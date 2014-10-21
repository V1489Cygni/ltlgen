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
    private static FST automaton;
    private static FST[] testAutomatons;
    private static FST[] mutants;
    private static Map<String, Double> correctness = new HashMap<>();
    private static Map<String, Double> results = new HashMap<>();
    private static Map<String, Double> mResults = new HashMap<>();

    @Override
    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);
        EVENT_NUMBER = state.parameters.getInt(base.push("automaton").push("event-number"), null);
        ACTION_NUMBER = state.parameters.getInt(base.push("automaton").push("action-number"), null);
        int stateNumber = state.parameters.getInt(base.push("automaton").push("state-number"), null);
        automaton = Verifier.loadFST(state.parameters.getString(base.push("automaton").push("file"), null), stateNumber);
        testAutomatons = new FST[state.parameters.getInt(base.push("test-automatons").push("number"), null)];
        mutants = new FST[state.parameters.getInt(base.push("test-automatons").push("mutated"), null)];
        RandomProvider.initialize(1, System.currentTimeMillis());
        RandomProvider.register();
        for (int i = 0; i < mutants.length; i++) {
            mutants[i] = automaton.mutate(false);
        }
        /*for (int i = mutants.length / 2; i < mutants.length; i++) {
            mutants[i] = mutants[i].mutate(false);
        }*/
        for (int i = 0; i < testAutomatons.length; i++) {
            try {
                Process p = Runtime.getRuntime().exec("./generate-efsm -n " + stateNumber + " -e " + EVENT_NUMBER + " -a " + ACTION_NUMBER + " -m 0 -s 2 -v 2");
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
    }

    @Override
    public void evaluate(EvolutionState state, Individual ind, int subpopulation, int threadnum) {
        if (!ind.evaluated) {
            LTLData input = (LTLData) this.input;
            ((GPIndividual) ind).trees[0].child.eval(state, threadnum, input, stack, ((GPIndividual) ind), this);
            SPEA2MultiObjectiveFitness f = (SPEA2MultiObjectiveFitness) ind.fitness;
            String formula = "G(" + input.result + ")";
            double result = 0, size = 0, mRes = 0, r;
            if (correctness.containsKey(formula)) {
                r = correctness.get(formula);
                result = results.get(formula);
                mRes = mResults.get(formula);
            } else {
                r = Verifier.getVerifiedTransitionsRatio(automaton, formula);
                if (r >= 0.75) {
                    for (FST fst : testAutomatons) {
                        double t = Verifier.getVerifiedTransitionsRatio(fst, formula);
                        result += Math.pow(t, 2);
                    }
                    result = 1 - Math.sqrt(result) / testAutomatons.length;
                    for (FST fst : mutants) {
                        double t = Verifier.getVerifiedTransitionsRatio(fst, formula);
                        mRes += Math.pow(t, 2);
                    }
                    mRes = 1 - Math.sqrt(mRes) / mutants.length;
                }
                correctness.put(formula, r);
                results.put(formula, result);
                mResults.put(formula, mRes);
            }
            if (r >= 0.75) {
                size = 1 / (1.0 + input.size);
            }
            f.setObjectives(state, new double[]{r, result, mRes, size});
            ind.evaluated = true;
        }
    }
}