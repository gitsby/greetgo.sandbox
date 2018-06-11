package kz.greetgo.sandbox.db.in_service.model;

public class CheckPoliceResponse {
  public PoliceStatus status;
  public String innWhoChecked;
  public String description;

  public CheckPoliceResponse() {}

  public CheckPoliceResponse(PoliceStatus status) {
    this.status = status;
  }

  @Override
  public String toString() {
    return "CheckPoliceResponse{" +
      "status=" + status +
      ", innWhoChecked='" + innWhoChecked + '\'' +
      ", description='" + description + '\'' +
      '}';
  }
}
