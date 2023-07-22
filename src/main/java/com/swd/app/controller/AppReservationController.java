package com.swd.app.controller;

import com.swd.app.reqDto.BookReservationDto;
import com.swd.app.reqDto.ReservationUpdateDto;
import com.swd.cms.mapper.ReservationMapper;
import com.swd.common.BaseController;
import com.swd.constraints.EReservationStatus;
import com.swd.entities.Reservation;
import com.swd.entities.Tables;
import com.swd.entities.User;
import com.swd.exception.BadRequestException;
import com.swd.model.dto.ApiMessageDto;
import com.swd.services.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/app/reservation")
@Tag(name = "App")
public class AppReservationController extends BaseController {
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private TableService tableService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ReservationMapper mapper;
    @Autowired
    private UserService userService;

    @Transactional
    @PostMapping("/book")
    public ApiMessageDto<Object> bookTable(@Valid @RequestBody BookReservationDto bookReservationDto) {
        // Check if user is logged in
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails == null) {
            throw new BadRequestException("User is not logged in");
        }
        Long userId = userDetails.getId();
        // Check if table exists
        if (Boolean.FALSE.equals(tableService.existsById(bookReservationDto.getTableId()))) {
            throw new BadRequestException("Table does not exist");
        }
        // Check if user exists
        if (Boolean.FALSE.equals(userService.existsById(userId))) {
            throw new BadRequestException("User does not exist");
        }
        User user = userService.getById(userId);
        // Check if table is booked
        if (Boolean.TRUE.equals(tableService.isBooked(bookReservationDto.getTableId()))) {
            throw new BadRequestException("Table is already booked");
        }
        // Check if start time is before end time
        if (bookReservationDto.getStartTime().after(bookReservationDto.getEndTime())) {
            throw new BadRequestException("Start time is after end time");
        }
        Tables table = tableService.getById(bookReservationDto.getTableId());
        // Check if party size is greater than table capacity
        if (bookReservationDto.getPartySize() > table.getCapacity()) {
            throw new BadRequestException("Party size is greater than table capacity");
        }
        Reservation reservation = modelMapper.map(bookReservationDto, Reservation.class);
        // Set table to booked
        reservation.setTable(table);
        reservation.setUser(user);
        reservation.setStatus(EReservationStatus.PENDING);
        reservation.setReservationDate(new Date());
        Reservation savedReservation = reservationService.addReservation(reservation);
        return makeResponse(true, mapper.fromEntityToBookReservationDto(savedReservation), "Reservation added successfully");
    }

    // Update reservation status
    @Transactional
    @PostMapping("/update")
    public ApiMessageDto<Object> updateReservationStatus(@Valid @RequestBody ReservationUpdateDto reservationUpdateDto) {
        Reservation reservation = reservationService.getById(reservationUpdateDto.getReservationId());
        if (reservation == null) {
            throw new BadRequestException("Reservation does not exist");
        }
        if (Boolean.TRUE.equals(reservationService.isValidStatus(reservationUpdateDto.getStatus()))) {
            throw new BadRequestException("Invalid status");
        }
        reservation.setStatus(EReservationStatus.valueOf(reservationUpdateDto.getStatus()));
        Reservation updatedReservation = reservationService.addReservation(reservation);
        // Find all reservation with status pending and has same start time and end time as updated reservation
        // If there is any, set status to rejected
        reservationService.rejectReservation(updatedReservation);
        return makeResponse(true, mapper.fromEntityToReservationDto(updatedReservation), "Reservation updated successfully");
    }

    @GetMapping("/get-by-date")
    ApiMessageDto<Object> getReservationByDate(@RequestParam String dateStr) {
        String dateFormat = "dd-MM-yyyy";
        DateFormat formatter = new SimpleDateFormat(dateFormat);
        try {
            Date date = formatter.parse(dateStr);
            return makeResponse(true, mapper.fromEntityToReservationDtoList(reservationService.getAllByDate(date)), "Reservation retrieved successfully");
        } catch (Exception e) {
            throw new BadRequestException("Invalid date format");
        }

    }
}
