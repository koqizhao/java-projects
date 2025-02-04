package io.study.bytebuddy.agent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.utility.JavaModule;

import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.concurrent.Callable;

import static net.bytebuddy.implementation.MethodDelegation.to;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.named;

public class TestTransformer implements AgentBuilder.Transformer {
    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
        return builder.method(named("sayHello").and(isPublic())).intercept(to(TimingInterceptor.class));
    }

    public static class TimingInterceptor {
        @RuntimeType
        public static Object intercept(@Origin Method method, @SuperCall Callable<?> callable) throws Exception {
            long start = System.currentTimeMillis();
            try {
                return callable.call();
            } finally {
                System.out.println(method + " took " + (System.currentTimeMillis() - start));
            }
        }
    }
}
