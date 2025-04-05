package crk;

import nano.http.bukkit.mock.InjectRegistry;
import nano.http.d2.console.Console;
import nano.http.d2.consts.Mime;
import nano.http.d2.consts.Status;
import nano.http.d2.core.Response;
import nano.http.d2.serve.ServeProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Properties;

public class MfMain extends JavaPlugin {
    private static MfMain instance;

    @Override
    public void onLoad() {
        instance = this;
        Console.magic = false;
        log("Loading CRK...");
        InjectRegistry.register("admin.ljxmc.top", new Mocking());
        InjectRegistry.register("ricedoc.handyplus.cn", new ServeProvider() {
            @Override
            public Response serve(String uri, String method, Properties header, Properties parms, Properties files) {
                return new Response(Status.HTTP_NOTFOUND, Mime.MIME_PLAINTEXT, "404");
            }
        });
    }

    public static void log(String message) {
        if (instance == null) {
            System.out.println(message);
        } else {
            instance.getLogger().info(message);
        }
    }
}
