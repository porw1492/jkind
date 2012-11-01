package jkind.translation;

import java.util.Map;

import jkind.lustre.Expr;
import jkind.lustre.IdExpr;

public class SubstitutionVisitor extends MapVisitor {
	private Map<String, ? extends Expr> map;

	public SubstitutionVisitor(Map<String, ? extends Expr> map) {
		this.map = map;
	}

	@Override
	public Expr visit(IdExpr e) {
		if (map.containsKey(e.id)) {
			return map.get(e.id);
		} else {
			return e;
		}
	}
}