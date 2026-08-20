package com.app.user_service.repository.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import com.app.user_service.domain.model.User;

public class UserSpecifications {
  public static Specification<User> search(String search, Long roleId, Boolean showDeleted) {

    return (root, cq, cb) -> {
      List<Predicate> predicates = new ArrayList<>();
      if (showDeleted != null && showDeleted) {
        predicates.add(cb.isNotNull(root.get("deletedAt")));
      } else {
        predicates.add(cb.isNull(root.get("deletedAt")));
      }

      if (search != null && !search.isEmpty()) {
        String q = "%" + search.toLowerCase() + "%";
        predicates.add(cb.or(
            cb.like(cb.lower(root.get("username")), q),
            cb.like(cb.lower(root.get("name")), q),
            cb.like(cb.lower(root.get("email")), q)));
      }

      if (roleId != null) {
        predicates.add(cb.equal(root.get("role").get("id"), roleId));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}