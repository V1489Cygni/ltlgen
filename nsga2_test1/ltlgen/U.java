package ec.app.ltlgen;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

public class U extends GPNode {
    @Override
    public int expectedChildren() {
        return 2;
    }

    @Override
    public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual, Problem problem) {
        LTLData data = ((LTLData) (input));
        children[0].eval(state, thread, input, stack, individual, problem);
        String left = data.result;
        int size = data.size;
        children[1].eval(state, thread, input, stack, individual, problem);
        data.result = "U(" + left + ", " + data.result + ")";
        data.size += size + 1;
    }

    @Override
    public String toStringForHumans() {
        return "U(" + children[0].toStringForHumans() + ", " + children[1].toStringForHumans() + ")";
    }

    @Override
    public String toString() {
        return "U";
    }
}
