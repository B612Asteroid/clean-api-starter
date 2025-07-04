package org.starter.api.core.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 메소드 시작과 끝에 시작 / 끝 로그를 찍는다
 */

@Aspect
@Component
class LogAspect {

    private val log = LoggerFactory.getLogger(LogAspect::class.java)

    @Around("execution(* org.starter.api..*Service.*(..)) || within(*..*UseCase)")
    @Throws(Throwable::class)
    fun serviceLogAdvice(pjp: ProceedingJoinPoint): Any? {
        val className = pjp.target.javaClass.simpleName
        val type = when {
            className.endsWith("Service") -> "SERVICE"
            className.endsWith("UseCase")    -> "USE_CASE"
            else -> "Unknown"
        }

        log.debug("############# [{}] {} | {} START #########", type, className, pjp.signature.name)

        val om = ObjectMapper()
        val args = pjp.args
        args.forEachIndexed { i, obj ->
            try {
                @Suppress("UNCHECKED_CAST")
                val params = om.convertValue(obj, Map::class.java) as Map<String, Any?>
                log.debug(">> args[$i] => Data Object >> ")
                params.forEach { (key, value) ->
                    log.debug("  ㄴ {} : {}", key, value)
                }
                log.debug(">> args[$i] END <<")
            } catch (e: IllegalArgumentException) {
                log.debug("args[$i] => {}", obj)
            } catch (e: NullPointerException) {
                log.debug("args[$i] => {}", obj)
            }
        }

        val result = pjp.proceed()
        log.debug("############# [{}] {} | {} END #########", type, className, pjp.signature.name)
        return result
    }
}
