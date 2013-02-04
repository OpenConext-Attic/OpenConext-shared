package nl.surfnet.coin.shared.log.diagnostics;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * <p>
 * A {@link javax.servlet.Filter} that uses {@link MDC} to log the aggregated diagnostic information, based on the outcome of the filter chain.
 * </p>
 * Usage: include this filter in the servlet filter chain. Mind that only filters coming after this one, and the actual servlet being called, will be subject to the processing by this filter.
 *
 * @author Geert van der Ploeg
 */
public class DiagnosticsLoggerFilter implements Filter {

  private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(DiagnosticsLoggerFilter.class);

  public static final String MEMORY_APPENDER = "MEMORYAPPENDER";
  public static final String MEMORY_LOGGER = "MEMORYLOGGER";
  private static final String SESSION_ATTRIBUTE_LOG_EVENTS_BUFFER = "nl.surfnet.coin.shared.log.diagnostics.LOG_EVENTS_BUFFER";
  private MemoryAppender memoryAppender;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {

    // The cast will work only if SLF4J is bound with logback
    Logger memoryLogger = (Logger) LoggerFactory.getLogger(MEMORY_LOGGER);

    memoryAppender = (MemoryAppender) memoryLogger.getAppender(MEMORY_APPENDER);
    if (memoryAppender == null || !memoryAppender.isStarted()) {
      throw new ServletException("Cannot start DiagnosticsLoggerFilter, memoryAppender is not started yet. (does the logback configuration contain one?)");
    }
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

    HttpServletRequest req = (HttpServletRequest) request;
    HttpServletResponse res = (HttpServletResponse) response;

    StatusExposingResponse wrappedResponse = new StatusExposingResponse(res);

    /*
     * Use HttpSession's id as discriminator, if an HttpSession exists..
     * If no session available, do not create one (be not intrusive) but use a request-local random id
     */
    HttpSession session = req.getSession(false);
    String discriminator;
    if (session == null) {
      discriminator = UUID.randomUUID().toString();
      LOG.debug("No HttpSession available, will use request-local, random discriminator: {}", discriminator);
    } else {
      discriminator = session.getId();
    }

    MDC.put(MemoryAppender.MDC_DISCRIMINATOR_FIELD, discriminator);

    try {
      chain.doFilter(request, wrappedResponse);

      if (wrappedResponse.getStatus() >=400) {
        LOG.debug("Response has error status ({}), will dump aggregated log events", wrappedResponse.getStatus());
        dumpAggregatedLogEvents(discriminator, req);
      } else {
        if (session != null) {
          // Store buffered logging events in session, if session available
          List<ILoggingEvent> loggingEvents = memoryAppender.getBuffer(discriminator);
          LOG.debug("Request processed successfully. Will save {} loggingEvents from this request in session", loggingEvents.size());
          if (session.getAttribute(SESSION_ATTRIBUTE_LOG_EVENTS_BUFFER) != null) {
            // Merge with previous requests' buffer
            ((List<ILoggingEvent>) session.getAttribute(SESSION_ATTRIBUTE_LOG_EVENTS_BUFFER)).addAll(loggingEvents);
          } else {
            session.setAttribute(SESSION_ATTRIBUTE_LOG_EVENTS_BUFFER, loggingEvents);
          }
        }
      }
    } catch (IOException ioe) {
      LOG.debug("Caught (checked) IOException (msg: {}), will dump aggregated log events", ioe.getMessage());
      dumpAggregatedLogEvents(discriminator, req);
      throw ioe;
    } catch (RuntimeException rte) {
      LOG.debug("Caught RTE (msg: {}), will dump aggregated log events", rte.getMessage());
      dumpAggregatedLogEvents(discriminator, req);
      throw rte;
    } finally {
      MDC.remove(MemoryAppender.MDC_DISCRIMINATOR_FIELD);
    }
  }

  private void dumpAggregatedLogEvents(String discriminator, HttpServletRequest req) {
    HttpSession session = req.getSession(false);
    if (session != null) {
      memoryAppender.dumpExternal((List<ILoggingEvent>) session.getAttribute(SESSION_ATTRIBUTE_LOG_EVENTS_BUFFER));
      session.removeAttribute(SESSION_ATTRIBUTE_LOG_EVENTS_BUFFER);
    }
    memoryAppender.dump(discriminator);
  }

  @Override
  public void destroy() {
  }

  /**
   * Wrapper for HttpServletResponse that overrides all methods that write its status code, to expose the status to users.
   * Can be eliminated once Servlet 3.0 is used (which has a native HttpServletResponse#getStatus())
   *
   */
  public static class StatusExposingResponse extends HttpServletResponseWrapper {

    private int httpStatus = SC_OK;

    /**
     * Constructs a response adapter wrapping the given response.
     *
     * @throws IllegalArgumentException
     *          if the response is null
     */
    public StatusExposingResponse(HttpServletResponse response) {
      super(response);
    }

    public int getStatus() {
      return httpStatus;
    }

    @Override
    public void sendError(int sc) throws IOException {
      httpStatus = sc;
      super.sendError(sc);
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
      httpStatus = sc;
      super.sendError(sc, msg);
    }


    @Override
    public void setStatus(int sc) {
      httpStatus = sc;
      super.setStatus(sc);
    }

    @Override
    public void reset() {
      super.reset();
      this.httpStatus = SC_OK;
    }
  }
}
