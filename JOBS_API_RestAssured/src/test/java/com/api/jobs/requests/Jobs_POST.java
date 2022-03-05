package com.api.jobs.requests;

import static Utilities.UtilConstants.CONST_EXCELFILEPATH;
import static Utilities.UtilConstants.CONST_JOBSPOSTSHEET;
import static Utilities.UtilConstants.CONST_PATH;
import static Utilities.UtilConstants.CONST_URL;
import static io.restassured.RestAssured.given;
import io.restassured.module.jsv.JsonSchemaValidator;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
//import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import Utilities.ExcelUtil;
import Utilities.PropertyFileReader;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.module.jsv.JsonSchemaValidatorSettings;

public class Jobs_POST{

	Properties prop;
	 String path;
	 
	 public Jobs_POST() throws Exception {
	        prop = PropertyFileReader.readPropertiesFile();
	        
	    }
	 
	 @DataProvider(name = "PostJobsData")
	 public Object[][] getpostData() throws IOException {
		 System.out.println("Inside DataProvider");
		    String excelpath = prop.getProperty(CONST_EXCELFILEPATH);
	        int rowCount = ExcelUtil.getRowCount(excelpath,CONST_JOBSPOSTSHEET);
	        System.out.println(rowCount);
	        int colCount = ExcelUtil.getCellCount(excelpath,CONST_JOBSPOSTSHEET,rowCount);
	        System.out.println(colCount);
	        String getpostData[][] = new String[rowCount][colCount];
	        
	        for (int i=1;i<=rowCount;i++){
	            for(int j=0;j<colCount;j++) {
	                getpostData[i-1][j]=ExcelUtil.getCellData(excelpath,CONST_JOBSPOSTSHEET,i,j);
	            }
	        }
	        return getpostData;
	    }
	 
