package ltlgen.filters;

import ec.EvolutionState;
import ec.Setup;
import ec.util.Parameter;

public abstract class Filter implements Setup {
    protected double threshold;

    @Override
    public void setup(EvolutionState state, Parameter base) {
        if (state.parameters.exists(base.push("threshold"), null)) {
            threshold = state.parameters.getDouble(base.push("threshold"), null);
        }
    }

    public abstract boolean accepts(String formula, int complexity);
}
