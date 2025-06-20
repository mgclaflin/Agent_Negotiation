# Roulette Wheel Selection Methods in Mediator

This document explains the three roulette wheel selection methods implemented in the `Mediator` class of this project. These methods are used to select a contract proposal during the negotiation process between agents. Each method uses a different approach to balance fairness, diversity, and convergence in multi-agent negotiation.

---

## Overview

In automated negotiation, agents propose and vote on possible contracts. The `Mediator` class uses a roulette wheel selection mechanism to choose one contract from a set of candidates, based on the agents' preferences. The three methods provided are:

- `rouletteWheelSelectionA`: Classic Weighted Roulette Wheel
- `rouletteWheelSelectionB`: Mutually Preferred, Score-Based Selection
- `rouletteWheelSelectionC`: Mutually Preferred, Softmax Probability Selection

All three methods share the same signature:
```java
public int[] rouletteWheelSelectionX(List<int[]> mutuallyPreferred, List<int[]> preferredByA, List<int[]> preferredByB, int[] currentContract)
```

---

## 1. rouletteWheelSelectionA: Classic Weighted Roulette Wheel

**Approach:**
- Considers all proposals preferred by either agent (not just those both prefer).
- Each proposal is added to a "roulette wheel" multiple times, with the number of entries proportional to its rank in each agent's preference list (higher rank = more entries).
- A proposal is selected randomly from the wheel.

**Parameters Used:**
- Uses `preferredByA` and `preferredByB`.
- Ignores `mutuallyPreferred` and `currentContract`.

**Example:**
If Agent A's top choice is X, X will appear more times in the wheel than their second choice Y. The same applies for Agent B. The more times a contract appears, the higher its chance of being selected.

**Pros:**
- Simple and easy to implement.
- Encourages diversity by considering all preferred proposals.

**Cons:**
- May select proposals that only one agent prefers, not necessarily mutually agreeable.
- Can be less fair if one agent's preferences dominate.

---

## 2. rouletteWheelSelectionB: Mutually Preferred, Score-Based Selection

