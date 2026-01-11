package com.ductien.hrmanagement.repository;

import com.ductien.hrmanagement.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PositionRepository extends JpaRepository<Position, Integer> {
    List<Position> findByIsActive(Boolean isActive); // Lấy tất cả vị trí theo trạng thái hoạt động
}
