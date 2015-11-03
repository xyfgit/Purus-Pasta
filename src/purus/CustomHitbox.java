package purus;

import java.awt.Color;

import javax.media.opengl.GL2;

import haven.BGL;
import haven.Coord3f;
import haven.GOut;
import haven.Gob;
import haven.RenderList;
import haven.Sprite;
import haven.States;

public class CustomHitbox extends Sprite {
    // TODO: make it generic to facilitate different gobs
	
	public float size;

    private static final States.ColState clrstate = new States.ColState(new Color(114, 159, 207, 200));
    private static int w = 12;

	private static int h = 12;

	private static final int x = -6;

	private static final int y = -6;

    public CustomHitbox(Gob gob, int size) {
        super(gob, null);
        w = size;
        h = size;
        
    }

    public boolean setup(RenderList rl) {
        rl.prepo(clrstate);
        return true;
    }

    public void draw(GOut g) {
        g.apply();
        BGL gl = g.gl;
        gl.glBegin(GL2.GL_QUADS);
        /*
        Coord3f a = rotatecar(x, y, gob.a);
        Coord3f b = rotatecar(x + w, y, gob.a);
        Coord3f c = rotatecar(x + w, y + h, gob.a);
        Coord3f d = rotatecar(x, y + h, gob.a);

        gl.glVertex3f(a.x, a.y, 1);
        gl.glVertex3f(b.x, b.y, 1);
        gl.glVertex3f(c.x, c.y, 1);
        gl.glVertex3f(d.x, d.y, 1);
        */
        gl.glVertex3f(x, y, 1);
        gl.glVertex3f(x + w, y, 1);
        gl.glVertex3f(x + w, y + h, 1);
        gl.glVertex3f(x, y + h, 1);
        gl.glEnd();
    }


    static Coord3f rotatecar(float x, float y, double degrees) {
        return new Coord3f((float) (x * Math.cos(degrees) - y * Math.sin(degrees)),
                (float) (x * Math.sin(degrees) + y * Math.cos(degrees)),
                0);
    }
}
