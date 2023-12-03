import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import processing.core.*;

public final class VirtualWorld extends PApplet {
    private static String[] ARGS;

    private static final int VIEW_WIDTH = 640;
    private static final int VIEW_HEIGHT = 480;
    private static final int TILE_WIDTH = 32;
    private static final int TILE_HEIGHT = 32;

    private static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
    private static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;

    private static final String IMAGE_LIST_FILE_NAME = "imagelist";
    private static final String DEFAULT_IMAGE_NAME = "background_default";
    private static final int DEFAULT_IMAGE_COLOR = 0x808080;

    private static final String FAST_FLAG = "-fast";
    private static final String FASTER_FLAG = "-faster";
    private static final String FASTEST_FLAG = "-fastest";
    private static final double FAST_SCALE = 0.5;
    private static final double FASTER_SCALE = 0.25;
    private static final double FASTEST_SCALE = 0.10;

    private String loadFile = "world.sav";
    private long startTimeMillis = 0;
    private double timeScale = 1.0;

    private ImageStore imageStore;
    private WorldModel world;
    private WorldView view;
    private EventScheduler scheduler;

    public void settings() {
        size(VIEW_WIDTH, VIEW_HEIGHT);
    }

    /*
       Processing entry point for "sketch" setup.
    */
    public void setup() {
        parseCommandLine(ARGS);
        loadImages(IMAGE_LIST_FILE_NAME);
        loadWorld(loadFile, this.imageStore);

        this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world, TILE_WIDTH, TILE_HEIGHT);
        this.scheduler = new EventScheduler();
        this.startTimeMillis = System.currentTimeMillis();
        this.scheduleActions(world, scheduler, imageStore);
    }

    public void draw() {
        double appTime = (System.currentTimeMillis() - startTimeMillis) * 0.001;
        double frameTime = appTime / timeScale - scheduler.getCurrentTime();
        this.update(frameTime);
        view.drawViewport();
    }

    public void update(double frameTime){
        scheduler.updateOnTime(frameTime);
    }

    // Just for debugging and for P5
    // Be sure to refactor this method as appropriate
    public void mousePressed() {
        Point pressed = mouseToPoint();
        System.out.println("CLICK! " + pressed.x + ", " + pressed.y);
        ///// test

        // first remove any entities to prevent a crash
        Optional<Entity> entityOptional1 = world.getOccupant(pressed);
        if (entityOptional1.isPresent()) {
            Entity entity = entityOptional1.get();
            world.removeEntity(scheduler,entity);

        }

//        set background cells
        world.setBackgroundCell(pressed, new Background("centerrubble", imageStore.getImageList("centerrubble")));
        Point xminus1 = new Point(pressed.x-1, pressed.y);
        Point xplus1 = new Point(pressed.x+1, pressed.y);
        Point ymin1 = new Point(pressed.x, pressed.y-1);
        Point yplus1 = new Point(pressed.x, pressed.y+1);
        world.setBackgroundCell(xminus1, new Background("rubble", imageStore.getImageList("rubble")));
        world.setBackgroundCell(xplus1, new Background("rubble", imageStore.getImageList("rubble")));
        world.setBackgroundCell(ymin1, new Background("rubble", imageStore.getImageList("rubble")));
        world.setBackgroundCell(yplus1, new Background("rubble", imageStore.getImageList("rubble")));

        Point tl = new Point(pressed.x-1, pressed.y-1);
        world.setBackgroundCell(tl, new Background("rubbleTL", imageStore.getImageList("rubbleTL")));
        Point bl = new Point(pressed.x-1, pressed.y+1);
        world.setBackgroundCell(bl, new Background("rubbleBL", imageStore.getImageList("rubbleBL")));
        Point tr = new Point(pressed.x+1, pressed.y-1);
        world.setBackgroundCell(tr, new Background("rubbleTR", imageStore.getImageList("rubbleTR")));
        Point br = new Point(pressed.x+1, pressed.y+1);
        world.setBackgroundCell(br, new Background("rubbleBR", imageStore.getImageList("rubbleBR")));

        Optional<Entity> entityOptional3 = world.getOccupant(xminus1);
        if (entityOptional3.isPresent()) {
            Entity entity = entityOptional3.get();
            world.removeEntity(scheduler,entity);
        }
        Optional<Entity> entityOptional4 = world.getOccupant(xplus1);
        if (entityOptional4.isPresent()) {
            Entity entity = entityOptional4.get();
            world.removeEntity(scheduler,entity);
        }
        Optional<Entity> entityOptional5= world.getOccupant(ymin1);
        if (entityOptional5.isPresent()) {
            Entity entity = entityOptional5.get();
            world.removeEntity(scheduler,entity);
        }
        Optional<Entity> entityOptional6= world.getOccupant(yplus1);
        if (entityOptional6.isPresent()) {
            Entity entity = entityOptional6.get();
            world.removeEntity(scheduler,entity);
        }
        Optional<Entity> entityOptional7= world.getOccupant(tl);
        if (entityOptional7.isPresent()) {
            Entity entity = entityOptional7.get();
            world.removeEntity(scheduler,entity);
        }
        Optional<Entity> entityOptional8= world.getOccupant(bl);
        if (entityOptional8.isPresent()) {
            Entity entity = entityOptional8.get();
            world.removeEntity(scheduler,entity);
        }
        Optional<Entity> entityOptional9= world.getOccupant(tr);
        if (entityOptional9.isPresent()) {
            Entity entity = entityOptional9.get();
            world.removeEntity(scheduler,entity);
        }
        Optional<Entity> entityOptional10= world.getOccupant(br);
        if (entityOptional10.isPresent()) {
            Entity entity = entityOptional10.get();
            world.removeEntity(scheduler,entity);
        }

        // add entities
        world.tryAddEntity(new Meteor(0,imageStore.getImageList("meteor"), "meteor", pressed, 0.0));


        // find target
        Optional<Entity> target = world.findNearest(pressed, new ArrayList<>(Arrays.asList(Person_Searching.class, Person_Full.class)));
        //if within distance
        if (target.isPresent()) {
            if (pressed.targetProximityEuclidean(target.get().getPosition())) {
                Point tgpos = target.get().getPosition();
                System.out.println("WITHIN DIST WITHIN DIST");
                Zombie zombie1 = Factory.createZombie("zombie", tgpos, .123, .123, imageStore.getImageList(WorldLoader.ZOMBIE_KEY));
                Entity entity = target.get();
                world.removeEntity(scheduler, entity);

                //scheduler.unscheduleAllEvents(zombie1);
                world.tryAddEntity(zombie1);
                zombie1.scheduleActions(scheduler, world, imageStore);

            }
        }

        /////// testing done
        Optional<Entity> entityOptional = world.getOccupant(pressed);
        if (entityOptional.isPresent()) {
            Entity entity = entityOptional.get();
            System.out.println("id: " + entity.getId() + " - " + entity.getClass() + " at " + entity.getPosition());
        }

    }

    private Point mouseToPoint() {
        return view.getViewport().viewportToWorld(mouseX / TILE_WIDTH, mouseY / TILE_HEIGHT);
    }

    public void keyPressed() {
        if (key == CODED) {
            int dx = 0;
            int dy = 0;

            switch (keyCode) {
                case UP -> dy -= 1;
                case DOWN -> dy += 1;
                case LEFT -> dx -= 1;
                case RIGHT -> dx += 1;
            }
            view.shiftView(dx, dy);
        }
    }

    public static Background createDefaultBackground(ImageStore imageStore) {
        return new Background(DEFAULT_IMAGE_NAME, imageStore.getImageList(DEFAULT_IMAGE_NAME));
    }

    public static PImage createImageColored(int width, int height, int color) {
        PImage img = new PImage(width, height, RGB);
        img.loadPixels();
        Arrays.fill(img.pixels, color);
        img.updatePixels();
        return img;
    }

    public void loadImages(String filename) {
        this.imageStore = new ImageStore(createImageColored(TILE_WIDTH, TILE_HEIGHT, DEFAULT_IMAGE_COLOR));
        try {
            Scanner in = new Scanner(new File(filename));
            ImageLoader.loadImages(in, imageStore,this);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public void loadWorld(String file, ImageStore imageStore) {
        this.world = new WorldModel();
        try {
            Scanner in = new Scanner(new File(file));
            WorldLoader.load(world, in, imageStore, createDefaultBackground(imageStore));
        } catch (FileNotFoundException e) {
            Scanner in = new Scanner(file);
            WorldLoader.load(world, in, imageStore, createDefaultBackground(imageStore));
        }
    }
    public void scheduleActions(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {

        for (Entity entity : world.getEntities()) {
            if (entity instanceof NeedSchedule) {
                ((NeedSchedule) entity).scheduleActions(scheduler, world, imageStore);
            }
        }
    }
    public void parseCommandLine(String[] args) {
        for (String arg : args) {
            switch (arg) {
                case FAST_FLAG -> timeScale = Math.min(FAST_SCALE, timeScale);
                case FASTER_FLAG -> timeScale = Math.min(FASTER_SCALE, timeScale);
                case FASTEST_FLAG -> timeScale = Math.min(FASTEST_SCALE, timeScale);
                default -> loadFile = arg;
            }
        }
    }

    public static void main(String[] args) {
        VirtualWorld.ARGS = args;
        PApplet.main(VirtualWorld.class);
    }

    public static List<String> headlessMain(String[] args, double lifetime){
        VirtualWorld.ARGS = args;

        VirtualWorld virtualWorld = new VirtualWorld();
        virtualWorld.setup();
        virtualWorld.update(lifetime);

        return virtualWorld.world.log();
    }
}
