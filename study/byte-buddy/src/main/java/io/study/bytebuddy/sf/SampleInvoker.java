package io.study.bytebuddy.sf;

public class SampleInvoker implements Invoker<String, String> {
    @Override
    public String invoke(String req) {
        return "Hello " + req;
    }
}
