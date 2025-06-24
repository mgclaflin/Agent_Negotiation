package negotiation;

import java.util.ArrayList;
import java.util.List;

public class Contract {

    int[] contract;

    List<MutationType> mutationType = new ArrayList<>();

    public Contract(int[] contract) {
        this.contract = contract;
    }

    public int[] getContract() {
        return contract;
    }

    public List<MutationType> getMutationType() {
        return mutationType;
    }

    public void setContract(int[] contract) {
        this.contract = contract;
    }

    public void setMutationType(MutationType mutationType) {
        this.mutationType.add(mutationType);
    }
}
