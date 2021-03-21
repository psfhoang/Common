package vn.siplab.medical.education.common.exception;

import lombok.Data;

@Data
public class BaseException extends RuntimeException {

  private int code;
  private Object data = null;

  public BaseException() {
  }

  public BaseException(int code, Throwable cause) {
    super(cause);
    this.code = code;
  }

  public BaseException(int code, String message) {
    super(message);
    this.code = code;
  }

  public BaseException(int code, String message, Object data) {
    super(message);
    this.code = code;
    this.data = data;
  }

  public BaseException(int code, String message, Throwable cause) {
    super(message, cause);
    this.code = code;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }


  public Object getData() {
    return data;
  }

  public void setData(Object data) {
    this.data = data;
  }
}
