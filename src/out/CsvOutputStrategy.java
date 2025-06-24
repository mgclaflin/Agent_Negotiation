package out;

import agents.Agent;
import negotiation.MutationType;

import java.io.FileWriter;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;

public class CsvOutputStrategy implements OutputStrategy {

    private final FileWriter writer;
    private boolean headerWritten = false;

    public CsvOutputStrategy(String filename) {
        try {
            writer = new FileWriter(filename, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void output(Agent supplierAgent, Agent consumerAgent, int i, int[] contract, List<MutationType> mutationTypes) {
        try {
            if (!headerWritten) {
                writer.write("Iteration,Supplier,Customer");
                for (MutationType type : MutationType.values()) {
                    writer.write("," + type);
                }
                writer.write("\n");
                headerWritten = true;
            }

            writer.write(i + ",");
            supplierAgent.printUtility(contract, writer);
            writer.write(",");
            consumerAgent.printUtility(contract, writer);

            // Track presence of each mutation type
            EnumSet<MutationType> presentMutations = EnumSet.noneOf(MutationType.class);
            if (mutationTypes != null) {
                presentMutations.addAll(mutationTypes);
            }

            for (MutationType type : MutationType.values()) {
                writer.write(",");
                if (presentMutations.contains(type)) {
                    writer.write("1");
                } else {
                    writer.write("");  // leave cell empty
                }
            }

            writer.write("\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
