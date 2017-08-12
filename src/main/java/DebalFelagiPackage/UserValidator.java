package DebalFelagiPackage;

import DebalFelagiPackage.UserRepository;
import DebalFelagiPackage.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {
    @Autowired
    UserRepository userRepository;

    public boolean supports(Class<?> clazz){
        return User.class.isAssignableFrom(clazz);
    }
    public void validate(Object target, Errors errors){
        User user = (User) target;
        String email = user.getEmail();
        String username = user.getUsername();
        long zipcode = user.getZipCode();
        String password = user.getPassword();
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "fullName", "user.fullName.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "user.username.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "user.email.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "user.password.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "zipCode", "user.zipCode.empty");

        if(username.length() < 5){
            errors.rejectValue("username","user.username.tooShort");
        }
        if(zipcode < 1000 || zipcode > 99999){
            errors.rejectValue("zipCode","user.zipCode.error");
        }
        if(password.length() < 5){
            errors.rejectValue("password","user.password.tooShort");
        }
        if(userRepository.countByEmail(email)>0){
            errors.rejectValue("email", "user.email.duplicate");
        }
        if(userRepository.countByUsername(username)>0){
            errors.rejectValue("username", "user.username.duplicate");
        }
    }
}