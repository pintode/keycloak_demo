package pro.danton.backend.service;

import java.util.List;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.shaded.gson.internal.LinkedTreeMap;

import pro.danton.backend.dto.UserDTO;

@Service
public class UserService {

    public UserDTO getInfo(Jwt jwt) {
        String email = jwt.getClaim("email");
        String firstName = jwt.getClaim("given_name");
        String lastName = jwt.getClaim("family_name");
        LinkedTreeMap<String, List<String>> realmAccessRoles = jwt.getClaim("realm_access");
        List<String> roles = realmAccessRoles.get("roles");

        return new UserDTO(email, firstName, lastName, roles);
    }
}