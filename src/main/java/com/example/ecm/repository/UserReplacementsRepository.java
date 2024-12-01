package com.example.ecm.repository;

import com.example.ecm.model.User;
import com.example.ecm.model.UserReplacement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserReplacementsRepository extends JpaRepository<UserReplacement, Long> {

    @Query(value = """
    select u from user_replacements ur
     left join users u on u.id = ur.predecessor_id
     where ur.until >= current_date and ur.successor_id = :successorId
""", nativeQuery = true)
    List<User> findAllCurrentlyReplacedByUser(Long successorId);
}
