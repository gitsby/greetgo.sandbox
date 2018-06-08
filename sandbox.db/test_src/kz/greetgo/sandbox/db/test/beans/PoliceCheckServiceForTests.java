package kz.greetgo.sandbox.db.test.beans;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.db.in_service.PoliceCheckService;
import kz.greetgo.sandbox.db.in_service.model.CheckPoliceResponse;
import kz.greetgo.sandbox.db.in_service.model.LegalPersonCheckParams;
import kz.greetgo.sandbox.db.in_service.model.NaturalPersonCheckParams;
import org.fest.assertions.api.Assertions;

import java.util.ArrayList;
import java.util.List;

@Bean
public class PoliceCheckServiceForTests implements PoliceCheckService {

  public void clean() {
    checkNaturalPerson_input.clear();
    checkNaturalPerson_out.clear();
  }

  public final List<NaturalPersonCheckParams> checkNaturalPerson_input = new ArrayList<>();
  public final List<CheckPoliceResponse> checkNaturalPerson_out = new ArrayList<>();

  @Override
  public CheckPoliceResponse checkNaturalPerson(NaturalPersonCheckParams checkParams) {
    checkNaturalPerson_input.add(checkParams);
    if (checkNaturalPerson_out.isEmpty()) Assertions.fail("No data to return");
    return checkNaturalPerson_out.remove(0);
  }

  @Override
  public CheckPoliceResponse checkLegalPerson(LegalPersonCheckParams checkParams) {
    throw new UnsupportedOperationException();
  }
}
