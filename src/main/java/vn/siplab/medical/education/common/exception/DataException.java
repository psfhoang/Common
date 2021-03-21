package vn.siplab.medical.education.common.exception;


import vn.siplab.medical.education.common.msg.Msg;

public class DataException extends BaseException {

  private final static int ERROR_CODE = 600;

  protected DataException(int code, String message) {
    super(ERROR_CODE + code, message);
  }

  public DataException(String message) {
    super(ERROR_CODE, message);
  }

  public static class InvalidId extends DataException {

    public InvalidId(Long id) {
      super(1, Msg.getMessage("AbstractBaseService.invalidId", new Object[]{String.valueOf(id)}));
    }
  }

  public static class NotFoundEntityById extends DataException {

    public NotFoundEntityById(Long id, String entity) {
      super(2, Msg.getMessage("AbstractBaseService.notFoundEntityById", new Object[]{String.valueOf(id), entity}));
    }
  }

  public static class InvalidDateFormat extends DataException {

    public InvalidDateFormat(String date, String format) {
      super(3, Msg.getMessage("AbstractBaseService.invalidDateFormat", new Object[]{date, format}));
    }
  }

  public static class InvalidDataType extends DataException {

    public InvalidDataType(String columnName) {
      super(4, Msg.getMessage("AbstractBaseService.invalidDataType", new Object[]{columnName}));
    }
  }

  public static class ExistsForeignKeyConstraint extends DataException {

    public ExistsForeignKeyConstraint() {
      super(5, Msg.getMessage("AbstractBaseService.existsForeignKeyConstraint"));
    }
  }

  public static class NotExistsData extends DataException {

    public NotExistsData() {
      super(6, Msg.getMessage("AbstractBaseService.notExistsData"));
    }
  }

  public static class InvalidConstructor extends DataException {

    public InvalidConstructor(String name) {
      super(7, Msg.getMessage("AbstractBaseService.invalidConstructor", new Object[]{name}));
    }
  }

  public static class CloneNotSupported extends DataException {

    public CloneNotSupported() {
      super(8, Msg.getMessage("AbstractBaseService.cloneNotSupported"));
    }
  }
}
