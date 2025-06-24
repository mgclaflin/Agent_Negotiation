package negotiation;

import agents.Agent;
import agents.CustomerAdvanced;
import agents.SupplierAgent;
import out.CsvOutputStrategy;
import out.OutputStrategy;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


// DO NOT USE EVALUATE-FUNCTIONS OF AGENTS WITHIN MEDIATOR OR NEGOTIATION!
// THESE OBJECTIVE-VALUES ARE NOT AVAILABLE IN REAL NEGOTIATIONS!!!!!!!!!!!!!!!!!!!!
// IT IS ONLY ALLOWED TO PRINT THESE OBJECTIVE-VALUES IN THE CONSOLE FOR ANALYZING REASONS

public class Negotiation {
		//Parameter of negotiation
		public static int maxRounds = 1_000_000;
		public static int populationSize = 15;

		public static OutputStrategy out = new CsvOutputStrategy("data/refinement_test.csv");
		
		public static void main(String[] args) {
			Contract contract, proposal;
			Agent agA, agB;
			Mediator med;
			boolean voteA, voteB;

			try{	
				String[] inSu200 = new String[1];
				String[] inCu200 = new String[1];
				//inSu200[0] = "data/daten3ASupplier_200.txt";
				//inSu200[1] = "data/daten3BSupplier_200.txt";
				//inSu200[2] = "data/daten4ASupplier_200.txt";
				//inSu200[3] = "data/daten4BSupplier_200.txt";
				//inCu200[0] = "data/daten3ACustomer_200_10.txt";
				//inCu200[1] = "data/daten3BCustomer_200_20.txt";
				//inCu200[2] = "data/daten4ACustomer_200_5.txt";
				//inCu200[3] = "data/daten4BCustomer_200_5.txt";

				inSu200[0] = "data/daten3ASupplier_200.txt";
				inCu200[0] = "data/daten3ACustomer_200_10.txt";

				//inSu200[0]= "data/daten3BSupplier_200.txt";
				//inCu200[0] = "data/daten3BCustomer_200_20.txt";

				//inSu200[0] = "data/daten4ASupplier_200.txt";
				//inCu200[0] = "data/daten4ACustomer_200_5.txt";

				//inSu200[0] = "data/daten4BSupplier_200.txt";
				//inCu200[0] = "data/daten4BCustomer_200_5.txt";

				for(int i=0;i<inSu200.length;i++) {
					for(int j=0;j<inCu200.length;j++) {
						System.out.println("Instance: " + i + " " + j);
						agA       = new SupplierAgent(new File(inSu200[i]));
						agB       = new CustomerAdvanced(new File(inCu200[j]));
						med       = new Mediator(agA.getContractSize(), agB.getContractSize());
						contract = new Contract(med.initContract());
						Contract contractA = new Contract(med.initContract());
						Contract contractB = new Contract(med.initContract());
						out.output(agA, agB, 0, contract.contract, contract.mutationType);
						
						for(int round=1;round<maxRounds;round++) {
							List<Contract> population;
							if(round == 1) {
								population = med.generatePopulation(contract, 10);
							}
							else{
								population = med.generatePopulationFromContracts(contract, contractA, contractB, 10);
							}

							List<Contract> preferredByA = new ArrayList<>();
							List<Contract> preferredByB = new ArrayList<>();

							//Narrow down the contracts to those that are better than the current solution for each agent
							for (Contract candidate : population) {
								if (agA.vote(contract.contract, candidate.contract)) preferredByA.add(candidate);
								if (agB.vote(contract.contract, candidate.contract)) preferredByB.add(candidate);
							}

							if(preferredByA.isEmpty() && preferredByB.isEmpty()){
								//System.out.println(round + " No preferred proposals this round by either agent, skipping");
								continue;
							}

							final Agent agentA = agA;
							final Agent agentB = agB;

							// Rank preferredByA using agent A's votes
							preferredByA.sort((c1, c2) -> {
								if (Arrays.equals(c1.contract, c2.contract)) return 0;
								return agentA.vote(c2.contract, c1.contract) ? -1 : 1; // If agent A prefers c1 over c2, c1 comes first
							});

							// Rank preferredByB using agent B's votes
							preferredByB.sort((c1, c2) -> {
								if (Arrays.equals(c1.contract, c2.contract)) return 0;
								return agentB.vote(c2.contract, c1.contract) ? -1 : 1; // If agent B prefers c1 over c2, c1 comes first
							});

							if(!preferredByA.isEmpty()) contractA = preferredByA.getFirst();
							if(!preferredByB.isEmpty()) contractB = preferredByB.getFirst();

							List<Contract> mutuallyPreferred = preferredByA.stream()
									.filter(a -> preferredByB.stream().anyMatch(b -> Arrays.equals(a.contract, b.contract)))
									.toList();

							proposal = med.rouletteWheelSelectionC(mutuallyPreferred, preferredByA, preferredByB, contract);

							//System.out.println("Selecting proposal using roulette wheel...");
							//proposal = med.rouletteWheelSelection(preferredByA, preferredByB);

							voteA    = agA.vote(contract.contract, proposal.contract);            //Autonomie + Private Infos
							voteB    = agB.vote(contract.contract, proposal.contract);
							if(voteA && voteB ) {
								contract = proposal;
								out.output(agA, agB, round, contract.contract, contract.mutationType);
							}
//							else{
//								System.out.println(round + " solution not selected this round");
//							}
						}			
					}
				}
			}
			catch(FileNotFoundException e){
				System.out.println(e.getMessage());
			}
		}


}