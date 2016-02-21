package haven;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import haven.util.ActionRecord;

public class CraftWindow extends Window {
    private static final IBox frame = new IBox("hud/tab", "tl", "tr", "bl", "br", "extvl", "extvr", "extht", "exthb");
    private final TabStrip tabStrip;
    private final Map<Glob.Pagina, TabStrip.Button> tabs = new HashMap<Glob.Pagina, TabStrip.Button>();
    private Widget makeWidget;
    public ArrayList<Object>  activeWdgmsgArgs= null;
    private Glob.Pagina lastAction;

    public CraftWindow() {
        super(Coord.z, "Crafting");
        tabStrip = add(new TabStrip() {
            // open crafting window
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
        //click crafting button
        if (sender == cbtn) {
            activeWdgmsgArgs = null;
            hide();
        } else {
            activeWdgmsgArgs = new ArrayList<>();
            super.wdgmsg(sender, msg, args);
            activeWdgmsgArgs.add(sender);
            activeWdgmsgArgs.add(msg);
            activeWdgmsgArgs.add(args);
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
            activeWdgmsgArgs = null;
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