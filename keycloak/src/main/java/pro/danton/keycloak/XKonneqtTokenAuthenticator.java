package pro.danton.keycloak;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ModelException;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserProvider;

import jakarta.ws.rs.core.Response;

public class XKonneqtTokenAuthenticator implements Authenticator {

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        // Get email from header
        String email = context.getHttpRequest().getHttpHeaders().getHeaderString("X-Konneqt-Token");

        // Check if email was provided
        if (email == null || email.isBlank()) {
            context.failure(AuthenticationFlowError.INVALID_CREDENTIALS);
            return;
        }

        // Simple email validation
        if (!email.matches("^[^@]+@[^@]+\\.[^@]+$")) {
            Response challenge = context.form().setError("E-mail inválido")
                    .createErrorPage(Response.Status.BAD_REQUEST);
            context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challenge);
            return;
        }

        try {
            // Find or create user
            UserModel user = findOrCreateUser(context, email);

            // Set user to be autenticated
            context.setUser(user);

            context.success();
        } catch (ModelException e) {
            Response errorPage = context.form().setError(e.getMessage())
                    .createErrorPage(Response.Status.INTERNAL_SERVER_ERROR);
            context.failureChallenge(AuthenticationFlowError.INTERNAL_ERROR, errorPage);
        }
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        // authenticate(context);
        context.failure(AuthenticationFlowError.GENERIC_AUTHENTICATION_ERROR);
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        // Never called since configuredFor is always true and
        // XKonneqtTokenAuthenticatorFactory.isUserSetupAllowed() is false.
    }

    @Override
    public void close() {
    }

    private UserModel findOrCreateUser(AuthenticationFlowContext context, String email) {
        KeycloakSession session = context.getSession();
        RealmModel realm = context.getRealm();
        UserProvider users = session.users();

        UserModel user = users.getUserByEmail(realm, email);

        // It user does not exists, create the user.
        if (user == null) {
            user = users.addUser(realm, email);
            user.setCreatedTimestamp(System.currentTimeMillis());
            user.setEnabled(true);
            user.setEmail(email);
            user.setEmailVerified(true);

            var normalizedName = normalizeName(email.split("@")[0].replace(".", " "));
            var firstAndLastName = normalizedName.split(" ", 2);
            user.setFirstName(firstAndLastName[0]);
            user.setLastName(firstAndLastName.length > 1 ? firstAndLastName[1] : "null");

            // Add user to the user role
            user.grantRole(getOrAddRole(realm, "USER"));
        }

        return user;
    }

    private String normalizeName(String name) {
        return Arrays.asList(name.strip().split("\s+")).stream()
                .map(part -> part.substring(0, 1).toUpperCase() + part.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    private RoleModel getOrAddRole(RealmModel realm, String name) {
        var role = realm.getRole(name);
        return role != null ? role : realm.addRole(name);
    }
}