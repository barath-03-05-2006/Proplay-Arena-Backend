package com.proplay.arena.dto.response;

import com.proplay.arena.entity.User;

public class AuthResponse {

    private String token;
    private UserResponse user;

    public AuthResponse() {}

    public AuthResponse(String token, UserResponse user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public UserResponse getUser() { return user; }
    public void setUser(UserResponse user) { this.user = user; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final AuthResponse r = new AuthResponse();
        public Builder token(String token) { r.token = token; return this; }
        public Builder user(UserResponse user) { r.user = user; return this; }
        public AuthResponse build() { return r; }
    }

    // ---- Inner class ----
    public static class UserResponse {
        private Long id;
        private String username;
        private String email;
        private User.Role role;

        public UserResponse() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public User.Role getRole() { return role; }
        public void setRole(User.Role role) { this.role = role; }

        public static Builder builder() { return new Builder(); }
        public static class Builder {
            private final UserResponse r = new UserResponse();
            public Builder id(Long id) { r.id = id; return this; }
            public Builder username(String username) { r.username = username; return this; }
            public Builder email(String email) { r.email = email; return this; }
            public Builder role(User.Role role) { r.role = role; return this; }
            public UserResponse build() { return r; }
        }
    }
}
