package br.jus.tjap.precatorio.modelo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Response<M> implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonProperty("code")
    private Integer code = null;
    @JsonProperty("messages")
    private List<String> messages = null;
    @JsonProperty("result")
    private M result = null;
    @JsonProperty("status")
    private String status = null;

    public Response() {
    }

    public Response(Integer code, List<String> messages, M result, String status) {
        this.code = code;
        this.messages = messages;
        this.result = result;
        this.status = status;
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public List<String> getMessages() {
        return this.messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public M getResult() {
        return this.result;
    }

    public void setResult(M result) {
        this.result = result;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.code, this.messages, this.result, this.status});
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            Response other = (Response)obj;
            return Objects.equals(this.code, other.code) && Objects.equals(this.messages, other.messages) && Objects.equals(this.result, other.result) && Objects.equals(this.status, other.status);
        }
    }

    public String toString() {
        return "Response [code=" + this.code + ", messages=" + this.messages + ", result=" + this.result + ", status=" + this.status + "]";
    }
}
