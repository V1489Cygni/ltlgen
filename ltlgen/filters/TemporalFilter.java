package ltlgen.filters;

public class TemporalFilter extends Filter {

    @Override
    public boolean accepts(String formula, int size) {
        if (formula.charAt(2) == 'X' || formula.charAt(2) == 'F' || formula.charAt(2) == '!') {
            return false;
        }
        for (int i = 0; i < formula.length(); i++) {
            char c = formula.charAt(i);
            if (c == 'X' || c == 'F' || c == 'U' || c == 'R') {
                return true;
            }
        }
        return false;
    }
}
