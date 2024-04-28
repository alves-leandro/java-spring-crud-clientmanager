package com.attus.clientmanager.models;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.*;

public class ClientDto {
	@NotEmpty(message = "The name is required")
	private String name;
	
	@NotEmpty(message = "The phone is required")
	private String phone;
	
	@NotEmpty(message = "The function is required")
	private String function;
	
	@Min(0)
	private double salary;
	
	@Size(min = 10, message= "The description should be at least 10 characters")
	@Size(max = 2000, message= "The description cannot exceed 2000 characters")
	private String description;
	
	private MultipartFile imageFile;

	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public MultipartFile getImageFile() {
		return imageFile;
	}

	public void setImageFile(MultipartFile imageFile) {
		this.imageFile = imageFile;
	}
}
