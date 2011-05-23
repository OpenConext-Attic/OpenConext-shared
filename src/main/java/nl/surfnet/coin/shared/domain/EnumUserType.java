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

package nl.surfnet.coin.shared.domain;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

/**
 * 
 * {@link UserType} for enum's. Mainly introduced to be able store the actual
 * description of an Enum in the database instead of the enum#value
 */
public class EnumUserType<E extends Enum<E>> implements UserType {
  /*
   * Underlying enum class
   */
  private Class<E> clazz = null;

  /*
   * Constructor
   */
  protected EnumUserType(Class<E> c) {
    this.clazz = c;
  }

  /*
   * We store the String representation
   */
  private static final int[] SQL_TYPES = { Types.VARCHAR };

  /*
   * (non-Javadoc)
   * 
   * @see org.hibernate.usertype.UserType#sqlTypes()
   */
  public int[] sqlTypes() {
    return SQL_TYPES;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.hibernate.usertype.UserType#returnedClass()
   */
  public Class<E> returnedClass() {
    return clazz;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.hibernate.usertype.UserType#nullSafeGet(java.sql.ResultSet,
   * java.lang.String[], java.lang.Object)
   */
  public Object nullSafeGet(ResultSet resultSet, String[] names, Object owner)
      throws HibernateException, SQLException {
    String name = resultSet.getString(names[0]);
    E result = null;
    if (!resultSet.wasNull()) {
      result = Enum.valueOf(clazz, name);
    }
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.hibernate.usertype.UserType#nullSafeSet(java.sql.PreparedStatement,
   * java.lang.Object, int)
   */
  public void nullSafeSet(PreparedStatement preparedStatement, Object value,
      int index) throws HibernateException, SQLException {
    if (null == value) {
      preparedStatement.setNull(index, Types.VARCHAR);
    } else {
      preparedStatement.setString(index, ((Enum<?>) value).name());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.hibernate.usertype.UserType#deepCopy(java.lang.Object)
   */
  public Object deepCopy(Object value) throws HibernateException {
    return value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.hibernate.usertype.UserType#isMutable()
   */
  public boolean isMutable() {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.hibernate.usertype.UserType#assemble(java.io.Serializable,
   * java.lang.Object)
   */
  public Object assemble(Serializable cached, Object owner)
      throws HibernateException {
    return cached;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.hibernate.usertype.UserType#disassemble(java.lang.Object)
   */
  public Serializable disassemble(Object value) throws HibernateException {
    return (Serializable) value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.hibernate.usertype.UserType#replace(java.lang.Object,
   * java.lang.Object, java.lang.Object)
   */
  public Object replace(Object original, Object target, Object owner)
      throws HibernateException {
    return original;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.hibernate.usertype.UserType#hashCode(java.lang.Object)
   */
  public int hashCode(Object x) throws HibernateException {
    return (x == null) ? 0 : x.hashCode();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.hibernate.usertype.UserType#equals(java.lang.Object,
   * java.lang.Object)
   */
  public boolean equals(Object x, Object y) throws HibernateException {
    if (x == y)
      return true;
    if (null == x || null == y)
      return false;
    return x.equals(y);
  }
}
