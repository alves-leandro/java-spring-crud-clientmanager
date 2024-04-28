package com.attus.clientmanager.controllers;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.attus.clientmanager.models.Client;
import com.attus.clientmanager.models.ClientDto;
import com.attus.clientmanager.services.ClientsRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/clients")
public class ClientsController {
	
	@Autowired
	private ClientsRepository repo;
	
	@GetMapping({"", "/"})
	public String showClientList(Model model) {
		List<Client> clients = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
		model.addAttribute("clients", clients);
		return "clients/index";
	}
	
	@GetMapping("/create")
	public String showCreatePage(Model model) {
		ClientDto clientDto = new ClientDto();
		model.addAttribute("clientDto", clientDto);
		return "clients/CreateClient";
	}
	
	@PostMapping("/create")
	public String createClient(@Valid @ModelAttribute ClientDto clientDto, BindingResult result) {
		
		if (clientDto.getImageFile().isEmpty()) {
			result.addError(new FieldError("clientDto", "imageFile", "The image file is empty"));
		}
		
		if (result.hasErrors()) {
			return "clients/CreateClient";
		}
		
		// save image file
		MultipartFile image = clientDto.getImageFile();
		Date createdAt = new Date();
		String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();
		
		try {
			String uploadDir = "public/images/";
			Path uploadPath = Paths.get(uploadDir);
			
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}
			
			try (InputStream inputStream = image.getInputStream()) {
				Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (Exception ex) {
			System.out.println("Exception " + ex.getMessage());
		}
		
		Client client = new Client();
		client.setName(clientDto.getName());
		client.setPhone(clientDto.getPhone());
		client.setFunction(clientDto.getFunction());
		client.setSalary(clientDto.getSalary());
		client.setDescription(clientDto.getDescription());
		client.setCreatedAt(createdAt);
		client.setImageFileName(storageFileName);
		
		repo.save(client);
		
		return "redirect:/clients";
	}
	
	@GetMapping("/edit")
	public String showEditPage(Model model, @RequestParam int id) {
	
		try {
			Client client = repo.findById(id).get();
			model.addAttribute("client", client);
			
			ClientDto clientDto = new ClientDto();
			clientDto.setName(client.getName());
			clientDto.setPhone(client.getPhone());
			clientDto.setFunction(client.getFunction());
			clientDto.setSalary(client.getSalary());
			clientDto.setDescription(client.getDescription());

			model.addAttribute("clientDto", clientDto);
		} catch(Exception ex) {
			System.out.println("Exception " + ex.getMessage());
			return "redirect: /clients";
		}
		
		return "clients/EditClient";
	}
	
	@PostMapping("/edit")
	public String updateClient(
			Model model, 
			@RequestParam int id, 
			@Valid @ModelAttribute ClientDto clientDto, 
			BindingResult result) {
		
		try {
			Client client = repo.findById(id).get();
			model.addAttribute("client", client);
			
			if (result.hasErrors()) {
				return "clients/EditClient";
			}
			
			if (!clientDto.getImageFile().isEmpty()) {
				// delete old image
				String uploadDir = "public/images/";
				Path oldImagePath = Paths.get(uploadDir + client.getImageFileName());
				
				try {
					Files.delete(oldImagePath);
				} 
				catch(Exception ex) {
					System.out.println("Exception " + ex.getMessage());
				}
				
				// save new image file
				MultipartFile image = clientDto.getImageFile();
				Date createdAt = new Date();
				String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();
				
				try (InputStream inputStream = image.getInputStream()) {
					Files.copy(inputStream, Paths.get(uploadDir + storageFileName), 
							StandardCopyOption.REPLACE_EXISTING);
				}
				
				client.setImageFileName(storageFileName);
			}
			
			client.setName(clientDto.getName());
			client.setPhone(clientDto.getPhone());
			client.setFunction(clientDto.getFunction());
			client.setSalary(clientDto.getSalary());
			client.setDescription(clientDto.getDescription());
			
			repo.save(client);
			
		} 
		catch(Exception ex) {
			System.out.println("Exception " + ex.getMessage());
		}
		
		return "redirect:/clients";
	}
	
	@GetMapping("/delete")
	public String deleteClient(@RequestParam int id) {
		
		try {
			Client client = repo.findById(id).get();
			
			// delete client image
			Path imagePath = Paths.get("public/images/" + client.getImageFileName());
			
			try {
				Files.delete(imagePath);
			}
			catch(Exception ex) {
				System.out.println("Exception " + ex.getMessage());
			}
			
			// delete client
			repo.delete(client);
		}
		catch(Exception ex) {
			System.out.println("Exception " + ex.getMessage());
		}
		
		return "redirect:/clients";
	}
}
