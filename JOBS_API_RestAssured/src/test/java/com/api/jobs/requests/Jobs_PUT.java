package com.api.jobs.requests;

import Utilities.ExcelUtil;
import Utilities.PropertyFileReader;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static Utilities.UtilConstants.*;

public class Jobs_PUT {
    Properties prop;

    public Jobs_PUT() throws Exception {
        prop = PropertyFileReader.readPropertiesFile();

    }
    @DataProvider(name = "putJobsData")
    public Object[][] putJobsData() throws IOException {
    	 System.out.println("Inside DataProvider");
        String path = prop.getProperty(CONST_EXCELFILEPATH);
        int rowCount = ExcelUtil.getRowCount(path,CONST_JOBSPUTSHEET);
        System.out.println(rowCount);
        int colCount = ExcelUtil.getCellCount(path,CONST_JOBSPUTSHEET,rowCount);
        System.out.println(colCount);
        String putJobsData[][] = new String[rowCount][colCount];

        for (int i=1;i<=rowCount;i++){
            for(int j=0;j<colCount;j++) {
            	putJobsData[i-1][j]=ExcelUtil.getCellData(path,CONST_JOBSPUTSHEET,i,j);
            }
        }
        return putJobsData;
    }

    @Test(dataProvider="putJobsData")
    public void updateJob(String Job_Id,String Job_Title,String Job_Company_Name,String Job_Location,
                      String Job_Type,String Job_Posted_time,String Job_Description,String StatusCode_expected) {
        //Specify base URI
        RestAssured.baseURI=prop.getProperty(CONST_URL);
        RestAssured.basePath=prop.getProperty(CONST_PATH);
       
    
      
        Response response= given().queryParam("Job Id", Job_Id).queryParam("Job Title", Job_Title).queryParam("Job Company Name", Job_Company_Name).
    			queryParam("Job Location",Job_Location).queryParam("Job Type",Job_Type).queryParam("Job Posted time", Job_Posted_time).
    			when().put(RestAssured.baseURI+RestAssured.basePath);
        
        String responsebody=response.getBody().asString();
      Assert.assertEquals(response.getStatusCode(),Integer.parseInt(StatusCode_expected));
        System.out.println("Response Body is"+responsebody);
        Reporter.log("Response Body is"+responsebody);
        Reporter.log(" Response Status Code :"+response.getStatusCode());
        System.out.println(" Response Status Code :"+response.getStatusCode());
        System.out.println(" Expected Status Code :"+StatusCode_expected);
        
       if (StatusCode_expected.equals(Success_Status)){
            if (Job_Id!=null) {
                Assert.assertEquals(responsebody.contains(Job_Id),true);
            }
            if (Job_Title!=null) {
                Assert.assertEquals(responsebody.contains(Job_Title),true);
            }
            if (Job_Company_Name!=null) {
                Assert.assertEquals(responsebody.contains(Job_Company_Name),true);
            }
            if (Job_Location!=null) {
                Assert.assertEquals(responsebody.contains(Job_Location),true);
            }
            if (Job_Type!=null) {
                Assert.assertEquals(responsebody.contains(Job_Type),true);
            }
            if (Job_Posted_time!=null) {
                Assert.assertEquals(responsebody.contains(Job_Posted_time),true);
            }
         

        }
     
        String result = response.asPrettyString();
        result = result.replace("NaN", "null");
        assertThat("Schema Validation Failed",result, matchesJsonSchemaInClasspath("PutResponse.json"));


    }


}
