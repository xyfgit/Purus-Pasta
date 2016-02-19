package haven;

public class Settings {
    transient private static boolean DebugMode = false;
    transient private static boolean KeepWalk = false;
    public static boolean getSettings() {
        return DebugMode;
    }
    public static void setSettings(boolean debug) {
        DebugMode=debug;
    }
    public static boolean getKeepWalk() {
        return KeepWalk;
    }
    public static void setKeepWalk(boolean walk) {
        KeepWalk=walk;
    }
}
