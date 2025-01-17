package com.github.splendor_mobile_game.websocket.response;

import java.util.UUID;

import com.google.gson.Gson;

public class ErrorResponse {

    private class Data {
        @SuppressWarnings("unused")
        String error;

        public Data(String error) {
            this.error = error;
        }
    }

    public String messageContextId;
    public String type;
    public String result;
    public Data data;

    public ErrorResponse(Result result, String error, ResponseType responseType, String messageContextId) {
        this.messageContextId = messageContextId;
        this.type = responseType.toString();
        this.result = result.name();
        this.data = new Data(error);
    }

    public ErrorResponse(Result result, String error, ResponseType responseType) {
        this(result, error, responseType, UUID.randomUUID().toString());
    }

    public ErrorResponse(Result result, String error) {
        this(result, error, ResponseType.UNKNOWN);
    }

    public String ToJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
