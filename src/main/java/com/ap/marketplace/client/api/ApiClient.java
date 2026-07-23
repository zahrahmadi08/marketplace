package com.ap.marketplace.client.api;

import com.ap.marketplace.client.Session;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * لایه پایه ارتباط با سرور روی HTTP + JSON.
 * توکن نشست خودکار در هدر Authorization قرار می‌گیرد. فقط این لایه به شبکه وصل است.
 */
public final class ApiClient {

    private static final String BASE_URL = "http://localhost:8080";
    private static final HttpClient HTTP = HttpClient.newHttpClient();
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private ApiClient() { }

    public static ObjectMapper mapper() { return MAPPER; }

    // ---- verbs ----

    public static <T> T get(String path, Class<T> type) {
        return read(exec(request(path).GET().build()), type);
    }

    public static <T> T get(String path, TypeReference<T> type) {
        return read(exec(request(path).GET().build()), type);
    }

    public static <T> T post(String path, Object body, Class<T> type) {
        return read(exec(request(path).POST(json(body)).build()), type);
    }

    public static <T> T post(String path, Object body, TypeReference<T> type) {
        return read(exec(request(path).POST(json(body)).build()), type);
    }

    /** POST بدون بدنه و بدون پاسخ (approve/block/...) . */
    public static void postNoContent(String path) {
        exec(request(path).POST(HttpRequest.BodyPublishers.noBody()).build());
    }

    public static <T> T put(String path, Object body, Class<T> type) {
        return read(exec(request(path).PUT(json(body)).build()), type);
    }

    public static void delete(String path) {
        exec(request(path).DELETE().build());
    }

    // ---- internals ----

    private static HttpRequest.Builder request(String path) {
        HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");
        if (Session.get().isLoggedIn()) {
            b.header("Authorization", "Bearer " + Session.get().token());
        }
        return b;
    }

    private static HttpRequest.BodyPublisher json(Object body) {
        try {
            return HttpRequest.BodyPublishers.ofString(MAPPER.writeValueAsString(body));
        } catch (Exception e) {
            throw new ApiClientException(0, "خطا در ساخت درخواست");
        }
    }

    /** ارسال و بررسی کد وضعیت؛ در صورت خطا پیام سرور استخراج و پرتاب می‌شود. */
    private static HttpResponse<String> exec(HttpRequest req) {
        try {
            HttpResponse<String> res = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() >= 400) {
                throw new ApiClientException(res.statusCode(), extractMessage(res.body(), res.statusCode()));
            }
            return res;
        } catch (ApiClientException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiClientException(0, "اتصال به سرور برقرار نشد. آیا Backend اجرا است؟");
        }
    }

    private static String extractMessage(String body, int status) {
        try {
            JsonNode node = MAPPER.readTree(body);
            if (node.hasNonNull("message")) return node.get("message").asText();
        } catch (Exception ignored) { }
        return "خطای سرور (" + status + ")";
    }

    private static <T> T read(HttpResponse<String> res, Class<T> type) {
        try {
            if (type == Void.class || res.body() == null || res.body().isBlank()) return null;
            return MAPPER.readValue(res.body(), type);
        } catch (Exception e) {
            throw new ApiClientException(0, "پاسخ سرور نامعتبر بود");
        }
    }

    private static <T> T read(HttpResponse<String> res, TypeReference<T> type) {
        try {
            return MAPPER.readValue(res.body(), type);
        } catch (Exception e) {
            throw new ApiClientException(0, "پاسخ سرور نامعتبر بود");
        }
    }
}
