package ltlgen.fitnesses;

import ec.EvolutionState;
import ec.util.Parameter;
import ru.ifmo.ctddev.genetic.transducer.algorithm.FST;
import ru.ifmo.ctddev.tools.Verifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MutatedFitness extends SingleFitness {
    private final List<FST> mutated = new ArrayList<>();

    @Override
    public void setup(EvolutionState state, Parameter base) {
        super.setup(state, base);
        Parameter a = new Parameter("automaton");
        String file = state.parameters.getString(a.push("file"), null);
        int stateNumber = state.parameters.getInt(a.push("state-number"), null);
        FST automaton = Verifier.loadFST(file, stateNumber);
        int number = state.parameters.getInt(base.push("number"), null);
        Set<String> m = new HashSet<>();
        m.add(automaton.toString());
        for (int i = 0; i < number; i++) {
            for (int j = 0; ; j++) {
                FST mt = automaton.mutate(false);
                if (!m.contains(mt.toString())) {
                    mutated.add(mt);
                    m.add(mt.toString());
                    break;
                }
                if (j == 99) {
                    state.output.warning("Couldn't create enough different mutants, " + mutated.size() + " will be used.");
                    return;
                }
            }
        }
    }

    @Override
    public double getFitness(String formula, int size) {
        double result = 0;
        for (FST fst : mutated) {
            result += Math.pow(Verifier.getVerifiedTransitionsRatio(fst, formula), 2);
        }
        result = 1 - result / mutated.size();
        return result < threshold ? -1 : result;
    }
}
