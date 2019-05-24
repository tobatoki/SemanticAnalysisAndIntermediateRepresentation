import java.util.*;

public class SemanticAnalyser implements calVisitor {

	private static Hashtable<String, LinkedHashSet<String>> matching = new Hashtable<>();
	private static String scope = "global";
	private static HashSet<String> func_summoned = new HashSet<>();
	private static SymbolTable symbol_table;

	public Object visit(SimpleNode node, Object data) {
		throw new RuntimeException("Visit SimpleNode");
	}

	private static void setSymbolTable(Object data) {
		symbol_table = (SymbolTable) (data);
	}

	public Object visit(Program node, Object data) {
		setSymbolTable(data);
		int num;
		num = node.jjtGetNumChildren();
		for (int i = 0; i < num; i++) {
			node.jjtGetChild(i).jjtAccept(this, data);
		}
		matching_Checker();
		func_summoned_analyser();
		return data;
	}

	private static void matching_Checker() {
		if (matching == null || matching.isEmpty()) {
			System.out.println("No duplicates detected");
		} else {
			Enumeration e = matching.keys();
			while (e.hasMoreElements()) {
				String scope;
				scope = (String) e.nextElement();
				LinkedHashSet<String> match = matching.get(scope);
				Iterator k;
				k = match.iterator();
				System.out.print("WARNING: Multiple declarations of [");
				while (k.hasNext()) {
					System.out.print(" " + k.next());
				}
				System.out.println(" ] in " + scope);
			}
		}
	}

	private void func_summoned_analyser() {
		ArrayList<String> funcs = symbol_table.funcs_list();
		for (int i = 0; i < funcs.size(); i++) {
			if (!func_summoned.contains(funcs.get(i))) {
				System.out.println("WARNING: (" + funcs.get(i) + ") is never invoked");
			}
		}
	}


	private static void dups_Check(String id, String scope) {
		if (!symbol_table.noMatching(id, scope)) {
			HashSet<String> match;
			match = matching.get(scope);
			if (match == null) {
				LinkedHashSet<String> set;
				set = new LinkedHashSet<>();
				set.add(id);
				matching.put(scope, set);
			} else {
				match.add(id);
			}
		}

		if (!symbol_table.noMatching(id, "global")) {
			HashSet<String> match = matching.get(scope);
			if (match == null) {
				LinkedHashSet<String> set = new LinkedHashSet<>();
				set.add(id);
				matching.put(scope, set);
			} else {
				match.add(id);
			}
		}
	}

	public Object visit(Var_Decl node, Object data) {
		String id;
		id = (String) node.jjtGetChild(0).jjtAccept(this, data);
		String category;
		category = (String) node.jjtGetChild(1).jjtAccept(this, data);
		dups_Check(id, scope);
		return data;
	}

	public Object visit(Identifier node, Object data) {
		return node.value;
	}

	public Object visit(Const_Decl node, Object data) {
		String id;
		id = (String) node.jjtGetChild(0).jjtAccept(this, data);
		dups_Check(id, scope);
		String category;
		category = (String) node.jjtGetChild(1).jjtAccept(this, data);
		return data;
	}

	public Object visit(Main node, Object data) {
		this.scope = "main";
		int num;
		num = node.jjtGetNumChildren();
		for (int i = 0; i < num; i++) {
			node.jjtGetChild(i).jjtAccept(this, data);
		}
		return data;
	}

	public Object visit(Function node, Object data) {
		this.scope = (String) node.jjtGetChild(1).jjtAccept(this, data);
		int num;
		num = node.jjtGetNumChildren();
		for (int i = 0; i < num; i++) {
			node.jjtGetChild(i).jjtAccept(this, data);
		}
		return data;
	}

	public Object visit(Ret node, Object data) {
		return data;
	}

	public Object visit(FunctionRet node, Object data) {
		return node.value;
	}

	public Object visit(Type node, Object data) {
		return node.value;
	}

	public Object visit(Parameter_list node, Object data) {
		int num;
		num = node.jjtGetNumChildren();
		for (int i = 0; i < num; i++) {
			node.jjtGetChild(i).jjtAccept(this, data);
		}
		return data;
	}

	private static boolean declare(String id, String scope) {
		LinkedList<String> checkList;
		checkList = symbol_table.scopeGetter(scope);
		LinkedList<String> global_checkList;
		global_checkList = symbol_table.scopeGetter("global");
		if (checkList != null) {
			if (!global_checkList.contains(id) && !checkList.contains(id)) {
				return false;
			}
		}
		return true;
	}

	public Object visit(Assignment node, Object data) {
		return data;
	}

	public Object visit(FunctionAssignment node, Object data) {
		return data;
	}

	public Object visit(AddOp node, Object data) {
		return "+";
	}

	public Object visit(MinusOp node, Object data) {
		return "-";
	}

	public Object visit(Num node, Object data) {
		return node.value;
	}

	public Object visit(Bool node, Object data) {
		return node.value;
	}

