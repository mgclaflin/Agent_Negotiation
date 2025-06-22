package out;

import agents.Agent;

import java.io.FileWriter;
import java.io.IOException;

public class CsvOutputStrategy implements OutputStrategy {

    private final FileWriter writer;
    private boolean headerWritten = false;

    public CsvOutputStrategy(String filename)  {
        try {
            writer = new FileWriter(filename, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void output(Agent supplierAgent, Agent consumerAgent, int i, int[] contract) {
        try {
            if (!headerWritten) {
                writer.write("Iteration,Supplier,Customer\n");
                headerWritten = true;
            }

            writer.write(i + ",");
            supplierAgent.printUtility(contract, writer);  // Agent 1 writes its utility
            writer.write(",");
            consumerAgent.printUtility(contract, writer);  // Agent 2 writes its utility
            writer.write("\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
