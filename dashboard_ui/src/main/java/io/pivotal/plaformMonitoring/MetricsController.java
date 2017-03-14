package io.pivotal.plaformMonitoring;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MetricsController {
    @RequestMapping(method = RequestMethod.GET, value = "/")
    @ResponseBody
    public String firehoseLossRate() {
        return "<html>I am a ui</html>";
    }
}
