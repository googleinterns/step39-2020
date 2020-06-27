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

public class GetUserListsServletTest extends TestCase {
  private static final String CORRECT_REQUEST_STRING = "{\"userId\": 1}";
  private static final String INCORRECT_REQUEST_STRING = "{\"bad response\" list:\"bread\"}";

  private class SetupObj {
    HttpServletRequest request;
    HttpServletResponse response;
    StringWriter writer;

    public SetupObj(HttpServletRequest request, HttpServletResponse response, 
        StringWriter writer) {
      this.request = request;
      this.response = response;
      this.writer = writer;
    }
  }

  public void testDoPostSucceed() throws ServletException, IOException {
    SetupObj setupObj = setupMockData(CORRECT_REQUEST_STRING);

    CreateOrUpdateUserlistServlet servlet = new GetUserListsServlet();
    servlet.doPost(setupObj.request, setupObj.response);

    Mockito.verify(setupObj.response).setStatus(HttpServletResponse.SC_OK);
    String result = setupObj.writer.toString();
    assertTrue("should be valid json format", 
        result.startsWith("{") && result.endsWith("}"));
    assertTrue("Should contain userLists object", result.contains("UserLists:"));
  }

  public void testDoPostBadRequest() throws ServletException, IOException {
    SetupObj = setupMockData(INCORRECT_REQUEST_STRING);

    CreateOrUpdateUserlistServlet servlet = new GetUserListsServlet();
    servlet.doPost(setupObj.request, setupObj.response);

    Mockito.verify(setupObj.response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    String result = setupObj.writer.toString();
    assertTrue("Should say invalid syntax", result.contains("Invalid request syntax."));
  }

  public SetupObj setupMockData(String reqString) {
    HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
    HttpServletResponse res = Mockito.mock(HttpServletResponse.class);
    StringWriter writer = new StringWriter();
    StringReader reader = new StringReader(reqString);
    Mockito.when(req.getMethod()).thenReturn("POST");
    Mockito.when(req.getReader()).thenReturn(new BufferedReader(reader));
    Mockito.when(res.getWriter()).thenReturn(new PrintWriter(writer));
    return new SetupObj(req, res, writer);
  }
}