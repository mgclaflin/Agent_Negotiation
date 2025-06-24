package out;

import agents.Agent;
import negotiation.MutationType;

import java.util.List;

public class PrintOutputStrategy implements OutputStrategy {

    @Override
    public  void output(Agent a1, Agent a2, int i, int[] contract, List<MutationType> mutationType){
        System.out.print(i + " -> " );
        a1.printUtility(contract);
        System.out.print("  ");
        a2.printUtility(contract);
        System.out.println();
    }
}
