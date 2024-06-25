package org.apereo.cas.config;

import org.apereo.cas.authentication.CasSSLContext;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.features.CasFeatureModule;
import org.apereo.cas.pac4j.web.DelegatedClientOidcBuilder;
import org.apereo.cas.pac4j.web.DelegatedClientOidcSessionManager;
import org.apereo.cas.pac4j.web.DelegatedClientsOidcEndpointContributor;
import org.apereo.cas.support.pac4j.authentication.clients.ConfigurableDelegatedClientBuilder;
import org.apereo.cas.support.pac4j.authentication.clients.DelegatedClientSessionManager;
import org.apereo.cas.support.pac4j.authentication.clients.DelegatedClientsEndpointContributor;
import org.apereo.cas.util.spring.boot.ConditionalOnFeatureEnabled;
import org.apereo.cas.web.flow.DelegatedClientAuthenticationConfigurationContext;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ScopedProxyMode;

/**
 * This is {@link DelegatedAuthenticationOidcConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 7.1.0
 */
@ConditionalOnFeatureEnabled(feature = CasFeatureModule.FeatureCatalog.DelegatedAuthentication, module = "oidc")
@Configuration(value = "DelegatedAuthenticationOidcConfiguration", proxyBeanMethods = false)
@EnableConfigurationProperties(CasConfigurationProperties.class)
class DelegatedAuthenticationOidcConfiguration {
    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    @ConditionalOnMissingBean(name = "delegatedClientsOidcEndpointContributor")
    public DelegatedClientsEndpointContributor delegatedClientsOidcEndpointContributor() {
        return new DelegatedClientsOidcEndpointContributor();
    }


    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    @ConditionalOnMissingBean(name = "delegatedOidcClientBuilder")
    public ConfigurableDelegatedClientBuilder delegatedOidcClientBuilder(
        @Qualifier(CasSSLContext.BEAN_NAME) final CasSSLContext casSslContext,
        final CasConfigurationProperties casProperties) {
        return new DelegatedClientOidcBuilder(casSslContext, casProperties);
    }

    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    @ConditionalOnMissingBean(name = "delegatedClientOidcSessionManager")
    public DelegatedClientSessionManager delegatedClientOidcSessionManager(
        @Qualifier(DelegatedClientAuthenticationConfigurationContext.BEAN_NAME)
        final ObjectProvider<DelegatedClientAuthenticationConfigurationContext> contextProvider) {
        return new DelegatedClientOidcSessionManager(contextProvider);
    }

}
