import java.util.*;

public class SymbolTable {

	private Hashtable<String, LinkedList<String>> st;
	private Hashtable<String, String> itype;
	private Hashtable<String, String> ivalue;

	SymbolTable() {
		st = new Hashtable<>();
		itype = new Hashtable<>();
		ivalue = new Hashtable<>();

		st.put("global", new LinkedList<>());
	}

	public void getter(String id, String category, String scope) {
		LinkedList<String> checkList;
		checkList = st.get(scope);
		if (checkList == null) {
			System.out.println("Variable (" + id + ") is not declared in " + scope);
		}
	}

	public String categoryDescriptionGetter(String id, String scope) {
		return ivalue.get(id + scope);
	}

	public String categoryGetter(String id, String scope) {
		return itype.get(id + scope);
	}

	public LinkedList<String> scopeGetter(String scope) {
		return st.get(scope);
	}

	public int parameterGetter(String id) {
		LinkedList<String> checkList;
		checkList = st.get(id);
		int count = 0;
		for (int i = 0; i < checkList.size(); i++) {
			String categoryDescription;
			categoryDescription = ivalue.get(checkList.get(i) + id);
			if (categoryDescription.equals("parameter")) {
				count++;
			}
		}
		return count;
	}

	public String parameter_type_getter(int i, String scope) {
		int count = 0;
		LinkedList<String> idCheckList;
		idCheckList = st.get(scope);
		for (String id : idCheckList) {
			String category;
			category = itype.get(id + scope);
			String categoryDescription;
			categoryDescription = ivalue.get(id + scope);
			if (categoryDescription.equals("parameter")) {
				count++;
				if (count == i) {
					return category;
				}
			}
		}
		return null;
	}

	public boolean noMatching(String id, String scope) {
		LinkedList<String> checkList;
		checkList = st.get(scope);
		LinkedList<String> global_checkList;
		global_checkList = st.get("global");
		if (scope.equals("global")) {
			return global_checkList.indexOf(id) == global_checkList.lastIndexOf(id);
		}
		return ((checkList.indexOf(id) == checkList.lastIndexOf(id)) && (global_checkList.indexOf(id) == -1));
	}

	public boolean defFunc(String id) {

		LinkedList<String> checkList;
		ArrayList<String> funcs;
		checkList = st.get("global");
		funcs = new ArrayList<String>();
		for (int i = 0; i < checkList.size(); i++) {
			String categoryDescription;
			categoryDescription = ivalue.get(checkList.get(i) + "global");
			if (categoryDescription.equals("function") && checkList.get(i).equals(id)) {
				return true;
			}
		}
		return false;
	}

	public boolean withinScope(String id, String scope) {
		LinkedList<String> i;
		i = st.get(scope);
		if (i == null)
			return false;
		if (i.contains(id))
			return true;
		return false;
	}

	public void put(String id, String category, String data, String scope) {

		LinkedList<String> checkList;
		checkList = st.get(scope);
		if (checkList == null) {
			checkList = new LinkedList<>();
			checkList.add(id);
			st.put(scope, checkList);
		} else {
			checkList.addFirst(id);
		}
		itype.put(id + scope, category);
		ivalue.put(id + scope, data);
	}

	public void printST() {
		Enumeration e;
		e = st.keys();
		String scope;
		while (e.hasMoreElements()) {
			scope = (String) e.nextElement();
			System.out.println("Scope is - " + scope + "\n");
			LinkedList<String> idList;
			idList = st.get(scope);
			for (String id : idList) {
				String category = itype.get(id + scope);
				String categoryDescription = ivalue.get(id + scope);

				System.out.printf("%s: %s (%s)\n", categoryDescription, id, category);
			}
			System.out.println();
		}
	}

	public ArrayList<String> funcs_list() {

		LinkedList<String> checkList;
		ArrayList<String> funcs;
		checkList = st.get("global");
		funcs = new ArrayList<String>();
		for (int i = 0; i < checkList.size(); i++) {
			String categoryDescription;
			categoryDescription = ivalue.get(checkList.get(i) + "global");
			if (categoryDescription.equals("function"))
				funcs.add(checkList.get(i));
		}
		return funcs;
	}

}
