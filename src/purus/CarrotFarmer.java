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
			for(String temp :Plants) {
				if (temp.equals( "gfx/terobjs/plants/flax")){
					Stage=3;
				}else if (temp.equals(  "gfx/terobjs/plants/pepper")){
					Stage=6;
				}else if (temp.equals(  "gfx/terobjs/plants/barley")){
					Stage=3;
				}
				BotUtils.sysMsg(temp, Color.WHITE);
				gob_temp = BotUtils.findNearestStageCrop(500, Stage, temp);
				if (gob_temp != null) {
					Coord3f f = gob_temp.getrc();
					Coord3f p1 = ui.sess.glob.oc.getgob(MapView.plgob).getrc();
					if (distance > f.dist(p1)) {
						gob = gob_temp;
						Plant = temp;
						distance = f.dist(p1);
						BotUtils.sysMsg(Plant+distance, Color.WHITE);
					}
				}
			}
		}else {
			if (Plant.equals( "gfx/terobjs/plants/flax")){
				Stage=3;
			}else if (Plant.equals(  "gfx/terobjs/plants/pepper")){
				Stage=6;
			}else if (Plant.equals(  "gfx/terobjs/plants/barley")){
				Stage=3;
			}
			gob = BotUtils.findNearestStageCrop(500, Stage, Plant);
			Plants.add(Seed);
		}
		return gob;
	}
	private void sleep(int t){
		try {
			Thread.sleep(t);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
	}
	public void Run () {
		t.start();
		}
		Thread t = new Thread(new Runnable() {
		public void run()  {
			BotUtils.sysMsg("Modified Carrot Farmer Started", Color.WHITE);
			window = BotUtils.gui().add(new StatusWindow(), 300, 200);
			while (true) {
				try {
//					BotUtils.drop_item(1);
					Gob gob = get_plant_gob();
					if (gob != null)
						CarrotsNearby = true;
					else
						CarrotsNearby = false;
					while (CarrotsNearby = true) {

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
							WItem item = BotUtils.findDrink(BotUtils.playerInventory());
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
											BotUtils.sysMsg("wait for stam back to 84", Color.WHITE);
											while (gui.getmeter("stam", 0).a <= 84) {
												sleep(550);
											}
										}
									}
								}
							}
							else{
								BotUtils.sysMsg("slowly wait for stam", Color.WHITE);
								sleep(3000);
							}
						}
						//end of drink
						BotUtils.doClick(gob, 3, 0);
						sleep(350);
						@SuppressWarnings("deprecation")
						FlowerMenu menu = ui.root.findchild(FlowerMenu.class);
						if (menu != null) {
							for (FlowerMenu.Petal opt : menu.opts) {
								if (opt.name.equals("Harvest")) {
									menu.choose(opt);
									menu.destroy();
								}
							}
						}
						while (gui.prog >= 0) {
							sleep(150);
						}
						// Some better method should be implemented, but now it just waits a bit for items to appear on inventory and stuff
						sleep(150);
						GItem item = BotUtils.getItemAtHand();

						if (item == null) {
							Inventory inv = BotUtils.playerInventory();
							for (Widget w = inv.child; w != null; w = w.next) {
								if (w instanceof GItem && isCarrot((GItem) w)) {
									item = (GItem) w;
									break;
								}
							}
						} else if (!isCarrot(item)) {
							BotUtils.sysMsg("Item in hand is not seed", Color.WHITE);
							BotUtils.sysMsg("Carrot Farmer Cancelled, not carrot", Color.WHITE);
//							t.stop();
//							return;
						}
						if (item != null) {
							BotUtils.takeItem(item);
						} else {
//							BotUtils.sysMsg("Couldnt find any "+Plant, Color.WHITE);
//							BotUtils.sysMsg("Carrot Farmer Cancelled", Color.WHITE);
//							t.stop();
//							return;
						}
						// Planttaa, siemen käteen tähän vaiheeseen mennessä
						BotUtils.mapInteractClick(1);
						//  TODO Droppaa kaikki siemenet tms. invistä + kädestä = saa toimimaan kaikkiin siemeniin
						//
						gob = get_plant_gob();
						if (gob != null)
							CarrotsNearby = true;
						else
							break;
						sleep(100);
					}
					window.destroy();
					if (t != null) {
						BotUtils.sysMsg("Carrot Farmer Cancelled", Color.WHITE);
						t.stop();
					}
					return;
				} catch (Exception e) {

					BotUtils.sysMsg(e.getMessage(), Color.RED);
					sleep(350);
				}
				sleep(150);
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
	                    	gameui().info("Carrot Farmer Cancelled", Color.WHITE);
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
