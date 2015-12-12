package purus;

import haven.Callback;
import haven.Coord;
import haven.FlowerMenu;
import haven.FlowerMenu.Petal;
import haven.Gob;
import haven.Loading;
import haven.Moving;
import haven.Resource;
import haven.UI;
import haven.Widget;

public class BotUtils {

	private final UI ui;
    private haven.Widget w;
    public Petal[] opts;
    
	public BotUtils (UI ui, Widget w) {
		this.ui = ui;
		this.w = w;
	}
	
    public Gob findObjectById(long id) {
        return ui.sess.glob.oc.getgob(id);
    }
	// Primitive stuff for scripts/bots to use
	
	// true if player moving
	public boolean isMoving() {
		Moving m = player().getattr(Moving.class);
		if (m == null)
			return false;
		else
			return true;
	}
	
	// Chooses option from flower menu
	public void Choose(Petal option) {
        w.wdgmsg("cl", option.num, ui.modflags());
	}
	
	// Finds nearest objects
	 public Gob findObjectByNames(int radius, String... names) {
	        Coord plc = player().rc;
	        double min = radius;
	        Gob nearest = null;
	        synchronized (ui.sess.glob.oc) {
	            for (Gob gob : ui.sess.glob.oc) {
	                double dist = gob.rc.dist(plc);
	                if (dist < min) {
	                    boolean matches = false;
	                    for (String name : names) {
	                        if (isObjectName(gob, name)) {
	                            matches = true;
	                            break;
	                        }
	                    }
	                    if (matches) {
	                        min = dist;
	                        nearest = gob;
	                    }
	                }
	            }
	        }
	        return nearest;
	    }
	
	// Click some object with specific button and modifier
	public void doClick(Gob gob, int button, int mod) {
		 ui.gui.map.wdgmsg("click", Coord.z, gob.rc, button, 0, mod, (int)gob.id, gob.rc, 0, -1);
		}
	
    public Gob player() {
        return ui.gui.map.player();
    }
    
    public static boolean isObjectName(Gob gob, String name) {
        try {
            Resource res = gob.getres();
            return (res != null) && res.name.contains(name);
        } catch (Loading e) {
            return false;
        }   
    }
    
    public FlowerMenu getMenu() {
        return ui.root.findchild(FlowerMenu.class);
    }
}