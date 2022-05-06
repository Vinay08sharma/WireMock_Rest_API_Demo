import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.restassured.RestAssured.given;

public class GetCallMockingDemo {

    //Mocking the reqres endpoint with get call
    private static final String host = "localhost";
     //   private static final int port = 8080;
    //   private static WireMockServer wireMockServer = new WireMockServer(port);

    // Dynamic port allocation
    private static final WireMockServer wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());

    @BeforeClass
    public void initializeServer() {
        wireMockServer.start();
        WireMock.configureFor(host, wireMockServer.port());

        ResponseDefinitionBuilder mockResponse = new ResponseDefinitionBuilder();
        mockResponse.withStatus(200);
        mockResponse.withHeader("Content-Type", "application/json");
        mockResponse.withStatusMessage("Success");

        System.out.println("Using port number: " + wireMockServer.port());

        //Stubbing for the endpoint
        wireMockServer.stubFor(WireMock.get("/api/users/1").willReturn(mockResponse));
    }

    @Test
    public void getUserMocking() {
        Response response = given().
                get("http://" + host + ":" + wireMockServer.port() + "/api/users/1")
                .then()
                .assertThat()
                .statusCode(200)
                .extract().response();

        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.getHeader("Content-Type"), "application/json");
    }

    @AfterClass
    public void tearDown() {
        if (wireMockServer.isRunning() || wireMockServer != null)
            wireMockServer.shutdown();
    }

}
