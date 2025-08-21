package pet.store.controller.model;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import lombok.NoArgsConstructor;
import pet.store.entity.Customer;
import pet.store.entity.Employee;
import pet.store.entity.PetStore;

@Data
@NoArgsConstructor
public class PetStoreData {

    private Long id;
    private String storeName;
    private Set<PetStoreCustomer> customers = new HashSet<>();
    private Set<PetStoreEmployee> employees = new HashSet<>();

    public PetStoreData(PetStore petStore) {
        this.id = petStore.getId();
        this.storeName = petStore.getStoreName();

        for (Customer customer : petStore.getCustomers()) {
            this.customers.add(new PetStoreCustomer(customer));
        }

        for (Employee employee : petStore.getEmployees()) {
            this.employees.add(new PetStoreEmployee(employee));
        }
    }

    @Data
    @NoArgsConstructor
    public static class PetStoreCustomer {
        private Long id;
        private String customerName;
        private String customerEmail;

        public PetStoreCustomer(Customer customer) {
            this.id = customer.getId();
            this.customerName = customer.getCustomerName();
            this.customerEmail = customer.getCustomerEmail();
        }
    }

    @Data
    @NoArgsConstructor
    public static class PetStoreEmployee {
        private Long id;
        private String employeeName;

        public PetStoreEmployee(Employee employee) {
            this.id = employee.getId();
            this.employeeName = employee.getEmployeeName();
        }
    }
}

