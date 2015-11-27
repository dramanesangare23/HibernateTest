package pojos;

import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class ManageEmployee {
	
	private static SessionFactory sessionFactory;
	
	public static void main(String[] args) {
		try {
			sessionFactory = new Configuration().configure().buildSessionFactory();
		} catch (Throwable ex) {
			System.err.println("Failed to create SessionFactory object : " + ex);
			throw new ExceptionInInitializerError(ex);
		}
		ManageEmployee ME = new ManageEmployee();
		
		/* Add few employee records */
		Integer EmpID1 = ME.addEmployee("Fred", "Aklamanu", 1000);
		Integer EmpID2 = ME.addEmployee("Dramane", "Sangare", 5000);
		Integer EmpID3 = ME.addEmployee("Jonasse", "Juliana", 10000);
		
		// List down all the employees
		ME.listEmployees();
		
		//Update empployee's records
		ME.updateEmployee(EmpID1, 5000);
		ME.updateEmployee(EmpID3 , 15000);
		
		
		//Delete an employee from the database
		ME.deleteEmployee(EmpID2);
		
		//List down new list of the employees
		ME.listEmployees();
		
		sessionFactory.close();
	}
	
	/**
	 * Method to CREATE an employee in the database
	 */
	public Integer addEmployee(String fname, String lname, int salary){
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		Integer employeeID = null;
		try {
			transaction = session.beginTransaction();
			Employee employee = new Employee(fname, lname, salary);
			employeeID = (Integer) session.save(employee);
			transaction.commit();
		} catch (HibernateException he) {
			if(transaction != null)
				transaction.rollback();
			he.printStackTrace();
		} finally{
			session.close();
		}
		return employeeID;
	}
	
	/**
	 * Method to READ all the employees
	 */
	@SuppressWarnings("unchecked")
	public void listEmployees(){
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		try{
			transaction = session.beginTransaction();
			//In this query, we must use the name of the Class and not the one of table
			//Maybe the complete path to the class !!! (to be verified)
			List<Employee> employees = session.createQuery("FROM pojos.Employee").list();
			for(Iterator<Employee> iterator = employees.iterator(); iterator.hasNext();){
				Employee employee = iterator.next();
				System.out.println("-----------------------------------------------");
				System.out.println("ID = " + employee.getId());
				System.out.println("Firstname = " + employee.getFirstName());
				System.out.println("Lastname = " + employee.getLastName());
				System.out.println("Salary = " + employee.getSalary());
			}
			transaction.commit();
		} catch(HibernateException he){
			if(transaction != null)
				transaction.rollback();
			he.printStackTrace();
		} finally{
			session.close();
		}
	}
	
	/**
	 * Method UPDATE salary for an employee
	 */
	private void updateEmployee(Integer empID, int salary) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			Employee employee = session.get(Employee.class, empID);
			employee.setSalary(salary);
			session.update(employee);
			transaction.commit();
		} catch (HibernateException he) {
			if(transaction != null)
				transaction.rollback();
			he.printStackTrace();
		} finally{
			session.close();
		}
	}
	
	/**
	 * DELETE an employee from the employee
	 */
	public void deleteEmployee(Integer empID){
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		try{
			transaction = session.beginTransaction();
			Employee employee = session.get(Employee.class, empID);
			session.delete(employee);
			transaction.commit();
		} catch(HibernateException he){
			if(transaction != null)
				transaction.rollback();
			he.printStackTrace();
		} finally{
			session.close();
		}
	}

}
