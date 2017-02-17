package io.pivotal.plaformMonitoring.RestTest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by pivotal on 2/16/17.
 */

@RestController
public class Test {

    @RequestMapping("/greeting")
    public String greeting() {
        return "Hey!";
    }

}
