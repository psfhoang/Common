package vn.siplab.medical.education.common.dto;

import java.io.Serializable;

public abstract class DTO implements Serializable, Cloneable {

  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException e) {
//      throw new DataException.CloneNotSupported();
      throw new RuntimeException() ;
    }
  }
}