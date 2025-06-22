package agents;

import java.io.FileWriter;
import java.io.IOException;

public abstract class Agent {

	public abstract boolean vote(int[] contract, int[] proposal);
	public abstract void    printUtility(int[] contract);
	public abstract void    printUtility(int[] contract, FileWriter writer) throws IOException;
	public abstract int     getContractSize();
	
}
