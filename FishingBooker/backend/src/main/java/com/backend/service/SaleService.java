package com.backend.service;

import com.backend.model.Adventure;
import com.backend.model.Client;
import com.backend.model.RentingEntity;
import com.backend.model.Sale;
import com.backend.repository.IAdventureRepository;
import com.backend.repository.IEntityRepository;
import com.backend.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SaleService {

    @Autowired
    private IEntityRepository entityRepository;

    @Autowired
    private IAdventureRepository adventureRepository;

    @Autowired
    IUserRepository userRepository;

    @Autowired
    EmailService emailService;

    public Sale createSaleForEntity(Sale sale, Integer entityId) {
        RentingEntity entity = entityRepository.fetchById(entityId);
        sale.setRentingEntity(entity);
        entity.getSales().add(sale);
        entityRepository.save(entity);

        sendEmailForSubscribers(sale, entityId);

        return sale;
    }

    public Set<Sale> getAllSalesForLoggedInstructor(String email) {
        List<Adventure> adventures = adventureRepository.fetchAdventuresByFishingInstructor_Email(email);

        Set<Sale> sales = new HashSet<>();
        for(Adventure a : adventures) {
            for(Sale s : a.getSales()) {
                s.setRentingEntity(a);
                sales.add(s);
            }
        }
        return sales;
    }

    private void sendEmailForSubscribers(Sale sale, Integer entityId) {
        List<Client> subscribers = userRepository.fetchClientsSubscribedToEntity(entityId);

        for(Client c : subscribers) {
            emailService.sendSimpleMessage(c.getEmail(), "NEW SALE", generateEmailContentForSubscribers(sale, c));
        }
    }

    private String generateEmailContentForSubscribers(Sale sale, Client c) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Hello, ").append(c.getFirstName()).append(" ").append(c.getLastName())
                .append("\nThere is new sale on the entity you are subscribed to. Check it out on our website!")
                .append("\nHurry up! Sale expires on ").append(sale.getExpireDateTime());
        return stringBuilder.toString();
    }

}
