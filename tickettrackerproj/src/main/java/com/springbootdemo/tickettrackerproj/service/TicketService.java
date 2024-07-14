package com.springbootdemo.tickettrackerproj.service;

import java.time.LocalDate;
import java.util.List;

import com.springbootdemo.tickettrackerproj.entity.Ticket;

public interface TicketService {

	public List<Ticket> getAllTickets();
	public Ticket saveTicket(Ticket ticket);
	public Ticket updateTicket(Ticket ticket,long id);
	public void deleteTicket(long id);
	public Ticket findById(long id);
	public List<Ticket> findByKeyword(String keyword);
	public void saveAllEntities(List<Ticket> entities);
	List<Ticket> findByticketCreatedOnBetween(LocalDate startDate, LocalDate endDate);
}
