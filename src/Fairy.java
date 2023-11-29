import processing.core.PImage;

import java.util.*;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Fairy extends Entity implements NeedSchedule, NeedAnimationPeriod{


    private final double actionPeriod;
    private final double animationPeriod;

    public Fairy(int imageIndex, List<PImage> images, String id, Point position, double actionPeriod, double animationPeriod) {
        super(imageIndex, images, id, position);
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
    }

    public void executeFairyActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fairyTarget = world.findNearest(this.getPosition(), new ArrayList<>(List.of(Stump.class)));

        if (fairyTarget.isPresent()) {
            Point tgtPos = fairyTarget.get().getPosition();

            if (moveToFairy(world, fairyTarget.get(), scheduler)) {

                Sapling sapling = Factory.createSapling(WorldLoader.SAPLING_KEY + "_" + fairyTarget.get().getId(), tgtPos, imageStore.getImageList(WorldLoader.SAPLING_KEY));

                world.tryAddEntity(sapling);
                sapling.scheduleActions(scheduler, world, imageStore);
            }
        }

        scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), actionPeriod);
    }

    public double getAnimationPeriod() {
        return animationPeriod;
    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), actionPeriod);
        scheduler.scheduleEvent(this, Factory.createAnimationAction(this, 0), animationPeriod);
    }

    public boolean moveToFairy(WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.getPosition().adjacent(target.getPosition())) {
            world.removeEntity(scheduler, target);
            return true;
        } else {
            Point nextPos = nextPositionFairy(world, target.getPosition());

            if (!this.getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

//    public Point nextPositionFairy(WorldModel world, Point destPos) {
//        PathingStrategy strat = new AStarPathingStrategy();
//
//        Predicate<Point> canPassThrough = x -> world.getOccupant(destPos).get().getClass() != Obstacle.class; // can pass through if not an obstacle
//        BiPredicate<Point, Point> withinReach = (p1,p2) -> p1.adjacent(p2); // is withinreach if adjacent
//        List<Point> path = strat.computePath(getPosition(), destPos, canPassThrough,withinReach, PathingStrategy.CARDINAL_NEIGHBORS);
//
//        if(path.getFirst() == null){
//
//            return null;
//        }
//        else{
//            return path.getFirst();
//        }
//        List<Point> points = new ArrayList<>();
//
//
//        while (!(getPosition().adjacent(destPos)))
//        {
//            points = strat.computePath(getPosition(), destPos,
//                    x -> world.getOccupant(destPos).get().getClass() != Obstacle.class,
//                    (p1,p2) -> p1.adjacent(p2),
//                    PathingStrategy.CARDINAL_NEIGHBORS);
//
//            if (points.size() == 0)
//            {
//                System.out.println("No path found");
//            }
//
//            setPosition(points.get(0));
////         path.addAll(points);
//        }
//
//        return points.get(0);
//
//    }

        public Point nextPositionFairy(WorldModel world, Point destPos) {
            PathingStrategy strat = new AStarPathingStrategy();

            Predicate<Point> canPassThrough = x -> world.getOccupant(destPos).get().getClass() != Obstacle.class;
            BiPredicate<Point, Point> withinReach = (p1,p2) -> p1.adjacent(p2);

            List<Point> path = strat.computePath(getPosition(), destPos, canPassThrough, withinReach, PathingStrategy.CARDINAL_NEIGHBORS);

            if (path.isEmpty()) {
                System.out.println("No path found");
                return null;
            } else {
                return path.get(0);
            }
        }





//        int horiz = Integer.signum(destPos.x - this.getPosition().x);
//        Point newPos = new Point(this.getPosition().x + horiz, this.getPosition().y);
//
//        if (horiz == 0 || world.getOccupant(newPos).isPresent() && world.getOccupant(newPos).get().getClass() != House.class) {
//            int vert = Integer.signum(destPos.y - this.getPosition().y);
//            newPos = new Point(this.getPosition().x, this.getPosition().y + vert);
//
//            if (vert == 0 || world.getOccupant(newPos).isPresent() && world.getOccupant(newPos).get().getClass() != House.class) {
//                newPos = this.getPosition();
//            }
//        }
//
//        return newPos;
//    }
}
