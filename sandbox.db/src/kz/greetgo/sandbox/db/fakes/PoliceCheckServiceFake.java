package kz.greetgo.sandbox.db.fakes;

import kz.greetgo.sandbox.db.in_service.PoliceCheckService;
import kz.greetgo.sandbox.db.in_service.model.CheckPoliceResponse;
import kz.greetgo.sandbox.db.in_service.model.LegalPersonCheckParams;
import kz.greetgo.sandbox.db.in_service.model.NaturalPersonCheckParams;
import kz.greetgo.sandbox.db.in_service.model.PoliceStatus;

public class PoliceCheckServiceFake implements PoliceCheckService {
  @Override
  public CheckPoliceResponse checkNaturalPerson(NaturalPersonCheckParams checkParams) {
    CheckPoliceResponse ret = new CheckPoliceResponse();
    ret.description = "asd dsa asd";
    ret.innWhoChecked = "4321543254";
    ret.status = PoliceStatus.GREEN;
    return ret;
  }

  @Override
  public CheckPoliceResponse checkLegalPerson(LegalPersonCheckParams checkParams) {
    CheckPoliceResponse ret = new CheckPoliceResponse();
    ret.description = "asd dsa asd";
    ret.innWhoChecked = "4321543254";
    ret.status = checkParams.name.contains("left") ? PoliceStatus.RED : PoliceStatus.GREEN;
    return ret;
  }
}
