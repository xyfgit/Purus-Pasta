
package haven;

public class CraftWindow extends Window {
    private Widget makeWidget;

    public CraftWindow() {
        super(Coord.z, "Crafting", true);
        setLocal(true);
        setHideOnClose(true);
    }

    @Override
    public void wdgmsg(Widget sender, String msg, Object... args) {
        if (sender == cbtn) {
            hide();
        } else {
            super.wdgmsg(sender, msg, args);
        }
    }

    @Override
    public void addchild(Widget child, Object... args) {
        super.addchild(child, args);
        if (child instanceof Makewindow) {
            makeWidget = child;
        }
    }
    
    @Override
    public void hide() {
        super.hide();
        if (makeWidget != null)
            makeWidget.wdgmsg("close");
    }

    @Override
    public void cdestroy(Widget w) {
        if (makeWidget == w) {
            makeWidget = null;
            if (visible)
            hide();
        }
    }
}