package pet.store.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import pet.store.dao.PetStoreDao;
import pet.store.dao.EmployeeDao;
import pet.store.dao.CustomerDao;
import pet.store.controller.model.PetStoreData;
import pet.store.controller.model.PetStoreData.PetStoreCustomer;
import pet.store.controller.model.PetStoreData.PetStoreEmployee;
import pet.store.entity.Customer;
import pet.store.entity.Employee;
import pet.store.entity.PetStore;

@Service // Marks this class as a Spring service component
public class PetStoreService {

    // Injecting the DAOs to perform database operations
    @Autowired
    private PetStoreDao petStoreDao;
    
    @Autowired
    private EmployeeDao employeeDao;
    
    @Autowired
    private CustomerDao customerDao;

    @Transactional
    public PetStoreData savePetStore(PetStoreData petStoreData) {
        // Step 1: Find or create PetStore
        PetStore petStore = findOrCreatePetStore(petStoreData.getId());

        // Step 2: Copy fields from PetStoreData DTO to PetStore entity
        copyPetStoreFields(petStore, petStoreData);

        // Step 3: Handle customers - clear existing, add new
        petStore.getCustomers().clear();
        for (PetStoreCustomer customerDTO : petStoreData.getCustomers()) {
            Customer customer = new Customer();
            customer.setCustomerName(customerDTO.getCustomerName());
            customer.setCustomerEmail(customerDTO.getCustomerEmail());
            petStore.getCustomers().add(customer);
        }

        // Step 4: Handle employees - clear existing, add new
        petStore.getEmployees().clear();
        for (PetStoreEmployee employeeDTO : petStoreData.getEmployees()) {
            Employee employee = new Employee();
            employee.setEmployeeName(employeeDTO.getEmployeeName());
            employee.setPetStore(petStore); // Set back-reference to PetStore
            petStore.getEmployees().add(employee);
        }

        // Step 5: Save PetStore and return the updated DTO
        return new PetStoreData(petStoreDao.save(petStore));
    }

    // Method to find an existing PetStore by ID or create a new one if the ID is null
    private PetStore findOrCreatePetStore(Long petStoreId) {
        if (petStoreId == null) {
            return new PetStore();  // Create a new PetStore if no ID is provided
        }
        return petStoreDao.findById(petStoreId)
                .orElseThrow(() -> new NoSuchElementException("Pet store with ID=" + petStoreId + " not found."));
    }

    // Method to copy fields from PetStoreData DTO to PetStore entity
    private void copyPetStoreFields(PetStore petStore, PetStoreData petStoreData) {
        petStore.setStoreName(petStoreData.getStoreName());
    }
    
    // Employee methods
    @Transactional(readOnly = false)
    public PetStoreEmployee saveEmployee(Long petStoreId, PetStoreEmployee petStoreEmployee) {
        // Find the pet store
        PetStore petStore = findPetStoreById(petStoreId);
        
        // Find or create employee
        Employee employee = findOrCreateEmployee(petStoreEmployee.getId(), petStoreId);
        
        // Copy fields from DTO to entity
        copyEmployeeFields(employee, petStoreEmployee);
        
        // Set the pet store in the employee
        employee.setPetStore(petStore);
        
        // Add employee to pet store's employee set
        petStore.getEmployees().add(employee);
        
        // Save the employee
        Employee savedEmployee = employeeDao.save(employee);
        
        // Convert back to DTO and return
        return new PetStoreEmployee(savedEmployee);
    }
    
    private Employee findEmployeeById(Long petStoreId, Long employeeId) {
        Employee employee = employeeDao.findById(employeeId)
                .orElseThrow(() -> new NoSuchElementException("Employee with ID=" + employeeId + " not found."));
        
        if (!employee.getPetStore().getId().equals(petStoreId)) {
            throw new IllegalArgumentException("Employee does not belong to the specified pet store.");
        }
        
        return employee;
    }
    
    private Employee findOrCreateEmployee(Long employeeId, Long petStoreId) {
        if (employeeId == null) {
            return new Employee();
        }
        return findEmployeeById(petStoreId, employeeId);
    }
    
    private void copyEmployeeFields(Employee employee, PetStoreEmployee petStoreEmployee) {
        employee.setEmployeeName(petStoreEmployee.getEmployeeName());
    }
    
    // Customer methods
    @Transactional(readOnly = false)
    public PetStoreCustomer saveCustomer(Long petStoreId, PetStoreCustomer petStoreCustomer) {
        // Find the pet store
        PetStore petStore = findPetStoreById(petStoreId);
        
        // Find or create customer
        Customer customer = findOrCreateCustomer(petStoreCustomer.getId(), petStoreId);
        
        // Copy fields from DTO to entity
        copyCustomerFields(customer, petStoreCustomer);
        
        // Add customer to pet store's customer set
        petStore.getCustomers().add(customer);
        
        // Save the customer
        Customer savedCustomer = customerDao.save(customer);
        
        // Convert back to DTO and return
        return new PetStoreCustomer(savedCustomer);
    }
    
    private Customer findCustomerById(Long petStoreId, Long customerId) {
        Customer customer = customerDao.findById(customerId)
                .orElseThrow(() -> new NoSuchElementException("Customer with ID=" + customerId + " not found."));
        
        // Check if customer belongs to the specified pet store
        boolean found = customer.getPetStores().stream()
                .anyMatch(store -> store.getId().equals(petStoreId));
        
        if (!found) {
            throw new IllegalArgumentException("Customer does not belong to the specified pet store.");
        }
        
        return customer;
    }
    
    private Customer findOrCreateCustomer(Long customerId, Long petStoreId) {
        if (customerId == null) {
            return new Customer();
        }
        return findCustomerById(petStoreId, customerId);
    }
    
    private void copyCustomerFields(Customer customer, PetStoreCustomer petStoreCustomer) {
        customer.setCustomerName(petStoreCustomer.getCustomerName());
        customer.setCustomerEmail(petStoreCustomer.getCustomerEmail());
    }
    
    // Pet Store retrieval methods
    @Transactional(readOnly = true)
    public List<PetStoreData> retrieveAllPetStores() {
        List<PetStore> petStores = petStoreDao.findAll();
        
        return petStores.stream()
                .map(petStore -> {
                    PetStoreData data = new PetStoreData(petStore);
                    // Remove customer and employee data for summary view
                    data.getCustomers().clear();
                    data.getEmployees().clear();
                    return data;
                })
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public PetStoreData retrievePetStoreById(Long petStoreId) {
        PetStore petStore = findPetStoreById(petStoreId);
        return new PetStoreData(petStore);
    }
    
    // Pet Store deletion method
    @Transactional(readOnly = false)
    public void deletePetStoreById(Long petStoreId) {
        PetStore petStore = findPetStoreById(petStoreId);
        petStoreDao.delete(petStore);
    }
    
    // Helper method to find pet store by ID
    private PetStore findPetStoreById(Long petStoreId) {
        return petStoreDao.findById(petStoreId)
                .orElseThrow(() -> new NoSuchElementException("Pet store with ID=" + petStoreId + " not found."));
    }
}

