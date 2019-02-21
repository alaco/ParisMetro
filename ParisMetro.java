// Allie LaCompte
// Paris Metro

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.StringTokenizer;

import net.datastructures.AdjacencyMapGraph;
import net.datastructures.Edge;
import net.datastructures.Graph;
import net.datastructures.Vertex;

/**
 * The class that represents the Paris metro system
 *
 * Created by alacompte on 2017-12-02.
 */
public class ParisMetro {

	private Graph<String, Integer> sGraph;

	// Create ParisMetro from a file
	public ParisMetro(LinkedList<String> data) throws Exception {

		sGraph = new AdjacencyMapGraph<>(true);
		inputData(data);
	}

	// Read in file and return lines representing vertices (stations) and edges
	// (distance between stations)
	private static LinkedList<String> readMetro(String fileName) throws IOException {

		BufferedReader dataReader = new BufferedReader(new FileReader(fileName));

		LinkedList<String> data = new LinkedList<>();

		// Skip over lines until list of edges and vertices is reached
		while(!dataReader.readLine().equals("$")) {
		    // Intentionally empty
		}

		String line;

		while ((line = dataReader.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line);

			if (st.countTokens() != 3) {
				throw new IOException("Incorrect input file at line " + line);
			}
			data.add(line);
		}
		return data;
	}

	// Insert vertices and edges in the graph
	private void inputData(LinkedList<String> data) throws IOException {
		
		// Store the vertices
		Hashtable<String, Vertex<String>> vertices = new Hashtable<>();

		while(data.size() > 0) {

			StringTokenizer st = new StringTokenizer(data.remove());

			String origin = st.nextToken();
			String dest = st.nextToken();
			Integer distance = new Integer(st.nextToken());

			// Insert vertices if they are not already contained in the graph
			vertices.computeIfAbsent(origin, v -> sGraph.insertVertex(origin));
			vertices.computeIfAbsent(dest, v -> sGraph.insertVertex(dest));

			// Insert edge in graph if it is not already in the graph (AdjacencyMapGraph checks that
			// it does not already contain the edge before inserting it)
			sGraph.insertEdge(vertices.get(origin), vertices.get(dest), distance);
		}
	}

	// Get vertex by it's String representation
	private Vertex<String> getVertex(String vertex) throws Exception {

		// Go through vertex list to find vertex
		for (Vertex<String> v : sGraph.vertices()) {

			if (v.getElement().equals(vertex)) {
				return v;
			}
		}
		throw new Exception("Vertex not in graph: " + vertex);
	}

	// Recursive function to get stations that share an edge with the given station, insert stations into the
	// list of stations on the line if they are not already in the list
	private void getAdjacentStations(Vertex<String> v, LinkedList<Vertex<String>> stations) {

		Iterable<Edge<Integer>> iter = sGraph.incomingEdges(v);

		for (Edge<Integer> e : iter) {

			if(!e.getElement().equals(-1)) {
				Vertex<String> ov = sGraph.opposite(v, e);

				if(!stations.contains(ov)) {
					stations.add(ov);
					getAdjacentStations(ov, stations);
				}
			}
		}

		iter = sGraph.outgoingEdges(v);

		for (Edge<Integer> e : iter) {

			if(!e.getElement().equals(-1)) {
				Vertex<String> ov = sGraph.opposite(v, e);

				if(!stations.contains(ov)) {
					stations.add(ov);
					getAdjacentStations(ov, stations);
				}
			}
		}
	}

	// Find and return all stations belonging to the same line as the given station
	private LinkedList<Vertex<String>> getStationsOnLine(Vertex<String> v) {

		LinkedList<Vertex<String>> stations = new LinkedList<>();
		stations.add(v);
		getAdjacentStations(v, stations);
		return stations;
	}

	// Print all stations belonging to the same line as the given station
	void printStationsOnLine(String vertex) throws Exception {

		Vertex<String> sourceVertex = getVertex(vertex);
		LinkedList<Vertex<String>> stations = getStationsOnLine(sourceVertex);
        System.out.print("line: ");

        for(Vertex<String> v : stations) {
			System.out.print(v.getElement() + " ");
		}
		System.out.println();
	}

	// Print the shortest distance (in seconds) and the shortest path from an origin station to
	// a destination station using DijkstraAlgorithm, omit stations from a line if it is non-functioning
    void printShortestPath(String origin, String dest, LinkedList<Vertex<String>> lineDown) throws Exception {

		DijkstraAlgorithm da = new DijkstraAlgorithm(sGraph, lineDown);
		Vertex<String> sv = getVertex(origin);
		da.execute(sv);
		Vertex<String> dv = getVertex(dest);
		System.out.println("Time = " + da.getShortestDistance(dv));
		LinkedList<Vertex<String>> path = da.getPath(dv);
        System.out.print("Path: ");

        for(Vertex<String> v : path) {
			System.out.print(v.getElement() + " ");
		}
		System.out.println();
	}

	// Find all stations belonging to a non functioning line, print the shortest path given that the
	// specified line is non-functioning
    void printShortestPathOmitLine(String origin, String dest, String omitLine) throws Exception {

		Vertex<String> omitVertex = getVertex(omitLine);
		LinkedList<Vertex<String>> omitStations = getStationsOnLine(omitVertex);
		printShortestPath(origin, dest, omitStations);
	}

	public static void main(String[] args) {

		try {
			// Create an instance of ParisMetro from metro.txt
			ParisMetro parisMetro = new ParisMetro(readMetro("metro.txt"));
			
			// Test ParisMetro
			if(args.length == 1) {
                
                if(args[0].equals("Test")) {
                    TestParisMetro.test(parisMetro);
                }
                else {
                	// Print all stations belonging to the same line as station N1
                    String N1 = args[0];
                    parisMetro.printStationsOnLine(N1);
                }
			}
			
			// Print the shortest distance and path from station N1 to N2
			else if(args.length == 2) {
				String N1 = args[0];
				String N2 = args[1];
				parisMetro.printShortestPath(N1, N2, null);
			}
			
			// Print the shortest distance and path from station N1 to N2
			// given that the line to which station N3 belongs is non-functioning
			else if(args.length == 3) {
				String N1 = args[0];
				String N2 = args[1];
				String N3 = args[2];
				parisMetro.printShortestPathOmitLine(N1, N2, N3);
			}
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}