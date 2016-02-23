package purus;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import haven.*;
import haven.Button;
import haven.FlowerMenu.Petal;
import haven.Window;

public class MusselPicker {

	private final UI ui;
    private haven.Widget w;
    private haven.Inventory i;
    public Petal[] opts;
    private static Widget window;
    
	BotUtils BotUtils;

	public MusselPicker (UI ui, Widget w, Inventory i) {
		this.ui = ui;
		this.w = w;
		this.i = i;
		BotUtils = new BotUtils(ui, w, i);
	};
	ArrayList<String> targets =  new ArrayList<String>(Arrays.asList("gfx/terobjs/herbs", "gfx/terobjs/trees/appletree"));// "gfx/terobjs/herbs/mussels","gfx/terobjs/herbs/blueberry", "gfx/terobjs/herbs/stingingnettle"));

	String boat_gob_name = "gfx/terobjs/vehicle/rowboat";
	ArrayList<Coord> exclude_gobs=  new ArrayList<Coord>();
	static Gob  boat_gob = null;
	boolean on_boat = false;
	public Gob gob = null;
	public synchronized void Run () {
//		gob = null;
		Settings.setCancelAuto(false);
		window = BotUtils.gui().add(new StatusWindow(), 300, 200);
		boat_gob = BotUtils.findObjectByNames(100, boat_gob_name);
		if (Config.autoPickAnimal){
			targets.add("gfx/kritter");
		}
		if (boat_gob != null  ){
			// check ui.modflags(), 2 means press control
			ui.gui.map.wdgmsg("click", BotUtils.getCenterScreenCoord(), boat_gob.rc,1, 2);
			BotUtils.sleep(300);
			on_boat=false;
		}
		gob = null;
		if (BotUtils.MusselPicker == null) {
			BotUtils.MusselPicker = new Thread(new Runnable() {
				public synchronized void run()  {
					while (true){
						while(gob == null) {
							BotUtils.sleep(400);
							gob =BotUtils.get_target_gob(targets, exclude_gobs);
//							BotUtils.sysMsg("cancel?" +Settings.getCancelAuto(), Color.WHITE);
							if (Settings.getCancelAuto() && boat_gob != null){
								gob = boat_gob;
								break;
							}
						}
						// gob cannot be null
						if (gob==boat_gob){
							BotUtils.sysMsg("Go back to boat!", Color.WHITE);
							BotUtils.goToCoord(gob.rc,30, false);
							BotUtils.sleep(200);
							BotUtils.doClick(gob, 3, 0);
							BotUtils.sleep(300);
							window.destroy();
							BotUtils.MusselPicker.suspend();
							// important to trigger find gob
							gob = null;
							continue;
						}else{
							if (BotUtils.goToCoord(gob.rc,15, true)){
								if (!exclude_gobs.contains(gob.rc)){ exclude_gobs.add(gob.rc);}
							}
						}
						BotUtils.doClick(gob, 3, 0);
						BotUtils.sleep(400);
						FlowerMenu menu = ui.root.findchild(FlowerMenu.class);
						boolean nex_pick = true;
						if (menu==null){
							BotUtils.doClick(gob, 3, 0);
							BotUtils.sleep(300);
						}
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
						gob = null;
					}
				}
			});;
			BotUtils.MusselPicker.start();
		}else{
			BotUtils.MusselPicker.resume();
		}
	}

	// This thingy makes that stupid window with cancel button, todo: make it better
			private class StatusWindow extends Window {
				Button cancel_btn =new Button(120, "Cancel") {
					public void click() {
						if(BotUtils.MusselPicker != null) {
							if (Settings.getCancelAuto()||boat_gob==null) {
								BotUtils.MusselPicker.suspend();
								window.destroy();
							}
							if (boat_gob !=null) {
								Settings.setCancelAuto(true);
							}
						}
						gameui().info("Mussel Picker Cancelled, handle cancel: " + haven.Settings.getCancelAuto() , Color.WHITE);

					}
				};

		        public StatusWindow() {
		            super(Coord.z, "Mussel Picker");
		            setLocal(true);
		            add(cancel_btn);
		            pack();
		        }
		        public void wdgmsg(Widget sender, String msg, Object... args) {
					// comment out as this code can cause bug
//		            if (sender == this && msg.equals("close")) {
					if (BotUtils.MusselPicker.isAlive()){
					BotUtils.MusselPicker.suspend();}
					window.destroy();
//		            }
					if (boat_gob !=null) {
						BotUtils.doClick(boat_gob, 3, 0);
					}
		        }
			}
			//
}