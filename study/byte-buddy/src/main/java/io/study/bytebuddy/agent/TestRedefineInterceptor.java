package io.study.bytebuddy.agent;

import net.bytebuddy.implementation.bind.annotation.*;

public class TestRedefineInterceptor {

    @RuntimeType
    public static Object intercept(@This Object obj,
                                    @Origin String method,
                                    @FieldValue("name") String name,
                                    @AllArguments Object[] args) throws Exception {
        return name + "(redefined)";
    }

}
