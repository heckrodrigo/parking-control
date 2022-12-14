package com.api.parkingcontrol.controllers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.parkingcontrol.dtos.ParkingSpotDto;
import com.api.parkingcontrol.models.ParkingSpotModel;
import com.api.parkingcontrol.repositories.ParkingSpotRepository;
import com.api.parkingcontrol.services.ParkingSpotService;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/parking-spot")
public class ParkingSpotController {
	
	final ParkingSpotService parkingSpotService;
	
	public ParkingSpotController(ParkingSpotService parkingSpotService) {
		this.parkingSpotService = parkingSpotService;
		
	}
	
	// Implementando o metodo POST com validações das requisições
	
	@PostMapping
	public ResponseEntity<Object> saveParkingSpot(@RequestBody @Valid ParkingSpotDto parkingSpotDto){
		
		if (parkingSpotService.existsByLicencePlateCar(parkingSpotDto.getLicencePlateCar())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflito: Essa placa já está em uso.");	
			
		}
		
		if (parkingSpotService.existsByParkingSpotNumber(parkingSpotDto.getParkingSpotNumber())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflito: Esse número de vaga já está em uso.");	
		}
		
		if (parkingSpotService.existsByApartmentAndBlock(parkingSpotDto.getApartment(), parkingSpotDto.getBlock())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflito: Já existe uma vaga cadastrada para esse apartamento e bloco.");	
		}
		
				
		// Converte parkingSpotDto para parkingSpotModel. Cria o metodo SAVE
		var parkingSpotModel = new ParkingSpotModel();
		BeanUtils.copyProperties(parkingSpotDto, parkingSpotModel);
		parkingSpotModel.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));
		return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpotService.save(parkingSpotModel));
		
		
		
	}

	// Implementando o metodo GET
	
	@GetMapping
	public ResponseEntity<List<ParkingSpotModel>> getAllParkingSpots(){
		return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.findAll());
		
		
	}
	
	// Implementando o metodo GET ID
	
	@GetMapping("/{id}")
	public ResponseEntity<Object> getOneParkingSpot (@PathVariable (value = "id")UUID id)	{
		Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findByid(id);
		
		//: Se ele não estiver presente retorna 
		if (!parkingSpotModelOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vaga de estacionamento não encontrada.");
					
		}
		
		// Se estiver presente retorna
		return ResponseEntity.status(HttpStatus.OK).body(parkingSpotModelOptional.get());
		
		
		
		
	}
	
	
	
	// Implementando o metodo GET DELETE
	
		@DeleteMapping("/{id}")
		public ResponseEntity<Object> deleteParkingSpot (@PathVariable (value = "id")UUID id)	{
			Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findByid(id);
			
			//: Se ele não estiver presente retorna 
			if (! parkingSpotModelOptional.isPresent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vaga de estacionamento não encontrada.");
						
			}
			
			// Se estiver presente DELETA
			parkingSpotService.delete(parkingSpotModelOptional.get());
			return ResponseEntity.status(HttpStatus.OK).body("Vaga de estacionamento deletada com sucesso.");
			
			
			
			
		}
		
		
		// Atualizando os campos da tabela
		
		@PutMapping("/{id}")
		public ResponseEntity<Object> updateParkingSpot(@PathVariable(value = "id")UUID id, 
														@RequestBody @Valid ParkingSpotDto parkingSpotDto){
			
		Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findByid(id);
		if (! parkingSpotModelOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vaga de estacionamento não encontrada.");
					
		}
		
		var parkingSpotModel = new ParkingSpotModel(); 
		BeanUtils.copyProperties(parkingSpotDto, parkingSpotModelOptional);
		parkingSpotModel.setId(parkingSpotModelOptional.get().getId());
		parkingSpotModel.setRegistrationDate(parkingSpotModelOptional.get().getRegistrationDate());
		
		return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.save(parkingSpotModel));
			
		}
		
	
	
	
	
}
