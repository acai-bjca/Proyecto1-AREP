/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apps;

/**
 *
 * @author amalia
 */
public class ResponseHTTP {
    private String error;
    private String message;
    private String content;
    private String archivo;

    public ResponseHTTP(String error, String message, String content) {
        this.error = error;
        this.message = message;
        this.content = content;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    
}
