package ra.librarymanagement.util;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class CriteriaUtil {
    public static <T> Result<T> getResult(EntityManager entityManager, Class<T> clazz) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz);
        Root<T> root = query.from(clazz);
        return new Result<>(cb, query, root);
    }

    public static class Result<T> {
        public final CriteriaBuilder cb;
        public final CriteriaQuery<T> query;
        public final Root<T> root;

        public Result(CriteriaBuilder cb, CriteriaQuery<T> query, Root<T> root) {
            this.cb = cb;
            this.query = query;
            this.root = root;
        }
    }
}
