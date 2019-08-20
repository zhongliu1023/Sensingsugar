package me.sensingself.sensingsugar.Engine.models;

/**
 * Created by liujie on 12/15/17.
 */

public class Dimension {
    public int x;
    public int y;
    public int width;
    public int height;

    public void  init(int x, int y, int w, int h)
    {
        this.x      = x;
        this.y      = y;
        this.width  = w;
        this.height = h;
    }
}
