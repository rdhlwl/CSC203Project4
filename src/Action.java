public interface Action {

    public void executeAction(EventScheduler scheduler);

}


//
//
///**
// * An action that can be taken by an entity.
// * Actions can be either an activity (involving movement, gaining health, etc)
// * or an animation (updating the image being displayed).
// */
//public final class Action {
//    private final ActionKind kind;
//    private final Entity entity;
//    private final WorldModel world;
//    private final ImageStore imageStore;
//    private final int repeatCount;
//
//    public Action(ActionKind kind, Entity entity, WorldModel world, ImageStore imageStore, int repeatCount) {
//        this.kind = kind;
//        this.entity = entity;
//        this.world = world;
//        this.imageStore = imageStore;
//        this.repeatCount = repeatCount;
//    }
//    public void executeAction(EventScheduler scheduler) {
//        switch (kind) {
//            case ACTIVITY:
//                this.executeActivityAction(scheduler);
//                break;
//
//            case ANIMATION:
//                this.executeAnimationAction(scheduler);
//                break;
//        }
//    }
//    private void executeAnimationAction(EventScheduler scheduler) {
//        entity.nextImage();
//
//        if (repeatCount != 1) {
//            scheduler.scheduleEvent(entity, Factory.createAnimationAction(entity, Math.max(repeatCount - 1, 0)), entity.getAnimationPeriod());
//        }
//    }
//
//    private void executeActivityAction(EventScheduler scheduler) {
//        switch (entity.getKind()) {
//            case SAPLING:
//                Sapling.executeSaplingActivity(world, imageStore, scheduler);
//                break;
//            case TREE:
//                Tree.executeTreeActivity(world, imageStore, scheduler);
//                break;
//            case FAIRY:
//                Fairy.executeFairyActivity(world, imageStore, scheduler);
//                break;
//            case PERSON_SEARCHING:
//                Person_Searching.executePersonSearchingActivity(world, imageStore, scheduler);
//                break;
//            case PERSON_FULL:
//                entity.executePersonFullActivity(world, imageStore, scheduler);
//                break;
//            default:
//                throw new UnsupportedOperationException(String.format("executeActivityAction not supported for %s", entity.getKind()));
//        }
//    }
//
//
//}
