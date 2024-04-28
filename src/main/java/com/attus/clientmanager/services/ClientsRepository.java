package com.attus.clientmanager.services;

import org.springframework.data.jpa.repository.JpaRepository;

import com.attus.clientmanager.models.Client;

public interface ClientsRepository extends JpaRepository<Client, Integer> {

}
