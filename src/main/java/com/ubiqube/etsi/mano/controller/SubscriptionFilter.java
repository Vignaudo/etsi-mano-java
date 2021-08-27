package com.ubiqube.etsi.mano.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.commons.beanutils.BeanUtils;

import com.ubiqube.etsi.mano.exception.GenericException;
import com.ubiqube.etsi.mano.grammar.EtsiLexer;
import com.ubiqube.etsi.mano.grammar.Etsifilter;
import com.ubiqube.etsi.mano.grammar.Node;
import com.ubiqube.etsi.mano.grammar.Node.Operand;
import com.ubiqube.etsi.mano.grammar.TreeBuilder;
import com.ubiqube.etsi.mano.model.vnf.sol005.SubscriptionObject;
import com.ubiqube.etsi.mano.model.vnf.sol005.SubscriptionsPkgmSubscription;

/**
 * Handle filter parsing and filering on objects for VNFPackage Subscription.
 *
 * @author ovi@ubiqube.com
 *
 */
public class SubscriptionFilter {

	public List<Node> getFilters(String query) {
		final TreeBuilder treeBuilder = new TreeBuilder();
		final EtsiLexer el = new EtsiLexer(new ANTLRInputStream(query));
		final CommonTokenStream tokens = new CommonTokenStream(el);
		final Etsifilter parser = new Etsifilter(tokens);
		parser.addParseListener(treeBuilder);
		parser.filterExpr();

		return treeBuilder.getListNode();
	}

	/**
	 * In fact this method could be more generic by taking an Object.
	 *
	 * @param subscriptionRepository
	 * @param nodes
	 * @return
	 */
	public boolean apply(SubscriptionObject subscriptionRepository, List<Node> nodes) {
		final SubscriptionsPkgmSubscription pkgSub = subscriptionRepository.getSubscriptionsPkgmSubscription();
		for (final Node node : nodes) {
			if (!apply(pkgSub, node)) {
				return false;
			}
		}

		return true;
	}

	private static boolean apply(SubscriptionsPkgmSubscription _pkgSub, Node node) {
		try {
			final String objectValue = BeanUtils.getNestedProperty(_pkgSub, node.getName());
			return decide(objectValue, node.getValue(), node.getOp());
		} catch (final InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
			throw new GenericException(e);
		}
	}

	private static boolean decide(String _objectValue, String _value, Operand operand) {
		if (Operand.EQ.equals(operand)) {
			return _value.equals(_objectValue);
		}
		if (Operand.NEQ.equals(operand)) {
			return !_value.equals(_objectValue);
		}
		// GT LT GTE LTE are numerical so cast everything in integer
		final int left = Integer.parseInt(_objectValue);
		final int right = Integer.parseInt(_value);
		if (Operand.GT.equals(operand)) {
			return left > right;
		}
		if (Operand.LT.equals(operand)) {
			return left < right;
		}
		if (Operand.GTE.equals(operand)) {
			return left >= right;
		}
		if (Operand.LTE.equals(operand)) {
			return left <= right;
		}
		return false;
	}
}
