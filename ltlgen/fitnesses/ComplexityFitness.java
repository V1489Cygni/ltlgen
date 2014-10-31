package ltlgen.fitnesses;

public class ComplexityFitness extends SingleFitness {
    @Override
    public double getFitness(String formula, int size) {
        double result = 1 / (1.0 + size);
        return result < threshold ? -1 : result;
    }
}
