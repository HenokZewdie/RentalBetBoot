package DebalFelagiPackage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class HouseValidator implements Validator {
    @Autowired
    HouseRepository houseRepository;
    public boolean supports(Class<?> clazz){
        return House.class.isAssignableFrom(clazz);
    }
    public void validate(Object target, Errors errors){

        House house = (House) target;
        String street = house.getStreet();
        String city = house.getCity();
        String state = house.getState();
        long  zipCode = house.getZipCode();
        String type = house.getType();
        String bath = house.getBath();
        String kitchen = house.getKitchen();
        String duration = house.getDuration();
        String photo = house.getPhoto();
        String remark = house.getRemark();
        long phone = house.getPhone();
        double price = house.getPrice();

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "street", "house.street.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "city", "house.city.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "state", "house.state.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "zipCode", "house.zipCode.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "type", "house.type.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "bath", "house.bath.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "kitchen", "house.kitchen.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "duration", "house.duration.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "photo", "house.photo.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "remark", "house.remark.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phone", "house.phone.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "price", "house.price.empty");


        if(zipCode < 1000 || zipCode > 99999){
            errors.rejectValue("zipCode","user.zipCode.error");
        }

    }
}