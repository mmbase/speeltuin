package demo.spring;
import javax.jws.*;

@WebService(endpointInterface = "demo.spring.HelloWorld")
public class HelloWorldImpl implements HelloWorld {

    public String sayHi(@org.mmbase.util.functions.Type("requiredfield") String text) {
        return "Hello " + text;
    }
}
