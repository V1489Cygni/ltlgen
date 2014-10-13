package ec.app.test1;

import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
import ec.gp.koza.KozaFitness;
import ec.util.Parameter;

public class LTLProblem extends GPProblem {
    public static final int EVENT_NUMBER = 10;
    public static final int ACTION_NUMBER = 10;

    @Override
    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);
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
            f.setStandardizedFitness(state, Math.abs(input.result - 50));
            ind.evaluated = true;
        }
    }
}