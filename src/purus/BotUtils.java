package purus;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import haven.Coord;
import haven.FlowerMenu;
import haven.FlowerMenu.Petal;
import haven.GAttrib;
import haven.GItem;
import haven.GameUI;
import haven.Gob;
import haven.Inventory;
import haven.ItemInfo;
import haven.Loading;
import haven.Moving;
import haven.Resource;
import haven.UI;
import haven.WItem;
import haven.Widget;

public class BotUtils {

	private final UI ui;
    private haven.Widget w;
    private haven.Inventory i;
    public Petal[] opts;
    private static Pattern liquidPattern;
    String liquids =  haven.Utils.join("|", new String[] { "Water", "Piping Hot Tea", "Tea" });
    String pattern = String.format("[0-9.]+ l of (%s)", liquids);
    Map<Class<? extends GAttrib>, GAttrib> attr = new HashMap<Class<? extends GAttrib>, GAttrib>();
    
	public BotUtils (UI ui, Widget w, Inventory i) {
		this.ui = ui;
		this.w = w;
		this.i = i;
		liquidPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
	}
	
    public GameUI gui() {
    	return ui.gui;
    }
    
    public void sysMsg(String msg, Color color ) {
    	ui.root.findchild(GameUI.class).info(msg,color);
    }
    
	// Takes item in hand
    public void takeItem(Widget item) {
        item.wdgmsg("take", Coord.z);
    }
    
    //  Returns item in hand
    public GItem getItemAtHand() {
        for (GameUI.DraggedItem item : ui.gui.hand)
            return item.item;
        for (GameUI.DraggedItem item : ui.gui.handSave)
            return item.item;
        return null;
    }
    
	//Drops thing from hand 
	public void drop_item(int mod) {
		ui.gui.map.wdgmsg("drop", mod);
	}
	
	// Use item in hand to ground below player, for example, plant carrot
	public void mapInteractClick(int mod) {
		 ui.gui.map.wdgmsg("itemact", getCenterScreenCoord(), player().rc, 3, ui.modflags());
	}
	
	// return center of screen
		public Coord getCenterScreenCoord() {
			Coord sc, sz;
				sz =  ui.gui.map.sz;
				sc = new Coord((int) Math.round(Math.random() * 200 + sz.x / 2
						- 100), (int) Math.round(Math.random() * 200 + sz.y / 2
						- 100));
				return sc;
		}
	
	// Find object by ID, returns null if not found (duh)
    public Gob findObjectById(long id) {
        return ui.sess.glob.oc.getgob(id);
    }
	
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
	public int get_o_y(int x_pc, int y_pc, int x_tar, int y_tar, int turn_x){
	//（x-x1)(x2-x1)+(y-y1)(y2-y1)=0
		return ((turn_x-x_pc)*(x_pc-x_tar)/(y_tar-y_pc)) + y_pc;
	};
	public int get_o_y( Coord pc, Coord tar, int turn_x){
		//（x-x1)(x2-x1)+(y-y1)(y2-y1)=0
		return ((turn_x -pc.x)*(pc.x-tar.x)/(tar.y-pc.y)) + pc.y;
	};
	// Click some object with specific button and modifier
	public void doClick(Gob gob, int button, int mod) {
		 ui.gui.map.wdgmsg("click", Coord.z, gob.rc, button, 0, mod, (int)gob.id, gob.rc, 0, -1);
		}

	// Finds nearest crop with x stage
		 public Gob findNearestStageCrop(int radius, int stage, String... names) {
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
		                        	if (gob.getStage() == stage) {
		                            matches = true;
		                            break;
		                        	}
		                            // TO DO: KEKSI MITEN HARVESTAA VAAN STAGE 4 OLEVAT PORKKANAT
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
	//
		 
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
	
    public Gob player() {
        return ui.gui.map.player();
    }

	public void turn_around(Coord tar_rc, int direction){
		// direction should be 1 or -1
		Coord pc = player().rc;
		int turn_x = pc.x+ 20 * direction;
		Coord target_rc = new Coord(turn_x, get_o_y(pc, tar_rc,turn_x));
		ui.gui.map.wdgmsg("click", getCenterScreenCoord(), target_rc,1 ,0, ui.modflags());
	}
    
    public Inventory playerInventory() {
        return ui.gui.maininv;
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
    
    public WItem findDrink(Inventory inv) {
        for (WItem item : inv.children(WItem.class)) {
            if (canDrinkFrom(item))
                return item;
        }
        return null;
    }
    public boolean canDrinkFrom(WItem item) {
        ItemInfo.Contents contents = getContents(item);
        if (contents != null && contents.sub != null) {
            for (ItemInfo info : contents.sub) {
                if (info instanceof ItemInfo.Name) {
                    ItemInfo.Name name = (ItemInfo.Name) info;
                    if (name.str != null && liquidPattern.matcher(name.str.text).matches())
                        return true;
                }
            }
        }
        return false;
    }
    public ItemInfo.Contents getContents(WItem item) {
        try {
            for (ItemInfo info : item.item.info())
                if (info instanceof ItemInfo.Contents)
                    return (ItemInfo.Contents)info;
        } catch (Loading ignored) {}
        return null;
    }
    
    
}