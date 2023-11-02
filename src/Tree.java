import processing.core.PImage;

import java.util.List;

public class Tree extends Entity implements NeedSchedule, NeedAnimationPeriod{

    private final int healthLimit;
    private int health;
    private final double actionPeriod;
    private final double animationPeriod;

    public Tree(int imageIndex, List<PImage> images, String id, Point position, int healthLimit, int health, double actionPeriod, double animationPeriod) {
        super(imageIndex, images, id, position);
        this.healthLimit = healthLimit;
        this.health = health;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
    }

    public double getAnimationPeriod() {
        return animationPeriod;
    }
    public void decreaseHealth(){ health--; }


    public void executeTreeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {

        if (!transformPlant(world, scheduler, imageStore)) {

            scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), actionPeriod);
        }
    }

    public boolean transformPlant(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        return transformTree(world, scheduler, imageStore);
    }

    private boolean transformTree(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (health <= 0) {
            Entity stump = Factory.createStump(WorldLoader.STUMP_KEY + "_" + this.getId(), this.getPosition(), imageStore.getImageList(WorldLoader.STUMP_KEY));

            world.removeEntity(scheduler, this);

            world.tryAddEntity(stump);

            return true;
        }

        return false;
    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
         scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), actionPeriod);
         scheduler.scheduleEvent(this, Factory.createAnimationAction(this, 0), animationPeriod);
    }

}
