package global.pastebin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class PastebinAPI {

    public static Pastebin getPaste(String pasteKey) {
        String urlString = "https://pastebin.com/raw/" + pasteKey;
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    result.write(buffer, 0, length);
                }
                String responseString = result.toString(StandardCharsets.UTF_8).replaceAll("id", "custom_id");
            return parsePastebinFromJson(filterEmbedsAndContent(responseString));
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    public static Pastebin getPasteContent(String apiKey, String apiUserKey, String pasteKey) {
//        String url = "https://pastebin.com/api/api_raw.php";
//        try {
//            URL obj = new URL(url);
//            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//
//            con.setRequestMethod("POST");
//
//            String urlParameters = "api_option=show_paste"
//                    + "&api_user_key=" + URLEncoder.encode(apiUserKey, StandardCharsets.UTF_8)
//                    + "&api_dev_key=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8)
//                    + "&api_paste_key=" + URLEncoder.encode(pasteKey, StandardCharsets.UTF_8);
//
//            con.setDoOutput(true);
//            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//            wr.writeBytes(urlParameters);
//            wr.flush();
//            wr.close();
//
//            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//            String inputLine;
//            StringBuilder response = new StringBuilder();
//
//            while ((inputLine = in.readLine()) != null) {
//                response.append(inputLine);
//            }
//            in.close();
//
//            return parsePastebinFromJson(filterEmbedsAndContent(response.toString()));
//        }
//        catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    private static String filterEmbedsAndContent(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode;
        try {
            rootNode = mapper.readTree(jsonString);

            if (rootNode.isObject()) {
                ObjectNode objectNode = (ObjectNode) rootNode;

                objectNode.retain("embeds", "content", "components");

                return mapper.writeValueAsString(objectNode);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return jsonString;
    }

    private static Pastebin parsePastebinFromJson(String jsonString) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(jsonString);

        String content = rootNode.get("content").asText();
        ArrayList<MessageEmbed> embeds = new ArrayList<>();

        JsonNode embedsNode = rootNode.get("embeds");
        if (embedsNode.isArray()) {
            for (JsonNode embedNode : embedsNode) {
                String embedJson = mapper.writeValueAsString(embedNode);
                embeds.add(EmbedBuilder.fromData(fromJson(embedJson)).build());
            }
        }

        ArrayList<ActionRow> components = new ArrayList<>();
        JsonNode componentsNode = rootNode.get("components");
        if (componentsNode.isArray()) {
            for (JsonNode componentNode : componentsNode) {
                DataObject dataObject = fromJson(mapper.writeValueAsString(componentNode));
                components.add(ActionRow.fromData(dataObject));
            }
        }

        return new Pastebin(content, embeds, components);
    }

    private static DataObject fromJson(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }

        return DataObject.fromJson(json);
    }
}
