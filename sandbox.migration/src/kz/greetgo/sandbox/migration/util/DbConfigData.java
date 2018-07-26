package kz.greetgo.sandbox.migration.util;

import kz.greetgo.sandbox.migration.model.ConfigModel;

public class DbConfigData {
 public ConfigModel TmpDb(){
    ConfigModel configModel =  new ConfigModel();
    configModel.url = "jdbc:postgresql://localhost/"+""+"_sandbox_tmp";
    configModel.user = ""+"_sandbox_tmp";
    configModel.password = "greetgo";
    return configModel;
 }

}
