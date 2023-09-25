/**
 *     Copyright (C) 2019-2023 Ubiqube.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.ubiqube.mano.service.search.jpa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ubiqube.etsi.mano.grammar.Node;
import com.ubiqube.etsi.mano.grammar.Node.Operand;
import com.ubiqube.mano.service.search.ManoSearch;
import com.ubiqube.mano.service.search.SearchException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class JpaSearch implements ManoSearch {
	private final EntityManager em;

	public JpaSearch(final EntityManager em) {
		this.em = em;
	}

	@Override
	public <T> List<T> getCriteria(final List<Node<?>> nodes, final Class<T> clazz) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<T> cq = cb.createQuery(clazz);
		final Root<T> itemRoot = cq.from(clazz);
		final Predicate pred = getCriteria(cb, nodes, itemRoot, Map.of());
		return em.createQuery(cq).getResultList();
	}

	public <T> Predicate getCriteria(final CriteriaBuilder cb, final List<Node<?>> nodes, final Root<T> root, final Map<String, From<?, ?>> joins) {
		final List<Predicate> predicates = new ArrayList<>();
		for (final Node<?> node : nodes) {
			final Optional<Predicate> res = applyOp(node.getName(), node.getOp(), node.getValue(), joins);
			if (res.isPresent()) {
				predicates.add(res.get());
			}
		}
		if (!predicates.isEmpty()) {
			return cb.and(predicates.toArray(new Predicate[0]));
		}
		return null;
	}

	private <U> Optional<Predicate> applyOp(final String name, final Operand op, final U value, final Map<String, From<?, ?>> joins) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final Attr attr = getParent(name, joins);
		final From<?, ?> p = attr.parent.orElse(joins.get("ROOT"));
		final Predicate pred = switch (op) {
		case EQ -> cb.equal(p.get(attr.name), value);
		case NEQ -> cb.notEqual(p.get(attr.name), value);
		case GT -> cb.greaterThan(p.get(attr.name), value.toString());
		case GTE -> cb.greaterThanOrEqualTo(p.get(attr.name), value.toString());
		case LT -> cb.lessThan(p.get(attr.name), value.toString());
		case LTE -> cb.lessThanOrEqualTo(p.get(attr.name), value.toString());
		case CONT, NCONT, IN, NIN -> throw new SearchException("Unknown query Op: " + op);
		};
		return Optional.ofNullable(pred);
	}

	private Attr getParent(final String name, final Map<String, From<?, ?>> joins) {
		final String[] arr = name.split("\\/");
		final Attr attr = new Attr();
		if (arr.length > 1) {
			final String[] ro = new String[arr.length - 1];
			System.arraycopy(arr, 0, ro, 0, ro.length);
			final String key = Arrays.asList(ro).stream().collect(Collectors.joining("."));
			attr.parent = Optional.ofNullable(joins.get(key));
		}
		attr.name = arr[arr.length - 1];
		return attr;
	}

	private static class Attr {
		private String name;
		private Optional<From<?, ?>> parent = Optional.empty();
	}

}
