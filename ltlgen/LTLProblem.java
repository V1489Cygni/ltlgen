package ec.app.ltlgen;

import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
import ec.gp.koza.KozaFitness;
import ec.util.Parameter;
import ru.ifmo.ctddev.genetic.transducer.algorithm.FST;
import ru.ifmo.ctddev.tools.Verifier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class LTLProblem extends GPProblem {
    public static int EVENT_NUMBER = 2;
    public static int ACTION_NUMBER = 2;
    private static FST automaton;
    private static FST[] testAut;

    @Override
    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);
        EVENT_NUMBER = state.parameters.getInt(base.push("en"), null);
        ACTION_NUMBER = state.parameters.getInt(base.push("an"), null);
        int ns = state.parameters.getInt(base.push("automaton-ns"), null);
        automaton = Verifier.loadFST(state.parameters.getString(base.push("automaton"), null), ns);
        testAut = new FST[state.parameters.getInt(base.push("test").push("size"), null)];
        for (int i = 0; i < testAut.length; i++) {
            try {
                Process p = Runtime.getRuntime().exec("./generate-efsm -n " + ns + " -e " + EVENT_NUMBER + " -a " + ACTION_NUMBER + " -m 0 -s 2 -v 1");
                Scanner sc = new Scanner(p.getInputStream());
                BufferedWriter out = new BufferedWriter(new FileWriter(new File("tmp.gv")));
                while (sc.hasNext()) {
                    out.write(sc.nextLine() + "\n");
                }
                out.close();
            } catch (IOException e) {
                state.output.fatal("Error while generating automatons");
            }
            testAut[i] = Verifier.loadFST("tmp.gv", ns);
        }
        if (!(input instanceof LTLData)) {
            state.output.fatal("GPData class must subclass from " + LTLData.class, base.push(P_DATA), null);
        }
    }

    @Override
    public void evaluate(EvolutionState state, Individual ind, int subpopulation, int threadnum) {
        if (!ind.evaluated) {
            LTLData input = (LTLData) this.input;
            ((GPIndividual) ind).trees[0].child.eval(state, threadnum, input, stack, ((GPIndividual) ind), this);
            KozaFitness f = ((KozaFitness) ind.fitness);
            String formula = input.result;
            int result = Verifier.isFormulaTrue(automaton, formula) ? 0 : Integer.MAX_VALUE;
            if (result == 0) {
                for (FST fst : testAut) {
                    if (Verifier.isFormulaTrue(fst, formula)) {
                        result++;
                    }
                }
            }
            f.setStandardizedFitness(state, result);
            ind.evaluated = true;
        }
    }
}