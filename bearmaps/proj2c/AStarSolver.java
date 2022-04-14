package bearmaps.proj2c;

import bearmaps.proj2ab.ArrayHeapMinPQ;
import bearmaps.proj2ab.ExtrinsicMinPQ;
import edu.princeton.cs.algs4.Stopwatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AStarSolver<Vertex> implements ShortestPathsSolver<Vertex> {

    private double timeLimit;
    private Vertex start;
    private Vertex goal;
    private AStarGraph<Vertex> graph;

    private SolverOutcome outcome;
    private HashMap<Vertex, WeightedEdge<Vertex>> edgeTo; //path: get to key by value
    private HashMap<Vertex, Double> distTo; //distTo any vertex dequeued
    private ArrayList<Vertex> solutionPath;
    private double solutionWeight;
    private int numExplored;
    private double totalTime;

    private ExtrinsicMinPQ<Vertex> nearestPQ;

    public AStarSolver(AStarGraph<Vertex> input, Vertex start, Vertex end, double timeout) {
        this.graph = input;
        this.timeLimit = timeout;
        this.start = start;
        this.goal = end;
        this.edgeTo = new HashMap<>();
        this.distTo = new HashMap<>();

        //start tracking time
        Stopwatch currTime = new Stopwatch();
        //creates the PQ to track states to be explored, add start state to the PQ
        nearestPQ = new ArrayHeapMinPQ<Vertex>();
        nearestPQ.add(start, input.estimatedDistanceToGoal(start, end));
        distTo.put(start, 0.0);

        while (nearestPQ.size() > 0) {
            Vertex curr = nearestPQ.removeSmallest();
            numExplored++; //explore one more state when dequeue

            //check time
            if (curr.equals(end)) {
                this.outcome = SolverOutcome.SOLVED;
                this.totalTime = currTime.elapsedTime();
                return;
            } else if (currTime.elapsedTime() >= this.timeLimit) {
                this.outcome = SolverOutcome.TIMEOUT;
                this.totalTime = currTime.elapsedTime();
                return;
            }

            for (WeightedEdge edge: input.neighbors(curr)) {
                Vertex to = (Vertex) edge.to();
                double sourceDist = distTo.get(curr) + edge.weight();

                //if the target vertex already in the distTo hashmap and when the new cost is less than the existing cost
                if (distTo.containsKey(to) && distTo.get(to) > sourceDist) {
                    update(to, sourceDist, edge);
                    // when the new cost is larger than the existing cost, ignored
                } else if (!distTo.containsKey(to)) { // if the target vertex not in the distTo hashmap,
                    update(to, sourceDist, edge);
                }
            }
        }

        //when PQ is empty but the target is not found
        this.outcome = SolverOutcome.UNSOLVABLE;
        this.totalTime = currTime.elapsedTime();

    }

    private void update(Vertex v, double sourceDist, WeightedEdge e) {
        edgeTo.put(v, e);
        distTo.put(v, sourceDist);
        if (nearestPQ.contains(v)) nearestPQ.changePriority(v, sourceDist+graph.estimatedDistanceToGoal(v, this.goal));
        else nearestPQ.add(v, sourceDist+graph.estimatedDistanceToGoal(v, this.goal));
    }

    @Override
    public SolverOutcome outcome() {
        return outcome;
    }

    @Override
    public List solution() {

        if (solutionPath != null) return solutionPath;
        else {
            if (outcome == SolverOutcome.SOLVED) {
                derivePath();
                return solutionPath;
            } else {
                solutionPath = new ArrayList<>();
                return solutionPath;
            }
        }
    }

    private void derivePath() {
        Vertex curr = this.goal;
        solutionPath = new ArrayList<>();

        while (!curr.equals(this.start)) {
            solutionPath.add(0,edgeTo.get(curr).from());
            this.solutionWeight += edgeTo.get(curr).weight();
            curr = edgeTo.get(curr).from();

        }
        solutionPath.add(this.goal);
    }

    @Override
    public double solutionWeight() {
        if (solutionPath != null) return solutionWeight;
        else {
            if (outcome == SolverOutcome.SOLVED) {
                derivePath();
                return solutionWeight;
            } else {
                solutionPath = new ArrayList<>();
                return 0;
            }
        }
    }

    @Override
    public int numStatesExplored() {
        return numExplored;
    }

    @Override
    public double explorationTime() {
        return totalTime;
    }
}
