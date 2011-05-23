/*
 * Copyright 2011 SURFnet bv, The Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.surfnet.coin.shared.service;

import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;

import nl.surfnet.coin.shared.domain.DomainObject;

/**
 * 
 * Generic service class for CRUD functionality for {@link DomainObject}
 * 
 * 
 */
public interface GenericService<T extends DomainObject> {

  Long saveOrUpdate(T t);

  /**
   * Find by primary key
   * 
   * @param id
   *          the entity primary key
   * @return the domainObject
   */
  T findById(Long id);

  void delete(T o);

  List<T> findAll();

  List<T> findByExample(T exampleInstance);

  Class<T> getPersistentClass();

  void detachFromSession(T o);

  int getCount();

  void saveOrUpdate(Collection<T> coll);

  Criteria createCriteria();
 
  List<T> findByExample(T exampleInstance, String[] excludes);
}