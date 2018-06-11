package kz.greetgo.sandbox.db.in_service.impl;

import kz.greetgo.sandbox.db.in_service.PoliceCheckService;
import kz.greetgo.sandbox.db.in_service.model.CheckPoliceResponse;
import kz.greetgo.sandbox.db.in_service.model.LegalPersonCheckParams;
import kz.greetgo.sandbox.db.in_service.model.NaturalPersonCheckParams;
import sun.net.www.http.HttpClient;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;

public class PoliceCheckServiceReal implements PoliceCheckService {

  private final String url;
  private final String username;
  private final String password;

  public PoliceCheckServiceReal(String url, String username, String password) {
    this.url = url;
    this.username = username;
    this.password = password;
  }

  @Override
  public CheckPoliceResponse checkNaturalPerson(NaturalPersonCheckParams checkParams) {

    try {
      HttpClient client = createHttpClient();

      try (PrintStream pr = new PrintStream(client.getOutputStream(), false, "UTF-8")) {
        pr.println("<request>" +
          "<check username=\"" + username + "\" password=\"" + password + "\"/>" +
          "<type>Natural</type>" +
          "<surname>" + checkParams.surname);
        //.....
      }

      return convertResponse(client);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }


  }

  private CheckPoliceResponse convertResponse(HttpClient client) {
    CheckPoliceResponse ret = new CheckPoliceResponse();
    OutputStream outputStream = client.getOutputStream();
    System.out.println(outputStream);
    //outputStream --> ret
    return ret;
  }

  @Override
  public CheckPoliceResponse checkLegalPerson(LegalPersonCheckParams checkParams) {
    try {
      HttpClient client = createHttpClient();

      try (PrintStream pr = new PrintStream(client.getOutputStream(), false, "UTF-8")) {
        pr.println("<request><type>Legal</type><name>" + checkParams.name);
        //.....
      }

      return convertResponse(client);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private HttpClient createHttpClient() throws IOException {

    //noinspection UnnecessaryLocalVariable
    HttpClient client = new HttpClient(new URL(url), null, 123);
    ///// put username,password into client

    return client;
  }

  public static void main(String[] args) {
    PoliceCheckServiceReal real = new PoliceCheckServiceReal("https://api/police.kz/checkers", "asd", "secret");

    NaturalPersonCheckParams checkParams = new NaturalPersonCheckParams();
    checkParams.surname = "asd";
    CheckPoliceResponse policeResponse = real.checkNaturalPerson(checkParams);
    System.out.println(policeResponse);
  }
}
