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

public class PostCallMockingDemo {

    private final String host = "localhost";

    public WireMockServer server = new WireMockServer(wireMockConfig().dynamicPort());

    @BeforeClass
    public void intializeServer() {
        server.start();
        WireMock.configureFor(host,server.port());

        ResponseDefinitionBuilder responseDefinitionBuilder = new ResponseDefinitionBuilder();
        responseDefinitionBuilder.withStatus(201);
        responseDefinitionBuilder.withBody("Successfully added");

        server.stubFor(WireMock.post("/api/users").willReturn(responseDefinitionBuilder));
    }

    @Test
    public void postUserMocking() {
        Response response = given()
                .post("http://" + host + ":" + server.port() + "/api/users")
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .response();

        Assert.assertEquals(response.getStatusCode(), 201);
        Assert.assertEquals(response.getBody().asString(), "Successfully added");
    }



    @AfterClass
    public void tearDown() {
        if (server.isRunning() || server != null)
            server.shutdown();
    }

}
