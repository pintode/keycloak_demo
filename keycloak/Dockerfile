FROM quay.io/keycloak/keycloak:26.2
WORKDIR /
COPY build/libs/keycloak-1.0.0.jar /opt/keycloak/providers/
RUN /opt/keycloak/bin/kc.sh build