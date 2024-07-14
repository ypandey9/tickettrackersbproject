package com.springbootdemo.tickettrackerproj.entity;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class Ticket {

	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	@Column(name="ticket_title")
	private String ticketTitle;
	
	@Column(name="Description")
	private String ticketShortDescription;
	
	@Column(name="content")
	private String content;
	
	@Temporal(value=TemporalType.DATE)
	@DateTimeFormat(pattern="dd-mm-yyyy")
	@Column(name="ticket_created_on")
	private Date ticketCreatedOn;
	
	@PrePersist
	private void onCreate() {
		ticketCreatedOn=new Date();
	}

	public Ticket() {}
	
	public Ticket(String ticketTitle, String ticketShortDescription, String content) {
		
		this.ticketTitle = ticketTitle;
		this.ticketShortDescription = ticketShortDescription;
		this.content = content;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTicketTitle() {
		return ticketTitle;
	}

	public void setTicketTitle(String ticketTitle) {
		this.ticketTitle = ticketTitle;
	}

	public String getTicketShortDescription() {
		return ticketShortDescription;
	}

	public void setTicketShortDescription(String ticketShortDescription) {
		this.ticketShortDescription = ticketShortDescription;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getTicketCreatedOn() {
		return ticketCreatedOn;
	}

	public void setTicketCreatedOn(Date ticketCreatedOn) {
		this.ticketCreatedOn = ticketCreatedOn;
	}

	@Override
	public String toString() {
		return "Ticket [id=" + id + ", ticketTitle=" + ticketTitle + ", ticketShortDescription="
				+ ticketShortDescription + ", content=" + content + ", ticketCreatedOn=" + ticketCreatedOn + "]";
	}
	
	
	
}
