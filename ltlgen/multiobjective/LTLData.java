package ltlgen.multiobjective;

import ec.gp.GPData;

public class LTLData extends GPData {
    public String result;
    public int size;

    public void copyTo(final GPData gpd) {
        ((LTLData) gpd).result = result;
        ((LTLData) gpd).size = size;
    }
}
