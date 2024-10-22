package com.example.ecm.repository;

import com.example.ecm.model.Voting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VotingRepository extends JpaRepository<Voting, Long> {
    List<Voting> findByStatus(String status);
}
