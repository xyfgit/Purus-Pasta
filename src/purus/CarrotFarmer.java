package purus;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import haven.*;

public class CarrotFarmer {
	/* This script harvests and replants carrots.
	 *  Doesn't pathfind around objects.
	 *  Does this for all stage 4 carrots in sight.
	 *  Designed for round fields, lift beehives at middle temporarily off before harvest.
	 */
	public static boolean CarrotsNearby;

	private final UI ui;
    private haven.Widget w;
    private Inventory i;
    private Widget window;

    private String Seed = "gfx/invobjs/carrot";
	// todo: use dict for this
	private ArrayList<String> Plants =  new ArrayList<String>(Arrays.asList("gfx/terobjs/plants/pumpkin", "gfx/terobjs/plants/wine",
			"gfx/terobjs/plants/carrot", "gfx/terobjs/plants/pepper",
			"gfx/terobjs/plants/flax","gfx/terobjs/plants/barley"));
	private String Plant = null;
	private int Stage = 4;

	BotUtils BotUtils;

	public CarrotFarmer (UI ui, Widget w, Inventory i) {
		this.ui = ui;
		this.w = w;
		this.i = i;
		BotUtils = new BotUtils(ui, w, i);
	}
	public Gob get_plant_gob() {
		Gob gob = null;
		if (Plant == null) {
			Gob gob_temp = null;
			float distance = 99999;
				gob_temp = BotUtils.findNearestHarvestCrop(500, "gfx/terobjs/plants");
				if (gob_temp != null) {
					Coord3f f = gob_temp.getrc();
					Coord3f p1 = ui.sess.glob.oc.getgob(MapView.plgob).getrc();
					if (distance > f.dist(p1)) {
						gob = gob_temp;
						Plant = gob_temp.getres().name;
//						distance = f.dist(p1);
//						BotUtils.sysMsg(Plant+distance, Color.WHITE);
					}

			}
		}else {
			gob = BotUtils.findNearestHarvestCrop(500, Plant);
			Plants.add(Plant.replace("terobjs/plants", "invobjs"));
		}
		return gob;
	}
	public void Run () {
		t.start();
		}
		Thread t = new Thread(new Runnable() {
		public void run()  {
			Settings.setCancelAuto(false);
			BotUtils.sysMsg("Modified Carrot Farmer Started", Color.WHITE);
			window = BotUtils.gui().add(new StatusWindow(), 300, 200);
			while (true) {
				try {
					Gob gob = get_plant_gob();
					while (!Settings.getCancelAuto()) {
						// Start of drink TODO: Make separate function of this maybe yeah?
						GameUI gui = HavenPanel.lui.root.findchild(GameUI.class);
						IMeter.Meter stam = gui.getmeter("stam", 0);
						// Check energy stop if it is lower than 1500
						IMeter.Meter nrj = gui.getmeter("nrj", 0);
						if (nrj.a <= 30){
							BotUtils.sysMsg("Carrot Farmer Stop as run out of energy.", Color.WHITE);
							t.stop();
							return;
						}
						else if (stam.a <= 30) {
							BotUtils.drink();
						}
						if (gob!=null) {
							//start harvest
							BotUtils.goToCoord(gob.rc, 20, false);
							BotUtils.doClick(gob, 3, 0);
							BotUtils.sleep(100);
//						@SuppressWarnings("deprecation")
							FlowerMenu menu = ui.root.findchild(FlowerMenu.class);
							if (menu != null) {
								for (FlowerMenu.Petal opt : menu.opts) {
									if (opt.name.equals("Harvest")) {
										menu.choose(opt);
										menu.destroy();
									}
								}
							}

						BotUtils.sleep(100);
						while (gui.prog >= 0) {
							BotUtils.sleep(150);
						}
						// Some better method should be implemented, but now it just waits a bit for items to appear on inventory and stuff
//						WItem left = ui.gui.getequipory().quickslots[6];
//						if (!left.item.getres().name.contains("scythe")){
//							BotUtils.sleep(200);
//						}
						BotUtils.sleep(300);

						GItem toPlantItem = null;
						Inventory inv = BotUtils.playerInventory();

						double maxQ = 0;
						double minQ = 9999;
						GItem toDrop = null;
						for (Widget w = inv.child; w != null; w = w.next) {
							if (w instanceof GItem && isSeed((GItem) w)) {
								if (((GItem) w).quality().max>maxQ) {
									toPlantItem = (GItem) w;
									maxQ = ((GItem) w).quality().max;
								}
								else if (((GItem) w).quality().max<minQ && inv.getFreeSpace() <= 5*w.sz.x*w.sz.y/ (inv.sqsz.x * inv.sqsz.y)) {
									minQ = ((GItem) w).quality().max;
									toDrop = (GItem) w;
								}
							}else if(w instanceof GItem && isCarrot((GItem)w)&&gob!=null){
									w.wdgmsg("drop", Coord.z);
									BotUtils.sleep(150);

							}
						}
						if (toDrop !=null&&gob!=null){
							toDrop.wdgmsg("drop", Coord.z);
							BotUtils.sleep(150);
						}
							BotUtils.sysMsg(maxQ+"", Color.WHITE);
						if (BotUtils.getItemAtHand()!= null ){
							GItem.NumberInfo ninf = ItemInfo.find(GItem.NumberInfo.class, BotUtils.getItemAtHand().info());
							if ((maxQ-10)>= BotUtils.getItemAtHand().quality().max||ninf.itemnum() <5){
							BotUtils.sysMsg("drop on hand, number " + ninf.itemnum(), Color.WHITE);
							BotUtils.getItemAtHand().wdgmsg("drop", Coord.z);
							BotUtils.sleep(150);}
						}
						if (BotUtils.getItemAtHand()== null) {
							BotUtils.takeItem(toPlantItem);
						} else {
//							BotUtils.sysMsg("Couldnt find any "+Plant, Color.WHITE);
//							BotUtils.sysMsg("Carrot Farmer Cancelled", Color.WHITE);
//							t.stop();
//							return;
						}
						// Planttaa, siemen käteen tähän vaiheeseen mennessä
						BotUtils.mapInteractClick(1);
						BotUtils.sleep(100);
						gob = get_plant_gob();
						BotUtils.sleep(100);

						}//end harvest
					}
					window.destroy();
					if (t != null) {
						BotUtils.sysMsg("Carrot Farmer Cancelled", Color.WHITE);
						t.stop();
					}
					return;
				} catch (Exception e) {
					BotUtils.sysMsg(e +"", Color.RED);
					BotUtils.sleep(350);
				}
				BotUtils.sleep(150);
			}

		}
        protected boolean isCarrot(final GItem item) {
	        String resName = item.resname();
	        if (resName != null && !resName.isEmpty()) {
	            for (String food : Plants)
	                if (resName.contains(food))
	                    return true;
	       }
	        return false;
	    }
			protected boolean isSeed(final GItem item) {
				String resName = item.resname();
				if (resName != null && !resName.isEmpty()&&resName.contains("/seed")) {
					return true;
				}
				return false;
			}
		});
		// This thingy makes that stupid window with cancel button, TODO: make it better
		private class StatusWindow extends Window {
	        public StatusWindow() {
	            super(Coord.z, "Modified Carrot Farmer");
	            setLocal(true);
	            add(new Button(120, "Cancel") {
	                public void click() {
	                    window.destroy();
	                    if(t != null) {
	                    	gameui().msg("Carrot Farmer Cancelled", Color.WHITE);
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
