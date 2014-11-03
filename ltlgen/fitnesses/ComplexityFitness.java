package ltlgen.fitnesses;

public class ComplexityFitness extends SingleFitness {
    @Override
    public double getFitness(String formula, int complexity) {
        double result = 1 / (1.0 + complexity);
        return result < threshold ? -1 : result;
    }
}
