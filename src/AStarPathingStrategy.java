import java.util.*;
import java.util.function.*;
import java.util.stream.*;

class AStarPathingStrategy implements PathingStrategy{
    @Override
    public List<Point> computePath(Point start, Point end, Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach, Function<Point, Stream<Point>> potentialNeighbors) {

        List<Point> path = new ArrayList<>();
        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparing(Node::getFcost));
        Map<Point, Node> openSet = new HashMap<>(); //map
        Set<Point> closed = new HashSet<>(); //vs set

        Node startNode = new Node(start, null, 0, 0, start.distance(end));
        open.add(startNode);
        openSet.put(start, startNode);

        while (!open.isEmpty()) {
            Node current = open.remove();
            openSet.remove(current.position);

            potentialNeighbors.apply(current.position)
                    .filter(canPassThrough)
                    .forEach(neighbor -> {
                        if (neighbor.equals(end)) {

                            List<Point> newPath = buildPath(current, new ArrayList<Point>());

                            path.clear();
                            path.addAll(newPath);

                            return;

                        }

                        if (!closed.contains(neighbor) && withinReach.test(current.position, neighbor)) {
                            Node neighborNode = new Node(neighbor, current, current.cost+1,0, neighbor.manhattanDistance(end));

                            if (openSet.containsKey(neighbor)) {
                                Node existing = openSet.get(neighbor);
                                if (existing.cost >= neighborNode.cost) {
                                    open.remove(existing);
                                    openSet.put(neighbor, neighborNode);
                                    open.add(neighborNode);
                                }
                            } else {
                                openSet.put(neighbor, neighborNode);
                                open.add(neighborNode);
                            }
                        }
                    });

            closed.add(current.position);
        }

        return path;
    }

    private List<Point> buildPath(Node node, List<Point> path) {
        if (node.prior == null) {
            return path;
        }
        path.add(0, node.position);
        return buildPath(node.prior, path);
    }


}

