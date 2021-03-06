package purus;

import java.awt.Color;

import haven.GItem;
import haven.Gob;
import haven.Inventory;
import haven.UI;
import haven.Widget;
import haven.FlowerMenu.Petal;

public class FillOven {

	private final UI ui;
    private haven.Widget w;
    private haven.Inventory i;
    private int branchesfed;
    public Petal[] opts;
    private Widget window; 
	BotUtils BotUtils;

	public FillOven (UI ui, Widget w, Inventory i) {
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
		/*
		 * 1. Etsi lahin uuni
		 * 2. Etsi oksa invista ja ota se kateen
		 * 3. Klikkaa uunia
		 * 4. Toista oksan otto ja uunin klikkaus niin etta 4 oksaa sisalla
		 * 5. Valmis!!
		 */
		Gob gob = BotUtils.findObjectByNames(BotUtils.player().rc, 50, "gfx/terobjs/oven");
			int AmountFilled = 0;
			while (AmountFilled != 4) {
		
           	 Inventory inv = BotUtils.playerInventory();
           	 GItem item = null;;
           	 
             for (Widget w = inv.child; w != null; w = w.next) {
                 if (w instanceof GItem && isBranch((GItem) w)) {
                    item = (GItem)w;
                     break;
                 	}
             	}
             if (item != null) {
	                BotUtils.takeItem(item);
	    			sleep(250);
	            } else {
	            	BotUtils.sysMsg("Couldnt find branch", Color.WHITE);
	            	BotUtils.sysMsg("Oven Filler Cancelled", Color.WHITE);
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
	final String[] coal = {"gfx/invobjs/branch"};
    protected boolean isBranch(final GItem item) {
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