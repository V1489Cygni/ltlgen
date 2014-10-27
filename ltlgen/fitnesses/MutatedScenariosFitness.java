package ltlgen.fitnesses;

import ec.EvolutionState;
import ec.util.Parameter;
import ltlgen.SingleFitness;
import ru.ifmo.ctddev.genetic.transducer.algorithm.FST;
import ru.ifmo.ctddev.tools.Verifier;

public class MutatedScenariosFitness extends SingleFitness {
    private FST[] mutated;

    @Override
    public void setup(EvolutionState state, Parameter base) {
        super.setup(state, base);
        Parameter s = new Parameter("scenarios");
        String file = state.parameters.getString(s.push("file"), null);
        int stateNumber = state.parameters.getInt(s.push("state-number"), null);
        FST scenarios = Verifier.loadFST(file, stateNumber);
        mutated = new FST[state.parameters.getInt(base.push("number"), null)];
        for (int i = 0; i < mutated.length; i++) {
            mutated[i] = scenarios.mutate(false);
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
