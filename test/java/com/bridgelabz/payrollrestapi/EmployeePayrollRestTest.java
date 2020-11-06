package com.bridgelabz.payrollrestapi;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.bridgelabz.payrollrestapi.EmployeePayrollService.IOService;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class EmployeePayrollRestTest {
	EmployeePayrollService employeePayrollService = null;
	private static Logger log = Logger.getLogger(EmployeePayrollRestTest.class.getName());

	@Before
	public void setUp() {
		employeePayrollService = new EmployeePayrollService();
		RestAssured.baseURI = "http://localhost"; // setting the URI(resource indicator)
		RestAssured.port = 3000; // port where JSON server is running
	}

	public EmployeePayrollData[] getEmployeeList() {
		Response response = RestAssured.get("/employees"); // going through json server & getting employeePayroll by
															// using get call
		log.info("Employee payroll entries in JSON Server :\n" + response.asString());
		// converting the json string to arrayOfEmps using Gson
		EmployeePayrollData[] arrayOfEmps = new Gson().fromJson(response.asString(), EmployeePayrollData[].class);
		return arrayOfEmps;
	}

	private Response addEmployeeToJSONServer(EmployeePayrollData employeePayrollData) {
		// adding newly inserted employee to the Json server
		String empJson = new Gson().toJson(employeePayrollData);
		RequestSpecification request = RestAssured.given(); // Allows to specify how the request will look like
		request.header("Content-Type", "application/json");
		request.body(empJson);
		return request.post("/employees"); // Perform a POST request to a path
	}

	@Test
	public void givenEmployeeDataInJSONServer_WhenRetrieved_ShouldmatchTheCount() {
		EmployeePayrollData[] arrayOfEmps = getEmployeeList();
		employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps)); // passing the array
		long entries = employeePayrollService.countEntries(IOService.REST_IO);
		Assert.assertEquals(3, entries);
	}

	@Test
	public void givenNewEmployee_WhenAddedInJsonServer_ShouldMatchResponseAndCount() {
		EmployeePayrollData[] arrayOfEmps = getEmployeeList(); // population the employeePayroll List
		employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
		// Adding employee to the employeePayroll object
		EmployeePayrollData employeePayrollData = new EmployeePayrollData(0, "Mark ZukerBerg", "M", 3000000,
				LocalDate.now());
		Response response = addEmployeeToJSONServer(employeePayrollData);
		int statusCode = response.getStatusCode();
		Assert.assertEquals(201, statusCode);
		// retrieving the employeePayrollData and counting the entries
		employeePayrollData = new Gson().fromJson(response.asString(), EmployeePayrollData.class);
		employeePayrollService.addEmployeeToPayroll(employeePayrollData, IOService.REST_IO);
		long entries = employeePayrollService.countEntries(IOService.REST_IO);
		Assert.assertEquals(3, entries);
	}

	@Test
	public void givenListOfNewEmployees_WhenAdded__ShouldMatchEmployeeCount() {
		EmployeePayrollData[] arrayOfEmps = getEmployeeList(); // population the employeePayroll List
		employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
		EmployeePayrollData[] arrayOfEmployeePayrolls = {
				new EmployeePayrollData(0, "Sundar Pichai", "M", 4000000.00, LocalDate.now()),
				new EmployeePayrollData(0, "Mukesh", "M", 3000000.00, LocalDate.now()),
				new EmployeePayrollData(0, "Anil", "M", 2000000.00, LocalDate.now()) };
		// Recursively calling each added employee and checking the statusCode
		for (EmployeePayrollData employeePayrollData : arrayOfEmployeePayrolls) {
			Response response = addEmployeeToJSONServer(employeePayrollData);
			int statusCode = response.getStatusCode();
			Assert.assertEquals(201, statusCode);
			// converting the added ones into objects from the json file
			employeePayrollData = new Gson().fromJson(response.asString(), EmployeePayrollData.class);
			// adding objects into the employee payroll
			employeePayrollService.addEmployeeToPayroll(employeePayrollData, IOService.REST_IO);
		}
		long entries = employeePayrollService.countEntries(IOService.REST_IO);
		Assert.assertEquals(6, entries);
	}

	@Test
	public void givenNewSalaryForEmployee_WhenUpdated_ShouldMatch() {
		EmployeePayrollData[] arrayOfEmps = getEmployeeList(); // population the employeePayroll List
		employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
		employeePayrollService.updateEmployeeSalary("Bill Gates", 3000000.00, IOService.REST_IO);
		EmployeePayrollData employeePayrollData = employeePayrollService.getEmployeePayrollData("Bill Gates");
		// adding newly inserted employee to the Json server
		String empJson = new Gson().toJson(employeePayrollData);
		RequestSpecification request = RestAssured.given(); // Allows to specify how the request will look like
		request.header("Content-Type", "application/json");
		request.body(empJson);
		Response response = request.put("/employees/" + employeePayrollData.id);
		int statusCode = response.getStatusCode();
		Assert.assertEquals(200, statusCode);
	}
}
