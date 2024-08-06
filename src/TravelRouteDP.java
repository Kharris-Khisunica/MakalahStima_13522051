import java.io.*;
import java.util.*;

public class TravelRouteDP {
    static class DestNode {
        String destName;
        int price;

        public DestNode(String destName, int price) {
            this.destName = destName;
            this.price = price;
        }
    }

    static class Node {
        String name;
        Map<Node, Integer> neighbors;

        Node(String name) {
            this.name = name;
            this.neighbors = new HashMap<>();
        }

        void addNeighbor(Node neighbor, int travelTime) {
            this.neighbors.put(neighbor, travelTime);
        }
    }

    static Map<String, Node> graph;
    static Map<String, DestNode> destinations;
    static Map<String, Integer> dp = new HashMap<>();
    static Map<String, String> previous = new HashMap<>();

    public static void main(String[] args) {
        try {
            initializeDestinations("destination.txt");
            initializeGraph("adjacent.txt");

            int minTime = findOptimalTimeandRoute("Start");
            System.out.println("Minimum total travel and stay time: " + minTime + " minutes.");

            List<String> route = getRoute();
            
            int totalPrice = calculateTotalPrice(route);
            System.out.println("Optimal Route: \n" + String.join(" -> ", simplifyRoute(route)));
            System.out.println("Total Price: Rp." + totalPrice);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void initializeDestinations(String filePath) throws IOException {
        destinations = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(" ");
            String nodeID = parts[0];
            String destName = parts[1].replace("_", " "); // replace underscores with spaces
            int price = Integer.parseInt(parts[2]);
            destinations.put(nodeID, new DestNode(destName, price));
        }
        br.close();
    }

    static void initializeGraph(String filePath) throws IOException {
        graph = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(" ");
            String from = parts[0];
            String to = parts[1];
            int travelTime = Integer.parseInt(parts[2]);

            if (!graph.containsKey(from)) {
                graph.put(from, new Node(from));
            }
            if (!graph.containsKey(to)) {
                graph.put(to, new Node(to));
            }
            graph.get(from).addNeighbor(graph.get(to), travelTime);
        }
        br.close();
    }

    static int findOptimalTimeandRoute(String current) {
        if (dp.containsKey(current)) {
            return dp.get(current);
        }

        if (current.equals("Goal")) {
            dp.put(current, 0);
            return 0;
        }

        int minTime = Integer.MAX_VALUE;
        Node currentNode = graph.get(current);
        String bestNextNode = null;

        for (Map.Entry<Node, Integer> neighborEntry : currentNode.neighbors.entrySet()) {
            Node neighbor = neighborEntry.getKey();
            int travelTime = neighborEntry.getValue();
            int newTime = travelTime + findOptimalTimeandRoute(neighbor.name);

            if (newTime < minTime) {
                minTime = newTime;
                bestNextNode = neighbor.name;  // Track the node that leads to the optimal path
            }
        }

        if (bestNextNode != null) {
            previous.put(current, bestNextNode);  // Store the best route leading from current to next node
        }

        dp.put(current, minTime);
        return minTime;
    }

    static List<String> getRoute() {
        List<String> route = new ArrayList<>();
        String current = "Start";  // Start from the beginning

        while (current != null && !current.equals("Goal")) {  // Traverse until reaching "Goal"
            route.add(current);
            current = previous.get(current);
        }

        if (current != null) {
            route.add(current);  // Add "Goal" to the route
        }

        return route;
    }

    static int calculateTotalPrice(List<String> route) {
        int totalPrice = 0;
        for (String nodeID : route) {
            String simplifiedNodeID = simplifyNodeID(nodeID);  // Simplify the node ID before checking price
            if (destinations.containsKey(simplifiedNodeID)) {
                totalPrice += destinations.get(simplifiedNodeID).price;  // Add the price of the destination
            }
        }
        return totalPrice;
    }

    static List<String> simplifyRoute(List<String> route) {
        List<String> simplifiedRoute = new ArrayList<>();
        for (String nodeID : route) {
            String simplifiedNodeID = simplifyNodeID(nodeID);
            if (destinations.containsKey(simplifiedNodeID)) {
                simplifiedRoute.add(destinations.get(simplifiedNodeID).destName);
            } else {
                simplifiedRoute.add(simplifiedNodeID);
            }
        }
        return simplifiedRoute;
    }

    static String simplifyNodeID(String nodeID) {
        if (nodeID.length() > 2 && nodeID.startsWith("W")) {
            return "" + nodeID.charAt(0) + nodeID.charAt(nodeID.length() - 1);
        }
        return nodeID;
    }
}
