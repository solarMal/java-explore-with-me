package ru.practicum.aop;

import lombok.AllArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ru.practicum.stat.StatService;

import javax.servlet.http.HttpServletRequest;

@Component
@Aspect
@AllArgsConstructor
public class SaveStatisticAspect {

    private StatService statService;
    private HttpServletRequest request;

    @AfterReturning(pointcut = "@annotation(ru.practicum.aop.SaveHitToStats)")
    public void hit() {
        statService.hit(request);
    }
}