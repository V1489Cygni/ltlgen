package ltlgen.filters;

import ec.EvolutionState;
import ec.util.Parameter;
import ru.ifmo.ctddev.genetic.transducer.algorithm.FST;
import ru.ifmo.ctddev.tools.Verifier;

public class CorrectnessFilter extends Filter {
    private FST automaton;

    @Override
    public void setup(EvolutionState state, Parameter base) {
        super.setup(state, base);
        Parameter a = new Parameter("automaton");
        String file = state.parameters.getString(a.push("file"), null);
        int stateNumber = state.parameters.getInt(a.push("state-number"), null);
        automaton = Verifier.loadFST(file, stateNumber);
    }

    @Override
    public boolean accepts(String formula, int size) {
        return Verifier.isFormulaTrue(automaton, formula);
    }
}
