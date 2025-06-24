package out;

import agents.Agent;
import negotiation.MutationType;

import java.util.List;

public interface OutputStrategy {
    void output(Agent a1, Agent a2, int i, int[] contract, List<MutationType> mutationType);
}
