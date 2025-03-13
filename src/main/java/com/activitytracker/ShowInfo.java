package com.activitytracker;

import com.activitytracker.enums.ResponseType;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Scanner;

public class ShowInfo {

    public static void show() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Insert github username: ");
        String userName = scanner.nextLine();

        final String API_URL = "https://api.github.com/users/%s/events".formatted(userName);

        // obtiene los datos de la api
        ApiConnection apiConnection = new ApiConnection();
        Integer responseCode = apiConnection.getFromApi(API_URL, ResponseType.GET_CODE, Integer.class);
        String responseBody = apiConnection.getFromApi(API_URL, ResponseType.GET_BODY, String.class);

        if (responseCode == 404) {
            System.out.println("User Not found");
            return;
        }

        if (responseCode == 200) {
            if (responseBody.equals("[]")) {
                System.out.println("Nothing to show");
                return;
            }
            ShowInfo showInfo = new ShowInfo();
            showInfo.showAcivityInfo(responseBody);
        }



    }

    public void showAcivityInfo(String responseBody) {
        JsonArray activityArray = JsonParser.parseString(responseBody).getAsJsonArray();
        for (JsonElement element : activityArray) {
            JsonObject event = element.getAsJsonObject();
            String type = event.get("type").getAsString();
            String repoName = event.get("repo").getAsJsonObject().get("name").getAsString();
            JsonObject payload = event.get("payload").getAsJsonObject();

            String message = switch (type) {
                case "CreateEvent" -> "- Created %s in %s".formatted(
                    payload.get("ref_type").getAsString(),
                    repoName
                );
                case "IssuesEvent" -> {
                    String action = payload.get("action").getAsString().toUpperCase();
                    yield "- %s an issue in %s".formatted(action, repoName);
                }
                case "PushEvent" -> "- Pushed %d commits in %s".formatted(payload.get("size").getAsInt(), repoName);
                case "WatchEvent" -> "- Starred %s".formatted(repoName);
                default -> "- %s in %s".formatted(
                    event.get("type").getAsString().replace("Event", ""),
                    repoName
                );
            };

            System.out.println(message);
        }
    }
}
