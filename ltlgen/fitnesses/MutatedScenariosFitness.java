package ltlgen.fitnesses;

import ec.EvolutionState;
import ec.util.Parameter;
import ru.ifmo.ctddev.genetic.transducer.algorithm.FST;
import ru.ifmo.ctddev.tools.Verifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MutatedScenariosFitness extends SingleFitness {
    private final List<FST> mutated = new ArrayList<>();

    @Override
    public void setup(EvolutionState state, Parameter base) {
        super.setup(state, base);
        Parameter s = new Parameter("scenarios");
        String file = state.parameters.getString(s.push("file"), null);
        int stateNumber = state.parameters.getInt(s.push("state-number"), null);
        FST scenarios = Verifier.loadFST(file, stateNumber);
        int number = state.parameters.getInt(base.push("number"), null);
        Set<String> m = new HashSet<>();
        m.add(scenarios.toString());
        for (int i = 0; i < number; i++) {
            for (int j = 0; ; j++) {
                FST mt = scenarios.mutate(false);
                if (!m.contains(mt.toString())) {
                    mutated.add(mt);
                    m.add(mt.toString());
                    break;
                }
                if (j == 99) {
                    state.output.warning("Couldn't create enough different scenarios mutants, " + mutated.size() + " will be used.");
                    return;
                }
            }
        }
    }

    @Override
    public double getFitness(String formula, int complexity) {
        double result = 0;
        for (FST fst : mutated) {
            result += Math.pow(Verifier.getVerifiedTransitionsRatio(fst, formula), 2);
        }
        result = 1 - result / mutated.size();
        return result < threshold ? -1 : result;
    }
}
