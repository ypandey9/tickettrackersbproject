package com.springbootdemo.tickettrackerproj.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.springbootdemo.tickettrackerproj.entity.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket,Long> {

	List<Ticket> findByticketCreatedOnBetween(LocalDate startDate, LocalDate endDate);
	
	@Query(value="select * from ticket where ticket.ticket_title like %:keyword%",nativeQuery=true)
	public List<Ticket> findByKeyword(@Param("keyword") String keyword);
	
	 
}
