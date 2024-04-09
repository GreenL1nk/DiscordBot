package global.pastebin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
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
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return parsePastebinFromJson(filterEmbedsAndContent(response.toString()));
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

                objectNode.retain("embeds", "content");

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

        return new Pastebin(content, embeds);
    }

    private static DataObject fromJson(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }

        return DataObject.fromJson(json);
    }
}