	 @SuppressWarnings("unchecked")
	@Test(dataProvider = "PostJobsData")
	 public void createJob(String Job_Title,String Job_Company_Name,String Job_Location,
			 String Job_Type,String Job_Posted_time,String Job_Description,String Job_Id,String status_Code_expected) throws JsonMappingException, JsonProcessingException {
		 
		 RestAssured.baseURI=prop.getProperty(CONST_URL);
         RestAssured.basePath=prop.getProperty(CONST_PATH);
         System.out.println("Inside getPostData Test Class");
      JSONObject requestParam=new JSONObject();
         requestParam.put("Job Id", Job_Id);
     	requestParam.put("Job Title",Job_Title);
     	requestParam.put("Job Company Name",Job_Company_Name);
     	requestParam.put("Job Location",Job_Location);
     	requestParam.put("Job Type",Job_Type);
     	requestParam.put("Job Posted time",Job_Posted_time);
     	requestParam.put("Job Description",Job_Description);
     	Response response= given().header("Content-Type","application/json").body(requestParam.toJSONString())
				   .when().post(RestAssured.baseURI+RestAssured.basePath)
				   .then().log().all().extract().response();
    
     String responseBody = response.getBody().asPrettyString();
     System.out.println("The Response Body is :"+responseBody);
     Reporter.log(responseBody);
     Assert.assertEquals(response.getStatusCode(),Integer.parseInt(status_Code_expected)); 
        
     	
    	String exp_jobTitle = null;
     	String exp_jobCompanyName = null;
     	String exp_jobLocation=null;
     	String exp_jobType=null;
     	String exp_jobPostedTime=null;
     	String exp_jobDescription=null;
     	String exp_jobId=null;
     
      	// Data validation using Jackson library -  Converts JSON strings to java object and vice-versa
     	
     	// Create an object of Object mapper 
     	ObjectMapper mapper = new ObjectMapper();
     	
     	// Enable mapper configuration to allow NaN values during conversion 
     	mapper.enable(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS.mappedFeature());
     		
     	// Convert the json string into  LinkedHashmap object with 3 level depth 
     	// ( since Json has 3 level hierarchy  like  "data" -> "Job Title" -> "0") 
     	
     	//  1st HashMap    			 Key ->data ,  			Value ->  dataMap
     	//  2nd HashMap dataMap  	 Key ->  Job Title,     Value -> jobTitleMap
     	//  3rd HashMap jobTitleMap   Key -> pos,  			Value -> "Sample Job Title"
     	
     	LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String,String>>> map =
     			(LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String,String>>>) 
     			mapper.readValue(responseBody, Map.class); 
     	
     
     	LinkedHashMap<String, LinkedHashMap<String,String>> dataMap = (LinkedHashMap<String, LinkedHashMap<String,String>>) map.get("data");
     	
     	// we get dataMap as NULL when POST API response is like {"message" : "Job Id already Exists" }
     	// so to avoid those cases, we need to check for NOT NULL condition as below
     	if (dataMap != null) {
     		
     		LinkedHashMap<String, String> jobTitleMap = (LinkedHashMap<String,String>) dataMap.get("Job Title");     		
     		String pos = jobTitleMap.size() - 1  + ""; 
     		exp_jobTitle = jobTitleMap.get(pos);     		
     		System.out.println("The job title Created : " + exp_jobTitle);
     		Assert.assertEquals(Job_Title, exp_jobTitle);
     		
     		LinkedHashMap<String, String> jobCompanyNameMap = (LinkedHashMap<String,String>) dataMap.get("Job Company Name");
     		pos = jobCompanyNameMap.size() - 1  + ""; 
     		exp_jobCompanyName = jobCompanyNameMap.get(pos);
     		System.out.println("The job CompanyName Created : " +exp_jobCompanyName);
     		Assert.assertEquals(Job_Company_Name, exp_jobCompanyName);
     		
     		LinkedHashMap<String, String> jobLocationMap = (LinkedHashMap<String,String>) dataMap.get("Job Location");
     		pos = jobLocationMap.size() - 1  + ""; 
     		exp_jobLocation = jobLocationMap.get(pos);
     		System.out.println("The job Location Created : " +exp_jobLocation);
     		Assert.assertEquals(Job_Location, exp_jobLocation);
     		
     		LinkedHashMap<String, String> jobTypeMap = (LinkedHashMap<String,String>) dataMap.get("Job Type");
     		pos = jobTypeMap.size() - 1  + ""; 
     		exp_jobType = jobTypeMap.get(pos);
     		System.out.println("The Job Type Created : " +exp_jobType);
     		Assert.assertEquals(Job_Type, exp_jobType);
     		
     		LinkedHashMap<String, String> jobPostedTimeMap = (LinkedHashMap<String,String>) dataMap.get("Job Posted time");
     		pos = jobPostedTimeMap.size() - 1  + ""; 
     		exp_jobPostedTime = jobPostedTimeMap.get(pos);
     		System.out.println("The Job Posted time Created : " +exp_jobPostedTime);
     		Assert.assertEquals(Job_Posted_time, exp_jobPostedTime);
     		
     		LinkedHashMap<String, String> jobDescriptionMap = (LinkedHashMap<String,String>) dataMap.get("Job Description");
     		pos = jobDescriptionMap.size() - 1  + ""; 
     		exp_jobDescription = jobDescriptionMap.get(pos);
     		System.out.println("The job Description Created : " +exp_jobDescription);
     		Assert.assertEquals(Job_Description, exp_jobDescription);
     		
     		LinkedHashMap<String, String> jobIdMap = (LinkedHashMap<String,String>) dataMap.get("Job Id");
     		pos = jobIdMap.size() - 1  + ""; 
     		exp_jobId = jobIdMap.get(pos);
     		System.out.println("The job Id Created : " +exp_jobId);
     		Assert.assertEquals(Job_Id, exp_jobId);
     	}
     	
     	
     // Schema Validation steps	
     	
      String result = response.asPrettyString();
      
      // Since Rest assured JsonSchemaValidator is not supporting NaN values, replace NaN with null
      
      result = result.replace("NaN", "null");
     
      // String result ="{\"data\": {\"Job Title\": {\"0\": \"SDET\"},\"Job Company Name\": {\"0\": \"Signature Consultants\" },\"Job Location\": {\"0\": \"Vinings, GA, USA\"},\"Job Type\": {\"0\": \"Contract\"},\"Job Posted time\": {\"0\": \"44 minutes ago\"},\"Job Description\": {\"0\": \"Desc\"}, \"Job Id\": {\"0\": \"1001\"}}}";
      // String result ="{\"data\": {\"Job Title\": {\"0\": \"SDET\"}}}";     
     
     assertThat("Schema Validation Failed",result, JsonSchemaValidator.matchesJsonSchemaInClasspath("PostResponse.json"));
     
        
	 }
	 
	 }
	 
	 

