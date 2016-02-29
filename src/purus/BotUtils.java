package purus;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import haven.*;
import haven.FlowerMenu.Petal;
import haven.GAttrib;
import haven.GItem;
import haven.GameUI;
import haven.Gob;
import haven.HavenPanel;
import haven.Inventory;
import haven.ItemInfo;
import haven.Loading;
import haven.Moving;
import haven.Resource;
import haven.Speedget;
import haven.UI;
import haven.WItem;
import haven.Widget;
import haven.Window;

public class  BotUtils {

	private final UI ui;
    private haven.Widget w;
    private haven.Inventory i;
    public Petal[] opts;
    private static Pattern liquidPattern;
    String liquids =  haven.Utils.join("|", new String[] { "Bucket", "Water", "Piping Hot Tea", "Tea" });
    String pattern = String.format("[0-9.]+ l of (%s)", liquids);
    Map<Class<? extends GAttrib>, GAttrib> attr = new HashMap<Class<? extends GAttrib>, GAttrib>();
	public static Thread MusselPicker;
	public  BotUtils (UI ui, Widget w, Inventory i) {
		this.ui = ui;
		this.w = w;
		this.i = i;
		liquidPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
	}
	
    public GameUI gui() {
    	return ui.gui;
    }
    
    // Drinks water/tea from containers in inventory
    public void drink() {
		GameUI gui = HavenPanel.lui.root.findchild(GameUI.class);
		WItem item = findDrinkOnHand();
		if (item==null) {
			item = findDrink(playerInventory());
		}
		 if (item != null) {
			 item.item.wdgmsg("iact", Coord.z, 3);
			 sleep(250);
				@SuppressWarnings("deprecation")
				FlowerMenu menu = ui.root.findchild(FlowerMenu.class);
		            if (menu != null) {
		                for (FlowerMenu.Petal opt : menu.opts) {
		                    if (opt.name.equals("Drink")) {
		                        menu.choose(opt);
		                        menu.destroy();
		                        while(gui.getmeter("stam", 0).a <= 90) {
		                        	sleep(100);
		                        }
		                    }
		                }
		            }
		 }
    }
    
    public void sysMsg(String msg, Color color ) {
    	ui.root.findchild(GameUI.class).info(msg,color);
    }
    
    // Sets speed for player
    // 0 = Crawl 1 = Walk  2 = Run 3 = Sprint
    public void setSpeed(int speed) {
    	haven.Speedget.setSpeed = true;
    	haven.Speedget.SpeedToSet = speed;
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
	
	// Click some object with item on hand
	// Modifier 1 - shift; 2 - ctrl; 4 alt;
    public void itemClick(Gob gob, int mod) {
        ui.gui.map.wdgmsg("itemact", Coord.z, gob.rc, mod, 0, (int)gob.id, gob.rc, 0, -1);
    }
	
	// Click some object with specific button and modifier
	// Button 1 = Left click and 3 = right click
	// Modifier 1 - shift; 2 - ctrl; 4 - alt;
    public void doClick(Gob gob, int button, int mod) {
        ui.gui.map.wdgmsg("click", Coord.z, gob.rc, button, 0, mod, (int)gob.id, gob.rc, 0, -1);
    }

	// Finds nearest crop with x stage
		 public Gob findNearestHarvestCrop(int radius, String... names) {
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
		                        	if (gob.getStage() == gob.getMaxStage() && gob.getStage() != 404) {
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
	public Gob get_target_gob(Coord center_rc, int radius, ArrayList<String> targets, ArrayList<Coord>  exclude_gobs){

		Gob gob = null;
		double near_dis = 9999;
		for (String target: targets){
			Gob this_gob = findObjectByNames(center_rc,radius, target);
			if(this_gob != null && !exclude_gobs.contains(this_gob.rc)){
				double this_gob_dis = center_rc.dist(this_gob.rc);
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
		while (middle_rc.dist(rc_st)> 600) {
			middle_rc = new Coord(rc_st.x + Math.abs(middle_rc.x - rc_st.x) / 2 * x_direction, rc_st.y + Math.abs(middle_rc.y - rc_st.y) / 2 * y_direction);
			if (middle_rc.dist(rc_st) > 3000){
				sysMsg("getReachRC get a middle rc > 3000!", Color.RED);
			}
		}
		return middle_rc;
	}
	public boolean goToCoord(Coord gob_rc, int radiation, boolean cancelable){
		int walk_sleep = Math.max(500, 500 * (2-haven.Speedget.SpeedToSet));
		Coord3f p_st = null;
		if (gob_rc==null){
			sysMsg("Get null gob in goToGob function.", Color.RED);
			return false;
		}
		sysMsg("gob_dis:"+player().rc.dist(gob_rc), Color.WHITE);
		Coord reach_rc = getReachRC(player().rc, gob_rc);
		ui.gui.map.wdgmsg("click", getCenterScreenCoord(), reach_rc,1 ,0);
//			ui.root.findchild(GameUI.class).info("begin pick", Color.WHITE);
		sleep(200);
		while (player().rc.dist(gob_rc) > radiation){
			if (haven.Settings.getCancelAuto() && cancelable){
				ui.gui.map.wdgmsg("click", getCenterScreenCoord(),  player().rc,1 ,0);
				return false;
			}
			// if distance to gob is larger than 10, still need to force walk
//				ui.root.findchild(GameUI.class).info("gob_dis:"+BotUtils.player().rc.dist(gob.rc), Color.WHITE);
			// check if player moved
			p_st =  player().getrc();
			p_st = new Coord3f(p_st.x, p_st.y, p_st.z);
			sleep(walk_sleep);
			if ( p_st.dist(player().getrc()) < 5){
				// if bocked try turn around
				p_st =  player().getrc();
				p_st = new Coord3f(p_st.x, p_st.y, p_st.z);
				turn_around(gob_rc, 1);
				sleep(walk_sleep);
				if (p_st.dist(player().getrc()) < 5){
					turn_around(gob_rc, -1);
					sleep(walk_sleep);
				}
				//climb the hill need 2 click.
				reach_rc = getReachRC(player().rc, gob_rc);
				ui.gui.map.wdgmsg("click", getCenterScreenCoord(), reach_rc,1 ,0);
				sleep(100);
				ui.gui.map.wdgmsg("click", getCenterScreenCoord(), reach_rc,1 ,0);
				sleep(walk_sleep);
			}
		}
		if (player().rc.dist(gob_rc) <= radiation){
			// if reached the gob, pick gob, and find next gob
//			sysMsg("Reached target:"+gob.getres().name, Color.WHITE)
			return true;
		}
		return false;
	}
	// Finds nearest objects
	 public Gob findObjectByNames(Coord center_rc,int radius, String... names) {
	        double min = radius;
	        Gob nearest = null;
	        synchronized (ui.sess.glob.oc) {
	            for (Gob gob : ui.sess.glob.oc) {
	                double dist = gob.rc.dist(center_rc);
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
	public WItem findDrinkOnHand() {
		WItem left = ui.gui.getequipory().quickslots[6];
		if (left!=null && canDrinkFrom(left))
				return left;
		WItem right = ui.gui.getequipory().quickslots[7];
		if (right!=null && canDrinkFrom(right))
			return right;
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