package org.apereo.cas.multitenancy;

import org.apereo.cas.configuration.CasConfigurationProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This is {@link TenantDefinition}.
 *
 * @author Misagh Moayyed
 * @since 7.2.0
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@NoArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class TenantDefinition implements Serializable {
    @Serial
    private static final long serialVersionUID = -9012299259747093234L;

    private String id;

    private String description;

    private Map<String, Object> properties = new LinkedHashMap<>();

    private TenantAuthenticationPolicy authenticationPolicy = new DefaultTenantAuthenticationPolicy();

    private TenantCommunicationPolicy communicationPolicy = new DefaultTenantCommunicationPolicy();
    
    private TenantUserInterfacePolicy userInterfacePolicy = new DefaultTenantUserInterfacePolicy();

    private TenantDelegatedAuthenticationPolicy delegatedAuthenticationPolicy = new DefaultTenantDelegatedAuthenticationPolicy();

    /**
     * Bind properties to type.
     *
     * @param <T>   the type parameter
     * @param clazz the clazz
     * @return the optional
     */
    @JsonIgnore
    public <T> Optional<T> bindPropertiesTo(final Class<T> clazz) {
        return CasConfigurationProperties.bindFrom(properties, clazz);
    }

    /**
     * Bind properties to CAS properties.
     *
     * @return the optional
     */
    @JsonIgnore
    public Optional<CasConfigurationProperties> bindProperties() {
        return CasConfigurationProperties.bindFrom(properties);
    }
}
