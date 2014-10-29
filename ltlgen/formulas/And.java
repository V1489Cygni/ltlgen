package ltlgen.formulas;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ltlgen.LTLData;

public class And extends GPNode implements Verifiable {
    @Override
    public int expectedChildren() {
        return 2;
    }

    @Override
    public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual, Problem problem) {
        LTLData data = ((LTLData) (input));
        children[0].eval(state, thread, input, stack, individual, problem);
        String left = data.result;
        int size = data.complexity;
        children[1].eval(state, thread, input, stack, individual, problem);
        data.result = "(" + left + " and " + data.result + ")";
        data.complexity += size + 1;
    }

    @Override
    public String toStringForHumans() {
        return "(" + children[0].toStringForHumans() + " and " + children[1].toStringForHumans() + ")";
    }

    @Override
    public String toStringForVerifier() {
        return "(" + ((Verifiable) children[0]).toStringForVerifier() + " and " + ((Verifiable) children[1]).toStringForVerifier() + ")";
    }

    @Override
    public String toString() {
        return "and";
    }
}
