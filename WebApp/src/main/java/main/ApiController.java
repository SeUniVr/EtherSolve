package main;

import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api")
public class ApiController {

    /*                              EXAMPLE

    @GetMapping(value = "/api")
    public String getRequest(@RequestParam(required = false) String address,
                             @RequestParam(defaultValue = "Contract") String name){
        return "<h1>API</h1>Address: " + address + "<br>Name: " + name;
    }*/

    @GetMapping
    public String getRequest(@RequestParam String address, @RequestParam(required = false) String name){
        return "{result:\"Banana\"}";
    }

    @PostMapping
    public String postRequest(@RequestParam Map<String, String> params){
        return "POST";
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public String exceptionHandler(){
        return "error";
    }
}

