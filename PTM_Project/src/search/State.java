package search;

public class State implements Comparable<State> {
	private String name;
	private double cost;
	private State cameFrom;
	
	
	
	public State(String name, double cost) {
		super();
		this.name = name;
		this.cost = cost;
		this.cameFrom = null;
	}

	public State(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean equals(State other) {
		return this.name.equals(other.name);
	}
	
	public State getPreviousState() {
		return this.cameFrom;
	}
	
	public void setPreviousState(State cameFrom) {
		this.cameFrom = cameFrom;
	}
	
	public double getCost() {
		return this.cost;
	}
	
	public void setCost(double cost) {
		this.cost = cost;
	}

	@Override
	public int compareTo(State other) {
		return this.cost > other.cost ? 1 : -1;
	}
}
