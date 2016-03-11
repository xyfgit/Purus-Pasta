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
			Settings.setCancelAuto(false);
			String targetName = Settings.getFindTargetName();
			if (targetName!=null)
				BotUtils.sysMsg("Target "+ targetName, Color.WHITE);
			BotUtils.sysMsg("Auto Drink Started", Color.WHITE);
			window = BotUtils.gui().add(new StatusWindow(), 300, 200);
			Gob gob;
			Glob.Pagina pagina;
			while (true) {
				BotUtils.sleep(1000);
				GameUI gui = HavenPanel.lui.root.findchild(GameUI.class);
				IMeter.Meter stam = gui.getmeter("stam", 0);
				// Check energy stop if it is lower than 1500
				IMeter.Meter nrj = gui.getmeter("nrj", 0);
			 	if (stam.a <= 30&&nrj.a > 20) {
					 BotUtils.drink();
				}
				CraftWindow makewnd=null;
				if(BotUtils.gui().gameui()!=null)
					makewnd = BotUtils.gui().gameui().makewnd;
				if (makewnd != null && makewnd.activeWdgmsgArgs!=null){
					BotUtils.sysMsg("repeat craft", Color.WHITE);
					Widget sender = (Widget) makewnd.activeWdgmsgArgs.get(0);
					String msg = (String) makewnd.activeWdgmsgArgs.get(1);
					Object[] args = (Object[]) makewnd.activeWdgmsgArgs.get(2);
					makewnd.wdgmsg(sender, msg, args);
				}else if(Settings.getlastAction()!=null && targetName!=null){
					BotUtils.sysMsg("repeat last action", Color.WHITE);
					pagina = Settings.getlastAction();
					ui.gui.wdgmsg("act", (Object[])pagina.act().ad);
					pagina.act();
					gob = BotUtils.findObjectByNames(BotUtils.player().rc, 1000, targetName);
					if (gob != null) {
						boolean isPlant = gob.getres().name.contains("terobjs");
						int targetR = isPlant? 12:200;
//							BotUtils.sysMsg("Start go to the point!", Color.WHITE);
						BotUtils.goToCoord(gob.rc, targetR, true);
//							BotUtils.sysMsg("Get to the point!", Color.WHITE);
						BotUtils.doClick(gob, 1, 0);
						BotUtils.sleep(4000);
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
				Settings.setFindTargetName(null);
				Settings.setlastAction(null);
	            super.wdgmsg(sender, msg, args);
	        }
	        
		}
		//
}
