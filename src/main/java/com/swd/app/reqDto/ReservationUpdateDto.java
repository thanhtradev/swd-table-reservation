package com.swd.app.reqDto;

import lombok.Data;

@Data
public class ReservationUpdateDto {
    private Long reservationId;
    private String status;
}
