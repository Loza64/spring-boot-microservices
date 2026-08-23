package com.app.user_service.infrastructure.persistence.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.app.user_service.domain.model.Role;

import jakarta.persistence.criteria.Predicate;

public class RoleSpecifications {

  public static Specification<Role> search(String search, Boolean deleted) {
    return (root, cq, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (deleted != null && deleted) {
        predicates.add(cb.isNotNull(root.get("deletedAt")));
      } else {
        predicates.add(cb.isNull(root.get("deletedAt")));
      }

      if (search != null && !search.isEmpty()) {
        predicates.add(cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%"));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
