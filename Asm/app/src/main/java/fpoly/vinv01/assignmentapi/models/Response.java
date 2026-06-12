package fpoly.vinv01.assignmentapi.models;

public class Response<T> {
    private int status;
    private String message;
    private T data;

    public Response(T data, int status, String message) {
        this.data = data;
        this.status = status;
        this.message = message;
    }

    public Response() {
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
