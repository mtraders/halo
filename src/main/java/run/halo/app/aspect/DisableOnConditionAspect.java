package run.halo.app.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import run.halo.app.annotation.DisableOnCondition;
import run.halo.app.config.properties.HaloProperties;
import run.halo.app.exception.ForbiddenException;
import run.halo.app.model.enums.Mode;

/**
 * 自定义注解DisableApi的切面.
 *
 * @author guqing
 * @date 2020-02-14 14:08
 */
@Aspect
@Component
public class DisableOnConditionAspect {

    private final HaloProperties haloProperties;

    public DisableOnConditionAspect(HaloProperties haloProperties) {
        this.haloProperties = haloProperties;
    }

    @Pointcut("within(run.halo.app.controller..*)")
    public void pointcut() {
    }

    /**
     * around for disable api.
     *
     * @param joinPoint joinPoint
     * @param disableApi disable api.
     * @return object containing.
     * @throws Throwable throws exception.
     */
    @Around("pointcut() && @annotation(disableApi)")
    public Object around(ProceedingJoinPoint joinPoint,
        DisableOnCondition disableApi) throws Throwable {
        Mode mode = disableApi.mode();
        if (haloProperties.getMode().equals(mode)) {
            throw new ForbiddenException("禁止访问");
        }

        return joinPoint.proceed();
    }
}
