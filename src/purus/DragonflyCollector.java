package purus;

import java.awt.Color;

import haven.*;

public class DragonflyCollector {
	/* This script collects dragonfly with crawlin speed around swamp
	 * How it works plan:
	 * 1. Right click nearest dragonfly
	 * 2. Check if player is not moving or dragonfly is out of camera/collected to inventory
	 * 3. Check if inv is on hand (something in hand) if not then repeat
	 */

	private final UI ui;
    private haven.Widget w;
    private Inventory i;
    private Widget window;  
    static String targetName = null;
	BotUtils BotUtils;

	public DragonflyCollector (UI ui, Widget w, Inventory i) {
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
			Gob gob =null;
			Settings.setCancelAuto(false);
			int stopEnergy = 81;
			String action = null;
			targetName = (Settings.getFindTargetName()==null)? "gfx/kritter/dragonfly/dragonfly": Settings.getFindTargetName();
			if (targetName.contains(" ")){
				 String[] keywords = targetName.split("\\s+");
				 if (keywords.length >= 2){
					 action = keywords[0];
					 targetName = keywords[1];
					 try {
						 if (keywords.length > 2) stopEnergy = Integer.parseInt(keywords[2]);
					 }catch (Exception e){stopEnergy = 10;
					 BotUtils.sysMsg("Energy set wrong, set it to 10 by default.", Color.RED);
					 }
				 }else if (keywords.length == 1){
					 targetName = keywords[0];
				 }else{
					 targetName = "gfx/kritter/dragonfly/dragonfly";
				 }
			}
			BotUtils.sysMsg("Target "+ targetName, Color.WHITE);
			window = BotUtils.gui().add(new StatusWindow(), 300, 200);
			ui.root.findchild(FlowerMenu.class);
				while(BotUtils.getItemAtHand() == null) {
					GameUI gui = HavenPanel.lui.root.findchild(GameUI.class);
					IMeter.Meter stam = gui.getmeter("stam", 0);
					// Check energy stop if it is lower than 1500
					IMeter.Meter nrj = gui.getmeter("nrj", 0);
					if (stam.a <= 30 && nrj.a >= stopEnergy) {
						BotUtils.drink();
						BotUtils.sysMsg("drink finished.", Color.WHITE);
					}
//					if (!BotUtils.isMoving()) {
						gob = BotUtils.findObjectByNames(BotUtils.player().rc, 1000, targetName);

						if (gob != null) {
							boolean isPlant = gob.getres().name.contains("terobjs");
							int targetR = isPlant? 12:200;
//							BotUtils.sysMsg("Start go to the point!", Color.WHITE);
							BotUtils.goToCoord(gob.rc, targetR, true);
//							BotUtils.sysMsg("Get to the point!", Color.WHITE);
							BotUtils.doClick(gob, 3, 0);
							if (action!=null){
								BotUtils.opGob(gob, action);
							}
						}
//					}
						sleep(600);
				}
		        window.destroy();
		       t.stop();
			}
		});
		
	private void sleep(int t){
		try {
			Thread.sleep(t);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
	}

		// This thingy makes that stupid window with cancel button, TODO: make it better
		private class StatusWindow extends Window {
	        public StatusWindow() {
	            super(Coord.z, "Dragonfly Collector");
	            setLocal(true);
	            add(new Button(120, "Cancel") {
	                public void click() {
	                    window.destroy();
	                    if(t != null) {
							Settings.setCancelAuto(true);
	                    	gameui().info("Dragonfly Collector Cancelled", Color.WHITE);
	                    	t.stop();
	                    }
	                }
	            });
	            pack();
	        }
	        public void wdgmsg(Widget sender, String msg, Object... args) {
				Settings.setFindTargetName(null);
	            if (sender == this && msg.equals("close")) {
					Settings.setFindTargetName(null);
					Settings.setCancelAuto(true);
	                t.stop();
	            }
	            super.wdgmsg(sender, msg, args);
	        }
		}
	}