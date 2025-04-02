package crk;

import nano.http.bukkit.api.debug.DebugMain;

public class MfMain {
    public static void main(String[] args) throws Exception {
        DebugMain.debug(Mocking.class, "/unused", 443);
    }
}
