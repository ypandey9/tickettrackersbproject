package com.springbootdemo.tickettrackerproj.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;
import com.springbootdemo.tickettrackerproj.entity.Ticket;
import com.springbootdemo.tickettrackerproj.service.TicketServiceImpl;

@Controller
public class TicketController {

	@Autowired
	private TicketServiceImpl ticketService;

	@GetMapping("/Alltickets")
	public String getAllTickets(Model model, String keyword) {
		if (keyword != null)
			model.addAttribute("tickets", ticketService.findByKeyword(keyword));
		else
			model.addAttribute("tickets", ticketService.getAllTickets());
		
		return "tickets";
	}

	@GetMapping("/addTicket")
	public String showAddTicketForm(Model model) {
		model.addAttribute("newTicket", new Ticket());
		return "add_ticket";
	}

	@PostMapping("/saveTicket")
	public String addTicket(@ModelAttribute Ticket ticket) {
		ticketService.saveTicket(ticket);
		return "redirect:/Alltickets";
	}

	@GetMapping("/updateForm/{id}")
	public String showUpdateForm(Model model, @PathVariable long id) {
		Ticket ticket = ticketService.findById(id);
		model.addAttribute("ticket", ticket);
		return "update";
	}

	@PostMapping("/update/{id}")
	public String updateTicket(@ModelAttribute Ticket ticket, @PathVariable long id) {

		ticketService.updateTicket(ticket, id);
		return "redirect:/Alltickets";
	}

	@PostMapping("/delete/{id}")
	public String deleteTicket(@PathVariable long id) {
		ticketService.deleteTicket(id);
		return "redirect:/Alltickets";
	}

	@GetMapping("/view/{id}")
	public String findById(@PathVariable long id, Model model) {
		Ticket tc = ticketService.findById(id);
		model.addAttribute("tc", tc);
		return "view_ticket";

	}
	
	 @GetMapping("/")
	    public String index() {
	        return "upload";
	    }

	    @PostMapping("/api/upload")
	    public String uploadCSVFile(@RequestParam("file") MultipartFile file, Model model) {
	        String message = "";

	        if (file.isEmpty()) {
	            message = "Please select a CSV file to upload.";
	        } else {
	            try {
	            	ticketService.processCSVFile(file);
	                message = "Data uploaded successfully!";
	            } catch (IOException e) {
	                message = "An error occurred while processing the CSV file: " + e.getMessage();
	            }
	        }

	        model.addAttribute("message", message);
	        return "upload";
	    }
	    
	    @GetMapping("/index")
	    public String displayDateFilterForm()
	    {
	    	return "index";
	    }
	    
	   
	    @GetMapping("/filter")
	    public String getEventsBetweenDates(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
	                                        @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
	                                        Model model) {
	        List<Ticket> tickets = ticketService.findByticketCreatedOnBetween(startDate, endDate);
	        model.addAttribute("tickets", tickets);
	        model.addAttribute("startDate", startDate);
	        model.addAttribute("endDate", endDate);
	        return "filter_tickets";
	    }
	    
	    @GetMapping("/export")
	    public ResponseEntity<byte[]> exportEventsToExcel(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
	                                                      @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws IOException {
	        List<Ticket> tickets = ticketService.findByticketCreatedOnBetween(startDate, endDate);

	        Workbook workbook = new XSSFWorkbook();
	        Sheet sheet = workbook.createSheet("tickets");

	        Row headerRow = sheet.createRow(0);
	        headerRow.createCell(0).setCellValue("ID");
	        headerRow.createCell(1).setCellValue("Title");
	        headerRow.createCell(2).setCellValue("Description");
	        headerRow.createCell(3).setCellValue("Content");
	        headerRow.createCell(4).setCellValue("CreatedOn");
	        

	        int rowNum = 1;
	        for (Ticket ticket : tickets) {
	            Row row = sheet.createRow(rowNum++);
	            row.createCell(0).setCellValue(ticket.getId());
	            row.createCell(1).setCellValue(ticket.getTicketTitle());
	            row.createCell(2).setCellValue(ticket.getTicketShortDescription());
	            row.createCell(3).setCellValue(ticket.getContent());
	            row.createCell(4).setCellValue(ticket.getTicketCreatedOn().toString());
	        }

	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        workbook.write(out);
	        workbook.close();

	        byte[] excelData = out.toByteArray();

	        HttpHeaders headers = new HttpHeaders();
	        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=events.xlsx");
	        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

	        return ResponseEntity.ok()
	                .headers(headers)
	                .body(excelData);
	    }
	    
	    @GetMapping("/exportpdf")
	    public ResponseEntity<byte[]> exportEventsToPdf(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
	                                                    @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws IOException {
	        List<Ticket> tickets = ticketService.findByticketCreatedOnBetween(startDate, endDate);

	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        PdfWriter writer = new PdfWriter(out);
	        com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(writer);
	        Document document = new Document(pdfDoc);

	        float[] columnWidths = {1, 5, 3,4,6};
	        Table table = new Table(columnWidths);

	        table.addCell("ID");
	        table.addCell("Title");
	        table.addCell("Description");
	        table.addCell("Content");
	        table.addCell("CreatedOn");

	        for (Ticket ticket : tickets) {
	        	table.addCell(String.valueOf(ticket.getId()));
	            table.addCell(ticket.getTicketTitle());
	            table.addCell(ticket.getTicketShortDescription().toString());
	            table.addCell(ticket.getContent().toString());
	            table.addCell(ticket.getTicketCreatedOn().toString());
	            
	            
	        }

	        document.add(table);
	        document.close();

	        byte[] pdfData = out.toByteArray();

	        HttpHeaders headers = new HttpHeaders();
	        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=events.pdf");
	        headers.setContentType(MediaType.APPLICATION_PDF);

	        return ResponseEntity.ok()
	                .headers(headers)
	                .body(pdfData);
	    }
	    
	    @GetMapping("/exporttext")
	    public ResponseEntity<byte[]> exportEventsToText(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
	                                                     @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws IOException {
	        List<Ticket> tickets = ticketService.findByticketCreatedOnBetween(startDate, endDate);

	        StringBuilder sb = new StringBuilder();
	        sb.append("ID,Name,Date\n"); // Header

	        for (Ticket ticket : tickets) {
	            sb.append(ticket.getId()).append(",");
	            sb.append(ticket.getTicketTitle()).append(",");
	            sb.append(ticket.getTicketShortDescription()).append(",");
	            sb.append(ticket.getContent()).append(",");
	            sb.append(ticket.getTicketCreatedOn().toString()).append("\n");
	        }

	        byte[] textData = sb.toString().getBytes(StandardCharsets.UTF_8);

	        HttpHeaders headers = new HttpHeaders();
	        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=events.txt");
	        headers.setContentType(MediaType.TEXT_PLAIN);

	        return ResponseEntity.ok()
	                .headers(headers)
	                .body(textData);
	    }
	}
	
