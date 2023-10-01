package com.getjavajob.training.maksyutovs.socialnetwork.web.config;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

public class SecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {

    @Override
    protected boolean enableHttpSessionEventPublisher() {
        return true;
    }

}