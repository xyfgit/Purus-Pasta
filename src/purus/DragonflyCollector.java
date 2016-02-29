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
			targetName = (Settings.getFindTargetName()==null)? "gfx/kritter/dragonfly/dragonfly": Settings.getFindTargetName();
			BotUtils.sysMsg("Target "+ targetName, Color.WHITE);
			window = BotUtils.gui().add(new StatusWindow(), 300, 200);
			ui.root.findchild(FlowerMenu.class);
				BotUtils.setSpeed(1);
				while(BotUtils.getItemAtHand() == null) {
					GameUI gui = HavenPanel.lui.root.findchild(GameUI.class);
					IMeter.Meter stam = gui.getmeter("stam", 0);
					// Check energy stop if it is lower than 1500
					IMeter.Meter nrj = gui.getmeter("nrj", 0);
					if (nrj.a <= 30){
						t.stop();
						return;
					}
					else if (stam.a <= 30 && nrj.a >= 85) {
						BotUtils.drink();
					}
//					if (!BotUtils.isMoving()) {
						Gob gob = BotUtils.findObjectByNames(BotUtils.player().rc, 1000, targetName);
						if (gob != null) {
							BotUtils.goToCoord(gob.rc, 100, false);
							BotUtils.doClick(gob, 3, 0);
						}
//					}
						sleep(1000);
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
						Settings.setFindTargetName(null);
	                    window.destroy();
	                    if(t != null) {
	                    	gameui().info("Dragonfly Collector Cancelled", Color.WHITE);
	                    	t.stop();
	                    }
	                }
	            });
	            pack();
	        }
	        public void wdgmsg(Widget sender, String msg, Object... args) {
	            if (sender == this && msg.equals("close")) {
					Settings.setFindTargetName(null);
	                t.stop();
	            }
	            super.wdgmsg(sender, msg, args);
	        }
		}
	}