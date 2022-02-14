package dev.natus.cqrs.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResponseMessage {
    private Date date;
    private String message;
    private Object result;


    public static ResponseMessage responseMessage(String message,
                                                  Object object) {
        return new ResponseMessage(new Date(), message, object);
    }
}
