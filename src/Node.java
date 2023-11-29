class Node {

    Point position;
    Node prior;
    int cost;
    int heuristic;
    double distance;

    public Node(Point position, Node prior, int cost, int heuristic, double distance) {
        this.position = position;
        this.prior = prior;
        this.cost = cost;
        this.heuristic = heuristic;
        this.distance = distance;
    }

    int getFcost() {
        return cost + heuristic;
    }

}