package haven;

public class Settings {
    transient private static boolean DebugMode = false;
    transient private static boolean KeepWalk = false;
    public static boolean getSettings() {
        return DebugMode;
    }
    public static void setSettings(boolean val) {
        DebugMode=val;
    }
    public static boolean getKeepWalk() {
        return KeepWalk;
    }
    public static void setKeepWalk(boolean val) {
        KeepWalk=val;
    }
    public static boolean CancelAuto = false;
    public static boolean getCancelAuto() {
        return CancelAuto;
    }
    public static void setCancelAuto(boolean val) {
        CancelAuto=val;
    }
    private static String FindTargetName = null;
    public static String getFindTargetName() {
        return FindTargetName;
    }
    public static void setFindTargetName(String val) {
        FindTargetName=val;
    }
}
