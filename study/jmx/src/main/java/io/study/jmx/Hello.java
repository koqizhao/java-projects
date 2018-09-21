package io.study.jmx;

public class Hello implements HelloMBean {
    
    private String _name;

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public void setName(String name) {
        _name = name;
    }

    @Override
    public String sayHello() {
        return String.format("Hello %s!", _name);
    }

}
