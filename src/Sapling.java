import processing.core.PImage;

import java.util.List;

public class Sapling extends Entity implements NeedSchedule{

    private int health;
    private final double actionPeriod;
    private final int healthLimit;
    private final double animationPeriod;
    public Sapling(int imageIndex, List<PImage> images, String id, Point position, int health, double actionPeriod, int healthLimit, double animationPeriod) {
        super(imageIndex, images, id, position);
        this.health = health;
        this.actionPeriod = actionPeriod;
        this.healthLimit = healthLimit;
        this.animationPeriod = animationPeriod;
    }

    public void executeSaplingActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        health++;
        if (!transformPlant(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), actionPeriod);
        }
    }

    public boolean transformPlant(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        return transformSapling(world, scheduler, imageStore);
    }


    private boolean transformSapling(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (health <= 0) {
            Stump stump = Factory.createStump(WorldLoader.STUMP_KEY + "_" + this.getId(), this.getPosition(), imageStore.getImageList(WorldLoader.STUMP_KEY));

            world.removeEntity(scheduler, this);

            world.tryAddEntity(stump);

            return true;
        } else if (health >= healthLimit) {
            Tree tree = Factory.createTreeWithDefaults(WorldLoader.TREE_KEY + "_" + this.getId(), this.getPosition(), imageStore.getImageList(WorldLoader.TREE_KEY));

            world.removeEntity(scheduler, this);

            world.tryAddEntity(tree);
            tree.scheduleActions(scheduler, world, imageStore);


            return true;
        }

        return false;
    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), actionPeriod);
        scheduler.scheduleEvent(this, Factory.createAnimationAction(this, 0), animationPeriod);
    }
}
