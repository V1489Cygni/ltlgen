package ec.app.ltlgen;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.*;
import ec.util.Code;

public class ERCAction extends ERC {
    private int value;

    @Override
    public void resetNode(EvolutionState state, int thread) {
        value = state.random[thread].nextInt(LTLProblem.ACTION_NUMBER);
    }

    @Override
    public boolean nodeEquals(GPNode node) {
        return node instanceof ERCAction && value == ((ERCAction) node).value;
    }

    @Override
    public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual, Problem problem) {
        LTLData data = ((LTLData) (input));
        data.result = "wasAction(co.z" + value + ")";
    }

    @Override
    public String encode() {
        return Code.encode(value);
    }

    @Override
    public String toStringForHumans() {
        return "wasAction(co.z" + value + ")";
    }
}
