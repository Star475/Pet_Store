package pet.store.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pet.store.entity.PetStore;

public interface PetStoreDao extends JpaRepository<PetStore, Long> {
    // You can add custom queries here if necessary, but JpaRepository gives basic CRUD functionality
}
