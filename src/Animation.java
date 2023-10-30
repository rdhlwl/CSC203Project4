//public class Animation implements Action {
//
//    private final Entity entity;
//    private final int repeatCount;
//
//    public Animation(Entity entity, int repeatCount) {
//        this.entity = entity;
//        this.repeatCount = repeatCount;
//    }
//
//    public void executeAction(EventScheduler scheduler) {
//        entity.nextImage();
//        if (repeatCount != 1) {
//            scheduler.scheduleEvent(entity, new Animation(entity, Math.max(repeatCount - 1, 0)), entity.animationPeriod());
//        }
//    }
//
//}
//
//


public class Animation implements Action {

    private final Entity entity;
    private final int repeatCount;

    public Animation(Entity entity, int repeatCount) {
        this.entity = entity;
        this.repeatCount = repeatCount;
    }

    public void executeAction(EventScheduler scheduler) {

        entity.nextImage();

        if (repeatCount != 1) {

            double animationPeriod = getAnimationPeriod(entity);

            scheduler.scheduleEvent(
                    entity,
                    new Animation(entity, Math.max(repeatCount - 1, 0)),
                    animationPeriod
            );

        }
    }

    private double getAnimationPeriod(Entity entity) {

        if (entity instanceof Sapling) {
            return ((Sapling)entity).getAnimationPeriod();
        }
        else if (entity instanceof Tree) {
            return ((Tree)entity).getAnimationPeriod();
        }
        else if (entity instanceof Fairy) {
            return ((Fairy)entity).getAnimationPeriod();
        }
        else if (entity instanceof Person_Full) {
            return ((Person_Full)entity).getAnimationPeriod();
        }
        else if (entity instanceof Person_Searching) {
            return ((Person_Searching)entity).getAnimationPeriod();
        }
        else if (entity instanceof Obstacle) {
            return ((Obstacle)entity).getAnimationPeriod();
        }

        //throw new IllegalArgumentException("Unsupported entity: " + entity);
        else {
            return 0.0;
        }
    }

}