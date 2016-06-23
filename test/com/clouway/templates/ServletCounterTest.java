package com.clouway.templates;

import com.clouway.links.ServletAccessCounter;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsString;

/**
 * @author Stanislava Kaukova(sisiivanovva@gmail.com)
 */
public class ServletCounterTest {
  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private HttpServletRequest request = context.mock(HttpServletRequest.class);
  private HttpServletResponse response = context.mock(HttpServletResponse.class);

  private PrintWriter writer;
  private ByteArrayOutputStream outputStream;

  @Before
  public void setUp() throws Exception {
    outputStream = new ByteArrayOutputStream();
    writer = new PrintWriter(outputStream);
  }

  @After
  public void tearDown() throws Exception {
    outputStream.close();
    writer.close();
  }

  @Test
  public void happyPath() throws Exception {
    ServletAccessCounter counter = new ServletAccessCounter();

    context.checking(new Expectations() {{
      oneOf(request).getParameter("link");
      will(returnValue(("https://github.com/clouway")));

      oneOf(response).setContentType("text/html");

      oneOf(response).getWriter();
      will(returnValue(writer));
    }});

    counter.doGet(request, response);

    String actual = counter.getHtml("web/WEB-INF/links.html", writer);

    assertThat(actual, containsString(" This link is accessed: 1 times"));
  }

  @Test
  public void multipleLinkVisits() throws Exception {
    ServletAccessCounter counter = new ServletAccessCounter();

    context.checking(new Expectations() {{
      oneOf(request).getParameter("link");
      will(returnValue(("https://github.com/clouway")));

      oneOf(response).setContentType("text/html");

      oneOf(response).getWriter();
      will(returnValue(writer));

      oneOf(request).getParameter("link");
      will(returnValue(("https://github.com/clouway")));

      oneOf(response).setContentType("text/html");

      oneOf(response).getWriter();
      will(returnValue(writer));
    }});

    counter.doGet(request, response);
    counter.doGet(request, response);

    String actual = counter.getHtml("web/WEB-INF/links.html", writer);

    assertThat(actual, containsString(" This link is accessed: 2 times"));
  }

  @Test
  public void notFoundResource() throws Exception {
    ServletAccessCounter counter = new ServletAccessCounter();
    String actual = counter.getHtml("asasasas", writer);

    assertThat(actual, is(equalTo("")));
  }
}
