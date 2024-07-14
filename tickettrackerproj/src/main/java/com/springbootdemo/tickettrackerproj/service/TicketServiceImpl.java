package com.springbootdemo.tickettrackerproj.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.springbootdemo.tickettrackerproj.entity.Ticket;
import com.springbootdemo.tickettrackerproj.repository.TicketRepository;

@Service
public class TicketServiceImpl implements TicketService, FileProcessService {

	@Autowired
	private TicketRepository ticketRepository;
	
	@Override
	public List<Ticket> getAllTickets() {
		
		return ticketRepository.findAll();
	}

	

	@Override
	public Ticket updateTicket(Ticket ticket,long id) {
		
		Ticket exstingTicket=findById(id);
		exstingTicket.setTicketTitle(ticket.getTicketTitle());
		exstingTicket.setContent(ticket.getContent());
		exstingTicket.setTicketShortDescription(ticket.getTicketShortDescription());
		return saveTicket(exstingTicket);
	}

	@Override
	public void deleteTicket(long id) {
		
		ticketRepository.deleteById(id);
	}

	@Override
	public Ticket findById(long id) {
		
		return ticketRepository.findById(id).get();
	}



	@Override
	public Ticket saveTicket(Ticket ticket) {
		
		return ticketRepository.save(ticket);
	}



	@Override
	public List<Ticket> findByKeyword(String keyword) {
		
		return ticketRepository.findByKeyword(keyword);
	}



	@Override
	public void saveAllEntities(List<Ticket> entities) {
		
		ticketRepository.saveAll(entities);
		
	}



	
	@Override
	public void processCSVFile(MultipartFile file) throws IOException {
		
		List<Ticket> entities = new ArrayList<>();

        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                Ticket entity = new Ticket();
                entity.setTicketTitle(csvRecord.get("ticket_title"));
                entity.setTicketShortDescription(csvRecord.get("description"));
                entity.setContent(csvRecord.get("content"));
                // Set other fields

                entities.add(entity);
            }
        

        saveAllEntities(entities);
    }
}



	@Override
	public List<Ticket> findByticketCreatedOnBetween(LocalDate startDate, LocalDate endDate) {
		
		return ticketRepository.findByticketCreatedOnBetween(startDate, endDate);
	}
}