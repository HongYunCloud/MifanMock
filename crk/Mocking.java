package crk;

import nano.http.bukkit.api.BukkitServerProvider;
import nano.http.d2.consts.Mime;
import nano.http.d2.consts.Status;
import nano.http.d2.core.Response;
import nano.http.d2.json.JSONArray;
import nano.http.d2.json.NanoJSON;
import nano.http.d2.utils.Misc;

import java.io.File;
import java.util.Properties;

public class Mocking extends BukkitServerProvider {
    @Override
    public void onEnable(String name, File dir, String uri) {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public Response serve(String uri, String method, Properties header, Properties parms, Properties files) {
        return null;
    }

    @Override
    public Response fallback(String uri, String method, Properties header, Properties parms, Properties files) {
        try {
            if (uri.endsWith("/verifySign")) {
                return new Response(Status.HTTP_OK, Mime.MIME_PLAINTEXT, Mifan.md5(parms.getProperty("mac") + "KFCCRAZYTHURSDAYV50" + true));
            } else if (uri.endsWith("/sql")) {
                System.out.println(parms.getProperty("json"));
                NanoJSON json = new NanoJSON(parms.getProperty("json"));
                String type = json.getString("dbType");
                json = json.getJSONObject("dbSql");
                StringBuilder sb = new StringBuilder();
                switch (type) {
                    case "updateDataSql":
                        sb.append("UPDATE ").append(json.getString("tableName")).append(" SET ");
                        JSONArray set = json.getJSONArray("updatefieldList");
                        for (int i = 0; i < set.length(); i++) {
                            sb.append(set.getString(i));
                        }
                        sb.append(json.getString("where"));
                        break;
                    case "insertDataSql":
                        sb.append("INSERT INTO ").append(json.getString("tableName")).append("(").append(json.getString("field")).append(") VALUES (");
                        for (int i = 0; i < json.getInt("fieldInfoMapSize"); i++) {
                            sb.append("?,");
                        }
                        sb.deleteCharAt(sb.length() - 1).append(")");
                        break;
                    case "selectDataSql":
                        sb.append("SELECT ").append(json.getString("field")).append(" FROM ").append(json.getString("tableName")).append(json.getString("where"));
                        break;
                    case "selectCountSql":
                        sb.append("SELECT COUNT(*) FROM ").append(json.getString("tableName")).append(json.getString("where"));
                        break;
                    case "deleteDataSql":
                        sb.append("DELETE FROM ").append(json.getString("tableName")).append(json.getString("where"));
                        break;
                    default:
                        break;
                }
                System.out.println(sb);
                return new Response(Status.HTTP_OK, Mime.MIME_PLAINTEXT, sb.toString());
            }
        } catch (Exception ignored) {
        }
        return new Response(Status.HTTP_FORBIDDEN, Mime.MIME_PLAINTEXT, Misc.BOM + "米饭插件验证服务器Mock\n适用于全部米饭插件\n\n使用方法：\n1，修改host，将admin.ljxmc.top指向本站\n2，使用激活码KFCCRAZYTHURSDAYV50\n\n作者：神秘人");
    }
}
