package telran.employees.net;

import java.util.Arrays;
import java.util.stream.Collectors;

import telran.employees.Company;
import telran.employees.Employee;
import telran.employees.Manager;
import telran.net.Protocol;
import telran.net.Request;
import telran.net.Response;
//	public void addEmployee(Employee empl) ;
//	public Employee getEmployee(long id) ;
//	public Employee removeEmployee(long id);
//	public int getDepartmentBudget(String department);
//	public String[] getDepartments() ;
//	public Manager[] getManagersWithMostFactor();
import telran.net.ResponseCode;


public class CompanyProtocol implements Protocol {
	Company company;

	public CompanyProtocol(Company company) {
		this.company = company;
	}

	@Override
	public Response getResponse(Request request) {
		String requestType = request.requestType();
		String requestData = request.requestData();
		Response response = null;
		try {
			response = switch( requestType) {
			case "addEmployee" -> addEmployee(requestData);
			case "getEmployee" -> getEmployee(requestData);
			case "removeEmployee" -> removeEmployee(requestData);
			case "getDepartmentBudget" -> getDepartmentBudget(requestData);
			case "getDepartments" -> getDepartments(requestData);
			case "getManagersWithMostFactor" -> getManagersWithMostFactor(requestData);
			default -> wrongTypeResponse(requestType);
			};
		} catch (Exception e) {
			response = wrongDataResponse( e.getMessage() );
		}
		return response;
	}

	private Response wrongDataResponse(String message) {
		return new Response(ResponseCode.WRONG_REQUEST_DATA, message);
	}

	private Response wrongTypeResponse(String message) {
		return new Response(ResponseCode.WRONG_REQUEST_TYPE, message);
	}
	

	private Response getManagersWithMostFactor(String requestData) {
		Manager[] managers = company.getManagersWithMostFactor();
		return new Response(ResponseCode.OK, managersToJSON(managers));
	}

	private String managersToJSON(Manager[] managers) {
		
		return Arrays.stream(managers).map(Employee::getJSON).collect(Collectors.joining(";"));
	}

	private Response getDepartments(String requestData) {
		String[] departments = company.getDepartments();
		return new Response(ResponseCode.OK, Arrays.stream(departments).collect(Collectors.joining(";")));
	}

	private Response getDepartmentBudget(String requestData) {
		return new Response(ResponseCode.OK, String.valueOf(company.getDepartmentBudget(requestData)));
	}

	private Response removeEmployee(String requestData) {
		Employee emp = company.removeEmployee(Long.parseLong(requestData));
		return new Response(ResponseCode.OK, emp.getJSON());
	}

	private Response getEmployee(String requestData) {
		Employee employee = company.getEmployee(Long.parseLong(requestData));
		if ( employee == null )
			throw new RuntimeException("Employee doesn't exis");
		return new Response(ResponseCode.OK, employee.getJSON());
	}

	private Response addEmployee(String emplJSON) {
		Employee employee = (Employee)new Employee().setObject(emplJSON);
		company.addEmployee(employee);
		return new Response(ResponseCode.OK, "");
	}

}
