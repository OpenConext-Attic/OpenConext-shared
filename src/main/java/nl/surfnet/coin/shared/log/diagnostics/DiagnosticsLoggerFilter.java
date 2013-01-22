package nl.surfnet.coin.shared.log.diagnostics;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * A {@link javax.servlet.Filter} that uses {@link ConextMDC} to log the aggregated diagnostic information, based on the outcome of the filter chain.
 * </p>
 * <ul>
 *   <li>
 *     If a RuntimeException was thrown, the information will be logged at error level.
 *   </li>
 *   <li>
 *     If something marked the diagnostic information as 'to be logged at warn' it will be logged at warn level.
 *   </li>
 *   <li>
 *     Otherwise it will be logged at trace level.
 *   </li>
 * </ul>
 *
 * Usage: include this filter in the servlet filter chain. Mind that only filters coming after this one, and the actual servlet being called, will be subject to the processing by this filter.
 *
 * @author Geert van der Ploeg
 */
public class DiagnosticsLoggerFilter implements Filter {

  Logger LOG = LoggerFactory.getLogger("nl.surfnet.coin.diagnostics");

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {

  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    try {
      chain.doFilter(request, response);
      if (ConextMDC.isMarkedForWarn()) {
        if (LOG.isWarnEnabled()) {
          LOG.warn("Logging diagnostics in warn level, as marked. {}", ConextMDC.toMessage());
        }
      } else {
        if (LOG.isTraceEnabled()) {
          LOG.trace(ConextMDC.toMessage());
        }
      }
    } catch (IOException ioe) {
      // Process the checked exception
      LOG.error(ConextMDC.toMessage());
      throw ioe;
    } catch (RuntimeException rte) {
      // Process unchecked exceptions
      LOG.error(ConextMDC.toMessage());
      throw rte;
    } finally {
      ConextMDC.cleanup();
    }
  }

  @Override
  public void destroy() {
    ConextMDC.cleanup();
  }
}
