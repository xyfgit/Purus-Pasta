package purus;

import java.awt.Color;

import haven.Button;
import haven.Coord;
import haven.FlowerMenu;
import haven.GItem;
import haven.GameUI;
import haven.Gob;
import haven.HavenPanel;
import haven.IMeter;
import haven.Inventory;
import haven.UI;
import haven.WItem;
import haven.Widget;
import haven.Window;

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
    private String Plant = "gfx/terobjs/plants/carrot";
    private int Stage = 4;
    
	BotUtils BotUtils;

	public CarrotFarmer (UI ui, Widget w, Inventory i) {
		this.ui = ui;
		this.w = w;
		this.i = i;
		BotUtils = new BotUtils(ui, w, i);
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
					BotUtils.drop_item(1);
					Gob gob = BotUtils.findNearestStageCrop(500, Stage, Plant);
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
								try {
									Thread.sleep(250);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								@SuppressWarnings("deprecation")
								FlowerMenu menu = ui.root.findchild(FlowerMenu.class);
								if (menu != null) {
									for (FlowerMenu.Petal opt : menu.opts) {
										if (opt.name.equals("Drink")) {
											menu.choose(opt);
											menu.destroy();
											BotUtils.sysMsg("wait for stam back to 84", Color.WHITE);
											while (gui.getmeter("stam", 0).a <= 84) {
												try {
													Thread.sleep(500);
												} catch (InterruptedException e) {
													e.printStackTrace();
												}
											}
										}
									}
								}
							}
							else{
								try {
									BotUtils.sysMsg("slowly wait for stam", Color.WHITE);
									Thread.sleep(3000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
						//end of drink
						BotUtils.doClick(gob, 3, 0);
						try {
							Thread.sleep(250);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
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
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						// Some better method should be implemented, but now it just waits a bit for items to appear on inventory and stuff
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
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
							t.stop();
							return;
						}
						if (item != null) {

							BotUtils.takeItem(item);

						} else {
							BotUtils.sysMsg("Couldnt find any seeds", Color.WHITE);
							BotUtils.sysMsg("Carrot Farmer Cancelled", Color.WHITE);
							t.stop();
							return;
						}
						// Planttaa, siemen käteen tähän vaiheeseen mennessä
						BotUtils.mapInteractClick(1);
						//  TODO Droppaa kaikki siemenet tms. invistä + kädestä = saa toimimaan kaikkiin siemeniin

						//
						gob = BotUtils.findNearestStageCrop(500, Stage, Plant);
						if (gob != null)
							CarrotsNearby = true;
						else
							break;
					}
					window.destroy();
					if (t != null) {
						BotUtils.sysMsg("Carrot Farmer Cancelled", Color.WHITE);
						t.stop();
					}
					return;
				} catch (Exception e) {

					BotUtils.sysMsg(e.getMessage(), Color.RED);
					try {
						Thread.sleep(300);
					} catch (InterruptedException ie) {
						ie.printStackTrace();
					}
				}
			}

		}

    	final String[] carrot = {Seed};
        protected boolean isCarrot(final GItem item) {
	        String resName = item.resname();
	        if (resName != null && !resName.isEmpty()) {
	            for (String food : carrot)
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
