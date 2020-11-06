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
}
