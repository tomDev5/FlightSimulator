package test;

import java.util.ArrayList;
import java.util.List;

import search.*;

public class MainTrain {
	
	private static String myGetMove(Tuple<Integer, Integer> first,Tuple<Integer, Integer> second) {
		System.out.print("FROM " + first.getElement1().toString() + "," + first.getElement2().toString());
		System.out.println("\tTO " + second.getElement1().toString() + "," + second.getElement2().toString());
		
		if(first.getElement1() > second.getElement1())
			return "Up";
		else if(first.getElement1() < second.getElement1())
			return "Down";
		else if(first.getElement2() > second.getElement2())
			return "Left";
		return "Right";
	}

	public static void main(String[] args) {
		double[][] mat = {
				{ 180.0, 198.0, 102.0, 181.0 },
				{ 58.0, 176.0, 166.0, 104.0 },
				{ 119.0, 1.0, 173.0, 96.0 },
				{ 111.0, 63.0, 67.0, 17.0 }
		};

		Tuple<Integer, Integer> initial = new Tuple<Integer, Integer>(0, 0);
		Tuple<Integer, Integer> goal = new Tuple<Integer, Integer>(3, 3);
		
		MatrixSearchable ms = new MatrixSearchable(mat, initial, goal);
		
		BestFirstSearcher bfs = new BestFirstSearcher();
		
		List<State> backtrace = bfs.search(ms);
		System.out.println("Done");
		Tuple<Integer, Integer>[] coordinates  = (Tuple<Integer, Integer>[]) new Tuple[backtrace.size()];
		
		for(int i = 0; i < coordinates.length; i++)
			coordinates[i] = ms.getStateIndex(backtrace.get(i));
		
		String result = "";
		for(int i = coordinates.length - 2; i > -1; i--)
			result += myGetMove(coordinates[i + 1], coordinates[i]) + ",";
		result = result.substring(0, result.length() - 1);
		System.out.println(result);
	}

}
