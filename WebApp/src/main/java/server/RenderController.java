package server;

import com.google.gson.Gson;
import json_utils.RenderRequestBody;
import json_utils.Response;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import processing.GraphvizException;
import processing.GraphvizProcessor;

@RestController
@RequestMapping("/render")
public class RenderController {
    private final static Response GRAPHVIZ_ERROR = new Response("0",
            "A critical error occurred during graphviz render",
            null);
    private static final Gson gson = new Gson();

    @PostMapping(consumes = "application/json", produces = "application/json")
    public String postRequest(@RequestBody String requestBody){
        RenderRequestBody parsedRequestBody;
        parsedRequestBody = gson.fromJson(requestBody, RenderRequestBody.class);

        String dotNotation = parsedRequestBody.getDot();
        Response response;
        try {
            String svg = GraphvizProcessor.process(dotNotation);
            response = new Response("1", "Graphviz render completed", svg);
        } catch (GraphvizException e) {
            response = GRAPHVIZ_ERROR;
        }

        return gson.toJson(response);
    }
}
