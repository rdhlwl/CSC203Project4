import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class Person_Full extends Entity implements NeedSchedule, NeedAnimationPeriod{

    private final double actionPeriod;
    private final double animationPeriod;
    private final int resourceLimit;


    public Person_Full(int imageIndex, List<PImage> images, String id, Point position, double actionPeriod, double animationPeriod, int resourceLimit) {
        super(imageIndex, images, id, position);

        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
        this.resourceLimit = resourceLimit;
    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), actionPeriod);
        scheduler.scheduleEvent(this, Factory.createAnimationAction(this, 0), animationPeriod);
    }

    public double getAnimationPeriod() {
        return animationPeriod;
    }

    public void executePersonFullActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fullTarget = world.findNearest(this.getPosition(), new ArrayList<>(List.of(House.class)));

        if (fullTarget.isPresent() && moveToFull(world, fullTarget.get(), scheduler)) {
            transformFull(world, scheduler, imageStore);
        } else {
            scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), actionPeriod);
        }
    }

    public boolean moveToFull(WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.getPosition().adjacent(target.getPosition())) {
            return true;
        } else {
            Point nextPos = nextPositionDude(world, target.getPosition());

            if (!this.getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    public Point nextPositionDude(WorldModel world, Point destPos) {
        PathingStrategy strat = new AStarPathingStrategy();

        Predicate<Point> canPassThrough = x -> world.getOccupant(destPos).get().getClass() != Obstacle.class;
        BiPredicate<Point, Point> withinReach = (p1, p2) -> p1.adjacent(p2);

        List<Point> path = strat.computePath(getPosition(), destPos, canPassThrough, withinReach, PathingStrategy.CARDINAL_NEIGHBORS);

        if (path.isEmpty()) {
            System.out.println("No path found");
            return null;
        } else {
            return path.get(0);
        }
    }


    private void transformFull(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        Person_Searching dude = Factory.createPersonSearching(this.getId(), this.getPosition(), actionPeriod, animationPeriod, resourceLimit, this.getImages());

        world.removeEntity(scheduler, this);

        world.tryAddEntity(dude);
        dude.scheduleActions(scheduler, world, imageStore);
    }
}
