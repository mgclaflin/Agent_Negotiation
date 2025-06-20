import java.io.*;
import java.util.*;

public class Mediator {

	int contractSize;
	Random rand = new Random();
	
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

	public List<int[]> generatePopulation(int[] baseContract, int count) {
		List<int[]> population = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			int[] copy = Arrays.copyOf(baseContract, baseContract.length);
			mutate(copy);
			population.add(copy);
		}
		return population;
	}

	public List<int[]> generatePopulationFromContracts(int[] base, int[] bestA, int[] bestB, int size) {
		List<int[]> population = new ArrayList<>();
		Random rand = new Random();

		for (int i = 0; i < size; i++) {
			int[] parent1 = switch (rand.nextInt(3)) {
				case 0 -> base;
				case 1 -> bestA;
				default -> bestB;
			};
			int[] parent2 = switch (rand.nextInt(3)) {
				case 0 -> base;
				case 1 -> bestA;
				default -> bestB;
			};

			int[] child = crossover(parent1, parent2);
			mutate(child); // Optional: include mutation logic
			population.add(child);
		}

		return population;
	}


	public int[] crossover(int[] parent1, int[] parent2) {
		if (parent1.length != parent2.length) {
			throw new IllegalArgumentException("Parent arrays must be the same length.");
		}

		int length = parent1.length;
		int[] child = new int[length];
		Arrays.fill(child, -1);

		// Step 1: Choose 3 crossover points
		Random rand = new Random();
		int[] points = new int[3];
		for (int i = 0; i < 3; i++) points[i] = rand.nextInt(length);
		Arrays.sort(points); // ensure increasing order
		int p1 = points[0], p2 = points[1], p3 = points[2];

		// Step 2: Copy segments alternatively from parent1 and parent2
		// Segments: [0..p1), [p1..p2), [p2..p3), [p3..end)

		// Segment 1 from parent1
		copySegment(child, parent1, 0, p1);
		// Segment 2 from parent2
		copySegment(child, parent2, p1, p2);
		// Segment 3 from parent1
		copySegment(child, parent1, p2, p3);
		// Segment 4 from parent2
		copySegment(child, parent2, p3, length);

		// Step 3: Resolve duplicates and fill in missing values
		boolean[] used = new boolean[length];
		for (int val : child) {
			if (val >= 0) used[val] = true;
		}

		// Collect missing values
		List<Integer> missing = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			if (!used[i]) missing.add(i);
		}

		// Replace duplicates with missing values
		Set<Integer> seen = new HashSet<>();
		int missingIndex = 0;
		for (int i = 0; i < length; i++) {
			if (seen.contains(child[i])) {
				child[i] = missing.get(missingIndex++);
			} else {
				seen.add(child[i]);
			}
		}

		return child;
	}

	private void copySegment(int[] child, int[] parent, int start, int end) {
		for (int i = start; i < end; i++) {
			child[i] = parent[i];
		}
	}

	public void mutate(int[] contract) {
		double choice = Math.random();
		if (choice < 0.33) constructProposal_SWAP(contract);
		else if (choice < 0.66) constructProposal_SHIFT(contract);
		else constructProposal_REVERSE(contract);
	}

	public void constructProposal_REVERSE(int[] contract) {
		int index1 = rand.nextInt(contract.length);
		int index2 = rand.nextInt(contract.length);
		if (index1 > index2) {
			int tmp = index1;
			index1 = index2;
			index2 = tmp;
		}
		while (index1 < index2) {
			int temp = contract[index1];
			contract[index1] = contract[index2];
			contract[index2] = temp;
			index1++;
			index2--;
		}
	}



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

	public int[] rouletteWheelSelection(List<int[]> preferredByA, List<int[]> preferredByB) {
		List<int[]> rouletteWheel = new ArrayList<>();

		int maxWeightA = preferredByA.size();
		int maxWeightB = preferredByB.size();

		for (int i = 0; i < preferredByA.size(); i++) {
			int weight = maxWeightA - i;
			int[] contract = preferredByA.get(i);
			for (int j = 0; j < weight; j++) {
				rouletteWheel.add(contract);
			}
		}

		for (int i = 0; i < preferredByB.size(); i++) {
			int weight = maxWeightB - i;
			int[] contract = preferredByB.get(i);
			for (int j = 0; j < weight; j++) {
				rouletteWheel.add(contract);
			}
		}

		if (rouletteWheel.isEmpty()) {
			// Optional fallback: return a random contract or the original
			return initContract();
		}

		int selectedIndex = new Random().nextInt(rouletteWheel.size());
		return rouletteWheel.get(selectedIndex);
	}

	// Roulette Wheel Selection A: Classic Weighted Roulette Wheel
	public int[] rouletteWheelSelectionA(List<int[]> mutuallyPreferred, List<int[]> preferredByA, List<int[]> preferredByB, int[] currentContract) {
		// Ignore mutuallyPreferred and currentContract for this method
		List<int[]> rouletteWheel = new ArrayList<>();
		int maxWeightA = preferredByA.size();
		int maxWeightB = preferredByB.size();
		for (int i = 0; i < preferredByA.size(); i++) {
			int weight = maxWeightA - i;
			int[] contract = preferredByA.get(i);
			for (int j = 0; j < weight; j++) {
				rouletteWheel.add(contract);
			}
		}
		for (int i = 0; i < preferredByB.size(); i++) {
			int weight = maxWeightB - i;
			int[] contract = preferredByB.get(i);
			for (int j = 0; j < weight; j++) {
				rouletteWheel.add(contract);
			}
		}
		if (rouletteWheel.isEmpty()) {
			return initContract();
		}
		int selectedIndex = rand.nextInt(rouletteWheel.size());
		return rouletteWheel.get(selectedIndex);
	}

	// Roulette Wheel Selection B: mutually preferred, score-based selection
	public int[] rouletteWheelSelectionB(List<int[]> mutuallyPreferred, List<int[]> preferredByA, List<int[]> preferredByB, int[] currentContract) {
		if (mutuallyPreferred.isEmpty()) return currentContract;

		// Score each contract based on rankings in preferredByA and preferredByB
		Map<int[], Integer> scores = new HashMap<>();
		for (int[] contract : mutuallyPreferred) {
			int rankA = preferredByA.indexOf(contract);
			int rankB = preferredByB.indexOf(contract);
			int score = 0;

//			// Higher rank = lower index, so invert rank to score
//			if (rankA >= 0) score += (preferredByA.size() - rankA);
//			if (rankB >= 0) score += (preferredByB.size() - rankB);
//			scores.put(contract, score);

			//Lower rank = lower index
			if (rankA >= 0) score += (rankA +1);
			if (rankB >= 0) score += (rankB +1);
			scores.put(contract,score);

//			//Lower rank = lower index (weighted heavier)
//			if (rankA >= 0) score += (int)Math.pow(rankA +1, 2);
//			if (rankB >= 0) score += (int)Math.pow(rankB +1, 2);
//			scores.put(contract,score);

		}

		int totalScore = scores.values().stream().mapToInt(Integer::intValue).sum();
		int randomValue = (int)(Math.random() * totalScore);

		int runningSum = 0;
		for (Map.Entry<int[], Integer> entry : scores.entrySet()) {
			runningSum += entry.getValue();
			if (runningSum >= randomValue) {
				return entry.getKey();
			}
		}
		// fallback (should not happen)
		return currentContract;
	}

	// Roulette Wheel Selection C: Softmax Probability Selection
	public int[] rouletteWheelSelectionC(List<int[]> mutuallyPreferred, List<int[]> preferredByA, List<int[]> preferredByB, int[] currentContract) {
		if (mutuallyPreferred.isEmpty()) return currentContract;
		double temperature = 1.0; // Can be tuned
		List<Double> expScores = new ArrayList<>();
		double sumExp = 0.0;
		for (int[] contract : mutuallyPreferred) {
			int rankA = preferredByA.indexOf(contract);
			int rankB = preferredByB.indexOf(contract);
			int score = 0;
			if (rankA >= 0) score += (rankA + 1);
			if (rankB >= 0) score += (rankB + 1);
			// Lower score = better, so use negative for softmax
			double expScore = Math.exp(-score / temperature);
			expScores.add(expScore);
			sumExp += expScore;
		}
		double randVal = Math.random() * sumExp;
		double cumulative = 0.0;
		for (int i = 0; i < mutuallyPreferred.size(); i++) {
			cumulative += expScores.get(i);
			if (cumulative >= randVal) {
				return mutuallyPreferred.get(i);
			}
		}
		return currentContract;
	}

}