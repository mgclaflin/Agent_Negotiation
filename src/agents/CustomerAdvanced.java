package agents;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class CustomerAdvanced extends Agent {

	private int[][] timeMatrix;
	private int[][] delayMatrix;// calculated based on timeMatrix for efficiency reason

	
	public CustomerAdvanced(File file) throws FileNotFoundException {

		Scanner scanner = new Scanner(file);
		int jobs = scanner.nextInt();
		int machines = scanner.nextInt();
		timeMatrix = new int[jobs][machines];
		for (int i = 0; i < timeMatrix.length; i++) {
			for (int j = 0; j < timeMatrix[i].length; j++) {
				int x = scanner.nextInt();
				timeMatrix[i][j] = x;
			}
		}
		calculateDelay(timeMatrix.length);		
		scanner.close();
	}

	public boolean vote(int[] contract, int[] proposal) {
		int timeContract = evaluate(contract);
		int timeProposal = evaluate(proposal);
		if (timeProposal < timeContract)
			return true;
		else
			return false;
	}

	public int getContractSize() {
		return timeMatrix.length;
	}

	public void printUtility(int[] contract) {
		System.out.print(evaluate(contract));
	}

	@Override
	public void printUtility(int[] contract, FileWriter writer) throws IOException {
		int utility = evaluate(contract);
		writer.write(String.valueOf(utility));
	}

	private void calculateDelay(int jobNr) {
		delayMatrix = new int[jobNr][jobNr];
		for (int h = 0; h < jobNr; h++) {
			for (int j = 0; j < jobNr; j++) {
				delayMatrix[h][j] = 0;
				if (h != j) {
					int maxWait = 0;
					for (int machine = 0; machine < timeMatrix[0].length; machine++) {
						int wait_h_j_machine;

						int time1 = 0;
						for (int k = 0; k <= machine; k++) {
							time1 += timeMatrix[h][k];
						}
						int time2 = 0;
						for (int k = 1; k <= machine; k++) {
							time2 += timeMatrix[j][k - 1];
						}
						wait_h_j_machine = Math.max(time1 - time2, 0);
						if (wait_h_j_machine > maxWait)
							maxWait = wait_h_j_machine;
					}
					delayMatrix[h][j] = maxWait;
				}
			}
		}
	}

	private int evaluate(int[] contract) {
		int result = 0;
		for (int i = 1; i < contract.length; i++) {
			int jobVor = contract[i - 1];
			int job = contract[i];
			result += delayMatrix[jobVor][job];
		}
		int lastjob = contract[contract.length - 1];
		for (int machine = 0; machine < timeMatrix[0].length; machine++) {
			result += timeMatrix[lastjob][machine];
		}
		return result;
	}

}