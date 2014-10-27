package ltlgen.fitnesses;

import ec.EvolutionState;
import ec.util.Parameter;
import ltlgen.SingleFitness;
import ru.ifmo.ctddev.genetic.transducer.algorithm.FST;
import ru.ifmo.ctddev.tools.Verifier;

public class MutatedFitness extends SingleFitness {
    private FST[] mutated;

    @Override
    public void setup(EvolutionState state, Parameter base) {
        super.setup(state, base);
        Parameter a = new Parameter("automaton");
        String file = state.parameters.getString(a.push("file"), null);
        int stateNumber = state.parameters.getInt(a.push("state-number"), null);
        FST automaton = Verifier.loadFST(file, stateNumber);
        mutated = new FST[state.parameters.getInt(base.push("number"), null)];
        for (int i = 0; i < mutated.length; i++) {
            mutated[i] = automaton.mutate(false);
        }
    }

    @Override
    public double getFitness(String formula, int size) {
        double result = 0;
        for (FST fst : mutated) {
            result += Math.pow(Verifier.getVerifiedTransitionsRatio(fst, formula), 2);
        }
        result = 1 - result / mutated.length;
        return result < threshold ? -1 : result;
    }
}
