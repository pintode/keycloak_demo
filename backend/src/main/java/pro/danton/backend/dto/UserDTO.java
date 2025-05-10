package pro.danton.backend.dto;

import java.util.List;

public record UserDTO(String email, String firstName, String lastName, List<String> roles) {
}