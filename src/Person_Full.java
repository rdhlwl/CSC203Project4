import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Person_Full extends Entity implements NeedSchedule{

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
        Optional<Entity> fullTarget = world.findNearest(this.getPosition(), new ArrayList<>(List.of(EntityKind.HOUSE)));

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
        int horiz = Integer.signum(destPos.x - this.getPosition().x);
        Point newPos = new Point(this.getPosition().x + horiz, this.getPosition().y);

        if (horiz == 0 || world.getOccupant(newPos).isPresent() && !(world.getOccupant(newPos).get().getClass().equals(Stump.class))) {
            int vert = Integer.signum(destPos.y - this.getPosition().y);
            newPos = new Point(this.getPosition().x, this.getPosition().y + vert);

            if (vert == 0 || world.getOccupant(newPos).isPresent() && !(world.getOccupant(newPos).get().getClass().equals(Stump.class))) {
                newPos = this.getPosition();
            }
        }

        return newPos;
    }


    private void transformFull(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        Person_Searching dude = Factory.createPersonSearching(this.getId(), this.getPosition(), actionPeriod, animationPeriod, resourceLimit, this.getImages());

        world.removeEntity(scheduler, this);

        world.tryAddEntity(dude);
        dude.scheduleActions(scheduler, world, imageStore);
    }
}
