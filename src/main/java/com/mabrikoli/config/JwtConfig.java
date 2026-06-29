package com.mabrikoli.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Type-safe binding for JWT configuration properties.
 *
 * <pre>
 *   app:
 *     jwt:
 *       secret: ...
 *       expiration-ms: 86400000
 *       refresh-expiration-ms: 604800000
 * </pre>
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.jwt")
public class JwtConfig {

    /**
     * Base64-encoded HMAC secret key.
     */
    private String secret;

    /**
     * Access-token lifetime in milliseconds (default: 24 h).
     */
    private long expirationMs = 86_400_000L;

    /**
     * Refresh-token lifetime in milliseconds (default: 7 days).
     */
    private long refreshExpirationMs = 604_800_000L;
}
