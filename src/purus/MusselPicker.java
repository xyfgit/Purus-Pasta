package purus;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import haven.*;
import haven.FlowerMenu.Petal;

public class MusselPicker {

public static boolean MusselsNearby;

	private final UI ui;
    private haven.Widget w;
    private haven.Inventory i;
    public Petal[] opts;
    private Widget window; 
    
	BotUtils BotUtils;

	public MusselPicker (UI ui, Widget w, Inventory i) {
		this.ui = ui;
		this.w = w;
		this.i = i;
		BotUtils = new BotUtils(ui, w, i);
	};
	ArrayList<String> targets =  new ArrayList<String>(Arrays.asList("gfx/terobjs/herbs/mussels",
			"gfx/kritter/frog/frog", "gfx/terobjs/herbs/blueberry"));
	private Gob get_target_gob(){
		Gob gob = null;
		for (String target: targets){
			gob = BotUtils.findObjectByNames(500, target);
			if(gob != null){
				break;}
		}
		return gob;
	};
	public void Run () {
	t.start();	
	}
	Thread t = new Thread(new Runnable() {
	public void run()  {
		window = BotUtils.gui().add(new StatusWindow(), 300, 200);
		Gob gob = get_target_gob();
		while(gob != null) {
			double start_dis = BotUtils.player().rc.dist(gob.rc);
			BotUtils.doClick(gob, 3, 0);
			//
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			double moved_dis = BotUtils.player().rc.dist(gob.rc);
			if (moved_dis > 50 && Math.abs(start_dis-moved_dis)<5){
				BotUtils.turn_around(gob.rc, 1);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (moved_dis > 50 && Math.abs(start_dis-moved_dis)<5){
					BotUtils.turn_around(gob.rc, -1);
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				BotUtils.doClick(gob, 3, 0);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			@SuppressWarnings("deprecation")
			FlowerMenu menu = ui.root.findchild(FlowerMenu.class);
	            if (menu != null) {
	                for (FlowerMenu.Petal opt : menu.opts) {
	                    if (opt.name.equals("Pick")) {
	                        menu.choose(opt);
	                        menu.destroy();
	            			boolean onolemassa = true;
	            			while(onolemassa) {
		            			Gob onko = BotUtils.findObjectById(gob.id);
		            			if (onko != null) 
		            			onolemassa = true;
		            			else
		            			onolemassa = false;
	            			}
	                    }
	                }
	            }
         //   BotUtils.Choose(opts[1])
			gob = get_target_gob();
		}
		BotUtils.sysMsg("Mussel Picker Finished", Color.WHITE);
        window.destroy();
		return;
	}
	});
	
	// This thingy makes that stupid window with cancel button, todo: make it better
			private class StatusWindow extends Window {
		        public StatusWindow() {
		            super(Coord.z, "Mussel Picker");
		            setLocal(true);
		            add(new Button(120, "Cancel") {
		                public void click() {
		                    window.destroy();
		                    if(t != null) {
		                    	gameui().info("Mussel Picker Cancelled", Color.WHITE);
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