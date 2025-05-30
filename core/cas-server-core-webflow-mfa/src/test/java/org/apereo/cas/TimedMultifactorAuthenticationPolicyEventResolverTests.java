package org.apereo.cas;

import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.MultifactorAuthenticationProvider;
import org.apereo.cas.authentication.mfa.TestMultifactorAuthenticationProvider;
import org.apereo.cas.util.MockRequestContext;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.apereo.cas.web.support.WebUtils;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.binding.expression.support.LiteralExpression;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.webflow.engine.Transition;
import org.springframework.webflow.engine.support.DefaultTargetStateResolver;
import org.springframework.webflow.engine.support.DefaultTransitionCriteria;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link TimedMultifactorAuthenticationPolicyEventResolverTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@TestPropertySource(properties = {
    "cas.authn.adaptive.policy.require-timed-multifactor[0].provider-id=mfa-dummy",
    "cas.authn.adaptive.policy.require-timed-multifactor[0].on-or-before-hour=-1",
    "cas.authn.adaptive.policy.require-timed-multifactor[0].on-or-after-hour=-1",
    "cas.authn.adaptive.policy.require-timed-multifactor[0].on-days=Saturday,Sunday,Monday,Tuesday,Wednesday,Thursday,Friday"
})
@Tag("WebflowEvents")
@Import(TimedMultifactorAuthenticationPolicyEventResolverTests.TimedMultifactorTestConfiguration.class)
class TimedMultifactorAuthenticationPolicyEventResolverTests extends BaseCasWebflowMultifactorAuthenticationTests {

    @Autowired
    @Qualifier("timedAuthenticationPolicyWebflowEventResolver")
    protected CasWebflowEventResolver resolver;

    private MockRequestContext context;

    @BeforeEach
    void initialize() throws Exception {
        this.context = MockRequestContext.create(applicationContext);
        context.setRemoteAddr("185.86.151.11").setLocalAddr("195.88.151.11").withUserAgent().setClientInfo();
        
        val targetResolver = new DefaultTargetStateResolver(TestMultifactorAuthenticationProvider.ID);
        val transition = new Transition(new DefaultTransitionCriteria(
            new LiteralExpression(TestMultifactorAuthenticationProvider.ID)), targetResolver);
        context.getRootFlow().getGlobalTransitionSet().add(transition);

        WebUtils.putServiceIntoFlowScope(context, CoreAuthenticationTestUtils.getWebApplicationService());
        TestMultifactorAuthenticationProvider.registerProviderIntoApplicationContext(applicationContext);
        WebUtils.putAuthentication(CoreAuthenticationTestUtils.getAuthentication(), context);
    }

    @Test
    void verifyOperationNeedsMfa() throws Throwable {
        val event = resolver.resolve(context);
        assertEquals(1, event.size());
        assertEquals(TestMultifactorAuthenticationProvider.ID, event.iterator().next().getId());
    }

    @TestConfiguration(value = "TimedMultifactorTestConfiguration", proxyBeanMethods = false)
    static class TimedMultifactorTestConfiguration {
        @Bean
        public MultifactorAuthenticationProvider dummyProvider() {
            return new TestMultifactorAuthenticationProvider();
        }
    }
}
