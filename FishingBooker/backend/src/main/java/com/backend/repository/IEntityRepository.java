package com.backend.repository;
import com.backend.model.RegisteredUser;
import com.backend.model.Adventure;
import com.backend.model.RentingEntity;
import com.backend.model.UnavailablePeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface IEntityRepository extends JpaRepository<RentingEntity,Integer> {

    @Query("SELECT e  FROM RentingEntity e LEFT JOIN FETCH e.images where type(e) = ?1")
    <T extends RentingEntity> List<T> getEntityByClass(Class<?> type);

    @Query("select entity" +
            " from RentingEntity entity,Client client LEFT JOIN FETCH entity.images " +
            "where client.email = ?1 and entity in (" +
            "select e from client.subscriptions e)")
    <T extends RentingEntity> List<T> findSubscriptions(String email);

    @Query(value = "select e from RentingEntity e left join fetch e.sales where e.id = :id")
    <T extends RentingEntity> T fetchById(@Param("id") Integer id);

    @Query(value = "select e from RentingEntity e left join fetch e.unavailablePeriods where e.id = :id")
    <T extends RentingEntity> T fetchWithPeriods(@Param("id") Integer id);
}
