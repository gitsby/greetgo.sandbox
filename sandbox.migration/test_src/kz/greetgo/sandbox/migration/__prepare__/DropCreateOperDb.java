package kz.greetgo.sandbox.migration.__prepare__;

import kz.greetgo.sandbox.migration.__prepare__.core.DbWorker;
import kz.greetgo.sandbox.migration.__prepare__.db.oper.OperDDL;
import kz.greetgo.sandbox.migration.util.ConfigFiles;

public class DropCreateOperDb {
  public static void main(String[] args) throws Exception {
    DbWorker dbWorker = new DbWorker();

    dbWorker.prepareConfigFiles();

    dbWorker.dropOperDb();
    dbWorker.createOperDb();

    dbWorker.applyDDL(ConfigFiles.operDb(), OperDDL.get());
  }
}
