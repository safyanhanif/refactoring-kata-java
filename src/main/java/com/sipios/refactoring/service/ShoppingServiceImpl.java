package com.sipios.refactoring.service;

import com.sipios.refactoring.controller.ShoppingController;
import com.sipios.refactoring.model.Body;
import com.sipios.refactoring.model.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Service
public class ShoppingServiceImpl implements ShoppingService {

    private Logger logger = LoggerFactory.getLogger(ShoppingServiceImpl.class);

    @Override
    public String getPrice(Body body) {
        double price = 0;
        double discount;

        // localDate
        Date date = new Date();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
        cal.setTime(date);

        // enum switch case + inverser
        // Compute discount for customer
        if (body.getClientType().equals("STANDARD_CUSTOMER")) {
            discount = 1;
        } else if (body.getClientType().equals("PREMIUM_CUSTOMER")) {
            discount = 0.9;
        } else if (body.getClientType().equals("PLATINUM_CUSTOMER")) {
            discount = 0.5;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        // sous methode + enlever if et garder que ce qu'il y a dedans
        // Compute total amount depending on the types and quantity of product and
        // if we are in winter or summer discounts periods
        if (
            !(
                cal.get(Calendar.DAY_OF_MONTH) < 15 &&
                    cal.get(Calendar.DAY_OF_MONTH) > 5 &&
                    cal.get(Calendar.MONTH) == 5
            ) &&
                !(
                    cal.get(Calendar.DAY_OF_MONTH) < 15 &&
                        cal.get(Calendar.DAY_OF_MONTH) > 5 &&
                        cal.get(Calendar.MONTH) == 0
                )
        ) {
            if (body.getItems() == null) {
                return "0";
            }

            for (int i = 0; i < body.getItems().length; i++) {
                Item it = body.getItems()[i];

                if (it.getType().equals("TSHIRT")) {
                    price += 30 * it.getQuantity() * discount;
                } else if (it.getType().equals("DRESS")) {
                    price += 50 * it.getQuantity() * discount;
                } else if (it.getType().equals("JACKET")) {
                    price += 100 * it.getQuantity() * discount;
                }
                // else if (it.getType().equals("SWEATSHIRT")) {
                //     price += 80 * it.getNb();
                // }
            }
        } else {
            if (body.getItems() == null) {
                return "0";
            }

            for (int i = 0; i < body.getItems().length; i++) {
                Item it = body.getItems()[i];

                if (it.getType().equals("TSHIRT")) {
                    price += 30 * it.getQuantity() * discount;
                } else if (it.getType().equals("DRESS")) {
                    price += 50 * it.getQuantity() * 0.8 * discount;
                } else if (it.getType().equals("JACKET")) {
                    price += 100 * it.getQuantity() * 0.9 * discount;
                }
                // else if (it.getType().equals("SWEATSHIRT")) {
                //     price += 80 * it.getNb();
                // }
            }
        }

        try {
            if (body.getClientType().equals("STANDARD_CUSTOMER")) {
                if (price > 200) {
                    throw new Exception("Price (" + price + ") is too high for standard customer");
                }
            } else if (body.getClientType().equals("PREMIUM_CUSTOMER")) {
                if (price > 800) {
                    throw new Exception("Price (" + price + ") is too high for premium customer");
                }
            } else if (body.getClientType().equals("PLATINUM_CUSTOMER")) {
                if (price > 2000) {
                    throw new Exception("Price (" + price + ") is too high for platinum customer");
                }
            } else {
                if (price > 200) {
                    throw new Exception("Price (" + price + ") is too high for standard customer");
                }
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return String.valueOf(price);
    }
}
