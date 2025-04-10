package crk;

import nano.http.d2.consts.Mime;
import nano.http.d2.consts.Status;
import nano.http.d2.core.Response;
import nano.http.d2.json.JSONArray;
import nano.http.d2.json.NanoJSON;
import nano.http.d2.serve.ServeProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Properties;

public class Mocking implements ServeProvider {
    private boolean printed = false;

    private void print() {
        if (!printed) {
            printed = true;
            MfMain.log("\n" +
                    "Stop the fire\n" +
                    "A mockingbird sings\n" +
                    "My name, laughing, calling me\n" +
                    "Who’s the liar?\n" +
                    "Maybe you’re in my bones, my blood\n" +
                    "Take it all\n" +
                    "Filling me up, I’m empty\n" +
                    "Heartbeats\n" +
                    "Counting down, if I could stop the clock, you’d win\n" +
                    "\nAuthor : matim");
        }
    }

    private static final String kfc = "KFCCRAZYTHURSDAYV50";

    @Override
    public Response serve(String uri, String method, Properties header, Properties parms, Properties files) {
        print();
        try {
            if (uri.endsWith("/verifySign")) {
                String sign = parms.getProperty("sign");
                if (!sign.equals(kfc)) {
                    MfMain.log("Consider using code: " + kfc);
                }

                String ans = Mifan.md5(parms.getProperty("mac") + sign + true);
                String ver = (String) parms.getOrDefault("signVersion", "2");
                if (!ver.contains("3")) {
                    return new Response(Status.HTTP_OK, Mime.MIME_PLAINTEXT, ans);
                }
                NanoJSON json = new NanoJSON();
                json.put("sign", ans);
                json.put("result", true);
                return new Response(Status.HTTP_OK, Mime.MIME_JSON, json.toString());
            } else if (uri.endsWith("/sql")) {
                NanoJSON json = new NanoJSON(parms.getProperty("json"));
                String type = json.getString("dbType");
                json = json.getJSONObject("dbSql");
                if (json.has("dbSqlStr")) {
                    try {
                        String dbSqlStr = json.getString("dbSqlStr");
                        String token = header.getProperty("signToken");
                        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(token.getBytes(), "AES"));
                        byte[] finalDbSqlBytes = cipher.doFinal(Base64.getDecoder().decode(dbSqlStr.getBytes(StandardCharsets.UTF_8)));
                        String finalDbSqlString = new String(finalDbSqlBytes, StandardCharsets.UTF_8);
                        json = new NanoJSON(finalDbSqlString);
                    } catch (Exception e) {
                        return new Response(Status.HTTP_NOTFOUND, Mime.MIME_PLAINTEXT, "404");
                    }
                }

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
                return new Response(Status.HTTP_OK, Mime.MIME_PLAINTEXT, sb.toString());
            }
        } catch (Exception ignored) {
        }
        return new Response(Status.HTTP_NOTFOUND, Mime.MIME_PLAINTEXT, "404");
    }
}
