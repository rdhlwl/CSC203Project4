import java.util.*;

import processing.core.PImage;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class Entity {
    private final EntityKind kind;
    private final String id;
    private Point position;
    private final List<PImage> images;
    private int imageIndex;
    private final int resourceLimit;
    private int resourceCount;
    private final double actionPeriod;
    private final double animationPeriod;
    private int health;
    private final int healthLimit;

    public Entity(EntityKind kind, String id, Point position, List<PImage> images, int resourceLimit, int resourceCount, double actionPeriod, double animationPeriod, int health, int healthLimit) {
        this.kind = kind;
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceCount;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
        this.health = health;
        this.healthLimit = healthLimit;
    }

    public void nextImage() {
        imageIndex = imageIndex + 1;
    }

    /**
     *
     * @param world - world model object
     * @param imageStore - ImageStore object holding all of the images in the project
     * @param scheduler
     */
    public void executeSaplingActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        health++;
        if (!transformPlant(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), actionPeriod);
        }
    }

    public void executeTreeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {

        if (!transformPlant(world, scheduler, imageStore)) {

            scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), actionPeriod);
        }
    }

    public void executeFairyActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fairyTarget = world.findNearest(position, new ArrayList<>(List.of(EntityKind.STUMP)));

        if (fairyTarget.isPresent()) {
            Point tgtPos = fairyTarget.get().position;

            if (moveToFairy(world, fairyTarget.get(), scheduler)) {

                Entity sapling = Factory.createSapling(WorldLoader.SAPLING_KEY + "_" + fairyTarget.get().id, tgtPos, imageStore.getImageList(WorldLoader.SAPLING_KEY));

                world.tryAddEntity(sapling);
                sapling.scheduleActions(scheduler, world, imageStore);
            }
        }

        scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), actionPeriod);
    }

    public void executePersonSearchingActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> target = world.findNearest(position, new ArrayList<>(Arrays.asList(EntityKind.TREE, EntityKind.SAPLING)));

        if (target.isEmpty() || !moveToSearching(world, target.get(), scheduler) || !transformSearching(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), actionPeriod);
        }
    }

    public void executePersonFullActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fullTarget = world.findNearest(position, new ArrayList<>(List.of(EntityKind.HOUSE)));

        if (fullTarget.isPresent() && moveToFull(world, fullTarget.get(), scheduler)) {
            transformFull(world, scheduler, imageStore);
        } else {
            scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), actionPeriod);
        }
    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        switch (kind) {
            case PERSON_FULL:
                scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), actionPeriod);
                scheduler.scheduleEvent(this, Factory.createAnimationAction(this, 0), getAnimationPeriod());
                break;

            case PERSON_SEARCHING:
                scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), actionPeriod);
                scheduler.scheduleEvent(this, Factory.createAnimationAction(this, 0), getAnimationPeriod());
                break;

            case OBSTACLE:
                scheduler.scheduleEvent(this, Factory.createAnimationAction(this, 0), getAnimationPeriod());
                break;

            case FAIRY:
                scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), actionPeriod);
                scheduler.scheduleEvent(this, Factory.createAnimationAction(this, 0), getAnimationPeriod());
                break;

            case SAPLING:
                scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), actionPeriod);
                scheduler.scheduleEvent(this, Factory.createAnimationAction(this, 0), getAnimationPeriod());
                break;

            case TREE:
                scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), actionPeriod);
                scheduler.scheduleEvent(this, Factory.createAnimationAction(this, 0), getAnimationPeriod());
                break;

            default:
        }
    }

    private boolean transformSearching(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (resourceCount >= resourceLimit) {
            Entity dude = Factory.createPersonFull(id, position, actionPeriod, animationPeriod, resourceLimit, images);

            world.removeEntity(scheduler, this);
            scheduler.unscheduleAllEvents(this);

            world.tryAddEntity(dude);
            dude.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    private void transformFull(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        Entity dude = Factory.createPersonSearching(id, position, actionPeriod, animationPeriod, resourceLimit, images);

        world.removeEntity(scheduler, this);

        world.tryAddEntity(dude);
        dude.scheduleActions(scheduler, world, imageStore);
    }

    public boolean transformPlant(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (kind == EntityKind.TREE) {
            return transformTree(world, scheduler, imageStore);
        } else if (kind == EntityKind.SAPLING) {
            return transformSapling(world, scheduler, imageStore);
        } else {
            throw new UnsupportedOperationException(String.format("transformPlant not supported for %s", this));
        }
    }

    private boolean transformTree(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (health <= 0) {
            Entity stump = Factory.createStump(WorldLoader.STUMP_KEY + "_" + id, position, imageStore.getImageList(WorldLoader.STUMP_KEY));

            world.removeEntity(scheduler, this);

            world.tryAddEntity(stump);

            return true;
        }

        return false;
    }

    private boolean transformSapling(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (health <= 0) {
            Entity stump = Factory.createStump(WorldLoader.STUMP_KEY + "_" + id, position, imageStore.getImageList(WorldLoader.STUMP_KEY));

            world.removeEntity(scheduler, this);

            world.tryAddEntity(stump);

            return true;
        } else if (health >= healthLimit) {
            Entity tree = Factory.createTreeWithDefaults(WorldLoader.TREE_KEY + "_" + id, position, imageStore.getImageList(WorldLoader.TREE_KEY));

            world.removeEntity(scheduler, this);

            world.tryAddEntity(tree);
            tree.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    public boolean moveToFairy(WorldModel world, Entity target, EventScheduler scheduler) {
        if (position.adjacent(target.position)) {
            world.removeEntity(scheduler, target);
            return true;
        } else {
            Point nextPos = nextPositionFairy(world, target.position);

            if (!position.equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    public boolean moveToSearching(WorldModel world, Entity target, EventScheduler scheduler) {
        if (position.adjacent(target.position)) {
            resourceCount += 1;
            target.health--;
            return true;
        } else {
            Point nextPos = nextPositionDude(world, target.position);

            if (!position.equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    public boolean moveToFull(WorldModel world, Entity target, EventScheduler scheduler) {
        if (position.adjacent(target.position)) {
            return true;
        } else {
            Point nextPos = nextPositionDude(world, target.position);

            if (!position.equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    public Point nextPositionFairy(WorldModel world, Point destPos) {
        int horiz = Integer.signum(destPos.x - position.x);
        Point newPos = new Point(position.x + horiz, position.y);

        if (horiz == 0 || world.getOccupant(newPos).isPresent() && world.getOccupant(newPos).get().kind != EntityKind.HOUSE) {
            int vert = Integer.signum(destPos.y - position.y);
            newPos = new Point(position.x, position.y + vert);

            if (vert == 0 || world.getOccupant(newPos).isPresent() && world.getOccupant(newPos).get().kind != EntityKind.HOUSE) {
                newPos = position;
            }
        }

        return newPos;
    }

    public Point nextPositionDude(WorldModel world, Point destPos) {
        int horiz = Integer.signum(destPos.x - position.x);
        Point newPos = new Point(position.x + horiz, position.y);

        if (horiz == 0 || world.getOccupant(newPos).isPresent() && world.getOccupant(newPos).get().kind != EntityKind.STUMP) {
            int vert = Integer.signum(destPos.y - position.y);
            newPos = new Point(position.x, position.y + vert);

            if (vert == 0 || world.getOccupant(newPos).isPresent() && world.getOccupant(newPos).get().kind != EntityKind.STUMP) {
                newPos = position;
            }
        }

        return newPos;
    }

    public PImage getCurrentImage() {
        return this.images.get(this.imageIndex % this.images.size());

    }

    public double getAnimationPeriod() {
        switch (kind) {
            case PERSON_FULL:
            case PERSON_SEARCHING:
            case OBSTACLE:
            case FAIRY:
            case SAPLING:
            case TREE:
                return animationPeriod;
            default:
                throw new UnsupportedOperationException(String.format("getAnimationPeriod not supported for %s", kind));
        }
    }

    /**
     * Helper method for testing. Preserve this functionality while refactoring.
     */
    public String log(){
        return this.id.isEmpty() ? null :
                String.format("%s %d %d %d", this.id, this.position.x, this.position.y, this.imageIndex);
    }

    public EntityKind getKind() {
        return kind;
    }

    public String getId() {
        return id;
    }

    public Point getPosition() {
        return position;
    }
    public void setPosition(Point pos) {
        this.position = pos;
    }
}
