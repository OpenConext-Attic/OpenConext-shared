/*
 * Copyright 2013 SURFnet bv, The Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.surfnet.coin.shared.log;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * JDBC implementation for ApiCallLogService
 * 
 */
public class ApiCallLogServiceImpl implements ApiCallLogService {

  private JdbcTemplate jdbcTemplate;

  public ApiCallLogServiceImpl(DataSource dataSource) {
    jdbcTemplate = new JdbcTemplate(dataSource);
  }

  public ApiCallLogServiceImpl(JdbcTemplate template) {
    jdbcTemplate = template;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.shared.log.ApiCallLogService#saveApiCallLog(nl.surfnet.
   * coin.shared.log.ApiCallLog)
   */
  @Override
  public void saveApiCallLog(ApiCallLog log) {
    Object[] args = new Object[] { log.getUserId(), log.getSpEntityId(), log.getIpAddress(), log.getApiVersion(),
        log.getResourceUrl(), log.getConsumerKey() };
    jdbcTemplate
        .update(
            "INSERT INTO api_call_log (user_id, spentity_id, ip_address, api_version, resource_url, consumer_key) VALUES  (?, ?, ?, ?, ?, ?)",
            args);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.shared.log.ApiCallLogService#findApiCallLog(java.lang.String
   * )
   */
  @Override
  public List<ApiCallLog> findApiCallLog(String serviceProvider) {
    return jdbcTemplate
        .query(
            "select user_id, spentity_id, ip_address, api_version, resource_url, consumer_key, log_timestamp from api_call_log where spentity_id = ?",
            new String[] { serviceProvider }, new RowMapper<ApiCallLog>() {

              @Override
              public ApiCallLog mapRow(ResultSet rs, int rowNum) throws SQLException {
                ApiCallLog log = new ApiCallLog();
                log.setApiVersion(rs.getString("api_version"));
                log.setConsumerKey(rs.getString("consumer_key"));
                log.setIpAddress(rs.getString("ip_address"));
                log.setResourceUrl(rs.getString("resource_url"));
                log.setSpEntityId(rs.getString("spentity_id"));
                log.setTimestamp(rs.getDate("log_timestamp"));
                log.setUserId(rs.getString("user_id"));
                return log;
              }
            });
  }

  /*
   * (non-Javadoc)
   * 
   * @see nl.surfnet.coin.shared.log.ApiCallLogService#findServiceProviders()
   */
  @Override
  public List<String> findServiceProviders() {
    return jdbcTemplate.query("select distinct spentity_id from api_call_log;", new RowMapper<String>() {
      @Override
      public String mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getString("spentity_id");
      }
    });
  }

}
