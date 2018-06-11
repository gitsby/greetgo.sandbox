package kz.greetgo.sandbox.db.in_service;

import kz.greetgo.sandbox.db.in_service.model.CheckPoliceResponse;
import kz.greetgo.sandbox.db.in_service.model.LegalPersonCheckParams;
import kz.greetgo.sandbox.db.in_service.model.NaturalPersonCheckParams;

public interface PoliceCheckService {

  CheckPoliceResponse checkNaturalPerson(NaturalPersonCheckParams checkParams);

  CheckPoliceResponse checkLegalPerson(LegalPersonCheckParams checkParams);

}
