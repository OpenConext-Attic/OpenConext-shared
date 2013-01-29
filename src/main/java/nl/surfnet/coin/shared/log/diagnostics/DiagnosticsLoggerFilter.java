package nl.surfnet.coin.shared.log.diagnostics;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;

/**
 * <p>
 * A {@link javax.servlet.Filter} that uses {@link MDC} to log the aggregated diagnostic information, based on the outcome of the filter chain.
 * </p>
 * Usage: include this filter in the servlet filter chain. Mind that only filters coming after this one, and the actual servlet being called, will be subject to the processing by this filter.
 *
 * @author Geert van der Ploeg
 */
public class DiagnosticsLoggerFilter implements Filter {


  public static final String MEMORY_APPENDER = "MEMORY";
  private MemoryAppender<LoggingEvent> memoryAppender;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {

    // The cast will work only if SLF4J is bound with logback
    Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

    memoryAppender = (MemoryAppender<LoggingEvent>) root.getAppender(MEMORY_APPENDER);
    if (memoryAppender == null || !memoryAppender.isStarted()) {
      throw new ServletException("Cannot start DiagnosticsLoggerFilter, memoryAppender is not started yet. (does the logback configuration contain one?)");
    }
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

    HttpServletRequest req = (HttpServletRequest) request;

    /*
     * Use HttpSession's id as discriminator, if an HttpSession exists..
     * If no session available, do not create one (be not intrusive) but use a request-local random id
     */
    HttpSession session = req.getSession(false);
    String discriminator;
    if (session == null) {
      discriminator = UUID.randomUUID().toString();
    } else {
      discriminator = session.getId();
    }

    MDC.put(MemoryAppender.MDC_DISCRIMINATOR_FIELD, discriminator);

    try {
      chain.doFilter(request, response);

    } catch (IOException ioe) {

      // Process the checked exception
      memoryAppender.dump(discriminator);
      throw ioe;
    } catch (RuntimeException rte) {
      memoryAppender.dump(discriminator);
      throw rte;
    } finally {

      MDC.remove(MemoryAppender.MDC_DISCRIMINATOR_FIELD);
//      ConextMDC.cleanup();
    }
  }

  @Override
  public void destroy() {
  }
}
