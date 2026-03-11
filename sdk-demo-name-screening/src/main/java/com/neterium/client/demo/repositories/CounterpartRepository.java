package com.neterium.client.demo.repositories;

import com.neterium.client.demo.domain.Counterpart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * CounterpartRepository
 *
 * @author Bernard Ligny
 */
@Repository
public interface CounterpartRepository extends MongoRepository<Counterpart, String> {
    
}
