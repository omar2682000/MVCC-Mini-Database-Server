package com.atypon.database;

import com.atypon.files.FilesManager;
import com.atypon.files.ObjectReader;
import com.atypon.files.ObjectWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class Id implements IdFactoryInterface {
  private static final File ID_FILE = FilesManager.ID_FILE;

  private static synchronized void update(Integer value){
    var writer = new ObjectWriter();
    var list = new ArrayList<Object>();
    list.add(value);
    writer.writeNewList(ID_FILE, list);
  }

  @Override
  public Integer generateId() {
    AtomicReference<Integer> id = new AtomicReference<>(0);
    try  {
      var reader = new ObjectReader();
      id.set((Integer) reader.read(ID_FILE));
    } finally {
      update(id.get() +1);
    }
    return id.get();
  }
}