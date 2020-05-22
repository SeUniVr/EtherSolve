package main;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
public class GuiController {

    @GetMapping
    public String getRequest(){
        return "GUI";
    }
}