	public Object visit(EQOperator node, Object data) {
		return node.value;
	}

	public Object visit(NEQOperator node, Object data) {
		return node.value;
	}

	public Object visit(LTOperator node, Object data) {
		return node.value;
	}

	public Object visit(LTEOperator node, Object data) {
		return node.value;
	}

	public Object visit(GTOperator node, Object data) {
		return node.value;
	}

	public Object visit(GTEOperator node, Object data) {
		return node.value;
	}

	public Object visit(OROperator node, Object data) {
		return node.value;
	}

	public Object visit(ANDOperator node, Object data) {
		return node.value;
	}

	public Object visit(Arg_List node, Object data) {
		int num;
		num = node.jjtGetNumChildren();
		for (int i = 0; i < num; i++) {
			node.jjtGetChild(i).jjtAccept(this, data);
		}
		return data;
	}

	public Object visit(Comp_Op node, Object data) {
		node.childrenAccept(this, data);
		return node.value;
	}

	public Object visit(Statement node, Object data) {

		String id;
		String category;
		String categoryDescription;
		String right_hand_side;
		int param_args;
		int arguments;
		String arg;
		String argumentType;
		String expected_category;
		Node argumentList;
		String name_of_function;
		String function_return;

		if (node.jjtGetNumChildren() > 0) {

			id = (String) node.jjtGetChild(0).jjtAccept(this, data);

			try {
				if (symbol_table.defFunc(id)) {
					func_summoned.add(id);
				}

				if (declare(id, scope)) {
					category = symbol_table.categoryGetter(id, scope);
					categoryDescription = symbol_table.categoryDescriptionGetter(id, scope);

					if (categoryDescription.equals("constant")) {
						System.out.println("WARNING: " + id + " is a constant and cannot be redeclared");
					}

					else {
						right_hand_side = node.jjtGetChild(1).toString();

						if (category.equals("integer")) {
							if (right_hand_side.equals("Num")) {
								node.jjtGetChild(1).jjtAccept(this, data);
							}

							else if (right_hand_side.equals("Bool")) {
								System.out.println("WARNING: Expected category type 'integer' for  (" + id
										+ ") instead received 'boolean'");
							} else if (right_hand_side.equals("FunctionRet")) {
								name_of_function = (String) node.jjtGetChild(1).jjtAccept(this, data);

								if (!declare(name_of_function, "global") && !declare(name_of_function, scope)) {
									System.out.println("WARNING: (" + name_of_function + ") is not declared");
								}

								else if (symbol_table.defFunc(name_of_function)) {
									func_summoned.add(name_of_function);
									function_return = symbol_table.categoryGetter(name_of_function, "global");
									if (!function_return.equals("integer")) {
										System.out.println("WARNING: Expected return category type integer instead got "
												+ function_return);
									}

									param_args = symbol_table.parameterGetter(name_of_function);
									arguments = node.jjtGetChild(1).jjtGetChild(0).jjtGetNumChildren();

									if (param_args != arguments)
										System.out.println("WARNING: (" + param_args + ") needed, received ("
												+ arguments + ") instead");

									else if (param_args == arguments) {
										argumentList = node.jjtGetChild(1).jjtGetChild(0);

										for (int i = 0; i < argumentList.jjtGetNumChildren(); i++) {
											arg = (String) argumentList.jjtGetChild(i).jjtAccept(this, data);

											if (declare(arg, scope)) {
												argumentType = symbol_table.categoryGetter(arg, scope);
												expected_category = symbol_table.parameter_type_getter(i + 1,
														name_of_function);

												if (!argumentType.equals(expected_category)) {
													System.out.println("WARNING: " + arg + " is a category type "
															+ argumentType + ". Was expecting category type "
															+ expected_category);
												}

											} else {
												System.out.println(
														"WARNING: " + arg + " has not been declared in this scope");
											}
										}
									}
								}
							}

						}

						else if (category.equals("boolean")) {

							if (right_hand_side.equals("Bool")) {
								node.jjtGetChild(1).jjtAccept(this, data);
							} else if (right_hand_side.equals("Num")) {
								System.out.println("WARNING: Expected category type 'boolean' for (" + id
										+ ") instead received an 'integer'");
							} else if (right_hand_side.equals("FunctionRet")) {
								name_of_function = (String) node.jjtGetChild(1).jjtAccept(this, data);

								if (!declare(name_of_function, "global")) {
									System.out.println("WARNING: (" + name_of_function + ") is not declared");
								} else {
									function_return = symbol_table.categoryGetter(name_of_function, "global");

									if (!function_return.equals("boolean")) {
										System.out.println("WARNING: Expected category type 'boolean' instead received "
												+ function_return);
									}
								}
							}
						}
					}

				} else if (!declare(id, scope)) {
					System.out.println("WARNING: (" + id + ") has to be declared before it can be used");
				}
			}

			catch (NullPointerException e) {
				System.out.println("(" + id + ") needs to be declared before use");
			}
		}
		return data;
	}

}
