// Allie LaCompte
// Paris Metro

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.datastructures.Edge;
import net.datastructures.Vertex;
import net.datastructures.Graph;

/**
 * The class that determines the shortest path to each vertex in
 * a graph from a specified source vertex
 *
 * Created by alacompte on 2017-12-02.
 */
class DijkstraAlgorithm {

    private Set<Vertex<String>> settledVertices;
    private Set<Vertex<String>> unSettledVertices;
    private Map<Vertex<String>, Vertex<String>> predecessors; // The vertex preceding each vertex in the shortest path to that vertex
    private Map<Vertex<String>, Integer> distance;  // The shortest distance from the source vertex to each vertex in the graph
    private Graph<String, Integer> graph;
    private LinkedList<Vertex<String>> omitted;  // The stations belonging to a specified non-functioning line

    DijkstraAlgorithm(Graph<String, Integer> graph, LinkedList<Vertex<String>> lineDown) {

        this.graph = graph;
        omitted = new LinkedList<>();

        if(lineDown != null) {
            omitted.addAll(lineDown);
        }
    }
    
    // Executes the DijkstraAlgorithm relative to the source vertex
    void execute(Vertex<String> source) {

        settledVertices = new HashSet<>();
        unSettledVertices = new HashSet<>();
        distance = new HashMap<>();
        predecessors = new HashMap<>();
        distance.put(source, 0);
        unSettledVertices.add(source);

        while (unSettledVertices.size() > 0) {
            Vertex<String> vertex = getMinimum(unSettledVertices);
            settledVertices.add(vertex);
            unSettledVertices.remove(vertex);
            findMinimalDistances(vertex);
        }
    }

    // Update minimal distances for vertices that share an edge with the given vertex
    private void findMinimalDistances(Vertex<String> vertex) {

        List<Vertex<String>> adjacentVertices = getNeighbours(vertex);

        for (Vertex<String> target : adjacentVertices) {

            if (getShortestDistance(target) > getShortestDistance(vertex) + getDistance(vertex, target)) {
                distance.put(target, getShortestDistance(vertex) + getDistance(vertex, target));
                predecessors.put(target, vertex);
                unSettledVertices.add(target);
            }
        }
    }

    // Get the distance from the given vertex to the target vertex which shares an edge
    private int getDistance(Vertex<String> vertex, Vertex<String> target) {

        Iterable<Edge<Integer>> iter = graph.outgoingEdges(vertex);

        for (Edge<Integer> edge : iter) {
            Vertex<String> ov = graph.opposite(vertex, edge);

            if(ov.equals(target)) {
                int seconds = edge.getElement();

                if(seconds == -1) {
                    seconds = 90;
                }
                return seconds;
            }
        }
        throw new RuntimeException("Edge does not exist");
    }

    // Get all vertices that share an edge with the given vertex
    private List<Vertex<String>> getNeighbours(Vertex<String> vertex) {

        List<Vertex<String>> neighbours = new ArrayList<>();
        Iterable<Edge<Integer>> iter = graph.outgoingEdges(vertex);

        for (Edge<Integer> edge : iter) {
            Vertex<String> ov = graph.opposite(vertex, edge);

            if (!isSettled(ov) && !omitted.contains(ov)) {
                neighbours.add(ov);
            }
        }
        return neighbours;
    }

    // Get the vertex with the current minimum distance
    private Vertex<String> getMinimum(Set<Vertex<String>> vertices) {

        Vertex<String> minimum = null;

        for (Vertex<String> vertex : vertices) {

            if (minimum == null) {
                minimum = vertex;
            } else {
                if (getShortestDistance(vertex) < getShortestDistance(minimum)) {
                    minimum = vertex;
                }
            }
        }
        return minimum;
    }

    // Return true if the shortest path to a given vertex has been found, return false otherwise
    private boolean isSettled(Vertex<String> vertex) {

        return settledVertices.contains(vertex);
    }

    // Get the current minimum distance for a given vertex
    int getShortestDistance(Vertex<String> destination) {

        Integer d = distance.get(destination);

        if (d == null) {
            return Integer.MAX_VALUE;
        } else {
            return d;
        }
    }

    // Return the shortest path, in order, to a given vertex from the source vertex
    LinkedList<Vertex<String>> getPath(Vertex<String> target) {

        LinkedList<Vertex<String>> path = new LinkedList<>();
        Vertex<String> step = target;
        // Check if a path exists
        if (predecessors.get(step) == null) {
            return null;
        }
        path.add(step);
        
        while (predecessors.get(step) != null) {
            step = predecessors.get(step);
            path.add(step);
        }
        // Put it into the correct order
        Collections.reverse(path);
        return path;
    }
}