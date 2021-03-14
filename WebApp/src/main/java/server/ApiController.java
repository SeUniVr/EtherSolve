package server;

import com.google.gson.Gson;
import json_utils.PostRequestBody;
import json_utils.Response;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import processing.AddressProcessor;
import processing.BytecodeProcessor;

import java.util.concurrent.atomic.AtomicInteger;


@RestController
@RequestMapping("/api")
public class ApiController {
    private static final AtomicInteger CONTACT_COUNTER = new AtomicInteger(0);
    private static final Gson gson = new Gson();
    private static final Response MISSING_INVALID_PARAMS_ERROR = new Response("0",
            "Missing/invalid argument. Request needs to have at least: \"address\" (String) with no other arguments or \"bytecode\" (String) with \"isOnlyRuntime\" (Boolean)",
            null);

    /*                              EXAMPLE

    @GetMapping(value = "/api")
    public String getRequest(@RequestParam(required = false) String address,
                             @RequestParam(defaultValue = "Contract") String name){
        return "<h1>API</h1>Address: " + address + "<br>Name: " + name;
    }*/

    @GetMapping
    public String getRequest(@RequestParam String address, @RequestParam(required = false) String name){
        Response response;
        if (name == null)
            name = "Contract" + CONTACT_COUNTER.getAndIncrement();
        response = AddressProcessor.process(name, address);
        return gson.toJson(response);
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public String postRequest(@RequestBody String requestBody){
        PostRequestBody parsedRequestBody;
        parsedRequestBody = gson.fromJson(requestBody, PostRequestBody.class);
        // System.out.println(String.format("address:%s\nbytecode:%s\nisOnlyRuntime%s\n", parsedRequestBody.getAddress(), parsedRequestBody.getBytecode(), parsedRequestBody.isOnlyRuntime()));
        Response response;

        String name;
        if (parsedRequestBody.getName() == null)
            name = "Contract" + CONTACT_COUNTER.getAndIncrement();
        else
            name = parsedRequestBody.getName();

        if (parsedRequestBody.getAddress() != null)
            response = AddressProcessor.process(name, parsedRequestBody.getAddress());
        else if (parsedRequestBody.getBytecode() != null)
            response = BytecodeProcessor.process(name, parsedRequestBody.getBytecode(), parsedRequestBody.isOnlyRuntime());
        else
            response = MISSING_INVALID_PARAMS_ERROR;
        return gson.toJson(response);
    }

    @ExceptionHandler({MissingServletRequestParameterException.class, IllegalStateException.class})
    public String requestFormatError() {
        return gson.toJson(MISSING_INVALID_PARAMS_ERROR);
    }
}

