package out;

import agents.Agent;

public interface OutputStrategy {
    void output(Agent a1, Agent a2, int i, int[] contract);
}
