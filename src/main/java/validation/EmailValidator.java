package validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for validating email addresses.
 * Implements the {@link ConstraintValidator} interface to define custom validation logic for email fields.
 */
public class EmailValidator implements ConstraintValidator<ValidEmail, String> {

    /**
     * Regular expression pattern for validating email addresses.
     * The pattern supports most valid email formats, including subdomains and internationalized top-level domains.
     */
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                    "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    /**
     * Initializes the validator. This method is called during the setup of the constraint validation.
     *
     * @param constraintAnnotation The annotation instance for the constraint being validated.
     */
    @Override
    public void initialize(ValidEmail constraintAnnotation) {
        // No initialization required for this validator.
    }

    /**
     * Validates the given email address against the defined email pattern.
     *
     * @param email   The email address to validate.
     * @param context Context in which the constraint is evaluated.
     * @return True if the email address matches the pattern, false otherwise.
     */
    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null) {
            return false; // Null email is invalid
        }
        return email.matches(EMAIL_PATTERN);
    }
}
