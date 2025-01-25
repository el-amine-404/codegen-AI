package com.amine.auth_service.Dto;

import java.util.Collection;

public record RegistrationRequest(
        String username,
        String password,
        Collection<String> roles
) {}