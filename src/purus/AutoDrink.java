package purus;

import haven.Button;
import haven.*;
import haven.Window;

import java.awt.*;

public class AutoDrink {
	/* This script harvests and replants carrots.
	 *  Doesn't pathfind around objects.
	 *  Does this for all stage 4 carrots in sight.
	 *  Designed for round fields, lift beehives at middle temporarily off before harvest.
	 */
	public static boolean CarrotsNearby;

	private final UI ui;
    private Widget w;
    private Inventory i;
    private Widget window;

    private int Stage = 4;

	BotUtils BotUtils;

	public AutoDrink(UI ui, Widget w, Inventory i) {
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
			BotUtils.sysMsg("Auto Drink Started", Color.WHITE);
			window = BotUtils.gui().add(new StatusWindow(), 300, 200);
			while (true) {
				BotUtils.sleep(1000);
				GameUI gui = HavenPanel.lui.root.findchild(GameUI.class);
				IMeter.Meter stam = gui.getmeter("stam", 0);
				// Check energy stop if it is lower than 1500
				IMeter.Meter nrj = gui.getmeter("nrj", 0);
				if (nrj.a <= 30){
					BotUtils.sysMsg("Auto Drink Stop as run out of energy.", Color.WHITE);
					t.stop();
					return;
				}
				else if (stam.a <= 30) {
					WItem item = BotUtils.findDrink(BotUtils.playerInventory());
					if (item != null) {
						item.item.wdgmsg("iact", Coord.z, 3);
						BotUtils.sleep(500);
						@SuppressWarnings("deprecation")
						FlowerMenu menu = ui.root.findchild(FlowerMenu.class);
						if (menu != null) {
							for (FlowerMenu.Petal opt : menu.opts) {
								if (opt.name.equals("Drink")) {
									menu.choose(opt);
									menu.destroy();
									BotUtils.sysMsg("wait for stam back to 84", Color.WHITE);
									while (gui.getmeter("stam", 0).a <= 84) {
										BotUtils.sleep(500);
									}
									//
									CraftWindow makewnd = BotUtils.gui().gameui().makewnd;
									if (makewnd != null && makewnd.activeWdgmsgArgs!=null){
										Widget sender = (Widget) makewnd.activeWdgmsgArgs.get(0);
										String msg = (String) makewnd.activeWdgmsgArgs.get(1);
										Object[] args = (Object[]) makewnd.activeWdgmsgArgs.get(2);
										makewnd.wdgmsg(sender, msg, args);
									}
								}
							}
						}
					}
					else{
						// no drink available
						BotUtils.sleep(3000);
					}

			}

		}}});
		// This thingy makes that stupid window with cancel button, TODO: make it better
		private class StatusWindow extends Window {
	        public StatusWindow() {
	            super(Coord.z, "Auto Drink");
	            setLocal(true);
	            add(new Button(120, "Cancel") {
	                public void click() {
	                    window.destroy();
	                    if(t != null) {
	                    	gameui().info("Auto Drink Cancelled", Color.WHITE);
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
