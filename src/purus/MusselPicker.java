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
	String boat_gob_name = "gfx/terobjs/vehicle/rowboat";
	ArrayList<Coord> exclude_gobs=  new ArrayList<Coord>();
	Gob boat_gob = null;
	public void Run () {
	t.start();	
	}
	Thread t = new Thread(new Runnable() {
	public void run()  {
		Settings.setCancelAuto(false);
		boat_gob = BotUtils.findObjectByNames(100, boat_gob_name);
		boolean on_boat = false;
		if (boat_gob != null){
			on_boat = true;
		}
		window = BotUtils.gui().add(new StatusWindow(), 300, 200);
		Gob gob = null;
		while (true){
			if (Settings.getCancelAuto() && boat_gob!=null){
				BotUtils.goToCoord(boat_gob.rc,30, false);
				BotUtils.doClick(boat_gob, 3, 0);
				BotUtils.sleep(200);
				t.stop();
				return;
			}
			while(gob == null) {
				gob =BotUtils.get_target_gob(targets, exclude_gobs);
			}
			BotUtils.doClick(gob, 3, 0);
			BotUtils.sleep(500);
			if (boat_gob != null && on_boat ){
				// check ui.modflags(), 2 means press control
				ui.gui.map.wdgmsg("click", BotUtils.getCenterScreenCoord(), gob.rc,1, 2);
				BotUtils.sleep(300);
				on_boat=false;
			}
			BotUtils.goToCoord(gob.rc,15, true);
			BotUtils.sleep(500);
			BotUtils.doClick(gob, 3, 0);
			BotUtils.sleep(500);
			FlowerMenu menu = ui.root.findchild(FlowerMenu.class);
			boolean nex_pick = true;
			while (nex_pick && menu != null&&!Settings.getCancelAuto()) {
				nex_pick = false;
				for (FlowerMenu.Petal opt : menu.opts) {
					if (opt.name.contains("Pick") || opt.name.equals("Chip stone")) {
						menu.choose(opt);
						menu.destroy();
						nex_pick = true;
						BotUtils.doClick(gob, 3, 0);
						BotUtils.sleep(1000);
						menu = ui.root.findchild(FlowerMenu.class);
						break;
					}
				}
				if (!nex_pick){
					menu.destroy();
				}
			}
			exclude_gobs.add(gob.rc);
			gob = null;
		}
	}
	});

	// This thingy makes that stupid window with cancel button, todo: make it better
			private class StatusWindow extends Window {
				Button cancel_btn =new Button(120, "Cancel") {
					public void click() {
						if(t != null) {
							if (Settings.getCancelAuto()||boat_gob==null) {
								t.stop();
								window.destroy();
							}
							if (boat_gob !=null) {
								Settings.setCancelAuto(true);
							}

						}
						gameui().info("Mussel Picker Cancelled" + haven.Settings.getCancelAuto() , Color.WHITE);

					}
				};

		        public StatusWindow() {
		            super(Coord.z, "Mussel Picker");
		            setLocal(true);
		            add(cancel_btn);
		            pack();
		        }
		        public void wdgmsg(Widget sender, String msg, Object... args) {
//		            if (sender == this && msg.equals("close")) {
					if (t.isAlive()){
					t.stop();}
					window.destroy();
//		            }
					if (boat_gob !=null) {
						BotUtils.doClick(boat_gob, 3, 0);
						BotUtils.sleep(500);
					}
		        }

			}
			//
}