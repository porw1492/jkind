package jkind.translation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jkind.lustre.Node;
import jkind.lustre.Type;
import jkind.lustre.VarDecl;

public class Util {
	public static List<VarDecl> getVarDecls(Node node) {
		List<VarDecl> decls = new ArrayList<VarDecl>();
		decls.addAll(node.inputs);
		decls.addAll(node.outputs);
		decls.addAll(node.locals);
		return decls;
	}

	public static Map<String, Type> createTypeMap(Node node) {
		Map<String, Type> map = new HashMap<String, Type>();
		for (VarDecl v : getVarDecls(node)) {
			map.put(v.id, v.type);
		}
		return map;
	}
}
