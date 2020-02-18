package hu.bsstudio.raktr.service;

import org.springframework.stereotype.Service;

@Service
public class DeviceService {

    public static final String HELLO_WORLD = "Hello world!";

    public String helloWorld(){
        return HELLO_WORLD;
    }

    public String helloWorld(String name) {
        return "Hello " + name + "!";
    }

}
