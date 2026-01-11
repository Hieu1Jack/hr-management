package com.ductien.hrmanagement.service;

import com.ductien.hrmanagement.entity.Position;
import com.ductien.hrmanagement.repository.PositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PositionService {

    @Autowired
    private PositionRepository positionRepository;

    public Position createPosition(Position position) {
        position.setCreatedAt(LocalDateTime.now());
        position.setIsActive(true);
        return positionRepository.save(position);
    }

    public Position updatePosition(Position position) {
        Optional<Position> existing = positionRepository.findById(position.getPositionId());
        if (existing.isPresent()) {
            Position p = existing.get();
            p.setPositionName(position.getPositionName());
            p.setDescription(position.getDescription());
            p.setIsActive(position.getIsActive());
            p.setUpdatedAt(LocalDateTime.now());
            return positionRepository.save(p);
        }
        throw new RuntimeException("Position not found");
    }

    public void deletePosition(Integer positionId) {
        positionRepository.deleteById(positionId);
    }

    public Optional<Position> getPositionById(Integer positionId) {
        return positionRepository.findById(positionId);
    }

    public List<Position> getAllPositions() {
        return positionRepository.findAll();
    }

    public List<Position> getActivePositions() {
        return positionRepository.findByIsActive(true);
    }
}
