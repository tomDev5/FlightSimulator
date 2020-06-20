package search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class BestFirstSearcher extends CommonSearcher<List<State>> {

	public BestFirstSearcher() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<State> search(Searchable s) {
		State initial =  s.getInitialState();
		addToOpenList(initial);
		HashSet<State> closedSet = new HashSet<State>();
		
		while(openList.size() > 0) {
			State n = popOpenList();
			closedSet.add(n);
			
			if(s.isGoalState(n)) {
				return backtrace(n, initial);
			}
			
			List<Tuple<State, Double>> tuples = s.getAllPossibleStates(n);
			for(Tuple<State, Double> tuple : tuples) {
				State state = tuple.getElement1();
				double originalCost = tuple.getElement2();

				if(!closedSet.contains(state) && !openList.contains(state)) {
					// No one got to state yet
					state.setPreviousState(n);
					state.setCost(state.getCost() + n.getCost());
					addToOpenList(state);

				} else if(n.getCost() + originalCost < state.getCost()) {
					// Better path to state found
					state.setPreviousState(n);
					state.setCost(n.getCost() + originalCost);
					if(openList.contains(state))
						openList.remove(state);
					openList.add(state);
				}
			}
		}
		return null;
	}
	
	protected List<State> backtrace(State n, State initialState) {
		List<State> list = new ArrayList<State>();
		while(n != null) {
			list.add(n);
			n = n.getPreviousState();
		}
		return list;
	}
}
