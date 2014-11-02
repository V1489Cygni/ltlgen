package ltlgen.formulas;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ltlgen.LTLData;

public class Or extends GPNode implements Verifiable {
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
        data.result = "(" + left + " or " + data.result + ")";
        data.complexity += size + 1;
        if (left.charAt(0) == '!') {
            data.complexity--;
        }
    }

    @Override
    public String toStringForHumans() {
        String left = children[0].toStringForHumans(), right = children[1].toStringForHumans();
        if (left.charAt(0) == '!') {
            return "(" + left.substring(2, left.length() - 1) + " -> " + right + ")";
        } else if (right.charAt(0) == '!') {
            return "(" + right.substring(2, right.length() - 1) + " -> " + left + ")";
        } else {
            return "(" + left + " or " + children[1].toStringForHumans() + ")";
        }
    }

    @Override
    public String toStringForVerifier() {
        return "(" + ((Verifiable) children[0]).toStringForVerifier() + " or " + ((Verifiable) children[1]).toStringForVerifier() + ")";
    }

    @Override
    public String toString() {
        return "or";
    }
}
