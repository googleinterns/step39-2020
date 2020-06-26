import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.StringWriter;
import java.io.StringReader;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import junit.framework.TestCase;
import org.mockito.Mockito;

public class CreateOrUpdateUserlistServletTest extends TestCase {
  public void testDoPostSucceed() throws ServletException, IOException {
    HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
    HttpServletResponse res = Mockito.mock(HttpServletResponse.class);
    StringWriter writer = new StringWriter();
    StringReader reader = new StringReader(
        "{\"userId\": 1,\"userList\":{\"listId\":2,\"displayName\":\"List Name\",\"itemTypes\":[\"Butter\",\"Orange Juice\", \"Peanuts\"]}");
    CreateOrUpdateUserlistServlet servlet = new CreateOrUpdateUserlistServlet();

    Mockito.when(req.getMethod()).thenReturn("POST");
    Mockito.when(req.getReader()).thenReturn(new BufferedReader(reader));
    Mockito.when(res.getWriter()).thenReturn(new PrintWriter(writer));

    servlet.doPost(req, res);
    Mockito.verify(res).setStatus(HttpServletResponse.SC_OK);
    String result = writer.toString();
    assertTrue("should be valid json format", 
        result.startsWith("{") && result.endsWith("}"));
    assertTrue("Should contain list items", result.contains("\"Butter\",\"Orange Juice\",\"Peanuts\""));
  }

  public void testDoPostBadRequest() throws ServletException, IOException {
    HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
    HttpServletResponse res = Mockito.mock(HttpServletResponse.class);
    StringWriter writer = new StringWriter();
    StringReader reader = new StringReader("{\"bad response\" list:\"bread\"}");
    CreateOrUpdateUserlistServlet servlet = new CreateOrUpdateUserlistServlet();

    Mockito.when(req.getMethod()).thenReturn("POST");
    Mockito.when(req.getReader()).thenReturn(new BufferedReader(reader));
    Mockito.when(res.getWriter()).thenReturn(new PrintWriter(writer));

    servlet.doPost(req, res);

    Mockito.verify(res).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    String result = writer.toString();
    assertTrue("Should say invalid syntax", result.contains("Invalid request syntax."));
  }
}