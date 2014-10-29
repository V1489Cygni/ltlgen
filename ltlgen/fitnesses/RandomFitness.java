package ltlgen.fitnesses;

import ec.EvolutionState;
import ec.util.Parameter;
import ltlgen.SingleFitness;
import ru.ifmo.ctddev.genetic.transducer.algorithm.FST;
import ru.ifmo.ctddev.tools.Verifier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class RandomFitness extends SingleFitness {
    private FST[] random;

    @Override
    public void setup(EvolutionState state, Parameter base) {
        super.setup(state, base);
        Parameter a = new Parameter("automaton");
        int stateNumber = state.parameters.getInt(a.push("state-number"), null);
        int eventNumber = state.parameters.getInt(a.push("event-number"), null);
        int actionNumber = state.parameters.getInt(a.push("action-number"), null);
        int variableNumber = state.parameters.getInt(a.push("variable-number"), null);
        random = new FST[state.parameters.getInt(base.push("number"), null)];
        for (int i = 0; i < random.length; i++) {
            try {
                String cmd = "./resources/generate-efsm -n " + stateNumber + " -e " + eventNumber + " -a " + actionNumber + " -m 0 -s 2 -v " + variableNumber;
                Process p = Runtime.getRuntime().exec(cmd);
                Scanner sc = new Scanner(p.getInputStream());
                BufferedWriter out = new BufferedWriter(new FileWriter(new File("tmp.gv")));
                while (sc.hasNext()) {
                    out.write(sc.nextLine() + "\n");
                }
                out.close();
            } catch (IOException e) {
                state.output.fatal("Error while generating random automatons: " + e.getMessage());
            }
            random[i] = Verifier.loadFST("tmp.gv", stateNumber);
        }
    }

    @Override
    public double getFitness(String formula, int size) {
        double result = 0;
        for (FST fst : random) {
            result += Math.pow(Verifier.getVerifiedTransitionsRatio(fst, formula), 2);
        }
        result = 1 - result / random.length;
        return result < threshold ? -1 : result;
    }
}
