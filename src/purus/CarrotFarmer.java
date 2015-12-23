package purus;

import java.awt.Color;

import haven.Button;
import haven.Config;
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
    private Widget window; 
    
    
	BotUtils BotUtils;

	public CarrotFarmer (UI ui, Widget w) {
		this.ui = ui;
		this.w = w;
		BotUtils = new BotUtils(ui, w);
	}
	
	public void Run () {
		t.start();
		}
		Thread t = new Thread(new Runnable() {
		public void run()  {
			BotUtils.sysMsg("Carrot Farmer Started", Color.WHITE);
			window = BotUtils.gui().add(new StatusWindow(), 300, 200);
			Gob gob = BotUtils.findNearestStageCrop(500, 4, "gfx/terobjs/plants/carrot");
			if(gob != null)
				CarrotsNearby = true;
			else
				CarrotsNearby = false;
			while(CarrotsNearby = true) {
				// Start of drink TODO: Make separate function of this maybe yeah?
				GameUI gui = HavenPanel.lui.root.findchild(GameUI.class);
				 IMeter.Meter stam = gui.getmeter("stam", 0);
				 if (stam.a <= 30) {
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
				                        while(gui.getmeter("stam", 0).a <= 84) {
				                        	 try {
												Thread.sleep(100);
											} catch (InterruptedException e) {
												e.printStackTrace();
											}
				                        }
				                    }
				                }
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
	            while(gui.prog >= 0) {
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
	                         item = (GItem)w;
	                         break;
	                     	}
	                 }
	            } else if(!isCarrot(item)) {
	            	BotUtils.sysMsg("Item in hand is not a filler", Color.WHITE);
	            	BotUtils.sysMsg("Carrot Farmer Cancelled", Color.WHITE);
	                t.stop();
	                return;
	            }
	            if (item != null) {
	                BotUtils.takeItem(item);
	            } else {
	            	BotUtils.sysMsg("Couldnt find any filler", Color.WHITE);
	            	BotUtils.sysMsg("Carrot Farmer Cancelled", Color.WHITE);
	                t.stop();
	                return;
	            }
	            // Seuraavaksi planttaa eli tähän asti vaan porkkana käteen!
			BotUtils.mapInteractClick(1);
			gob = BotUtils.findNearestStageCrop(500, 4, "gfx/terobjs/plants/carrot");
			if(gob != null) 
			CarrotsNearby = true;
			else
				break;
		}
			
			BotUtils.sysMsg("Carrot Farmer Finished", Color.WHITE);
            window.destroy();
			return;
		}

    	final String[] carrot = {"gfx/invobjs/carrot"};
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
		// This thingy makes that stupid window with cancel button, todo: make it better
		private class StatusWindow extends Window {
	        public StatusWindow() {
	            super(Coord.z, "Carrot Farmer");
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
