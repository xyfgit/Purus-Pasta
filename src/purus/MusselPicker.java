package purus;

import haven.FlowerMenu;
import haven.FlowerMenu.Petal;
import haven.Gob;
import haven.UI;
import haven.Widget;

public class MusselPicker {

public static boolean MusselsNearby;

	private final UI ui;
    private haven.Widget w;
    public Petal[] opts;
    
	BotUtils BotUtils;

	public MusselPicker (UI ui, Widget w) {
		this.ui = ui;
		this.w = w;
		BotUtils = new BotUtils(ui, w);
	}
	
	public void Run () {
	t.start();	
	}
	Thread t = new Thread(new Runnable() {
	public void run()  {
		Gob gob = BotUtils.findObjectByNames(500, "gfx/terobjs/herbs/mussels");
		if(gob != null)
			MusselsNearby = true;
		else
			MusselsNearby = false;
		while(MusselsNearby = true) {
			BotUtils.doClick(gob, 3, 0);
			//
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
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
			gob = BotUtils.findObjectByNames(500, "gfx/terobjs/herbs/mussels");
			if(gob != null) 
			MusselsNearby = true;
			else
				return;
		}
				return;
	}
	});

}