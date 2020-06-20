package search;

import java.util.List;

public interface Searchable {
	State getInitialState();
	boolean isGoalState(State s);
	List<Tuple<State, Double>> getAllPossibleStates(State s);
}
