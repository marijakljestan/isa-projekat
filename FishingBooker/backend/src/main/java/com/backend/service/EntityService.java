package com.backend.service;

import com.backend.dto.ComplaintDTO;
import com.backend.model.*;
import com.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EntityService {

    @Autowired
    private IEntityRepository entityRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IComplaintRepository complaintRepository;

    @Autowired
    private IReservationRepository reservationRepository;


    public EntityService(){ }

    public List<? extends RentingEntity> GetAllEntities(int state){
        List<? extends RentingEntity> entities=null;
        if(state==0)entities=entityRepository.getEntityByClass(Adventure.class);
        else if(state==1)entities=entityRepository.getEntityByClass(Ship.class);
        else if(state==2) entities=entityRepository.getEntityByClass(Cottage.class);
        return entities;
    }

    public RentingEntity getEntityById(Integer id) {
        return entityRepository.getById(id);
    }

    public List<? extends RentingEntity> GetByUsersSubscriptions(String email) {
        return entityRepository.findSubscriptions(email);
    }

    public void deleteEntity(Integer id) {
        if(isEntityBooked(id)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entity is now booked.");

        List<Client> clients = userRepository.fetchAllClients();

        for (Client c : clients) {
            for(RentingEntity e : c.getSubscriptions()) {
                if(e.getId() == id){
                    c.getSubscriptions().remove(e);
                    userRepository.save(c);
                    break;
                }
            }
        }

        entityRepository.deleteById(id);
    }

    private boolean isEntityBooked(Integer id) {
        List<Reservation> reservations = reservationRepository.getReservationByRentingEntity_Id(id);
        for(Reservation r : reservations) {
            if(r.getDateTime().before(new Date()) && r.getReservationEndTime().after(new Date()) && !r.getCanceled()) return true;
        }
        for(Reservation r : reservations) {
            reservationRepository.delete(r);
        }
        return false;
    }

    @Transactional
    public Reservation updateUnavailablePeriod(Reservation reservation) {
        Date endDate = getEndDate(reservation.getDateTime(),reservation.getDurationInHours());
        if(!CheckOverlappingDates(reservation,endDate)){
            UnavailablePeriod reservationPeriod = new UnavailablePeriod(reservation.getDateTime(),endDate);
            try{
                RentingEntity entityToUpdate = entityRepository.fetchWithPeriods(reservation.getRentingEntity().getId());
                entityToUpdate.getUnavailablePeriods().add(reservationPeriod);
                entityRepository.save(entityToUpdate);
                reservation.setRentingEntity(entityToUpdate);
                return reservation;
            } catch (ObjectOptimisticLockingFailureException ex){
                throw new ObjectOptimisticLockingFailureException("Entity got reserved in that period try again later.",ex);
            }
        }
        return null;
    }

    private boolean CheckOverlappingDates(Reservation reservation,Date endDate) {
        for(UnavailablePeriod period : reservation.getRentingEntity().getUnavailablePeriods())
            if(reservation.getDateTime().compareTo(period.getToDateTime()) <=0 || period.getFromDateTime().compareTo(endDate) <=0)
                return true;
        return false;
    }

    private Date getEndDate(Date fromDate,Integer durationInHours) {
        Integer hoursToDays= (int) Math.ceil(Double.valueOf(durationInHours/24));
        Calendar c = Calendar.getInstance();
        c.setTime(fromDate);
        c.add(Calendar.DATE, hoursToDays);
        Date endDate= c.getTime();
        return endDate;
    }


    @Transactional(readOnly = false)
    public Reservation updateUnavailablePeriodTest(Reservation reservation) {
        Date endDate = getEndDate(reservation.getDateTime(),reservation.getDurationInHours());
        if(!false){
            UnavailablePeriod reservationPeriod = new UnavailablePeriod(reservation.getDateTime(),endDate);
            try{
                RentingEntity entityToUpdate = entityRepository.fetchWithPeriods(reservation.getRentingEntity().getId());
                entityToUpdate.getUnavailablePeriods().add(reservationPeriod);
                entityToUpdate.setVersion(reservation.getRentingEntity().getVersion());
                entityRepository.save(entityToUpdate);
                reservation.setRentingEntity(entityToUpdate);
                return reservation;
            } catch (ObjectOptimisticLockingFailureException ex){
                throw new ObjectOptimisticLockingFailureException("Entity got reserved in that period try again later.",ex);
            }
        }
        return null;
    }

    public boolean checkIfSubscribed(String email,Integer entityId){
        RentingEntity e = entityRepository.checkIfSubscribed(email,entityId);
        if(e == null) return false;
        return true;
    }

    public void createComplaint(ComplaintDTO dto, String email) {
        Complaint complaint = new Complaint(dto.getContent(),new Client(userRepository.findByEmail(email)),entityRepository.findById(dto.getEntityId()).get());
        complaintRepository.save(complaint);
    }

    public List<? extends RentingEntity> getEntitiesOnSale(int state){
        List<? extends RentingEntity> entities=null;
        if(state==0)entities=entityRepository.getEntitiesOnSale(Adventure.class);
        else if(state==1)entities=entityRepository.getEntitiesOnSale(Ship.class);
        else if(state==2) entities=entityRepository.getEntitiesOnSale(Cottage.class);
        return entities;
    }

    public List<? extends RentingEntity> getAvailableEntities(UnavailablePeriod unavailablePeriod,int state){
        List<? extends RentingEntity> entities=null;
        if(state==0)entities=entityRepository.getEntityByClassWithPeriods(Adventure.class);
        else if(state==1)entities=entityRepository.getEntityByClassWithPeriods(Ship.class);
        else if(state==2) entities=entityRepository.getEntityByClassWithPeriods(Cottage.class);
        entities=entities
                .stream()
                .filter(e-> !CheckOverlappingDates(e,unavailablePeriod))
                .collect(Collectors.toList());
        return entities;
    }

    private boolean CheckOverlappingDates(RentingEntity e,UnavailablePeriod unavailablePeriod) {
        for(UnavailablePeriod period : e.getUnavailablePeriods())
            if(period.getFromDateTime().compareTo(unavailablePeriod.getToDateTime()) <=0 &&
                period.getToDateTime().compareTo(unavailablePeriod.getFromDateTime())>=0)
                return true;
        return false;
    }
}
