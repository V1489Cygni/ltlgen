package ltlgen.formulas;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.*;
import ec.util.Code;
import ltlgen.LTLData;
import ltlgen.LTLProblem;

public class Event extends ERC implements Verifiable {
    private int value;

    @Override
    public void resetNode(EvolutionState state, int thread) {
        value = state.random[thread].nextInt(LTLProblem.EVENT_NUMBER);
    }

    @Override
    public boolean nodeEquals(GPNode node) {
        return node instanceof Event && value == ((Event) node).value;
    }

    @Override
    public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual, Problem problem) {
        LTLData data = ((LTLData) (input));
        data.result = "wasEvent(ep." + (char) ('A' + value) + ")";
        data.complexity = 1;
    }

    @Override
    public String encode() {
        return Code.encode(value);
    }

    @Override
    public String toStringForHumans() {
        return toString();
    }

    @Override
    public String toStringForVerifier() {
        return toString();
    }

    @Override
    public String toString() {
        return "wasEvent(ep." + (char) ('A' + value) + ")";
    }
}
