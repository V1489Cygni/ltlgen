package ec.app.test1;

import ec.gp.GPData;

public class LTLData extends GPData {
    public int result;

    public void copyTo(final GPData gpd) {
        ((LTLData) gpd).result = result;
    }
}
