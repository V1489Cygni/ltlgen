package ltlgen.fitnesses;

import ec.EvolutionState;
import ec.util.Parameter;
import ltlgen.SingleFitness;
import ru.ifmo.ctddev.genetic.transducer.algorithm.FST;
import ru.ifmo.ctddev.tools.Verifier;

public class ScenariosFitness extends SingleFitness {
    private FST scenarios;

    @Override
    public void setup(EvolutionState state, Parameter base) {
        super.setup(state, base);
        Parameter s = new Parameter("scenarios");
        String file = state.parameters.getString(s.push("file"), null);
        int stateNumber = state.parameters.getInt(s.push("state-number"), null);
        scenarios = Verifier.loadFST(file, stateNumber);
    }

    @Override
    public double getFitness(String formula, int size) {
        double result = 1 - Verifier.getVerifiedTransitionsRatio(scenarios, formula);
        return result < threshold ? -1 : result;
    }
}
