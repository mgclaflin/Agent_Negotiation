import java.io.*;

public class Mediator {

	int contractSize;
	
	public Mediator(int contractSizeA, int contractSizeB) throws FileNotFoundException{
		if(contractSizeA != contractSizeB){
			throw new FileNotFoundException("Verhandlung kann nicht durchgefuehrt werden, da Problemdaten nicht kompatibel");
		}
		this.contractSize = contractSizeA;
	}
	
	public int[] initContract(){
		int[] contract = new int[contractSize];
		for(int i=0;i<contractSize;i++)contract[i] = i;
		
		for(int i=0;i<contractSize;i++) {
			int index1 = (int)(Math.random()*contractSize);
			int index2 = (int)(Math.random()*contractSize);
			int wertindex1 = contract[index1];
			contract[index1] = contract[index2];
			contract[index2] = wertindex1;
		}
		
		return contract;
	}

	//Two Mutation-Operators (Swap, Shift)
	public int[] constructProposal_SWAP(int[] contract) {
		int[] proposal = new int[contract.length];
		for(int i=0;i<proposal.length;i++)proposal[i] = contract[i];

		int element = (int)((proposal.length-1)*Math.random());
		int wert1   = proposal[element];
		int wert2   = proposal[element+1];
		proposal[element]   = wert2;
		proposal[element+1] = wert1;
		
		check(proposal);
		
		return proposal;
	}
	
	public int[] constructProposal_SHIFT(int[] contract) {
		int[] proposal = new int[contractSize];
		for(int i=0;i<proposal.length;i++)proposal[i] = contract[i];
		
		int index1 = (int)((proposal.length-1)*Math.random());
		int index2 = (int)((proposal.length-1)*Math.random());
		if(index1 > index2) {
			int tmp = index1;
			index1 = index2;
			index2 = tmp;
		}
		if(Math.random()<0.5) {
			int wert1 = proposal[index1];
			for(int i=index1;i<index2;i++) {
				proposal[i] = proposal[i+1];
			}
			proposal[index2] = wert1;
		}
		else {
			int wert2 = proposal[index2];
			for(int i=index2;i>index1;i--) {
				proposal[i] = proposal[i-1];
			}
			proposal[index1] = wert2;
		}
		check(proposal);
		return proposal;
	}
	
	public void check(int[] proposal) {
		int sum = 0;
		int summe = proposal.length*(proposal.length-1)/2;
		for(int i=0;i<proposal.length;i++) {
			sum += proposal[i];
		}
		if(sum != summe)System.out.println("Check Summe");
	}
	
	public int[] constructProposal(int[] contract) {
		int[] proposal;
		if(Math.random()<0.5) {
			proposal = constructProposal_SHIFT(contract);  
		}
		else {
			proposal = constructProposal_SWAP(contract); 
		}
		return proposal;
	}	
}
