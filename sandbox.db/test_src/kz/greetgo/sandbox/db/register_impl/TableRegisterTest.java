//package kz.greetgo.sandbox.db.register_impl;
//
//import kz.greetgo.depinject.core.BeanGetter;
//import kz.greetgo.sandbox.controller.errors.IllegalLoginOrPassword;
//import kz.greetgo.sandbox.controller.errors.NoAccountName;
//import kz.greetgo.sandbox.controller.errors.NoPassword;
//import kz.greetgo.sandbox.controller.errors.NotFound;
//import kz.greetgo.sandbox.controller.model.AuthInfo;
//import kz.greetgo.sandbox.controller.model.UserInfo;
//import kz.greetgo.sandbox.controller.register.AuthRegister;
//import kz.greetgo.sandbox.controller.register.model.SessionInfo;
//import kz.greetgo.sandbox.controller.register.model.UserParamName;
//import kz.greetgo.sandbox.controller.security.SecurityError;
//import kz.greetgo.sandbox.db.errors.RedPoliceResponse;
//import kz.greetgo.sandbox.db.in_service.model.CheckPoliceResponse;
//import kz.greetgo.sandbox.db.in_service.model.PoliceStatus;
//import kz.greetgo.sandbox.db.test.beans.PoliceCheckServiceForTests;
//import kz.greetgo.sandbox.db.test.dao.AuthTestDao;
//import kz.greetgo.sandbox.db.test.util.ParentTestNg;
//import kz.greetgo.util.RND;
//import org.testng.annotations.BeforeMethod;
//import org.testng.annotations.DataProvider;
//import org.testng.annotations.Test;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.fest.assertions.api.Assertions.assertThat;
//
///**
//
//public class TableRegisterTest {
//}
