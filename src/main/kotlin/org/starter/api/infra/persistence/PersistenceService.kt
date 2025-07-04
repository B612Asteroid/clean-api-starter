package org.starter.api.infra.persistence

import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.starter.api.core.error.PersistenceException
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.util.*

/**
 * Persistence service
 *
 * @property em
 * @constructor Create empty Persistence service
 */
@Service("persistenceService")
class PersistenceService(private val em: EntityManager) {
    private val log: Logger = LoggerFactory.getLogger(PersistenceService::class.java)

    /**
     * 오브젝트가 뭐든 간에 저장한다.
     *
     * @param obj
     * @param <T>
     * @return
     * @throws PersistenceException
    </T> */
    @Transactional(rollbackFor = [Exception::class])
    @Throws(PersistenceException::class)
    fun <T> save(obj: T): T {
        if (obj !is Persistable) {
            throw PersistenceException("해당 클래스는 최상위 클래스의 자식 클래스가 아닙니다.")
        }
        if ((obj as Persistable).isNew) {
            em.persist(obj)
        } else {
            em.merge<T?>(obj)
        }
        return obj
    }

    /**
     * 강제 인서트를 진행한다.
     *
     * @param obj
     * @param <T>
     * @return
     * @throws PersistenceException
    </T> */
    @Transactional(rollbackFor = [Exception::class])
    @Throws(PersistenceException::class)
    fun <T> insert(obj: T?): T? {
        if (obj !is Persistable) {
            throw PersistenceException("해당 클래스는 최상위 클래스의 자식 클래스가 아닙니다.")
        }
        em.persist(obj)
        return obj
    }

    /**
     * 오브젝트 리스트를 공통으로 저장한다.
     *
     * @param objs
     * @return
     * @throws PersistenceException
     */
    @Transactional(rollbackFor = [Exception::class])
    @Throws(PersistenceException::class)
    fun save(objs: MutableList<out Persistable>): MutableList<out Persistable> {
        for (obj in objs) {
            if (obj.isNew) {
                em.persist(obj)
            } else {
                em.merge(obj)
            }
        }
        return objs
    }

    /**
     * 식별자에 해당하는 데이터를 가져온다.
     *
     * @param clazz
     * @param id
     * @param <T>
     * @return
    </T> */
    fun <T> find(clazz: Class<T>, id: Long): T? {
        return em.find<T>(clazz, id)
    }

    /**
     * oid로부터 클래스를 추출, proxy객체를 반환한다.
     *
     * @param oid
     * @return
     * @throws PersistenceException
     */
    @Throws(PersistenceException::class)
    fun getReference(oid: String): Persistable {
        val bulksStrings: Array<String> = oid.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        try {
            val clazz = Class.forName(bulksStrings[0])
            val persistable = clazz.getConstructor().newInstance() as Persistable
            return em.getReference(persistable.javaClass, bulksStrings[1])
        } catch (e: ClassNotFoundException) {
            throw PersistenceException(e.message)
        } catch (e: NoSuchMethodException) {
            throw PersistenceException(e.message)
        } catch (e: InstantiationException) {
            throw PersistenceException(e.message)
        } catch (e: IllegalAccessException) {
            throw PersistenceException(e.message)
        } catch (e: InvocationTargetException) {
            throw PersistenceException(e.message)
        }
    }

    /**
     * 필드변수와 아이디로 필드 검색
     *
     * @param clazz
     * @param sourceId
     * @param <T>
     * @return
    </T> */
    @Throws(PersistenceException::class)
    fun <T> findLinkBy(clazz: Class<T?>, sourceId: Long?): Optional<T?> {
        return em.createQuery<T?>(this.buildFindByQuery<T?>(clazz, sourceId))
            .setMaxResults(1)
            .getResultStream()
            .findFirst()
    }

    /**
     * 필드 변수와 해당 필드 값을 가지고 데이터 검색
     *
     * @param clazz
     * @param sourceId
     * @param <T>
     * @return
    </T> */
    @Throws(PersistenceException::class)
    fun <T : Persistable?> findAllLinkBy(
        clazz: Class<T?>,
        sourceId: Long?
    ): MutableList<T?> {
        return em.createQuery<T?>(this.buildFindByQuery<T?>(clazz, sourceId)).getResultList()
    }


    /**
     * 객체를 삭제한다.
     * DB row를 직접 삭제하니 사용 시 주의!
     *
     * @param persistable
     * @throws PersistenceException
     */
    @Transactional(rollbackFor = [Exception::class])
    @Throws(PersistenceException::class)
    fun delete(persistable: Persistable?) {
        em.remove(persistable)
    }


    /**
     * source에 해당하는 PersistableLink를 찾아 모두 지운다.
     *
     * @param clazz
     * @param source
     * @param <T>
     * @throws PersistenceException
    </T> */
    @Transactional(rollbackFor = [Exception::class])
    @Throws(PersistenceException::class)
    fun <T : Persistable?> deleteBySource(clazz: Class<T?>, source: Persistable?) {
        if (source == null || source.isNew) {
            log.warn("source는 반드시 아이디를 가지고 있어야 합니다.")
            return
        }

        val targets = findAllLinkBy<T?>(clazz, source.id)
        for (t in targets) {
            delete(t)
        }
    }


    /**//////////////////////// #. private //////////////////////// */
    /**
     * (non-Javadoc)
     * 필드명과 아이디를 가지고 클래스를 찾아오는 동적 쿼리 생성
     *
     * @param clazz
     * @param sourceId
     * @ return CriteriaQuery<T>
    </T> */
    @Throws(PersistenceException::class)
    private fun <T> buildFindByQuery(clazz: Class<T?>, sourceId: Long?): CriteriaQuery<T?>? {
        if (!hasDeclaredField(clazz)) {
            throw PersistenceException("PersistableLink의 상속 클래스는 반드시 'source' 필드를 선언해야 합니다.")
        }

        val cb: CriteriaBuilder = em.getCriteriaBuilder()
        val query: CriteriaQuery<T?> = cb.createQuery<T?>(clazz)
        val root: Root<T?> = query.from<T?>(clazz)

        val predicates: MutableList<Predicate?> = ArrayList<Predicate?>()
        predicates.add(cb.equal(root.get<Any?>("source").get<Any?>("id"), sourceId))

        return query.select(root).where(*predicates.toTypedArray<Predicate?>())
    }

    /**
     * (non-javadoc)
     * Link 클래스로 부터 source가 있는지 검사한다.
     *
     * @param clazz
     * @return
     */
    private fun hasDeclaredField(clazz: Class<*>): Boolean {
        return Arrays.stream(clazz.getDeclaredFields())
            .anyMatch { field: Field -> field.name == "source" }
    }
}
