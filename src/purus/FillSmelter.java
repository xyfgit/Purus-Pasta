package purus;

import java.awt.Color;

import haven.GItem;
import haven.Gob;
import haven.Inventory;
import haven.UI;
import haven.Widget;
import haven.FlowerMenu.Petal;

public class FillSmelter {

	private final UI ui;
    private haven.Widget w;
    private haven.Inventory i;
    private int branchesfed;
    public Petal[] opts;
    private Widget window; 
	BotUtils BotUtils;

	public FillSmelter (UI ui, Widget w, Inventory i) {
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
		Gob gob = BotUtils.findObjectByNames(BotUtils.player().rc, 100, "gfx/terobjs/smelter");
			int AmountFilled = 0;
			while (AmountFilled != 12) {
		
           	 Inventory inv = BotUtils.playerInventory();
           	 GItem item = null;;
           	 
             for (Widget w = inv.child; w != null; w = w.next) {
                 if (w instanceof GItem && isCoal((GItem) w)) {
                    item = (GItem)w;
                     break;
                 	}
             	}
             if (item != null) {
	                BotUtils.takeItem(item);
	    			sleep(250);
	            } else {
	            	BotUtils.sysMsg("Couldnt find coal", Color.WHITE);
	            	BotUtils.sysMsg("Smelter Filler Cancelled", Color.WHITE);
	                t.stop();
	                return;
	            }
 			BotUtils.itemClick(gob, 1);
			sleep(250);
			AmountFilled ++;
			}
		}
	
	private void sleep(int t){
		try {
			Thread.sleep(t);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
	}
	
    public String itemName(GItem item) {
    return item.resname();
    }
	final String[] coal = {"gfx/invobjs/coal", "gfx/invobjs/blackcoal"};
    protected boolean isCoal(final GItem item) {
        String resName = item.resname();
        if (resName != null && !resName.isEmpty()) {
            for (String food : coal)
                if (resName.contains(food))
                    return true;
       }
        return false;
    }
	});
}