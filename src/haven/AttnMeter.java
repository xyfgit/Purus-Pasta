package haven;

import java.awt.*;

public class AttnMeter extends Widget {
    private static final Tex bg = Resource.loadtex("hud/meter/attention");

    private final CharWnd.StudyInfo study;

    public AttnMeter(CharWnd.StudyInfo study) {
        super(IMeter.fsz);
        this.study = study;
    }

    @Override
    public void draw(GOut g) {
        Coord isz = IMeter.msz;
        Coord off = IMeter.off;
        g.chcolor(0, 0, 0, 255);
        g.frect(off, isz);
        g.chcolor();

        study.upd();

        int w = isz.x;
        int c = study.tw;
        int t = ui.sess.glob.cattr.get("int").comp;

        if (c < t) {
            g.chcolor(Color.YELLOW);
        } else if (c == t) {
            g.chcolor(Color.GREEN);
        } else {
            g.chcolor(Color.RED);
        }

        g.frect(off, new Coord((int) Math.floor((float) c / t * w), isz.y));
        g.chcolor();
        g.image(bg, Coord.z);
    }

    @Override
    public Object tooltip(Coord c, Widget prev) {
        return RichText.render(String.format("Attention: %d/%d\nXP Cost: %d\nLP Gain: %d", study.tw, ui.sess.glob.cattr.get("int").comp, study.tenc, study.texp), -1).tex();
    }
}