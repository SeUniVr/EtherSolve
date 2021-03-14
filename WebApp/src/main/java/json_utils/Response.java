package json_utils;

public class Response {
    private final String status;
    private final String message;
    private final Object result;

    public Response(String status, String message, Object result) {
        this.status = status;
        this.message = message;
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Object getResult() {
        return result;
    }
}
