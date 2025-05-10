package pro.danton.backend.dto;

public record LoginDTO(String accessToken, String error, String errorDescription) {
}