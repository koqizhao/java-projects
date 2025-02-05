package io.study.bytebuddy.sf;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;

import static net.bytebuddy.matcher.ElementMatchers.is;

public class App {

    public static void main(String[] args) throws Exception {
        SampleService service = new SampleService(new SampleInvoker());
        System.out.println("service started, press any key to stop\n");
        service.start();

        Thread.sleep(5000);
        var transformer = hack();

        Thread.sleep(5000);
        unhack(transformer);

        System.in.read();
        service.stop();
        System.out.println("\nservice stopped");
    }

    private static ResettableClassFileTransformer hack() {
        ByteBuddyAgent.install();
        System.out.println("\nhack done\n");
        return new AgentBuilder.Default()
                .type(is(SampleInvoker.class))
                .transform(new SampleTransformer2())
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .with(AgentBuilder.InitializationStrategy.NoOp.INSTANCE)
                .with(AgentBuilder.TypeStrategy.Default.REDEFINE)
                .with(new io.study.bytebuddy.agent.App.Listener())
                .with(new io.study.bytebuddy.agent.App.InstallationListener())
                .installOn(ByteBuddyAgent.getInstrumentation());
    }

    private static void unhack(ResettableClassFileTransformer resettableClassFileTransformer)  {
        resettableClassFileTransformer
                //.reset(ByteBuddyAgent.getInstrumentation(), AgentBuilder.RedefinitionStrategy.REDEFINITION);
                .reset(ByteBuddyAgent.getInstrumentation(), AgentBuilder.RedefinitionStrategy.DISABLED);
        System.out.println("\nunhack done\n");
    }
}
