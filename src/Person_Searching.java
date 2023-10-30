import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Person_Searching extends Entity implements NeedSchedule{

    private final double actionPeriod;
    private final double animationPeriod;
    private int resourceCount;
    private final int resourceLimit;
    private int health;

    public Person_Searching(int imageIndex, List<PImage> images, String id, Point position, double actionPeriod, double animationPeriod, int resourceCount, int resourceLimit, int health) {
        super(imageIndex, images, id, position);

        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
        this.resourceCount = resourceCount;
        this.resourceLimit = resourceLimit;
        this.health = health;
    }

    public double getAnimationPeriod() {
        return animationPeriod;
    }

    public void executePersonSearchingActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> target = world.findNearest(this.getPosition(), new ArrayList<>(Arrays.asList(EntityKind.TREE, EntityKind.SAPLING)));

        if (target.isEmpty() || !moveToSearching(world, target.get(), scheduler) || !transformSearching(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), actionPeriod);
        }
    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), actionPeriod);
        scheduler.scheduleEvent(this, Factory.createAnimationAction(this, 0), animationPeriod);
    }

    public boolean moveToSearching(WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.getPosition().adjacent(target.getPosition())) {
            resourceCount += 1;
            //target.health--;
            return true;
        } else {
            Point nextPos = nextPositionDude(world, target.getPosition());

            if (!this.getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    private boolean transformSearching(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (resourceCount >= resourceLimit) {
            Person_Full dude = Factory.createPersonFull(this.getId(), this.getPosition(), actionPeriod, animationPeriod, resourceLimit, this.getImages());

            world.removeEntity(scheduler, this);
            scheduler.unscheduleAllEvents(this);

            world.tryAddEntity(dude);
            dude.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    public Point nextPositionDude(WorldModel world, Point destPos) {
        int horiz = Integer.signum(destPos.x - this.getPosition().x);
        Point newPos = new Point(this.getPosition().x + horiz, this.getPosition().y);

        if (horiz == 0 || world.getOccupant(newPos).isPresent() && world.getOccupant(newPos).get().getClass() != Stump.class) {
            int vert = Integer.signum(destPos.y - this.getPosition().y);
            newPos = new Point(this.getPosition().x, this.getPosition().y + vert);

            if (vert == 0 || world.getOccupant(newPos).isPresent() && world.getOccupant(newPos).get().getClass() != Stump.class) {
                newPos = this.getPosition();
            }
        }

        return newPos;
    }
}
