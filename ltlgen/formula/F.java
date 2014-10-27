package ltlgen.formula;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ltlgen.LTLData;

public class F extends GPNode {
    @Override
    public int expectedChildren() {
        return 1;
    }

    @Override
    public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual, Problem problem) {
        LTLData data = ((LTLData) (input));
        children[0].eval(state, thread, input, stack, individual, problem);
        data.result = "F(" + data.result + ")";
        data.size++;
    }

    @Override
    public String toStringForHumans() {
        return "F(" + children[0].toStringForHumans() + ")";
    }

    @Override
    public String toString() {
        return "F";
    }
}
