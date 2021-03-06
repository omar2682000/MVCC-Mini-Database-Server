package com.atypon.database;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Database implements DatabaseInterface {
  private final File DATABASE_FILE;
  private final TransactionDataInterface TRANSACTION_DATA;
  private static final Long serialVersionUID = 5L;
  private Cache cache;

  public Database(File dbFile) {
    DATABASE_FILE = dbFile;
    TRANSACTION_DATA = new TransactionData();
      cache = new Cache(DATABASE_FILE);
  }

  @Override
  public File getDatabaseFile() {
    return DATABASE_FILE;
  }

  @Override
  public TransactionDataInterface getTransactionData() {
    return TRANSACTION_DATA;
  }

  @Override
  public void create(String name, Integer age) {
    DatabaseCreate createHandler = new Create(DATABASE_FILE, TRANSACTION_DATA);
    createHandler.create(name, age);
  }

  @Override
  public void delete(Integer id) {
    new Delete(DATABASE_FILE, TRANSACTION_DATA).delete(id);
  }

  @Override
  public PersonInterface read(Integer id) {
    try {
      Object cacheRead = cache.read(id);
      return (PersonInterface) cacheRead;
    } catch (NullPointerException nullPointerException) {
      DatabaseRead readHandler = new Read(DATABASE_FILE, TRANSACTION_DATA);
      return (PersonInterface) readHandler.read(id);
    }
  }

  @Override
  public List<Person> readAll() {
    DatabaseRead readHandler = new Read(DATABASE_FILE, TRANSACTION_DATA);
    List<Person> list = new ArrayList<>();
    for (Object obj : readHandler.readAll()) {
      list.add((Person) obj);
    }
    return list;
  }

  @Override
  public void updateName(Integer id, String newValue) {
    DatabaseUpdate updateHandler = new Update(DATABASE_FILE, TRANSACTION_DATA);
    updateHandler.updateName(id, newValue);
  }

  @Override
  public void updateAge(Integer id, Integer newValue) {
    DatabaseUpdate updateHandler = new Update(DATABASE_FILE, TRANSACTION_DATA);
    updateHandler.updateAge(id, newValue);
  }
}
