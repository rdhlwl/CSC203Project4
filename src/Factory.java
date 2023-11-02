import java.util.*;

import processing.core.PImage;

/**
 * This class contains the creation logic for Actions and Entities
 */
public final class Factory {
    private static final double TREE_ANIMATION_MAX = 0.600;
    private static final double TREE_ANIMATION_MIN = 0.050;
    private static final double TREE_ACTION_MAX = 1.400;
    private static final double TREE_ACTION_MIN = 1.000;
    private static final int TREE_HEALTH_MAX = 3;
    private static final int TREE_HEALTH_MIN = 1;
    private static final double SAPLING_ACTION_ANIMATION_PERIOD = 1.000; // have to be in sync since grows and gains health at same time
    private static final int SAPLING_HEALTH_LIMIT = 5;


    public static Action createAnimationAction(Entity entity, int repeatCount) {
        //return new Action(ActionKind.ANIMATION, entity, null, null, repeatCount);
        return new Animation(entity, repeatCount);
    }

    public static Action createActivityAction(Entity entity, WorldModel world, ImageStore imageStore) {
        //return new Action(ActionKind.ACTIVITY, entity, world, imageStore, 0);
        return new Activity(entity, world, imageStore);
    }

    public static House createHouse(String id, Point position, List<PImage> images) {
        return new House(0, images, id, position);
        // return new Entity(EntityKind.HOUSE, id, position, images, 0, 0, 0, 0, 0, 0);
    }

    public static Obstacle createObstacle(String id, Point position, double animationPeriod, List<PImage> images) {
        return new Obstacle(0, images, id, position, animationPeriod);
        //return new Entity(EntityKind.OBSTACLE, id, position, images, 0, 0, 0, animationPeriod, 0, 0);
    }

    public static Tree createTree(String id, Point position, double actionPeriod, double animationPeriod, int health, List<PImage> images) {
        return new Tree(0,images, id, position, 0, health, actionPeriod, animationPeriod);
        // return new Entity(EntityKind.TREE, id, position, images, 0, 0, actionPeriod, animationPeriod, health, 0);
    }
    public static Tree createTreeWithDefaults(String id, Point position, List<PImage> images) {
        return new Tree(0, images, id, position, 0, Factory.getIntFromRange(TREE_HEALTH_MAX, TREE_HEALTH_MIN), Factory.getNumFromRange(TREE_ACTION_MAX, TREE_ACTION_MIN), Factory.getNumFromRange(TREE_ANIMATION_MAX, TREE_ANIMATION_MIN));
        // return new Entity(EntityKind.TREE, id, position, images, 0, 0, Factory.getNumFromRange(TREE_ACTION_MAX, TREE_ACTION_MIN), Factory.getNumFromRange(TREE_ANIMATION_MAX, TREE_ANIMATION_MIN), Factory.getIntFromRange(TREE_HEALTH_MAX, TREE_HEALTH_MIN), 0);
    }
    private static int getIntFromRange(int max, int min) {
        Random rand = new Random();
        return min + rand.nextInt(max-min);
    }

    private static double getNumFromRange(double max, double min) {
        Random rand = new Random();
        return min + rand.nextDouble() * (max - min);
    }

    public static Stump createStump(String id, Point position, List<PImage> images) {
        return new Stump(0, images, id, position);
        //return new Entity(EntityKind.STUMP, id, position, images, 0, 0, 0, 0, 0, 0);
    }

    // health starts at 0 and builds up until ready to convert to Tree
    public static Sapling createSapling(String id, Point position, List<PImage> images) {
        return new Sapling(0, images, id, position, 0, SAPLING_ACTION_ANIMATION_PERIOD, SAPLING_HEALTH_LIMIT, SAPLING_ACTION_ANIMATION_PERIOD);
        // return new Entity(EntityKind.SAPLING, id, position, images, 0, 0, SAPLING_ACTION_ANIMATION_PERIOD, SAPLING_ACTION_ANIMATION_PERIOD, 0, SAPLING_HEALTH_LIMIT);
    }

    public static Fairy createFairy(String id, Point position, double actionPeriod, double animationPeriod, List<PImage> images) {
        return new Fairy(0,images,id, position, actionPeriod, animationPeriod);
        // return new Entity(EntityKind.FAIRY, id, position, images, 0, 0, actionPeriod, animationPeriod, 0, 0);
    }

    // need resource count, though it always starts at 0
    public static Person_Searching createPersonSearching(String id, Point position, double actionPeriod, double animationPeriod, int resourceLimit, List<PImage> images) {
        return new Person_Searching(0, images, id, position, actionPeriod, animationPeriod, 0, resourceLimit, 0);
        // return new Entity(EntityKind.PERSON_SEARCHING, id, position, images, resourceLimit, 0, actionPeriod, animationPeriod, 0, 0);
    }

    // don't technically need resource count ... full
    public static Person_Full createPersonFull(String id, Point position, double actionPeriod, double animationPeriod, int resourceLimit, List<PImage> images) {
        return new Person_Full(0, images, id, position, actionPeriod, animationPeriod, resourceLimit);
        // return new Entity(EntityKind.PERSON_FULL, id, position, images, resourceLimit, 0, actionPeriod, animationPeriod, 0, 0);
    }

}
