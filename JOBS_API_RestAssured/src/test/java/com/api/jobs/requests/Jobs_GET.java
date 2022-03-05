package com.api.jobs.requests;

import java.util.Properties;
import static Utilities.UtilConstants.*;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;
import Utilities.PropertyFileReader;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class Jobs_GET {

	Properties prop;
    public Jobs_GET() throws Exception {
        prop = PropertyFileReader.readPropertiesFile();

    }
    
    @Test
    public void getallJobs() {
        RestAssured.baseURI=prop.getProperty(CONST_URL);
        RestAssured.basePath=prop.getProperty(CONST_PATH);
        Response response = given().when().get(RestAssured.baseURI+RestAssured.basePath);
        int status_code = response.statusCode();
        System.out.println(response.getBody().asPrettyString());
        Reporter.log(response.getBody().asPrettyString());
        Assert.assertEquals(status_code,200);

    }
}
