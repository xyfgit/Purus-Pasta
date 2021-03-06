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
	static ArrayList<String>  studyTargets =  new ArrayList<String>(Arrays.asList("bloatedbolete", "royaltoadstool",
			"ladysmantle"));
	static ArrayList<String>  targets =  new ArrayList<String>(Arrays.asList("gfx/terobjs/herbs", "-dead"));// "gfx/terobjs/herbs/mussels","gfx/terobjs/herbs/blueberry", "gfx/terobjs/herbs/stingingnettle"));
	static ArrayList<String>  animal_targets = new ArrayList<String>(Arrays.asList("gfx/kritter/frog", "gfx/kritter/rat", "gfx/kritter/chicken"));
	static ArrayList<String>  avoid_targets = new ArrayList<String>(Arrays.asList("gfx/kritter/badger", "gfx/kritter/boar", "gfx/kritter/borka/body", "gfx/kritter/lynx"));

	String boat_gob_name = "gfx/terobjs/vehicle/rowboat";
	ArrayList<Coord> exclude_gobs=  new ArrayList<Coord>();
	static Gob boat_gob = null;
	Gob gob = null;
	enum STATUS {
		BACK_TO_BOAT, RUNNING, RESTING
	}
	static STATUS curStatus = STATUS.RESTING;
	public void Run () {
		if (window!=null){
			try{
				window.destroy();
			}catch (Exception e){
			}
		}
		Settings.setKeepWalk(false);
		Settings.setCancelAuto(false);
		window = BotUtils.gui().add(new StatusWindow(), 300, 200);
		boat_gob = BotUtils.findObjectByNames(BotUtils.player().rc, 50, boat_gob_name);

		if (Config.autoPickAnimal){
			for (String animal: animal_targets){
			targets.add(animal);}
		}else{
			for (String animal: animal_targets){
				if(targets.contains(animal)){
				targets.remove(animal);
				}
			}
		}
		if (boat_gob != null){
			// check ui.modflags(), 2 means press control
			ui.gui.map.wdgmsg("click", BotUtils.getCenterScreenCoord(), boat_gob.rc,1, 2);
			BotUtils.sleep(300);
		}
		gob = null;
		curStatus = STATUS.RUNNING;
		if (BotUtils.MusselPicker == null) {
			BotUtils.MusselPicker = new Thread(new Runnable() {
				public synchronized void run()  {
					while (true) {
						BotUtils.sleep(200);
						while (gob == null) {
//							BotUtils.sysMsg("Find target!", Color.WHITE);
							if (exclude_gobs.size() > 100) {
								exclude_gobs=  new ArrayList<Coord>();
							}
							gob = BotUtils.get_target_gob(BotUtils.player().rc, 1000, studyTargets, exclude_gobs);
							if (gob ==null){
							gob = BotUtils.get_target_gob(BotUtils.player().rc, 1000, targets, exclude_gobs);}
// BotUtils.sysMsg("cancel?" +Settings.getCancelAuto(), Color.WHITE);
							if (Settings.getCancelAuto() ) {
								if (boat_gob != null){
								BotUtils.sysMsg("Go back to boat!", Color.WHITE);
								Settings.setCancelAuto(false);
								BotUtils.goToCoord(boat_gob.rc, 30, true);
								BotUtils.sleep(200);
									boat_gob = BotUtils.findObjectByNames(BotUtils.player().rc, 150, boat_gob_name);
									if (boat_gob!=null){
								BotUtils.doClick(boat_gob, 3, 0);
								BotUtils.sleep(300);}
								}
								window.destroy();
								BotUtils.MusselPicker.suspend();
								gob = null;
								continue;
							}
							if (gob!=null &&BotUtils.get_target_gob(gob.rc, 250, avoid_targets, exclude_gobs)!=null){
								gob =null;
								continue;
							}
							BotUtils.sleep(200);
						}
						Coord gob_rc = gob.rc;
						if (BotUtils.goToCoord(gob_rc, 15, true)) {
							if (!exclude_gobs.contains(gob_rc)) {
								exclude_gobs.add(gob_rc);
							}
							GItem item = BotUtils.getItemAtHand();
							if (item!=null){
								BotUtils.sysMsg("Detect item on hand, picker suspend now", Color.WHITE);
								Settings.setKeepWalk(true);
								Settings.setCancelAuto(true);
							}
						BotUtils.doClick(gob, 3, 0);
						BotUtils.sleep(400);
						BotUtils.opGob(gob, "Pick");
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
						targets =  new ArrayList<String>(Arrays.asList("gfx/terobjs/herbs", "gfx/terobjs/trees/appletree"));
						Settings.setKeepWalk(true);
						if(BotUtils.MusselPicker != null) {
							//gob = null; will not work as gob is not static
							if (curStatus != STATUS.RUNNING) {
								Settings.setCancelAuto(true);
								window.destroy();
								curStatus = STATUS.RESTING;
								return;
							}

							gameui().msg("Mussel Picker Status: " + curStatus , Color.WHITE);
							if(boat_gob != null&&curStatus != STATUS.BACK_TO_BOAT){
							curStatus = STATUS.BACK_TO_BOAT;
							}
							Settings.setCancelAuto(true);
						}
						gameui().msg("Mussel Picker Cancelled, handle cancel: " + haven.Settings.getCancelAuto() , Color.WHITE);
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
					BotUtils.MusselPicker.suspend();;
					}
					window.destroy();
//		            }
					curStatus = STATUS.RESTING;
		        }
			}
			//
}