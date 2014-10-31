package ltlgen;

import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
import ec.multiobjective.MultiObjectiveFitness;
import ec.util.Parameter;
import ltlgen.fitnesses.SingleFitness;
import ru.ifmo.random.RandomProvider;

import java.util.HashMap;
import java.util.Map;

public class LTLProblem extends GPProblem {
    public static int EVENT_NUMBER;
    public static int ACTION_NUMBER;
    private static SingleFitness[] fitnesses;
    private static final Map<String, EvaluationResult> results = new HashMap<>();

    @Override
    public void setup(final EvolutionState state, Parameter base) {
        super.setup(state, base);
        if (!(input instanceof LTLData)) {
            state.output.fatal("GPData class must subclass from " + LTLData.class, base.push(P_DATA));
        }
        RandomProvider.initialize(1, System.currentTimeMillis());
        RandomProvider.register();
        Parameter a = new Parameter("automaton");
        EVENT_NUMBER = state.parameters.getInt(a.push("event-number"), null);
        ACTION_NUMBER = state.parameters.getInt(a.push("action-number"), null);
        fitnesses = new SingleFitness[state.parameters.getInt(new Parameter(new String[]{"multi", "fitness", "num-objectives"}), null)];
        Parameter f = base.push("fitness");
        for (int i = 0; i < fitnesses.length; i++) {
            Class c = state.parameters.getClassForParameter(f.push(Integer.toString(i)), null, SingleFitness.class);
            try {
                fitnesses[i] = (SingleFitness) c.newInstance();
                fitnesses[i].setup(state, f.push(Integer.toString(i)));
            } catch (InstantiationException | IllegalAccessException e) {
                state.output.fatal("Error while loading fitness function: " + e.getMessage());
            }
        }
    }

    private double[] getFitness(String formula, int size) {
        if (results.containsKey(formula)) {
            return results.get(formula).result;
        }
        double[] result = new double[fitnesses.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = fitnesses[i].getFitness(formula, size);
            if (result[i] == -1) {
                for (int j = 0; j <= i; j++) {
                    result[j] = 0;
                }
                break;
            }
        }
        results.put(formula, new EvaluationResult(result));
        return result;
    }

    @Override
    public void evaluate(EvolutionState state, Individual ind, int subpopulation, int threadnum) {
        if (!ind.evaluated) {
            LTLData input = (LTLData) this.input;
            GPIndividual individual = (GPIndividual) ind;
            individual.trees[0].child.eval(state, threadnum, input, stack, individual, this);
            MultiObjectiveFitness f = (MultiObjectiveFitness) ind.fitness;
            String formula = "G(" + input.result + ")";
            f.setObjectives(state, getFitness(formula, input.complexity));
            ind.evaluated = true;
        }
    }

    private static class EvaluationResult {
        private final double[] result;

        public EvaluationResult(double[] result) {
            this.result = result;
        }
    }
}