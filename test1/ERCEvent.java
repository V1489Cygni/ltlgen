package ec.app.ltlgen;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.*;
import ec.util.Code;

public class ERCEvent extends ERC {
    private int value;

    @Override
    public void resetNode(EvolutionState state, int thread) {
        value = state.random[thread].nextInt(LTLProblem.EVENT_NUMBER);
    }

    @Override
    public boolean nodeEquals(GPNode node) {
        return node instanceof ERCEvent && value == ((ERCEvent) node).value;
    }

    @Override
    public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual, Problem problem) {
        LTLData data = ((LTLData) (input));
        data.result = 1;
    }

    @Override
    public String encode() {
        return Code.encode(value);
    }

    @Override
    public String toStringForHumans() {
        return "e" + value;
    }
}
