package io.study.bytebuddy.sf;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;

import java.security.ProtectionDomain;

import static net.bytebuddy.implementation.MethodDelegation.to;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.named;

public class SampleTransformer2 implements AgentBuilder.Transformer {
    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
        return builder.method(named("invoke").and(isPublic())).intercept(to(TimingInterceptor.class));
    }

    public static class TimingInterceptor {
        public static String intercept(String req) {
            return "Hello, " + req + "(from interceptor)";
        }
    }
}
