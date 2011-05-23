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
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.transaction.annotation.Transactional;

import nl.surfnet.coin.shared.domain.DomainObject;


/**
 * GenericService based on hibernate
 * 
 */
@Transactional
public class GenericServiceHibernateImpl<T extends DomainObject> implements GenericService<T> {

    @Autowired
    private SessionFactory portalSessionFactory;

    /*
     * The domainObject class
     */
    private Class<T> persistentClass;

    /**
     * Constructor
     * 
     * @param type the clazz
     */
    public GenericServiceHibernateImpl(Class<T> type) {
        this.persistentClass = type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see nl.surfnet.coin.service.GenericService#delete(nl.surfnet.coin.domain.DomainObject)
     */
    @Override
    public void delete(T o) {
        portalSessionFactory.getCurrentSession().delete(o);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * nl.surfnet.coin.service.GenericService#detachFromSession(nl.surfnet.coin.domain.DomainObject)
     */
    @Override
    public void detachFromSession(T o) {
        portalSessionFactory.getCurrentSession().evict(o);
    }

    /*
     * (non-Javadoc)
     * 
     * @see nl.surfnet.coin.service.GenericService#findAll()
     */
    @Override
    public List<T> findAll() {
        return findByCriteria();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * nl.surfnet.coin.service.GenericService#findByExample(nl.surfnet.coin.domain.DomainObject)
     */
    @Override
    public List<T> findByExample(T exampleInstance) {
        return findByExample(exampleInstance, new String[]{});
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * nl.surfnet.coin.service.GenericService#findByExample(nl.surfnet.coin.domain.DomainObject)
     */
    @Override
    public List<T> findByExample(T exampleInstance, String[] excludes) {
        Example create = Example.create(exampleInstance);
        for (String name : excludes) {
          create.excludeProperty(name);
        }
        return findByCriteria(create);
    }
    
    /**
     * Use this inside subclasses as a convenience method.
     */
    @SuppressWarnings("unchecked")
    protected List<T> findByCriteria(Criterion... criterion) {
        Criteria crit = portalSessionFactory.getCurrentSession().createCriteria(getPersistentClass());
        for (Criterion c : criterion) {
            crit.add(c);
        }
        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return crit.list();
    }
    
    
    
    /*
     * (non-Javadoc)
     * 
     * @see nl.surfnet.coin.service.GenericService#findById(java.lang.Long)
     */
    @SuppressWarnings("unchecked")
    @Override
    public T findById(Long id) {
        return (T) portalSessionFactory.getCurrentSession().load(getPersistentClass(), id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see nl.surfnet.coin.service.GenericService#getCount()
     */
    @Override
    public int getCount() {
        return DataAccessUtils.intResult(portalSessionFactory.getCurrentSession().createQuery("select count(*) from " + getPersistentClass().getName())
                .list());
    }

    /*
     * (non-Javadoc)
     * 
     * @see nl.surfnet.coin.service.GenericService#getPersistentClass()
     */
    @Override
    public Class<T> getPersistentClass() {
        return persistentClass;
    }

    /*
     * (non-Javadoc)
     * 
     * @see nl.surfnet.coin.service.GenericService#saveOrUpdate(nl.surfnet.coin.domain.DomainObject)
     */
    @Override
    public Long saveOrUpdate(T t) {
        portalSessionFactory.getCurrentSession().saveOrUpdate(t);
        return t.getId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see nl.surfnet.coin.service.GenericService#saveOrUpdate(nl.surfnet.coin.domain.DomainObject)
     */
    public void saveOrUpdate(Collection<T> coll) {
        for (T t : coll) {
          saveOrUpdate(t);
        }    
    }

    /**
     * @return the portalSessionFactory
     */
    protected Session getSession() {
      return portalSessionFactory.getCurrentSession();
    }

    /* (non-Javadoc)
     * @see nl.surfnet.coin.portal.service.GenericService#createCriteria()
     */
    @Override
    public Criteria createCriteria() {
      return portalSessionFactory.getCurrentSession().createCriteria(getPersistentClass());

    }
}
