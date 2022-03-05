package com.api.jobs.requests;

import java.io.IOException;
import java.util.Properties;
import static Utilities.UtilConstants.*;
import static io.restassured.RestAssured.given;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import Utilities.ExcelUtil;
import Utilities.PropertyFileReader;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class Jobs_DELETE {
	 Properties prop;

	    public Jobs_DELETE() throws Exception {
	        prop = PropertyFileReader.readPropertiesFile();

	    }
	    @DataProvider(name = "deleteJobsData")
	    public Object[][] deleteJobsData() throws IOException {
	        String path = prop.getProperty(CONST_EXCELFILEPATH);
	        int rowCount = ExcelUtil.getRowCount(path,CONST_JOBSDELETESHEET);
	        int colCount = ExcelUtil.getCellCount(path,CONST_JOBSDELETESHEET,rowCount);
	        String deljobData[][] = new String[rowCount][colCount];

	        for (int i=1;i<=rowCount;i++){
	            for(int j=0;j<colCount;j++) {
	            	deljobData[i-1][j]=ExcelUtil.getCellData(path,CONST_JOBSDELETESHEET,i,j);
	            }
	        }
        return deljobData;
	    }
	    
	    @Test(dataProvider = "deleteJobsData")
	    public void delete_Job_id(String jobId, String StatusCode) {
	    	RestAssured.baseURI=prop.getProperty(CONST_URL);
	        RestAssured.basePath=prop.getProperty(CONST_PATH);
	        Response response = given().queryParam("Job Id", jobId).when().delete(RestAssured.baseURI+RestAssured.basePath);
	        
	        Reporter.log(" The Jobs ID to be deleted is :" +jobId );
	        Reporter.log(" The Response Status Code is :" +response.statusCode());
	        Assert.assertEquals(response.statusCode(),Integer.parseInt(StatusCode));
	    }

}
