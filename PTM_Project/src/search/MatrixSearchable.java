package search;

import java.util.ArrayList;
import java.util.List;

public class MatrixSearchable implements Searchable {

	/*
	 * State: 2D array of pairs: (State, Original Cost)
	 * stateIndexes: HashMap that links a state and it's index
	 * initial: The initial state
	 * goal: The goal state
	 */

	private Tuple<State, Double>[][] states;
	private State initial;
	private State goal;

	@SuppressWarnings("unchecked") // For the casting on the 2nd line, which is technically unsafe
	public MatrixSearchable(double[][] matrix, Tuple<Integer, Integer> initialIndex, Tuple<Integer, Integer> goalIndex) {
		super();
		this.states = (Tuple<State, Double>[][]) new Tuple[matrix.length][matrix[0].length];

		for (int i = 0; i < this.states.length; i++) {
			for (int j = 0; j < this.states[i].length; j++) {
				this.states[i][j] = new Tuple<State, Double>(
						new State(String.valueOf(i) + "," + String.valueOf(j), matrix[i][j]), matrix[i][j]
								);
			}
		}
		
		this.initial = states[initialIndex.getElement1()][initialIndex.getElement2()].getElement1();
		this.goal = states[goalIndex.getElement1()][goalIndex.getElement2()].getElement1();
	}
	
	public Tuple<Integer, Integer> getStateIndex(State state) {
		String[] tokens = state.getName().split(",");
		int x = Integer.parseInt(tokens[0]);
		int y = Integer.parseInt(tokens[1]);
		return new Tuple<Integer, Integer>(x, y);
	}

	@Override
	public State getInitialState() {
		return this.initial;
	}

	@Override
	public boolean isGoalState(State s) {
		return s.equals(goal);
	}

	@Override
	public List<Tuple<State, Double>> getAllPossibleStates(State s) {
		List<Tuple<State, Double>> list = new ArrayList<Tuple<State, Double>>();
		
		Tuple<Integer, Integer> tuple = getStateIndex(s);
		int x = tuple.getElement1();
		int y = tuple.getElement2();
		
		if(x > 0)
			list.add(states[x - 1][y]);
		if(x < states.length - 1)
			list.add(states[x + 1][y]);
		if(y > 0)
			list.add(states[x][y - 1]);
		if(y < states.length - 1)
			list.add(states[x][y + 1]);
		
		return list;
	}
}
