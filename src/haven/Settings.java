package haven;

public class Settings {
    transient private static boolean DebugMode = false;
    public static boolean getSettings() {
        return DebugMode;
    }
    public static void setSettings(boolean debug) {
        DebugMode=debug;
    }
}
