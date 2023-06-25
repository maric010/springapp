package com.example.db1.Repositories;

import com.example.db1.Models.Contract;
import com.example.db1.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractRepository  extends JpaRepository<Contract,Long> {
    List<Contract> findAllByAuthor(User author);
    Contract findFirstById(long id);
}
