package com.activitytracker;

import com.activitytracker.enums.ResponseType;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiConnection {

    public <T> T getFromApi(String url, ResponseType responseType, Class<T> returnType) {
        URI uri = URI.create(url);
        try {
            HttpResponse<String> response;
            try (HttpClient client = HttpClient.newHttpClient()) {

                HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();

                response = client.send(request, HttpResponse.BodyHandlers.ofString());
            }
            if (responseType == ResponseType.GET_BODY) {

                return returnType.cast(response.body()) ;
            } else if (responseType == ResponseType.GET_CODE) {
                return returnType.cast(response.statusCode());
            } else {
                System.out.println("Response type not found");
                return null;
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
