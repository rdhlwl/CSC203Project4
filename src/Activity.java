public class Activity implements Action {

    private final Entity entity;
    private final WorldModel world;
    private final ImageStore imageStore;

    public Activity(Entity entity, WorldModel world, ImageStore imageStore) {
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
    }

    public void executeAction(EventScheduler scheduler) {
        if (entity instanceof Sapling) {
            Sapling sapling = (Sapling)entity;
            sapling.executeSaplingActivity(world, imageStore, scheduler);
        }
        else if (entity instanceof Tree) {
            Tree tree = (Tree)entity;
            tree.executeTreeActivity(world, imageStore, scheduler);
        }
        else if (entity instanceof Fairy) {
            Fairy fairy = (Fairy)entity;
            fairy.executeFairyActivity(world, imageStore, scheduler);
        }
        else if (entity instanceof Person_Searching) {
            Person_Searching personSearching = (Person_Searching)entity;
            personSearching.executePersonSearchingActivity(world, imageStore, scheduler);
        }
        else if (entity instanceof Person_Full) {
            Person_Full personFull = (Person_Full)entity;
            personFull.executePersonFullActivity(world, imageStore, scheduler);
        }
        else {
            throw new UnsupportedOperationException
                    (String.format("executeActivityAction not supported for %s", entity.getClass()));
        }

    }
}

