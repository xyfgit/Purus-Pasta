package purus;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
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
		if ((tar.y-pc.y) == 0){
			return pc.y;
		}
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
	public Gob get_target_gob(ArrayList<String> targets, ArrayList<Coord>  exclude_gobs){
		if (exclude_gobs.size() > 100) {
			exclude_gobs=  new ArrayList<Coord>();
		}
		Gob gob = null;
		double near_dis = 9999;
		for (String target: targets){
			Gob this_gob = findObjectByNames(600, target);
			if(this_gob != null && !exclude_gobs.contains(this_gob.rc)){
				double this_gob_dis = player().rc.dist(this_gob.rc);
				if(this_gob_dis< near_dis ){
					near_dis = this_gob_dis;
					gob = this_gob;
				}}
		}
		return gob;
	};
	public Coord getReachRC(Coord rc_st, Coord rc_end){
//		if ((rc_end.y - rc_st.y)==0 ||((rc_end.x-rc_st.x)/(rc_end.y-rc_st.y))==0) return rc_end;
//		float y = (x-rc_st.x)/((rc_end.x-rc_st.x)/(rc_end.y-rc_st.y))+rc_st.y ;
//		Coord reach_rc = new Coord(x, (int)(y));
		int x_direction = (player().rc.x >  rc_end.x)?-1:1;
		int y_direction = (player().rc.y >  rc_end.y)?-1:1;
		Coord middle_rc = rc_end;
		while (middle_rc.dist(rc_st)> 400) {
			middle_rc = new Coord(rc_st.x + Math.abs(middle_rc.x - rc_st.x) / 2 * x_direction, rc_st.y + Math.abs(middle_rc.y - rc_st.y) / 2 * y_direction);
			if (middle_rc.dist(rc_st) > 3000){
				sysMsg("getReachRC get a middle rc > 3000!", Color.RED);
			}
		}
		return middle_rc;
	}
	public void goToCoord(Coord gob_rc, int radiation, boolean cancelable){

		Coord p_st = null;
		if (gob_rc==null){
			sysMsg("Get null gob in goToGob function.", Color.RED);
			return;
		}
		sysMsg("gob_dis:"+player().rc.dist(gob_rc), Color.WHITE);
		Coord reach_rc = getReachRC(player().rc, gob_rc);
		ui.gui.map.wdgmsg("click", getCenterScreenCoord(), reach_rc,1 ,0);
//			ui.root.findchild(GameUI.class).info("begin pick", Color.WHITE);
		sleep(200);
		while (player().rc.dist(gob_rc) > radiation){
			if (haven.Settings.getCancelAuto() && cancelable){
				ui.gui.map.wdgmsg("click", getCenterScreenCoord(),  player().rc,1 ,0);
				return;
			}
			// if distance to gob is larger than 10, still need to force walk
//				ui.root.findchild(GameUI.class).info("gob_dis:"+BotUtils.player().rc.dist(gob.rc), Color.WHITE);
			// check if player moved
			p_st =  player().rc;
			p_st = new Coord(p_st.x, p_st.y);
			sleep(300);
			if ( p_st.dist(player().rc) < 5){
				// if bocked try turn around
				p_st =  player().rc;
				p_st = new Coord(p_st.x, p_st.y);
				turn_around(gob_rc, 1);
				sleep(500);
				if (p_st.dist(player().rc) < 5){
					turn_around(gob_rc, -1);
					sleep(500);
				}
				//climb the hill need 2 click.
				reach_rc = getReachRC(player().rc, gob_rc);
				ui.gui.map.wdgmsg("click", getCenterScreenCoord(), reach_rc,1 ,0);
				sleep(300);
				ui.gui.map.wdgmsg("click", getCenterScreenCoord(), reach_rc,1 ,0);
				sleep(500);
			}
			sleep(500);
		}
		if (player().rc.dist(gob_rc) <= radiation){
			// if reached the gob, pick gob, and find next gob
//			sysMsg("Reached target:"+gob.getres().name, Color.WHITE);
		}
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
	
    public Gob player() {
        return ui.gui.map.player();
    }

	public void turn_around(Coord tar_rc, int direction){
		// direction should be 1 or -1
		Coord pc = player().rc;
		int turn_x = pc.x+ 20 * direction;
		Coord target_rc = new Coord(turn_x, get_o_y(pc, tar_rc,turn_x));
		ui.gui.map.wdgmsg("click", getCenterScreenCoord(), target_rc,1 ,0);
	}
	public void sleep(int t){
		try {
			Thread.sleep(t);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
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