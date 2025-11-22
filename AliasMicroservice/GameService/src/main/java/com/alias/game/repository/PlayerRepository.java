package com.alias.game.repository;

import com.alias.game.entity.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerRepository extends JpaRepository<PlayerEntity, Long> {
    List<PlayerEntity> findByGameId(Long gameId);

    List<PlayerEntity> findByTeamId(Long teamId);
}