package ltlgen;

import ec.gp.GPData;

public class LTLData extends GPData {
    public String result;
    public int size;

    public void copyTo(GPData gpd) {
        LTLData ld = (LTLData) gpd;
        ld.result = result;
        ld.size = size;
    }
}
