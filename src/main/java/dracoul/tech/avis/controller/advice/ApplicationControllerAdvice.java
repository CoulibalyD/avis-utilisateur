package dracoul.tech.avis.controller.advice;

import io.jsonwebtoken.MalformedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.security.SignatureException;
import java.util.Map;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestControllerAdvice
public class ApplicationControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(ApplicationControllerAdvice.class);

    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(value = BadCredentialsException.class)
    public @ResponseBody
    ProblemDetail badCredentialsException(final BadCredentialsException exception){
        ApplicationControllerAdvice.log.error(exception.getMessage(), exception);
        ProblemDetail problemeDetail = ProblemDetail.forStatusAndDetail(
                UNAUTHORIZED,
                "identifiant invalides");
        problemeDetail.setProperty("erreur","nous avons pas pu vous identifier!!!");
        return problemeDetail;
    }

    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(value = {MalformedJwtException.class, SignatureException.class})
    public @ResponseBody
    ProblemDetail signatureException(Exception exception){
        ApplicationControllerAdvice.log.error(exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(
                UNAUTHORIZED,
                "Token invalide");
    }

    @ResponseStatus(FORBIDDEN)
    @ExceptionHandler(value =  AuthorizationDeniedException.class)
    public @ResponseBody
    ProblemDetail accessDeniedException(final AuthorizationDeniedException exception){
        ApplicationControllerAdvice.log.error(exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(
                FORBIDDEN,
                "Vous n'avez pas les droits pour faire cette action");
    }

    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(value = Exception.class)
    public Map<String, String> exceptionsHandler(){
        return Map.of("erreur", "exception");
    }
}
