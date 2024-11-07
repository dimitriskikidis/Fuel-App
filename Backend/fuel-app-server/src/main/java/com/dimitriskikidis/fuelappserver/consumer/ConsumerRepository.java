package com.dimitriskikidis.fuelappserver.consumer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsumerRepository extends JpaRepository<Consumer, Integer> {

    Optional<Consumer> findByUserId(Integer userId);
}
