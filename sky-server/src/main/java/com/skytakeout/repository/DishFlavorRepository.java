package com.skytakeout.repository;

import com.skytakeout.entity.DishFlavor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DishFlavorRepository extends JpaRepository<DishFlavor, Long> {

}
