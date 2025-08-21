package pet.store.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import pet.store.controller.model.PetStoreData;
import pet.store.controller.model.PetStoreData.PetStoreCustomer;
import pet.store.controller.model.PetStoreData.PetStoreEmployee;
import pet.store.service.PetStoreService;

@RestController
@RequestMapping("/pet_store") // The base path for the controller
@Slf4j
public class PetStoreController {

    @Autowired
    private PetStoreService petStoreService;

    @PostMapping
    public PetStoreData createPetStore(@RequestBody PetStoreData petStoreData) {
        log.info("Creating pet store: {}", petStoreData);
        return petStoreService.savePetStore(petStoreData);
    }
    
    // Add Employee to Pet Store
    @PostMapping("/{petStoreId}/employee")
    @ResponseStatus(code = HttpStatus.CREATED)
    public PetStoreEmployee addEmployeeToPetStore(
            @PathVariable Long petStoreId,
            @RequestBody PetStoreEmployee petStoreEmployee) {
        log.info("Adding employee to pet store ID={}: {}", petStoreId, petStoreEmployee);
        return petStoreService.saveEmployee(petStoreId, petStoreEmployee);
    }
    
    // Add Customer to Pet Store
    @PostMapping("/{petStoreId}/customer")
    @ResponseStatus(code = HttpStatus.CREATED)
    public PetStoreCustomer addCustomerToPetStore(
            @PathVariable Long petStoreId,
            @RequestBody PetStoreCustomer petStoreCustomer) {
        log.info("Adding customer to pet store ID={}: {}", petStoreId, petStoreCustomer);
        return petStoreService.saveCustomer(petStoreId, petStoreCustomer);
    }
    
    // Get all Pet Stores (summary data)
    @GetMapping
    public List<PetStoreData> retrieveAllPetStores() {
        log.info("Retrieving all pet stores");
        return petStoreService.retrieveAllPetStores();
    }
    
    // Get Pet Store by ID (with full data)
    @GetMapping("/{petStoreId}")
    public PetStoreData retrievePetStoreById(@PathVariable Long petStoreId) {
        log.info("Retrieving pet store with ID={}", petStoreId);
        return petStoreService.retrievePetStoreById(petStoreId);
    }
    
    // Delete Pet Store by ID
    @DeleteMapping("/{petStoreId}")
    public Map<String, String> deletePetStoreById(@PathVariable Long petStoreId) {
        log.info("Deleting pet store with ID={}", petStoreId);
        petStoreService.deletePetStoreById(petStoreId);
        return Map.of("message", "Pet store with ID=" + petStoreId + " was deleted successfully.");
    }
}
