package ltlgen.fitnesses;

import ec.EvolutionState;
import ec.util.Parameter;
import ltlgen.SingleFitness;

public class ComplexityFitness extends SingleFitness {
    @Override
    public void setup(EvolutionState state, Parameter base) {
        super.setup(state, base);
    }

    @Override
    public double getFitness(String formula, int size) {
        double result = 1 / (1.0 + size);
        return result < threshold ? -1 : result;
    }
}
