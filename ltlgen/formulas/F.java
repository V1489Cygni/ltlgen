package ltlgen.formulas;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ltlgen.LTLData;

public class F extends GPNode implements Verifiable {
    @Override
    public int expectedChildren() {
        return 1;
    }

    @Override
    public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual, Problem problem) {
        LTLData data = ((LTLData) (input));
        children[0].eval(state, thread, input, stack, individual, problem);
        data.result = "F(" + data.result + ")";
        data.complexity += 5;
    }

    @Override
    public String toStringForHumans() {
        return "F(" + children[0].toStringForHumans() + ")";
    }

    @Override
    public String toStringForVerifier() {
        return "F(" + ((Verifiable) children[0]).toStringForVerifier() + ")";
    }

    @Override
    public String toString() {
        return "F";
    }
}
