package org.sitenv.spring.auth;

import javax.servlet.http.HttpSession;

import org.aspectj.lang.JoinPoint;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

//@Aspect
public class Secure {

    //   @Before("execution(* gov..*.*(..))")
    public void checkLogin(JoinPoint joinPoint) {
     //   System.out.println("ASPECT: Checking login, Args: " + joinPoint.getArgs().length);

        HttpSession s = (HttpSession) RequestContextHolder
                .currentRequestAttributes()
                .resolveReference(RequestAttributes.REFERENCE_SESSION);

        if (s != null) {
            System.out.println("ASPECT: Session found");
        }
    }
}
