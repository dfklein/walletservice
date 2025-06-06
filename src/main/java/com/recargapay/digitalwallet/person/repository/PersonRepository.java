package com.recargapay.digitalwallet.person.repository;

import com.recargapay.digitalwallet.person.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, String> {

}
