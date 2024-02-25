package com.Bank.MoneyBank.service.smsService;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsService {
    private static String SID = System.getenv("SID");
    private static String authToken = System.getenv("TWILIO_AUTH_TOKEN");
    private static String from =  System.getenv("TWILIO_PHONE_NUMBER");

    public void sendSms(Sms sms){
        Twilio.init(SID, authToken);
        Message message = Message.creator(new PhoneNumber(sms.getTo()),
                        new PhoneNumber(from),
                        sms.getMessage())
                .create();
        System.out.println(message.getSid());
    }

}
