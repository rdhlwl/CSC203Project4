import processing.core.PImage;

import java.util.List;

public abstract class Entity {

    private int imageIndex;
    private final List<PImage> images;
    private final String id;
    private Point position;



    public Entity(int imageIndex, List<PImage> images, String id, Point position)
    {
        this.imageIndex = imageIndex;
        this.images = images;
        this.id = id;
        this.position = position;
    }

    public void nextImage() {
        imageIndex = imageIndex + 1;
    }

    public PImage getCurrentImage() {
        return this.images.get(this.imageIndex % this.images.size());

    }

    public String log(){
        return this.id.isEmpty() ? null :
                String.format("%s %d %d %d", this.id, this.position.x, this.position.y, this.imageIndex);
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

    public List<PImage> getImages() { return images; }

}