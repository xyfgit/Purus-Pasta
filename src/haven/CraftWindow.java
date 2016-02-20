package haven;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CraftWindow extends Window {
    private static final IBox frame = new IBox("hud/tab", "tl", "tr", "bl", "br", "extvl", "extvr", "extht", "exthb");
    private final TabStrip tabStrip;
    private final Map<Glob.Pagina, TabStrip.Button> tabs = new HashMap<Glob.Pagina, TabStrip.Button>();
    private Widget makeWidget;
    private Glob.Pagina lastAction;
    private Thread drink_th = null;

    protected void craftAssist(Widget sender, String msg, Object... args){
        GameUI gui = HavenPanel.lui.root.findchild(GameUI.class);
        FlowerMenu menu = ui.root.findchild(FlowerMenu.class);
        IMeter.Meter nrj = gui.getmeter("nrj", 0);
        IMeter.Meter stam = gui.getmeter("stam", 0);
        if (menu != null && stam.a <= 30) {
            if (nrj.a > 30){
                int slot  = 0;
                wdgmsg("belt",  slot + (slot * 12), 1, ui.modflags());
                ui.root.findchild(GameUI.class).info("Get some drink.", Color.WHITE);
                while (gui.getmeter("stam", 0).a <= 84) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                super.wdgmsg(sender, msg, args);

            }else{
                ui.root.findchild(GameUI.class).info("Energy is too low to drink.", Color.WHITE);
            }
        };
    };

    protected void startAssist(Widget sender, String msg, Object... args){
        ui.root.findchild(GameUI.class).info("check drink status "+Config.isCraftAssist, Color.WHITE);
        if (Config.isCraftAssist == true){
            if (drink_th == null || drink_th.isInterrupted()) {
                drink_th = new Thread(new Runnable() {
                    public void run() {
                        while (true) {
                            craftAssist(sender, msg, args);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
            if (drink_th !=null && !drink_th.isAlive() && !drink_th.isInterrupted()){
                drink_th.start();
            }
        }
    }

    protected void stopAssist(){
        if (drink_th != null && drink_th.isAlive()) {
            ui.root.findchild(GameUI.class).info("stop check drink status "+Config.isCraftAssist, Color.WHITE);
            drink_th.stop();
            drink_th = null;
        }
    }
    public CraftWindow() {
        super(Coord.z, "Crafting");
        tabStrip = add(new TabStrip() {
            protected void selected(Button button) {
                for (Map.Entry<Glob.Pagina, Button> entry : tabs.entrySet()) {
                    Glob.Pagina pagina = entry.getKey();
                    if (entry.getValue().equals(button) && pagina != lastAction) {
                        ui.gui.wdgmsg("act", (Object[])pagina.act().ad);
                        lastAction = null;
                        break;
                    }
                }
            }
        });
        setLocal(true);
        setHideOnClose(true);
        setfocusctl(true);
    }

    public void setLastAction(Glob.Pagina value) {
        lastAction = value;
    }

    @Override
    public void wdgmsg(Widget sender, String msg, Object... args) {
        if (sender == cbtn) {
            stopAssist();
            hide();
        } else {
            startAssist(sender, msg, args);
            super.wdgmsg(sender, msg, args);
        }
    }

    @Override
    public <T extends Widget> T add(T child) {
        child = super.add(child);
        if (child instanceof Makewindow) {
            if (lastAction != null) {
                addTab(lastAction);
            }
            makeWidget = child;
            makeWidget.c = new Coord(5, tabStrip.sz.y + 5);
        }
        return child;
    }

    @Override
    public void cdestroy(Widget w) {
    	if (makeWidget == w) {
            stopAssist();
    		makeWidget = null;
    		hide();
    		}
    	}

    @Override
    public void cdraw(GOut g) {
        super.cdraw(g);
        frame.draw(g, new Coord(0, Math.max(0, tabStrip.sz.y - 1)), asz.sub(0, tabStrip.sz.y));
    }

    @Override
    public void resize(Coord sz) {
        super.resize(sz.add(5, 5));
    }

    private void addTab(Glob.Pagina pagina) {
        if (tabs.containsKey(pagina)) {
            TabStrip.Button old = tabs.get(pagina);
            tabStrip.remove(old);
        }
        TabStrip.Button added = tabStrip.insert(0, lastAction.act().name);
        tabStrip.select(added);
        if (tabStrip.getButtonCount() > 4) {
            removeTab(tabStrip.getButtonCount() - 1);
        }
        tabs.put(lastAction, added);
    }

    private void removeTab(int index) {
        TabStrip.Button removed = tabStrip.remove(index);
        tabs.values().remove(removed);
    }
}