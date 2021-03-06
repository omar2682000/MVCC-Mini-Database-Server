package com.atypon.api;

import com.atypon.database.PersonInterface;
import java.util.List;

public interface QueryInterface {

  Object commit();

  void updateAge(Integer id, Integer newValue);

  void updateName(Integer id, String newValue);

  void delete(Integer id);

  void create(String name, Integer age);

  List<PersonInterface> readAll();

  PersonInterface read(Integer id);

}
