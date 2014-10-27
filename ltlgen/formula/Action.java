package ltlgen.formula;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.*;
import ec.util.Code;
import ltlgen.LTLData;
import ltlgen.LTLProblem;

public class Action extends ERC {
    private int value;

    @Override
    public void resetNode(EvolutionState state, int thread) {
        value = state.random[thread].nextInt(LTLProblem.ACTION_NUMBER);
    }

    @Override
    public boolean nodeEquals(GPNode node) {
        return node instanceof Action && value == ((Action) node).value;
    }

    @Override
    public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual, Problem problem) {
        LTLData data = ((LTLData) (input));
        data.result = "wasAction(co.z" + value + ")";
        data.size = 1;
    }

    @Override
    public String encode() {
        return Code.encode(value);
    }

    @Override
    public String toStringForHumans() {
        return "wasAction(co.z" + value + ")";
    }

    @Override
    public String toString() {
        return "wasAction(co.z" + value + ")";
    }
}