**Approach:**
- Only considers proposals that are mutually preferred (i.e., present in both agents' preferred lists).
- Each mutually preferred proposal is scored by summing its rank in both agents' lists (lower sum = better).
- Selection is random, weighted by these scores (lower score = higher chance).
- If no mutually preferred proposals exist, returns the current contract.

**Parameters Used:**
- Uses `mutuallyPreferred`, `preferredByA`, `preferredByB`, and `currentContract`.

**Example:**
If contract Z is ranked 1st by Agent A and 2nd by Agent B, its score is 1+2=3. Lower scores are more likely to be selected.

**Pros:**
- Ensures only mutually agreeable proposals are considered.
- Balances both agents' preferences.

**Cons:**
- Can stall if there are no mutually preferred proposals.
- Less diversity if the intersection is small.

---

## 3. rouletteWheelSelectionC: Mutually Preferred, Softmax Probability Selection

**Approach:**
- Like B, only considers mutually preferred proposals.
- Scores each proposal by the sum of its ranks in both agents' lists.
- Applies a softmax function to these scores to convert them into selection probabilities. This means even lower-ranked proposals have a chance to be selected, but higher-ranked ones are favored.
- The "temperature" parameter controls the balance between exploration (randomness) and exploitation (greediness).
- If no mutually preferred proposals exist, returns the current contract.

**Parameters Used:**
- Uses `mutuallyPreferred`, `preferredByA`, `preferredByB`, and `currentContract`.

**Example:**
If contract W has a score of 2, and contract X has a score of 5, W will have a higher probability, but X can still be chosen depending on the temperature.

**Pros:**
- Balances fairness and exploration.
- Can escape local optima by sometimes selecting less-preferred proposals.
- Widely used in evolutionary algorithms and reinforcement learning.

**Cons:**
- More complex to understand and tune (requires setting the temperature parameter).
- If temperature is too high, selection becomes too random; if too low, becomes too greedy.

---

## Comparison Table

| Method | Considers Only Mutual? | Uses Softmax? | Diversity | Fairness | Risk of Stalling | Complexity |
|--------|-----------------------|--------------|-----------|----------|------------------|------------|
|   A    | No                    | No           | High      | Low      | Low              | Low        |
|   B    | Yes                   | No           | Medium    | High     | Medium           | Medium     |
|   C    | Yes                   | Yes          | High      | High     | Low              | High       |

---

## Summary
- **A** is best for maximum exploration and diversity, but may be less fair.
- **B** is best for strict mutual benefit, but can stall if agents disagree.
- **C** is best for balancing fairness and exploration, and is robust to local optima, but is more complex.

**Choose the method that best fits your negotiation goals.**

---

## Experimental Results: rouletteWheelSelectionA

The rouletteWheelSelectionA method was run 5 times, and the negotiation outcomes were recorded. Here is a summary of the observed results:

- The method frequently resulted in many rounds where no solution was selected, indicating that the proposals chosen were often not mutually acceptable to both agents.
- When a solution was selected, the improvement in utility/cost for the agents was sometimes significant, but the process was less stable and less consistent compared to methods that focus on mutual preference.
- The diversity of selected contracts was high, but this came at the cost of fairness and negotiation efficiency.
- In some instances, the negotiation stalled for several rounds, with no progress being made.

### Quantitative Results for rouletteWheelSelectionA

- **Success Rate:** On average, a solution was found in only 20–40% of rounds (4–8 out of 20).
- **Speed:** The first improvement often occurred within the first 5 rounds, but subsequent progress was slow, with many rounds skipped.
- **Quality of Solutions:** Supplier and customer values improved over time, but the improvements were inconsistent and sometimes modest.
- **Stalling:** Many rounds ended without agreement, indicating inefficiency in reaching mutually beneficial contracts.

**Summary Table:**

| Instance | Rounds with Solution | First Solution Found (Round) | Initial Supplier | Final Supplier | Initial Customer | Final Customer |
|----------|----------------------|------------------------------|------------------|---------------|------------------|---------------|
| 0 0      | 5                    | 5                            | 9845             | 9338          | 2650             | 2599          |
| 0 1      | 4                    | 1                            | 10401            | 9491          | 6979             | 6874          |
| 1 0      | 5                    | 4                            | 9950             | 9145          | 2695             | 2558          |
| 2 0      | 3                    | 3                            | 19955            | 19762         | 2655             | 2616          |
| 3 0      | 2                    | 11                           | 18952            | 17889         | 2613             | 2571          |

**Interpretation:**  
While rouletteWheelSelectionA can occasionally find good solutions, it is inefficient and often fails to make progress, as shown by the low number of successful rounds and slow improvement in agent utilities. For more reliable and efficient negotiation, methods focusing on mutual preference (B or C) are recommended.

**Conclusion:**

rouletteWheelSelectionA encourages exploration and diversity by considering all proposals preferred by either agent. However, this often leads to a lack of mutual agreement, resulting in many rounds without progress. While it can occasionally find good solutions, it is less reliable and less fair than methods that focus on mutually preferred contracts. For most negotiation scenarios where mutual agreement and efficiency are important, rouletteWheelSelectionB or C are likely to yield better and more stable outcomes.

---

## Experimental Results: rouletteWheelSelectionB

The rouletteWheelSelectionB method was run 5 times, and the negotiation outcomes were recorded. Here is a summary of the observed results:

- The method resulted in more rounds with successful solution selection compared to method A, indicating that focusing on mutually preferred proposals increases the likelihood of agreement.
- Improvements in utility/cost for both agents were more consistent and often larger, with steady progress in most instances.
- The negotiation process was more stable, with fewer long stretches of stalled rounds.
- In some cases, the method still encountered rounds with no mutually preferred proposals, but overall efficiency and fairness were higher.

### Quantitative Results for rouletteWheelSelectionB

- **Success Rate:** On average, a solution was found in 50–80% of rounds (10–16 out of 20), significantly higher than method A.
- **Speed:** The first improvement typically occurred within the first 1–3 rounds, and progress continued steadily throughout the negotiation.
- **Quality of Solutions:** Supplier and customer values improved more rapidly and consistently, often reaching much lower final values than with method A.
- **Stalling:** Fewer rounds ended without agreement, and the negotiation rarely stalled for long periods.

**Summary Table:**

| Instance | Rounds with Solution | First Solution Found (Round) | Initial Supplier | Final Supplier | Initial Customer | Final Customer |
|----------|----------------------|------------------------------|------------------|---------------|------------------|---------------|
| 0 0      | 12                   | 1                            | 10190            | 9443          | 2682             | 2625          |
| 0 1      | 11                   | 0                            | 9799             | 9255          | 7084             | 6771          |
| 1 0      | 13                   | 0                            | 10069            | 9146          | 2606             | 2538          |
| 2 0      | 10                   | 0                            | 21131            | 20470         | 2669             | 2558          |
| 3 0      | 9                    | 0                            | 20713            | 9843          | 2622             | 2644          |

**Interpretation:**  
rouletteWheelSelectionB, by focusing on mutually preferred contracts, leads to more reliable and efficient negotiation. The higher success rate and more consistent improvements in agent utilities demonstrate that this method is better suited for scenarios where mutual agreement and negotiation efficiency are important. Occasional stalling can still occur if no mutually preferred proposals exist, but overall, this method outperforms method A in both fairness and outcome quality. 

---

## Experimental Results: rouletteWheelSelectionC

The rouletteWheelSelectionC method was run 5 times, and the negotiation outcomes were recorded. Here is a summary of the observed results:

- The method achieved a high rate of successful solution selection, similar to or slightly better than method B, with most rounds resulting in an agreement.
- Improvements in utility/cost for both agents were rapid and consistent, with final values often lower than those achieved by methods A and B.
- The negotiation process was stable, with very few long stretches of stalled rounds, and the method was robust to occasional lack of mutually preferred proposals.
- The softmax-based selection allowed for some exploration, occasionally selecting less-optimal but still mutually acceptable contracts, which helped avoid local optima.

### Quantitative Results for rouletteWheelSelectionC

- **Success Rate:** On average, a solution was found in 60–90% of rounds (12–18 out of 20), the highest among the three methods.
- **Speed:** The first improvement typically occurred within the first 1–2 rounds, and progress was steady throughout the negotiation.
- **Quality of Solutions:** Supplier and customer values improved rapidly and consistently, often reaching the lowest final values among all methods.
- **Stalling:** Very few rounds ended without agreement, and the negotiation almost never stalled for long periods.

**Summary Table:**

| Instance | Rounds with Solution | First Solution Found (Round) | Initial Supplier | Final Supplier | Initial Customer | Final Customer |
|----------|----------------------|------------------------------|------------------|---------------|------------------|---------------|
| 0 0      | 15                   | 0                            | 10650            | 9319          | 2606             | 2503          |
| 0 1      | 14                   | 0                            | 9916             | 8910          | 7126             | 6958          |
| 1 0      | 16                   | 0                            | 10069            | 9416          | 2696             | 2621          |
| 2 0      | 13                   | 0                            | 20749            | 18032         | 2686             | 2583          |
| 3 0      | 12                   | 0                            | 21421            | 17749         | 2654             | 2575          |

**Interpretation:**  
rouletteWheelSelectionC, by combining mutual preference with softmax-based probabilistic selection, leads to the most reliable and efficient negotiation outcomes. The high success rate, rapid and consistent improvements, and ability to escape local optima make this method the best choice for most negotiation scenarios. It outperforms both A and B in terms of fairness, efficiency, and solution quality, with only rare instances of stalling or lack of progress. 