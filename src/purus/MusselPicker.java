package purus;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import haven.*;
import haven.FlowerMenu.Petal;

public class MusselPicker {

	private final UI ui;
    private haven.Widget w;
    private haven.Inventory i;
    public Petal[] opts;
    private Widget window; 
    
	BotUtils BotUtils;

	public MusselPicker (UI ui, Widget w, Inventory i) {
		this.ui = ui;
		this.w = w;
		this.i = i;
		BotUtils = new BotUtils(ui, w, i);
	};
	ArrayList<String> targets =  new ArrayList<String>(Arrays.asList("gfx/terobjs/herbs",
			"gfx/kritter/frog/frog",
			"gfx/kritter/rat/rat", "gfx/terobjs/trees/appletree",
			"no gfx/terobjs/bumlings/porphyry2"));// "gfx/terobjs/herbs/mussels","gfx/terobjs/herbs/blueberry", "gfx/terobjs/herbs/stingingnettle"));

	ArrayList<Coord> exclude_gobs=  new ArrayList<Coord>();
	private Gob get_target_gob(ArrayList exclude_gobs){
		if (exclude_gobs.size() > 100) {
			exclude_gobs=  new ArrayList<Coord>();
		}
		Gob gob = null;
		double near_dis = 9999;
		for (String target: targets){
			Gob this_gob = BotUtils.findObjectByNames(800, target);
			if(this_gob != null && !exclude_gobs.contains(this_gob.rc)){
				double this_gob_dis = BotUtils.player().rc.dist(this_gob.rc);
				if(this_gob_dis< near_dis ){
				near_dis = this_gob_dis;
				gob = this_gob;
			}}
		}
		return gob;
	};
	public void Run () {
	t.start();	
	}
	Thread t = new Thread(new Runnable() {
	public void run()  {
		window = BotUtils.gui().add(new StatusWindow(), 300, 200);
		Gob gob = get_target_gob(exclude_gobs);
		long init_gob_id =  0;
		Coord p_st =  null;

		while(gob != null) {
			if (init_gob_id == 0){
				init_gob_id = gob.id;
			}
//			ui.root.findchild(GameUI.class).info("begin pick", Color.WHITE);
			if (BotUtils.player().rc.dist(gob.rc) <= 20){
				BotUtils.doClick(gob, 3, 0);
				BotUtils.sleep(600);
				// if reached the gob, pick gob, and find next gob
				exclude_gobs.add(gob.rc);
				gob = null;
				while(gob == null) {
					if (gob == null){
						gob =get_target_gob(exclude_gobs);
					}
					BotUtils.sleep(500);
				}
				BotUtils.sysMsg("Find target:"+gob.getres().name, Color.WHITE);
			}
			while (BotUtils.player().rc.dist(gob.rc) > 20){
				// if distance to gob is larger than 10, still need to force walk
				ui.root.findchild(GameUI.class).info("gob_dis:"+BotUtils.player().rc.dist(gob.rc), Color.WHITE);
				// check if player moved
				p_st =  BotUtils.player().rc;
				p_st = new Coord(p_st.x, p_st.y);
				BotUtils.sleep(500);
				if ( p_st.dist(BotUtils.player().rc) < 5){
					// if bocked try turn around
					p_st =  BotUtils.player().rc;
					p_st = new Coord(p_st.x, p_st.y);
					BotUtils.turn_around(gob.rc, 1);
					BotUtils.sleep(500);
					if (p_st.dist(BotUtils.player().rc) < 5){
						BotUtils.turn_around(gob.rc, -1);
						BotUtils.sleep(500);
					}
					if (p_st.dist(BotUtils.player().rc) < 5){
						// check ui.modflags(), 2 means press control
						ui.gui.map.wdgmsg("click", BotUtils.getCenterScreenCoord(), gob.rc,1, 2);
						BotUtils.sleep(100);
					}
					ui.gui.map.wdgmsg("click", BotUtils.getCenterScreenCoord(), gob.rc,1 ,0);
					BotUtils.sleep(500);
					BotUtils.doClick(gob, 3, 0);
					BotUtils.sleep(500);
				}
			BotUtils.sleep(500);
			}
			// the Pick block is nerver executed during test
			@SuppressWarnings("deprecation")
			FlowerMenu menu = ui.root.findchild(FlowerMenu.class);
	            if (menu != null) {
	                for (FlowerMenu.Petal opt : menu.opts) {
	                    if (opt.name.contains("Pick") || opt.name.equals("Chip stone") ) {
	                        menu.choose(opt);
	                        menu.destroy();
//							ui.root.findchild(GameUI.class).info("Pick", Color.WHITE);
	                    }
	                }
	            }
			BotUtils.sleep(700);
//			ui.root.findchild(GameUI.class).info("Found gob", Color.WHITE);

         //   BotUtils.Choose(opts[1])
		}
		BotUtils.sysMsg("Mussel Picker Finished", Color.WHITE);
        window.destroy();
		t.stop();
		return;
	}
	});

	// This thingy makes that stupid window with cancel button, todo: make it better
			private class StatusWindow extends Window {
		        public StatusWindow() {
		            super(Coord.z, "Mussel Picker");
		            setLocal(true);
		            add(new Button(120, "Cancel") {
		                public void click() {
		                    window.destroy();
		                    if(t != null) {
		                    	gameui().info("Mussel Picker Cancelled", Color.WHITE);
		                    	t.stop();
		                    }
		                }
		            });
		            pack();
		        }
		        public void wdgmsg(Widget sender, String msg, Object... args) {
		            if (sender == this && msg.equals("close")) {
		                t.stop();
		            }
		            super.wdgmsg(sender, msg, args);
		        }

			}
			//
}