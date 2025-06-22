package out;

import agents.Agent;

public class PrintOutputStrategy implements OutputStrategy {

    @Override
    public  void output(Agent a1, Agent a2, int i, int[] contract){
        System.out.print(i + " -> " );
        a1.printUtility(contract);
        System.out.print("  ");
        a2.printUtility(contract);
        System.out.println();
    }
}
