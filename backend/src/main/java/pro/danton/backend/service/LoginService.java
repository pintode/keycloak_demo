package pro.danton.backend.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import pro.danton.backend.dto.LoginDTO;

@Service
public class LoginService {
    private static final Logger logger = Logger.getLogger(LoginService.class.getName());

    public static final String X_KONNEQT_TOKEN_HEADER = "X-Konneqt-Token";

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String KEYCLOAK_CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String KEYCLOAK_CLIENT_SECRET;

    @Value("${spring.security.oauth2.client.registration.keycloak.authorization-grant-type}")
    private String KEYCLOAK_AUTHORIZATION_GRANT_TYPE;

    @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
    private String KEYCLOAK_ISSUER_URI;

    public LoginDTO login(String email) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(KEYCLOAK_ISSUER_URI + "/protocol/openid-connect/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header(X_KONNEQT_TOKEN_HEADER, email)
                .POST(HttpRequest.BodyPublishers.ofString("" +
                        "grant_type=password" +
                        "&client_id=" + KEYCLOAK_CLIENT_ID +
                        "&client_secret=" + KEYCLOAK_CLIENT_SECRET))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.body());
            String accessToken = rootNode.get("access_token").asText();
            return new LoginDTO(accessToken, null, null);
        } else if (response.statusCode() == 400) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.body());
            var error = rootNode.get("error").asText();
            var errorDescription = rootNode.get("error_description").asText();
            return new LoginDTO(null, error, errorDescription);
        } else {
            logger.info("Login failed. status: " + response.statusCode() + ", body: " + response.body());
            return new LoginDTO(null, "unknown_error", "Unknown Error");
        }
    }
}