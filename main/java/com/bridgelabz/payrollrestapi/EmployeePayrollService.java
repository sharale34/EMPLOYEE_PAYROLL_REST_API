package com.bridgelabz.payrollrestapi;

import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollService {
	private List<EmployeePayrollData> employeePayrollList;

	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO;
	}

	public EmployeePayrollService() {
	}

	public EmployeePayrollService(List<EmployeePayrollData> employeeList) {
		this();
		this.employeePayrollList = new ArrayList<>(employeeList); // Use new memory not the same as provided by client
	}

	public long countEntries(IOService ioService) {
		if (ioService.equals(IOService.REST_IO))
			return employeePayrollList.size();
		return 0;
	}

	public void addEmployeeToPayroll(EmployeePayrollData employeePayrollData, IOService ioService) {
		employeePayrollList.add(employeePayrollData);
	}

	public void updateEmployeeSalary(String name, double salary, IOService ioService) {
		EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
		System.out.println(this.getEmployeePayrollData(name));
		if (employeePayrollData != null)
			employeePayrollData.salary = salary;
	}

	public void deleteEmployeePayroll(String name, IOService ioService) {
		EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
		System.out.println(this.getEmployeePayrollData(name));
		if (employeePayrollData != null)
			employeePayrollList.remove(employeePayrollData);
	}

	public EmployeePayrollData getEmployeePayrollData(String name) {
		return this.employeePayrollList.stream()
				.filter(employeePayrollData -> employeePayrollData.name.equalsIgnoreCase(name)).findFirst()
				.orElse(null);
	}
}
