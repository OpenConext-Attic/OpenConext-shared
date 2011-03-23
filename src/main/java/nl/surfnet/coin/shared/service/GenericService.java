package nl.surfnet.coin.shared.service;

import java.util.Collection;
import java.util.List;

import nl.surfnet.coin.shared.domain.DomainObject;

import org.hibernate.Criteria;

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