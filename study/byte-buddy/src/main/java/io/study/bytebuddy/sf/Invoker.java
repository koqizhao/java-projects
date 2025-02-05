package io.study.bytebuddy.sf;

public interface Invoker<Req, Resp> {
    Resp invoke(Req req);
}
