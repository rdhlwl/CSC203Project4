import processing.core.PImage;

import java.util.List;

public class Meteor extends Entity implements NeedSchedule, NeedAnimationPeriod{

    private final double animationPeriod;

    public Meteor(int imageIndex, List<PImage> images, String id, Point position, double animationPeriod) {
        super(imageIndex, images, id, position);
        this.animationPeriod = animationPeriod;
    }

    public double getAnimationPeriod() {
        return animationPeriod;
    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {

        scheduler.scheduleEvent(this, Factory.createAnimationAction(this, 0), animationPeriod);
    }


}
